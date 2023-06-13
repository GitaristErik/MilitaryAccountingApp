package com.example.militaryaccountingapp.data.repository

import com.example.militaryaccountingapp.data.helper.ResultHelper.safetyResultWrapper
import com.example.militaryaccountingapp.domain.entity.extension.await
import com.example.militaryaccountingapp.domain.entity.user.UserPermission
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.PermissionRepository
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

    override suspend fun deletePermission(
        permission: UserPermission
    ): Results<Void> = safetyResultWrapper({
        collection.document(permission.userId).delete().await()
    }) {
        Results.Success(it)
    }


}