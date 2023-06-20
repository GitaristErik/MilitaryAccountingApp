package com.example.militaryaccountingapp.data.repository

import com.example.militaryaccountingapp.data.helper.ResultHelper.resultWrapper
import com.example.militaryaccountingapp.data.helper.ResultHelper.safetyResultWrapper
import com.example.militaryaccountingapp.domain.entity.data.Action
import com.example.militaryaccountingapp.domain.entity.data.ActionType
import com.example.militaryaccountingapp.domain.entity.data.Data
import com.example.militaryaccountingapp.domain.entity.extension.await
import com.example.militaryaccountingapp.domain.entity.user.UserPermission
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.HistoryRepository
import com.example.militaryaccountingapp.domain.repository.PermissionRepository
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class PermissionRepositoryImpl @Inject constructor(
    private val historyRepository: HistoryRepository
) : PermissionRepository {

    companion object {
        //                private const val PERMISSIONS = "permissions"
        const val ALL_PERMISSIONS = "all_permissions"
        const val ALL_PERMISSIONS_CATEGORIES = "all_permissions_categories"
        const val ALL_PERMISSIONS_ITEMS = "all_permissions_items"

        private const val PERMISSIONS_CATEGORIES = "permissions_categories"
        private const val PERMISSIONS_ITEMS = "permissions_items"
        private const val USERS = "users"
    }

    private val firestoreInstance: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val collectionUsers = firestoreInstance.collection(USERS)


    override suspend fun createPermission(
        userId: String,
        type: Data.Type,
        permission: UserPermission
    ): Results<UserPermission> = safetyResultWrapper({
        getCollection(userId, type).document(permission.id).set(permission).await()
    }) {
        firestoreInstance.collection(
            when (type) {
                Data.Type.CATEGORY -> PERMISSIONS_CATEGORIES
                Data.Type.ITEM -> PERMISSIONS_ITEMS
            }
        ).document(permission.id).let {
            updateInAllPermissions(it, userId = userId, type, permission)
        }

        Results.Success(permission)
    }

    data class PermissionLocation(
        val paths: List<DocumentReference> = listOf(),
        val ids: List<String> = listOf()
    )

    override suspend fun updateInAllPermissions(
        reference: DocumentReference,
        userId: String,
        type: Data.Type,
        permission: UserPermission
    ) {
        val ref = getCollectionAll(type)
            .document(permission.id)
        safetyResultWrapper({
            ref.get().await()
        }) {
            if (!it.exists()) {
                ref.set(
                    PermissionLocation(listOf(reference), listOf(permission.id))
                )
            } else {
                val i = it.toObject(PermissionLocation::class.java)!!
                ref.update(
                    permission.id, PermissionLocation(
                        (i.paths + reference).distinct(),
                        (i.ids + userId).distinct()
                    )
                )
            }.await()
        }
    }

    override suspend fun deleteAllPermissions(
        id: String,
        type: Data.Type,
    ): Results<Void?> = safetyResultWrapper({
        getCollectionAll(type).document(id).get().await()
    }) {
        if (it.exists()) {
            it.toObject(PermissionLocation::class.java)!!.paths.forEach { ref ->
                ref.delete()
            }
            it.reference.delete()
        }
        Results.Success(null)
    }

    /*
            override suspend fun createPermissionByParent(
                userId: String,
                type: Data.Type,
                parentId: String,
                permission: UserPermission,
            ): Results<UserPermission> = safetyResultWrapper({
                getCollection(userId, type).whereEqualTo("id", parentId).get().await()
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
    */


    override suspend fun getPermissionByUser(
        userId: String,
        type: Data.Type,
        elementId: String,
    ): Results<UserPermission?> = safetyResultWrapper({
        getCollection(userId, type).document(elementId).get().await()
    }) {
        Results.Success(it.toObject(UserPermission::class.java))
    }


    override suspend fun getPermissionsIdsByUsers(
        grantUserId: String,
        destinationUserId: String,
        type: Data.Type,
    ): Results<List<String>> = safetyResultWrapper({
        getCollection(destinationUserId, type)
            .whereArrayContains("grantedUsersId", grantUserId)
            .get().await()
    }) { querySnapshot ->
        Results.Success(querySnapshot.map { it.id })
    }


    override suspend fun deletePermissionByUser(
        userId: String,
        grantUserId: String,
        type: Data.Type,
        elementId: String,
        isShareAction: Boolean
    ): Results<Void?> = safetyResultWrapper({
        collectionUsers.document(userId).collection(
            when (type) {
                Data.Type.CATEGORY -> PERMISSIONS_CATEGORIES
                Data.Type.ITEM -> PERMISSIONS_ITEMS
            }
        ).document(elementId).delete().await()
    }) {
        // Додати до історії
        if (isShareAction) {
            historyRepository.addToHistory(
                Action(
                    action = ActionType.UNSHARE,
                    userId = grantUserId,
                    itemId = if (type == Data.Type.ITEM) elementId else null,
                    categoryId = if (type == Data.Type.CATEGORY) elementId else null,
                )
            )
        }
        Results.Success(it)
    }


    override suspend fun getReadCount(grantUserId: String, destinationUserId: String) =
        getSumCount(grantUserId, destinationUserId)

    override suspend fun getEditCount(grantUserId: String, destinationUserId: String) =
        getSumCount(grantUserId, destinationUserId, "canWrite")

    override suspend fun getShareForReadCount(grantUserId: String, destinationUserId: String) =
        getSumCount(grantUserId, destinationUserId, "canShare")

    override suspend fun getShareForEditCount(grantUserId: String, destinationUserId: String) =
        getSumCount(grantUserId, destinationUserId, "canShareForWrite")


    private suspend fun getSumCount(
        grantUserId: String,
        destinationUserId: String,
        additionalQueryFiler: String? = null
    ): Results<Long> = resultWrapper(
        getCount(
            grantUserId,
            destinationUserId,
            PERMISSIONS_CATEGORIES,
            additionalQueryFiler
        )
    ) { categoriesCount ->
        resultWrapper(
            getCount(
                grantUserId,
                destinationUserId,
                PERMISSIONS_ITEMS,
                additionalQueryFiler
            )
        ) { itemsCount ->
            Results.Success(categoriesCount + itemsCount)
        }
    }

    private suspend fun getCount(
        grantUserId: String,
        destinationUserId: String,
//        categoriesIds: List<String>,
        collection: String,
        additionalQueryFiler: String? = null
    ): Results<Long> = safetyResultWrapper({
        collectionUsers.document(destinationUserId).collection(collection)
//            .whereIn("id", categoriesIds)
            .whereArrayContains("grantedUsersId", grantUserId)
            .let { additionalQueryFiler?.run { it.whereEqualTo(this, true) } ?: it }
            .count()
            .get(AggregateSource.SERVER).await()
    }) {
        Results.Success(it.count)
    }


    override suspend fun updatePermissionByUser(
        userId: String,
        grantUserId: String,
        type: Data.Type,
        permission: UserPermission,
        isShareAction: Boolean
    ) = safetyResultWrapper({
        getCollection(userId, type).document(permission.id).get().await()
    }) { snapshot ->
        if (!snapshot.exists()) {
            resultWrapper(getPermissionByUser(grantUserId, type, permission.id)) { origPermission ->
                origPermission?.grantedUsersId?.let {
                    resultWrapper(createPermission(userId, type,
                        permission.apply { grantedUsersId = it + grantUserId }
                    )) {
                        if (isShareAction) {
                            historyRepository.addToHistory(
                                Action(
                                    action = ActionType.SHARE,
                                    userId = grantUserId,
                                    itemId = if (type == Data.Type.ITEM) permission.id else null,
                                    categoryId = if (type == Data.Type.CATEGORY) permission.id else null,
                                )
                            )
                        }
                        Results.Success(it)
                    }
                } ?: Results.Failure(NullPointerException("Parent category not found"))
            }
        } else resultWrapper(
            getCollection(userId, type).document(permission.id).update(
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

    override suspend fun getAllUsersIds(id: String, type: Data.Type): List<String> =
        (resultWrapper(getCollectionAll(type).document(id).get().await()) {
            val i = it.toObject(PermissionLocation::class.java)
            Results.Success(i?.ids)
        } as? Results.Success)?.data ?: emptyList()


    private fun getCollection(userId: String, type: Data.Type) =
        collectionUsers.document(userId).collection(
            when (type) {
                Data.Type.CATEGORY -> PERMISSIONS_CATEGORIES
                Data.Type.ITEM -> PERMISSIONS_ITEMS
            }
        )

    private fun getCollectionAll(type: Data.Type) = firestoreInstance
        .collection(
            when (type) {
                Data.Type.CATEGORY -> ALL_PERMISSIONS_CATEGORIES
                Data.Type.ITEM -> ALL_PERMISSIONS_ITEMS
            }
        )


}