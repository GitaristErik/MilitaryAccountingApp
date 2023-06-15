package com.example.militaryaccountingapp.data.repository

import com.example.militaryaccountingapp.data.helper.ResultHelper.safetyResultWrapper
import com.example.militaryaccountingapp.domain.entity.data.Action
import com.example.militaryaccountingapp.domain.entity.data.ActionType
import com.example.militaryaccountingapp.domain.entity.data.Category
import com.example.militaryaccountingapp.domain.entity.data.Item
import com.example.militaryaccountingapp.domain.entity.extension.await
import com.example.militaryaccountingapp.domain.entity.user.UserPermission
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.CategoryRepository
import com.example.militaryaccountingapp.domain.repository.HistoryRepository
import com.example.militaryaccountingapp.domain.repository.PermissionRepository
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
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
        userId: String?
    ): Results<Category> = safetyResultWrapper({
        category.userId = userId ?: return@safetyResultWrapper Results.Failure(
            NullPointerException("User id is null")
        );
        getCategory(category.parentId!!)
    }) { parentCategory ->
        category.allParentIds = parentCategory.allParentIds + parentCategory.id
        category.allParentNames = parentCategory.allParentNames + parentCategory.name
        val ref = collection.document()
        category.id = ref.id

        // TODO load photo to storage
        category.imagesUrls = emptyList()

        safetyResultWrapper({
            ref.set(category).await()
        }) {
            val userPermission = UserPermission(
                userId = category.userId!!,
                categoryId = category.id,
                canShare = true,
                canShareForWrite = true,
                canWrite = true,
            )
            permissionRepository.createPermissionByParent(
                userPermission,
                parentCategory.id
            )

            val action = Action(
                action = ActionType.CREATE,
                userId = category.userId!!,
                categoryId = category.id,
            )
            historyRepository.addToHistory(action)

            Results.Success(category)
        }
    }

    override suspend fun createRootCategory(
        category: Category,
        userId: String?
    ): Results<Category> = safetyResultWrapper({
        category.userId = userId ?: return@safetyResultWrapper Results.Failure(
            NullPointerException("User id is null")
        );
        val ref = collection.document()
        category.id = ref.id

        // TODO load photo to storage
        category.imagesUrls = emptyList()

        ref.set(category).await()
    }) {

        val userPermission = UserPermission(
            userId = category.userId!!,
            categoryId = category.id,
            canShare = true,
            canShareForWrite = true,
            canWrite = true,
        )
        permissionRepository.createPermission(userPermission)

        val action = Action(
            action = ActionType.CREATE,
            userId = category.userId!!,
            categoryId = category.id,
        )
        historyRepository.addToHistory(action)
        Results.Success(category)
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

    override suspend fun changeRules(
        elementId: String,
        userId: String,
        canRead: Boolean,
        canEdit: Boolean,
        canShareRead: Boolean,
        canShareEdit: Boolean
    ) {
        updatePermissionForAllParents(
            elementId,
            UserPermission(
                userId = userId,
                itemId = null,
                categoryId = elementId,
                canWrite = canEdit,
                canShare = canShareRead,
                canShareForWrite = canShareEdit
            ),
            canRead
        )
    }

    private suspend fun updatePermissionForAllParents(
        categoryId: String,
        permission: UserPermission,
        canRead: Boolean
    ): Results<Unit> {
        try {
            val batch = firestoreInstance.batch()

            // Отримати дочірні категорії вказаної категорії
            // Рекурсивно викликати цю функцію для кожної дочірньої категорії
            for (childCategorySnapshot in collection
                .whereEqualTo("parentId", categoryId)
                .get()
                .awaitCoroutine()
            ) {
                val childCategoryId = childCategorySnapshot.id
                val deleteChildResult =
                    updatePermissionForAllParents(childCategoryId, permission, canRead)
                if (deleteChildResult is Results.Failure) {
                    return deleteChildResult // Повертаємо помилку, якщо виникла під час видалення дочірньої категорії
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
                val permissionsQuery = permissionsCollection
                    .whereEqualTo("itemId", itemId)
                    .whereEqualTo("userId", permission.userId)

                if (canRead) {
                    if (permissionsQuery.get().awaitCoroutine().isEmpty) {
                        val permissionRef = permissionsCollection.document()
                        batch.set(
                            permissionRef, UserPermission(
                                itemId = itemId,
                                userId = permission.userId,
                                canShare = permission.canShare,
                                canShareForWrite = permission.canShareForWrite,
                                canWrite = permission.canWrite,
                            )
                        )
                        // Додати до історії
                        val action = Action(
                            action = ActionType.SHARE,
                            userId = permission.userId,
                            itemId = itemId,
                        )
                        historyRepository.addToHistory(action)
                    } else {
                        for (permissionSnapshot in permissionsQuery.get().awaitCoroutine()) {
                            val permissionId = permissionSnapshot.id
                            val permissionRef = permissionsCollection.document(permissionId)
                            batch.update(
                                permissionRef, mapOf(
                                    "canShare" to permission.canShare,
                                    "canShareForWrite" to permission.canShareForWrite,
                                    "canWrite" to permission.canWrite,
                                )
                            )
                        }
                    }
                } else {
                    for (permissionSnapshot in permissionsQuery.get().awaitCoroutine()) {
                        val permissionId = permissionSnapshot.id
                        val permissionRef = permissionsCollection.document(permissionId)
                        batch.delete(permissionRef)
                    }
                    // Додати до історії
                    val action = Action(
                        action = ActionType.UNSHARE,
                        userId = permission.userId,
                        itemId = itemId,
                    )
                }
            }

            // Обновити доступ до категорії
            val categoryRef = collection.document(categoryId)
            val categoryPermissionsQuery = permissionsCollection
                .whereEqualTo("categoryId", categoryId)
                .whereEqualTo("userId", permission.userId)

            if (canRead) {
                if (categoryPermissionsQuery.get().awaitCoroutine().isEmpty) {
                    val permissionRef = permissionsCollection.document()
                    batch.set(
                        permissionRef, UserPermission(
                            categoryId = categoryId,
                            userId = permission.userId,
                            canShare = permission.canShare,
                            canShareForWrite = permission.canShareForWrite,
                            canWrite = permission.canWrite,
                        )
                    )
                    // Додати до історії
                    historyRepository.addToHistory(
                        Action(
                            action = ActionType.SHARE,
                            userId = permission.userId,
                            categoryId = categoryId,
                        )
                    )
                } else {
                    for (permissionSnapshot in categoryPermissionsQuery.get().awaitCoroutine()) {
                        val permissionId = permissionSnapshot.id
                        val permissionRef = permissionsCollection.document(permissionId)
                        batch.update(
                            permissionRef, mapOf(
                                "canShare" to permission.canShare,
                                "canShareForWrite" to permission.canShareForWrite,
                                "canWrite" to permission.canWrite,
                            )
                        )
                    }
                }
            } else {
                for (permissionSnapshot in categoryPermissionsQuery.get().awaitCoroutine()) {
                    val permissionId = permissionSnapshot.id
                    val permissionRef = permissionsCollection.document(permissionId)
                    batch.delete(permissionRef)
                }
                // Додати до історії
                historyRepository.addToHistory(
                    Action(
                        action = ActionType.UNSHARE,
                        userId = permission.userId,
                        categoryId = categoryId,
                    )
                )
            }

            // Застосувати зміни
            batch.commit().awaitCoroutine()

            return Results.Success(Unit)
        } catch (e: Exception) {
            return Results.Failure(e)
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
    private val permissionsCollection = firestoreInstance.collection("permissions")

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
                val permissionsQuery = permissionsCollection
                    .whereEqualTo("itemId", itemId)

                for (permissionSnapshot in permissionsQuery.get().awaitCoroutine()) {
                    val permissionId = permissionSnapshot.id
                    val permissionRef = permissionsCollection.document(permissionId)
                    batch.delete(permissionRef)
                }
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
            val permissionsQuery = permissionsCollection
                .whereEqualTo("categoryId", categoryId)

            for (permissionSnapshot in permissionsQuery.get().awaitCoroutine()) {
                val permissionId = permissionSnapshot.id
                val permissionRef = permissionsCollection.document(permissionId)
                batch.delete(permissionRef)
            }

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
        safetyResultWrapper({
            collection
                .whereIn("id", categoriesIds)
                .get()
                .await()
        }) {
            Results.Success(it.toObjects(Category::class.java))
        }
}
