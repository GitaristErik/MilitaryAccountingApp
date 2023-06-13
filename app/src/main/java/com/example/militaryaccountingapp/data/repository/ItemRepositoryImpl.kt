package com.example.militaryaccountingapp.data.repository

import com.example.militaryaccountingapp.data.helper.ResultHelper.resultWrapper
import com.example.militaryaccountingapp.data.helper.ResultHelper.safetyResultWrapper
import com.example.militaryaccountingapp.domain.entity.data.Item
import com.example.militaryaccountingapp.domain.entity.extension.await
import com.example.militaryaccountingapp.domain.entity.user.UserPermission
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.CategoryRepository
import com.example.militaryaccountingapp.domain.repository.ItemRepository
import com.example.militaryaccountingapp.domain.repository.PermissionRepository
import com.example.militaryaccountingapp.domain.usecase.auth.CurrentUserUseCase
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val currentUserUseCase: CurrentUserUseCase,
    private val permissionRepository: PermissionRepository,
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
                isCanShare = true,
                isCanShareForWrite = true,
                isCanWrite = true,
            )
            permissionRepository.createPermissionByParent(userPermission, parentCategory.id)
            // return result
            Results.Success(item)
        }
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
        }) { Results.Success(null) }


    override suspend fun deleteItem(id: String): Results<Void?> {
        return safetyResultWrapper({
            collection.document(id)
                .delete()
                .await()
        }) {
            // TODO delete images from storage
            Results.Success(null)
        }
    }

    override suspend fun getItem(id: String): Results<Item> {
        return resultWrapper(
            collection.document(id)
                .get()
                .await()
        ) {
            when (val item = it.toObject(Item::class.java)) {
                null -> Results.Failure(ClassCastException("Error while casting item"))
                else -> Results.Success(item)
            }
        }
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