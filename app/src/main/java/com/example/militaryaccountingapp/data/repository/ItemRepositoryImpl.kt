package com.example.militaryaccountingapp.data.repository

import com.example.militaryaccountingapp.data.helper.ResultHelper.safetyResultWrapper
import com.example.militaryaccountingapp.domain.entity.data.Action
import com.example.militaryaccountingapp.domain.entity.data.ActionType
import com.example.militaryaccountingapp.domain.entity.data.Item
import com.example.militaryaccountingapp.domain.entity.extension.await
import com.example.militaryaccountingapp.domain.entity.user.UserPermission
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.CategoryRepository
import com.example.militaryaccountingapp.domain.repository.HistoryRepository
import com.example.militaryaccountingapp.domain.repository.ItemRepository
import com.example.militaryaccountingapp.domain.repository.PermissionRepository
import com.example.militaryaccountingapp.domain.usecase.auth.CurrentUserUseCase
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val currentUserUseCase: CurrentUserUseCase,
    private val permissionRepository: PermissionRepository,
    private val historyRepository: HistoryRepository
) : ItemRepository {

    companion object {
        private const val COLLECTION_NAME = "items"
    }

    private val firestoreInstance: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val collection = firestoreInstance.collection(COLLECTION_NAME)


    override suspend fun createItem(item: Item): Results<Item> = safetyResultWrapper({
        item.userId = currentUserUseCase()?.id ?: return@safetyResultWrapper Results.Failure(
            NullPointerException("User id is null")
        );
        categoryRepository.getCategory(item.parentId ?: item.userId!!)
    }) { parentCategory ->
        item.allParentIds = parentCategory.allParentIds + parentCategory.id
        item.allParentNames = parentCategory.allParentNames + parentCategory.name
        val ref = collection.document()
        item.id = ref.id

        // TODO load photo to storage
        item.imagesUrls = emptyList()

        safetyResultWrapper({
            ref.set(item).await()
        }) {
            // create permission for current user
            val userPermission = UserPermission(
                userId = item.userId!!,
                itemId = item.id,
                canShare = true,
                canShareForWrite = true,
                canWrite = true,
            )
            permissionRepository.createPermissionByParent(userPermission, parentCategory.id)

            val action = Action(
                userId = item.userId!!,
                itemId = item.id,
                action = ActionType.CREATE,
            )
            historyRepository.addToHistory(action)
            // return result
            Results.Success(item)
        }
    }

    override suspend fun getItems(
        itemsIds: List<String>
    ): Results<List<Item>> = safetyResultWrapper({
        collection
            .whereIn("id", itemsIds)
            .get()
            .await()
    }) {
        Results.Success(it.toObjects(Item::class.java))
    }


    override suspend fun getItemsCount(parentId: String): Results<Long> = safetyResultWrapper({
        collection
            .whereEqualTo("parentId", parentId)
            .count()
            .get(AggregateSource.SERVER)
            .await()
    }) {
        Results.Success(it.count)
    }


    override suspend fun updateItem(id: String, query: Map<String, Any?>): Results<Void?> =
        safetyResultWrapper({
            collection.document(id)
                .update(query)
                .await()
        }) {
            currentUserUseCase()?.let { user ->
                var countParamsToUpdate = query.size
                if (query.containsKey("count")) {
                    countParamsToUpdate--
                    (getItem(id) as? Results.Success)?.data?.let { oldItem ->
                        val count = oldItem.count - query["count"] as Int
                        (if (count < 0) ActionType.DECREASE_COUNT
                        else if (count > 0) ActionType.INCREASE_COUNT
                        else null)?.let { type ->
                            val action = Action(
                                userId = user.id,
                                itemId = id,
                                action = type,
                                value = count
                            )
                            historyRepository.addToHistory(action)
                        }
                    }
                }
                if (countParamsToUpdate > 0) {
                    val action = Action(
                        userId = user.id,
                        itemId = id,
                        action = ActionType.UPDATE,
                    )
                    historyRepository.addToHistory(action)
                }
            }



            Results.Success(null)
        }


    override suspend fun deleteItem(id: String): Results<Void?> {
        return safetyResultWrapper({
            collection.document(id)
                .delete()
                .await()
        }) {
            // TODO delete images from storage
            currentUserUseCase()?.let { user ->
                val action = Action(
                    userId = user.id,
                    itemId = id,
                    action = ActionType.DELETE,
                )
                historyRepository.addToHistory(action)
            }
            Results.Success(null)
        }
    }

    override suspend fun getItem(id: String): Results<Item> = safetyResultWrapper({
        Timber.d("getItem, id: $id")
        collection.document(id)
            .get()
            .await()
    }) {
        when (val item = it.toObject(Item::class.java)) {
            null -> Results.Failure(ClassCastException("Error while casting item"))
            else -> Results.Success(item)
        }
    }

    override suspend fun changeRules(
        elementId: String,
        userId: String,
        canRead: Boolean,
        canEdit: Boolean,
        canShareRead: Boolean,
        canShareEdit: Boolean
    ) {
        if (canRead) permissionRepository.updatePermission(
            UserPermission(
                itemId = elementId,
                userId = userId,
                canWrite = canEdit,
                canShare = canShareRead,
                canShareForWrite = canShareEdit
            )
        ) else permissionRepository.deletePermission(
            userId = userId,
            itemId = elementId
        )
    }

    /*
    suspend fun setBarcodes(
        itemId: String,
        barcodes: Set<Barcode>
    ): Results<Void?> = safetyResultWrapper({
        barcodeRepository.updateCodes(barcodes)
    }) { updatedBarcodes ->
        safetyResultWrapper({
            getItem(itemId)
        }) { item ->
            val newIds = updatedBarcodes.map { it.id }.toSet()
            val idsForDelete = item.barCodesIds.minus(newIds)
            safetyResultWrapper({
                barcodeRepository.deleteCodes(idsForDelete)
            }) {
                updateItem(itemId, mapOf("barCodesIds" to newIds))
            }
        }
    }*/

}