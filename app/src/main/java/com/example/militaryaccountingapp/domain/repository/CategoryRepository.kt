package com.example.militaryaccountingapp.domain.repository

import com.example.militaryaccountingapp.domain.entity.data.Category
import com.example.militaryaccountingapp.domain.helper.Results

interface CategoryRepository {

    suspend fun createCategory(
        category: Category,
        userId: String?
    ): Results<Category>

    suspend fun createRootCategory(
        category: Category,
        userId: String?
    ): Results<Category>

    suspend fun getCategory(id: String): Results<Category>

    suspend fun updateCategory(
        id: String,
        userId: String,
        query: Map<String, Any?>
    ): Results<Void?>

    suspend fun deleteCategoryAndChildren(
        userId: String,
        categoryId: String
    ): Results<Unit>

    suspend fun getCategoriesCount(parentId: String): Results<Long>

    suspend fun changeRules(
        elementId: String,
        userId: String,
        canRead: Boolean,
        canEdit: Boolean,
        canShareRead: Boolean,
        canShareEdit: Boolean
    )

    suspend fun getCategories(categoriesIds: List<String>): Results<List<Category>>
}