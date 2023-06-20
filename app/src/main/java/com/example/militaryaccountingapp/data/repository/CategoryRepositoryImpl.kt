package com.example.militaryaccountingapp.data.repository

import com.example.militaryaccountingapp.data.helper.ResultHelper.resultWrapper
import com.example.militaryaccountingapp.data.helper.ResultHelper.safetyResultWrapper
import com.example.militaryaccountingapp.domain.entity.data.Action
import com.example.militaryaccountingapp.domain.entity.data.ActionType
import com.example.militaryaccountingapp.domain.entity.data.Category
import com.example.militaryaccountingapp.domain.entity.data.Data
import com.example.militaryaccountingapp.domain.entity.data.Item
import com.example.militaryaccountingapp.domain.entity.extension.await
import com.example.militaryaccountingapp.domain.entity.user.UserPermission
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.CategoryRepository
import com.example.militaryaccountingapp.domain.repository.HistoryRepository
import com.example.militaryaccountingapp.domain.repository.PermissionRepository
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.tasks.await as awaitCoroutine

class CategoryRepositoryImpl @Inject constructor(
    private val permissionRepository: PermissionRepository,
    private val historyRepository: HistoryRepository
) : CategoryRepository {

    companion object {
        private const val COLLECTION_NAME = "categories"
    }

    private val firestoreInstance: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val collection = firestoreInstance.collection(COLLECTION_NAME)


    override suspend fun createCategory(
        category: Category,
    ): Results<Category> = safetyResultWrapper({
        getCategory(category.parentId!!)
    }) { parentCategory ->
        category.allParentIds = parentCategory.allParentIds + parentCategory.id
        category.allParentNames = parentCategory.allParentNames + parentCategory.name
        val ref = collection.document()
        category.id = ref.id

        // TODO load photo to storage
        category.imagesUrls = emptyList()

        resultWrapper(ref.set(category).await()) {
            createHandler(category)
        }
    }

    override suspend fun createRootCategory(
        category: Category,
    ): Results<Category> = safetyResultWrapper({
        val ref = collection.document()
        category.id = ref.id

        // TODO load photo to storage
        category.imagesUrls = emptyList()

        ref.set(category).await()
    }) {
        createHandler(category)
    }

    private suspend fun createHandler(category: Category): Results<Category> {
        permissionRepository.createPermission(
            userId = category.userId!!,
            type = Data.Type.CATEGORY,
            UserPermission(
                grantedUsersId = listOf(category.userId!!),
                id = category.id,
                canShare = true,
                canShareForWrite = true,
                canWrite = true,
            ),
        )

        historyRepository.addToHistory(
            Action(
                action = ActionType.CREATE,
                userId = category.userId!!,
                categoryId = category.id,
            )
        )

        return Results.Success(category)
    }


    override suspend fun getCategory(id: String): Results<Category> = safetyResultWrapper({
        Timber.d("getCategory, id: $id")
        collection.document(id)
            .get()
            .await()
    }) {
        when (val category = it.toObject(Category::class.java)) {
            null -> Results.Failure(ClassCastException("Error while casting category"))
            else -> Results.Success(category)
        }
    }

    override suspend fun getCategories(
        parentId: String,
        userId: String,
        isAscending: Boolean
    ): Results<Map<Category, List<String>>> = safetyResultWrapper({
        collection.whereEqualTo("parentId", parentId).orderBy(
            "name",
            if (isAscending) Query.Direction.ASCENDING else Query.Direction.DESCENDING
        ).get().await()
    }) {
        Results.Success(
            it.toObjects(Category::class.java).map { item ->
                item to permissionRepository.getAllUsersIds(
                    id = item.id,
                    type = Data.Type.CATEGORY
                )
            }.toMap()
        )
    }

    override suspend fun changeRules(
        elementId: String,
        userId: String,
        grantUserId: String,
        canRead: Boolean,
        canEdit: Boolean,
        canShareRead: Boolean,
        canShareEdit: Boolean
    ) {
        updatePermissionForAllParents(
            categoryId = elementId,
            canRead = canRead,
            userId = userId,
            grantUserId = grantUserId,
            permission = UserPermission(
                grantedUsersId = listOf(grantUserId),
                id = elementId,
                canWrite = canEdit,
                canShare = canShareRead,
                canShareForWrite = canShareEdit
            )
        )
    }

    private suspend fun updatePermissionForAllParents(
        grantUserId: String,
        userId: String,
        categoryId: String,
        permission: UserPermission,
        canRead: Boolean
    ): Results<Unit> {
        try {
            // Отримати дочірні категорії вказаної категорії
            // Рекурсивно викликати цю функцію для кожної дочірньої категорії
            for (childCategorySnapshot in collection
                .whereEqualTo("parentId", categoryId)
                .get()
                .awaitCoroutine()
            ) {
                val childCategoryId = childCategorySnapshot.id
                val deleteChildResult =
                    updatePermissionForAllParents(
                        grantUserId = grantUserId,
                        userId = userId,
                        categoryId = childCategoryId,
                        permission = permission,
                        canRead = canRead,
                    )
                if (deleteChildResult is Results.Failure) {
                    // Повертаємо помилку, якщо виникла під час видалення дочірньої категорії
                    return deleteChildResult
                }
            }

            // Отримати елементи, які належать вказаній категорії
            val itemsQuery = itemsCollection.whereEqualTo("parentId", categoryId)
            val itemsSnapshots = itemsQuery.get().awaitCoroutine()

            // Оновити дозволи на елементи
            for (itemSnapshot in itemsSnapshots) {
                val itemId = itemSnapshot.id
                val itemRef = itemsCollection.document(itemId)

                // Оновити дозволи на елемент
                /*val ref = firestoreInstance
                    .collection("users")
                    .document(userId!!)
                    .collection("permissions_item")
                    .document(itemId)*/

                updatePermission(
                    canRead = canRead,
                    userId = userId,
                    type = Data.Type.ITEM,
                    elementId = itemId,
                    grantUserId = grantUserId,
                    permission = permission.copy(id = itemId)
                )
            }

            // Обновити доступ до категорії
            updatePermission(
                canRead = canRead,
                userId = userId,
                type = Data.Type.CATEGORY,
                elementId = categoryId,
                grantUserId = grantUserId,
                permission = permission.copy(id = categoryId)
            )

            return Results.Success(Unit)
        } catch (e: Exception) {
            return Results.Failure(e)
        }
    }

    private suspend inline fun updatePermission(
        canRead: Boolean,
        userId: String,
        elementId: String,
        type: Data.Type,
        grantUserId: String,
        permission: UserPermission
    ) {
        if (canRead) {
            permissionRepository.updatePermissionByUser(
                userId = userId,
                grantUserId = grantUserId,
                type = type,
                permission = permission.copy(id = elementId),
                isShareAction = true
            )
        } else {
            permissionRepository.deletePermissionByUser(
                userId = userId,
                grantUserId = grantUserId,
                type = type,
                elementId = elementId,
                isShareAction = true
            )
        }
    }


    override suspend fun updateCategory(
        id: String,
        userId: String,
        query: Map<String, Any?>
    ): Results<Void?> = safetyResultWrapper({
        collection.document(id)
            .update(query)
            .await()
    }) {
        historyRepository.addToHistory(
            Action(
                action = ActionType.CREATE,
                userId = userId,
                categoryId = id,
            )
        )
        Results.Success(null)
    }


    private val itemsCollection = firestoreInstance.collection("items")

    override suspend fun deleteCategoryAndChildren(
        userId: String,
        categoryId: String
    ): Results<Unit> {
        try {
            val batch = firestoreInstance.batch()

            // Отримати дочірні категорії вказаної категорії
            // Рекурсивно видалити дочірні категорії
            for (childCategorySnapshot in collection
                .whereEqualTo("parentId", categoryId)
                .get()
                .awaitCoroutine()
            ) {
                val childCategoryId = childCategorySnapshot.id
                val deleteChildResult = deleteCategoryAndChildren(userId, childCategoryId)
                if (deleteChildResult is Results.Failure) {
                    return deleteChildResult // Повертаємо помилку, якщо виникла під час видалення дочірньої категорії
                }
            }

            // Отримати елементи, які належать вказаній категорії
            val itemsQuery = itemsCollection.whereEqualTo("parentId", categoryId)
            val itemsSnapshots = itemsQuery.get().awaitCoroutine()

            // Видалити елементи
            for (itemSnapshot in itemsSnapshots) {
                val itemId = itemSnapshot.id
                val itemRef = itemsCollection.document(itemId)

                // Занести до історії
                historyRepository.addToHistory(
                    Action(
                        action = ActionType.DELETE,
                        userId = userId,
                        itemId = itemId,
                        value = itemSnapshot.toObject(Item::class.java)
                    )
                )

                // Видалити елемент
                batch.delete(itemRef)

                // Видалити дозволи на елемент
                permissionRepository.deleteAllPermissions(itemId, type = Data.Type.ITEM)
            }

            // Занести до історії
            historyRepository.addToHistory(
                Action(
                    action = ActionType.DELETE,
                    userId = userId,
                    categoryId = categoryId,
                    value = getCategory(categoryId) as? Results.Success<Category>
                )
            )

            // Видалити саму категорію
            val categoryRef = collection.document(categoryId)
            batch.delete(categoryRef)

            // Видалити дозволи на категорію
            permissionRepository.deleteAllPermissions(categoryId, type = Data.Type.CATEGORY)

            // Застосувати зміни
            batch.commit().awaitCoroutine()

            return Results.Success(Unit)
        } catch (e: Exception) {
            return Results.Failure(e)
        }
    }


    override suspend fun getCategoriesCount(parentId: String): Results<Long> = safetyResultWrapper({
        collection
            .whereEqualTo("parentId", parentId)
            .count()
            .get(AggregateSource.SERVER)
            .await()
    }) {
        Results.Success(it.count)
    }

    override suspend fun getCategories(categoriesIds: List<String>): Results<List<Category>> =
        if (categoriesIds.isEmpty()) Results.Success(emptyList())
        else safetyResultWrapper({
            collection
                .whereIn("id", categoriesIds)
                .get()
                .await()
        }) {
            Results.Success(it.toObjects(Category::class.java))
        }
}
