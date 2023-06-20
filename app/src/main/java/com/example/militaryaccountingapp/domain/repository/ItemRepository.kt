package com.example.militaryaccountingapp.domain.repository

import com.example.militaryaccountingapp.domain.entity.data.Item
import com.example.militaryaccountingapp.domain.helper.Results

interface ItemRepository {

    suspend fun createItem(item: Item): Results<Item>

    suspend fun getItem(id: String): Results<Item>

    suspend fun updateItem(id: String, query: Map<String, Any?>): Results<Void?>

    suspend fun deleteItem(id: String): Results<Void?>

    suspend fun getItemsCount(parentId: String): Results<Long>

    suspend fun changeRules(
        elementId: String,
        userId: String,
        grantUserId: String,
        canRead: Boolean,
        canEdit: Boolean,
        canShareRead: Boolean,
        canShareEdit: Boolean
    )

    suspend fun getItems(itemsIds: List<String>): Results<List<Item>>

    suspend fun getItems(
        parentId: String,
        userId: String,
        isAscending: Boolean
    ): Results<Map<Item, List<String>>>

}