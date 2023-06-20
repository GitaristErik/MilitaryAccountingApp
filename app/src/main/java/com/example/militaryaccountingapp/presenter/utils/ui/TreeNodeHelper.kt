package com.example.militaryaccountingapp.presenter.utils.ui

import com.example.militaryaccountingapp.domain.entity.data.Category
import com.example.militaryaccountingapp.domain.entity.data.Data
import com.example.militaryaccountingapp.domain.entity.data.Item
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.presenter.model.filter.TreeNodeItem
import timber.log.Timber

object TreeNodeHelper {

    fun loadNodesHandler(
        categoriesRes: Results<List<Category>>,
        itemsRes: Results<List<Item>>,
        cache: MutableMap<String, TreeNodeItem>,
        onSuccessLoad: (List<TreeNodeItem>) -> Unit
    ) {
        if (categoriesRes is Results.Success && itemsRes is Results.Success) {
            val items = itemsRes.data
            val categories = categoriesRes.data
            val nodes = findRootCategories(categories).map { category ->
                convertCategoryToTreeNodeItem(
                    category,
                    categories + items,
                    cache = cache
                )
            }
            Timber.d("loadNodes | nodes: $nodes")
            onSuccessLoad(nodes)
        } else {
            Timber.e("Error loadNodes | categoriesRes: $categoriesRes | itemsRes: $itemsRes")
        }
    }


    fun findRootCategories(categories: List<Category>): List<Category> {
        val categoryIndex = categories.associateBy { it.id }
        val rootCategories = mutableListOf<Category>()

        for (category in categories) {
            // Перевіряємо, чи відсутнє поле parentId у списку всіх категорій
            val isRootCategory = category.parentId !in categoryIndex
            if (isRootCategory) {
                rootCategories.add(category)
            }
        }

        return rootCategories
    }

    private fun convertCategoriesToTreeNodeItems(categories: List<Category>): List<TreeNodeItem> {
//        currentUser.rootCategories
        val rootCategories = mutableListOf<TreeNodeItem>()

        // Перетворюємо кожну категорію в TreeNodeItem
        for (category in categories) {
            if (category.parentId == null) {
                val treeNodeItem = convertCategoryToTreeNodeItem(category, categories)
                rootCategories.add(treeNodeItem)
            }
        }

        return rootCategories
    }

    private fun convertCategoryToTreeNodeItem(
        category: Data,
        elements: List<Data>
    ): TreeNodeItem {
        val childItems = mutableListOf<TreeNodeItem>()

        // Перетворюємо дочірні категорії в TreeNodeItem
        for (childCategory in elements) {
            if (childCategory.parentId == category.id) {
                val childTreeNodeItem = convertCategoryToTreeNodeItem(childCategory, elements)
                childItems.add(childTreeNodeItem)
            }
        }

        return TreeNodeItem(
            nodeViewId = category.id,
            child = childItems,
            name = category.name,
            id = category.id,
            isCategory = true
        )
    }


    fun convertCategoryToTreeNodeItem(
        rootElement: Data,
        elements: List<Data>,
        cache: MutableMap<String, TreeNodeItem>
    ): TreeNodeItem {
        // Перевіряємо, чи результат вже є в кеші
        if (rootElement.id in cache) {
            return cache[rootElement.id]!!
        }

        val childItems = mutableListOf<TreeNodeItem>()

        // Перетворюємо дочірні категорії в TreeNodeItem
        for (childCategory in elements) {
            if (childCategory.parentId == rootElement.id) {
                val childTreeNodeItem =
                    convertCategoryToTreeNodeItem(childCategory, elements, cache)
                childItems.add(childTreeNodeItem)
            }
        }

        val treeNodeItem = TreeNodeItem(
            nodeViewId = rootElement.id,
            child = childItems,
            name = rootElement.name,
            id = rootElement.id,
            isCategory = true
        )

        // Зберігаємо результат у кеші
        cache[rootElement.id] = treeNodeItem

        return treeNodeItem
    }
}