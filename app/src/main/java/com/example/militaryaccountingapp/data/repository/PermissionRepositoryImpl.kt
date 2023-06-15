package com.example.militaryaccountingapp.data.repository

import com.example.militaryaccountingapp.data.helper.ResultHelper.resultWrapper
import com.example.militaryaccountingapp.data.helper.ResultHelper.safetyResultWrapper
import com.example.militaryaccountingapp.domain.entity.extension.await
import com.example.militaryaccountingapp.domain.entity.user.UserPermission
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.PermissionRepository
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class PermissionRepositoryImpl @Inject constructor(
) : PermissionRepository {

    companion object {
        private const val PERMISSIONS = "permissions"
    }

    private val firestoreInstance: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val collection = firestoreInstance.collection(PERMISSIONS)


    override suspend fun createPermission(
        permission: UserPermission
    ): Results<UserPermission> = safetyResultWrapper({
        collection.document().set(permission).await()
    }) {
        Results.Success(permission)
    }

    override suspend fun createPermissionByParent(
        permission: UserPermission,
        parentId: String
    ): Results<UserPermission> = safetyResultWrapper({
        collection.whereEqualTo("categoryId", parentId).get().await()
    }) { snapshot ->
        if (snapshot.isEmpty) {
            Results.Failure(NullPointerException("Parent category not found"))
        } else {
            snapshot.toObjects(UserPermission::class.java).forEach {
                permission.userId = it.userId
                createPermission(permission)
            }
            Results.Success(permission)
        }
    }

    override suspend fun getPermission(
        userId: String?,
        categoryId: String?,
        itemId: String?
    ): Results<UserPermission?> = safetyResultWrapper({
        collection
            .let { userId?.run { it.whereEqualTo("userId", this) } ?: it }
            .let { categoryId?.run { it.whereEqualTo("categoryId", this) } ?: it }
            .let { itemId?.run { it.whereEqualTo("itemId", this) } ?: it }
            .get()
            .await()
    }) {
        Results.Success(it.firstOrNull()?.toObject(UserPermission::class.java))
    }

    /**
     * @return pair of list of categories ids and list of items ids
     */
    override suspend fun getPermissionsByUsers(
        userId: String,
        selectedUsersIds: List<String>
    ): Results<Pair<List<String>, List<String>>> = safetyResultWrapper({
        collection.whereEqualTo("userId", userId).get().await()
    }) { mainQuery ->
        val mainCategoriesIds = mutableSetOf<String>()
        val mainItemsIds = mutableSetOf<String>()

        mainQuery.toObjects(UserPermission::class.java).forEach {
            if (!it.categoryId.isNullOrEmpty()) {
                mainCategoriesIds.add(it.categoryId)
            } else if (!it.itemId.isNullOrEmpty()) {
                mainItemsIds.add(it.itemId)
            }
        }


        val usersIds = selectedUsersIds.minusElement(userId)
        if (usersIds.isEmpty()) {
            return@safetyResultWrapper Results.Success(mainCategoriesIds.toList() to mainItemsIds.toList())
        }
        val allUsersPermissions = collection
            .whereIn("userId", usersIds)
            .where(
                Filter.or(
                    Filter.inArray("categoryId", mainCategoriesIds.toList()),
                    Filter.inArray("itemId", mainItemsIds.toList())
                )
            ).get().await()

        (allUsersPermissions as? Results.Success)?.data
            ?.toObjects(UserPermission::class.java)?.forEach {
                if (!it.categoryId.isNullOrEmpty()) {
                    mainCategoriesIds.add(it.categoryId)
                } else if (!it.itemId.isNullOrEmpty()) {
                    mainItemsIds.add(it.itemId)
                }
            }

        Results.Success(mainCategoriesIds.toList() to mainItemsIds.toList())
    }


    override suspend fun deletePermission(
        userId: String?,
        itemId: String?,
        categoryId: String?,
    ): Results<Void?> = safetyResultWrapper({
        collection
            .let { userId?.run { it.whereEqualTo("userId", this) } ?: it }
            .let { itemId?.run { it.whereEqualTo("itemId", this) } ?: it }
            .let { categoryId?.run { it.whereEqualTo("categoryId", this) } ?: it }
            .get().await()
    }) { snapshot ->
        snapshot.forEach {
            collection.document(it.id).delete().await()
        }
        Results.Success(null)
    }


    override suspend fun getReadCount(userId: String): Results<Long> = safetyResultWrapper({
        collection.whereEqualTo("userId", userId)
            .count()
            .get(AggregateSource.SERVER).await()
    }) {
        Results.Success(it.count)
    }

    override suspend fun getEditCount(userId: String): Results<Long> = safetyResultWrapper({
        collection.whereEqualTo("userId", userId)
            .whereEqualTo("canWrite", true)
            .count()
            .get(AggregateSource.SERVER).await()
    }) {
        Results.Success(it.count)
    }

    override suspend fun getShareForReadCount(userId: String): Results<Long> = safetyResultWrapper({
        collection.whereEqualTo("userId", userId)
            .whereEqualTo("canShare", true)
            .count()
            .get(AggregateSource.SERVER).await()
    }) {
        Results.Success(it.count)
    }

    override suspend fun getShareForEditCount(userId: String): Results<Long> = safetyResultWrapper({
        collection.whereEqualTo("userId", userId)
            .whereEqualTo("canShareForWrite", true)
            .count()
            .get(AggregateSource.SERVER).await()
    }) {
        Results.Success(it.count)
    }

    override suspend fun updatePermission(permission: UserPermission) = safetyResultWrapper({
        collection.whereEqualTo("userId", permission.userId)
            .whereEqualTo("itemId", permission.itemId)
            .whereEqualTo("categoryId", permission.categoryId)
            .get().await()
    }) { snapshot ->
        val id = snapshot.firstOrNull()?.id
        if (id == null) {
            createPermission(permission)
        } else resultWrapper(
            collection.document(id).update(
                mapOf(
                    "canWrite" to permission.canWrite,
                    "canShare" to permission.canShare,
                    "canShareForWrite" to permission.canShareForWrite,
                )
            ).await()
        ) {
            Results.Success(it)
        }
    }
}