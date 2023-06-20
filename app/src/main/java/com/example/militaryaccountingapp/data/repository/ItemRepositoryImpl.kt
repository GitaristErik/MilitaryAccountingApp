package com.example.militaryaccountingapp.data.repository

import com.example.militaryaccountingapp.data.helper.ResultHelper.safetyResultWrapper
import com.example.militaryaccountingapp.domain.entity.data.Action
import com.example.militaryaccountingapp.domain.entity.data.ActionType
import com.example.militaryaccountingapp.domain.entity.data.Data
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
import com.google.firebase.firestore.Query.Direction
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
        categoryRepository.getCategory(item.parentId!!)
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
            permissionRepository.createPermission(
                userId = item.userId!!,
                type = Data.Type.ITEM,
                UserPermission(
                    id = item.id,
                    grantedUsersId = listOf(item.userId!!),
                    canShare = true,
                    canShareForWrite = true,
                    canWrite = true,
                )
            )

            historyRepository.addToHistory(
                Action(
                    userId = item.userId!!,
                    itemId = item.id,
                    action = ActionType.CREATE,
                )
            )

            // return result
            Results.Success(item)
        }
    }

    override suspend fun getItems(
        itemsIds: List<String>
    ): Results<List<Item>> = if (itemsIds.isEmpty()) Results.Success(emptyList())
    else safetyResultWrapper({
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
            permissionRepository.deleteAllPermissions(
                id = id,
                type = Data.Type.ITEM,
            )

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

    override suspend fun getItems(
        parentId: String,
        userId: String,
        isAscending: Boolean
    ): Results<Map<Item, List<String>>> = safetyResultWrapper({
        collection.whereEqualTo("parentId", parentId).orderBy(
            "name",
            if (isAscending) Direction.ASCENDING else Direction.DESCENDING
        ).get().await()
    }) {
        Results.Success(
            it.toObjects(Item::class.java).map { item ->
                item to permissionRepository.getAllUsersIds(
                    id = item.id,
                    type = Data.Type.ITEM
                )
            }.toMap()
        )
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
        grantUserId: String,
        canRead: Boolean,
        canEdit: Boolean,
        canShareRead: Boolean,
        canShareEdit: Boolean
    ) {
        if (canRead) permissionRepository.updatePermissionByUser(
            userId = userId,
            grantUserId = grantUserId,
            type = Data.Type.ITEM,
            permission = UserPermission(
                id = elementId,
                canWrite = canEdit,
                canShare = canShareRead,
                canShareForWrite = canShareEdit
            )
        ) else permissionRepository.deletePermissionByUser(
            type = Data.Type.ITEM,
            userId = userId,
            grantUserId = grantUserId,
            elementId = elementId
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