package com.example.militaryaccountingapp.domain.repository

import com.example.militaryaccountingapp.domain.entity.user.UserPermission
import com.example.militaryaccountingapp.domain.helper.Results

interface PermissionRepository {

    suspend fun createPermission(
        permission: UserPermission
    ): Results<UserPermission>

    suspend fun deletePermission(
        userId: String? = null,
        itemId: String? = null,
        categoryId: String? = null,
    ): Results<Void?>

    suspend fun createPermissionByParent(
        permission: UserPermission,
        parentId: String
    ): Results<UserPermission>

    suspend fun getReadCount(userId: String): Results<Long>
    suspend fun getEditCount(userId: String): Results<Long>
    suspend fun getShareForReadCount(userId: String): Results<Long>
    suspend fun getShareForEditCount(userId: String): Results<Long>

    suspend fun updatePermission(permission: UserPermission): Results<Any>

    suspend fun getPermission(
        userId: String? = null,
        categoryId: String? = null,
        itemId: String? = null
    ): Results<UserPermission?>

    suspend fun getPermissionsByUsers(
        userId: String,
        selectedUsersIds: List<String>
    ): Results<Pair<List<String>, List<String>>>
}
