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

    suspend fun updateCategory(id: String, query: Map<String, Any?>): Results<Void?>

    suspend fun deleteCategoryAndChildren(categoryId: String): Results<Unit>

    suspend fun getCategoriesCount(parentId: String): Results<Long>
}