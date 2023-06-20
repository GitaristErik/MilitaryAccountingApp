package com.example.militaryaccountingapp.domain.repository

import com.example.militaryaccountingapp.domain.entity.data.Data
import com.example.militaryaccountingapp.domain.entity.user.UserPermission
import com.example.militaryaccountingapp.domain.helper.Results
import com.google.firebase.firestore.DocumentReference

interface PermissionRepository {


    suspend fun createPermission(
        userId: String,
        type: Data.Type,
        permission: UserPermission,
    ): Results<UserPermission>

    suspend fun deletePermissionByUser(
        userId: String,
        grantUserId: String,
        type: Data.Type,
        elementId: String,
        isShareAction: Boolean = false
    ): Results<Void?>


    suspend fun deleteAllPermissions(
        id: String,
        type: Data.Type,
    ): Results<Void?>


    suspend fun updateInAllPermissions(
        reference: DocumentReference,
        userId: String,
        type: Data.Type,
        permission: UserPermission
    ): Unit

    /*    suspend fun createPermissionByParent(
            permission: UserPermission,
            parentId: String
        ): Results<UserPermission>*/


    suspend fun getReadCount(
        grantUserId: String,
        destinationUserId: String,
    ): Results<Long>

    suspend fun getEditCount(
        grantUserId: String,
        destinationUserId: String,
    ): Results<Long>

    suspend fun getShareForReadCount(
        grantUserId: String,
        destinationUserId: String,
    ): Results<Long>

    suspend fun getShareForEditCount(
        grantUserId: String,
        destinationUserId: String,
    ): Results<Long>


    suspend fun updatePermissionByUser(
        userId: String,
        grantUserId: String,
        type: Data.Type,
        permission: UserPermission,
        isShareAction: Boolean = false
    ): Results<Any>


    suspend fun getPermissionByUser(
        userId: String,
        type: Data.Type,
        elementId: String
    ): Results<UserPermission?>

    suspend fun getPermissionsIdsByUsers(
        grantUserId: String,
        destinationUserId: String,
        type: Data.Type,
    ): Results<List<String>>

    suspend fun getAllUsersIds(id: String, type: Data.Type): List<String>
}
