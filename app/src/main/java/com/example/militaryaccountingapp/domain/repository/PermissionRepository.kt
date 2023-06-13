package com.example.militaryaccountingapp.domain.repository

import com.example.militaryaccountingapp.domain.entity.user.UserPermission
import com.example.militaryaccountingapp.domain.helper.Results

interface PermissionRepository {

    suspend fun createPermission(
        permission: UserPermission
    ): Results<UserPermission>

    suspend fun deletePermission(
        permission: UserPermission
    ): Results<Void>

    suspend fun createPermissionByParent(
        permission: UserPermission,
        parentId: String
    ): Results<UserPermission>
}
