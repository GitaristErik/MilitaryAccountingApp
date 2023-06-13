package com.example.militaryaccountingapp.domain.repository

import com.example.militaryaccountingapp.domain.entity.data.Item
import com.example.militaryaccountingapp.domain.helper.Results

interface ItemRepository {

    suspend fun createItem(item: Item): Results<Item>

    suspend fun getItem(id: String): Results<Item>

    suspend fun updateItem(id: String, query: Map<String, Any?>): Results<Void?>

    suspend fun deleteItem(id: String): Results<Void?>

    suspend fun getItemsCount(parentId: String): Results<Long>

    /*

        */
/**
     * @param limit group size (-1 - all)
     * @param page page number (0 - first)
     * @param parentId id of parent category (null - all)
     * @param userId id of user (null - all)
     * @param query query string for search by filters
     * @param filters set of filters for search
     * @param isAscending true - ascending, false - descending
     * @param sortType sort type for returned list
     *//*

    suspend fun getItems(
        limit: Int = -1,
        page: Int = 0,
        parentId: Int? = null,
        userId: Int? = null,
        query: String,
        filters: Set<Filters> = emptySet(),
        isAscending: Boolean = true,
        sortType: SortType = SortType.NAME,
    ): Result<List<Item>>
*/

}