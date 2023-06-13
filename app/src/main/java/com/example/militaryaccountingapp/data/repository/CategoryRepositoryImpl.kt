package com.example.militaryaccountingapp.data.repository

import com.example.militaryaccountingapp.data.helper.ResultHelper.safetyResultWrapper
import com.example.militaryaccountingapp.domain.entity.data.Category
import com.example.militaryaccountingapp.domain.entity.extension.await
import com.example.militaryaccountingapp.domain.entity.user.UserPermission
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.CategoryRepository
import com.example.militaryaccountingapp.domain.repository.PermissionRepository
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlinx.coroutines.tasks.await as awaitCoroutine

class CategoryRepositoryImpl @Inject constructor(
    private val permissionRepository: PermissionRepository
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
                isCanShare = true,
                isCanShareForWrite = true,
                isCanWrite = true,
            )
            permissionRepository.createPermissionByParent(
                userPermission,
                parentCategory.id
            )
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
            isCanShare = true,
            isCanShareForWrite = true,
            isCanWrite = true,
        )
        permissionRepository.createPermission(userPermission)
        Results.Success(category)
    }


    override suspend fun getCategory(id: String): Results<Category> = safetyResultWrapper({
        collection.document(id)
            .get()
            .await()
    }) {
        when (val category = it.toObject(Category::class.java)) {
            null -> Results.Failure(ClassCastException("Error while casting category"))
            else -> Results.Success(category)
        }
    }


    override suspend fun updateCategory(
        id: String,
        query: Map<String, Any?>
    ): Results<Void?> = safetyResultWrapper({
        collection.document(id)
            .update(query)
            .await()
    }) { Results.Success(null) }


    override suspend fun deleteCategoryAndChildren(categoryId: String): Results<Unit> {
        try {
            val batch = firestoreInstance.batch()
            val itemsCollection = firestoreInstance.collection("items")

            // Отримати дочірні категорії вказаної категорії
            // Рекурсивно видалити дочірні категорії
            for (childCategorySnapshot in collection
                .whereEqualTo("parentId", categoryId)
                .get()
                .awaitCoroutine()
            ) {
                val childCategoryId = childCategorySnapshot.id
                val deleteChildResult = deleteCategoryAndChildren(childCategoryId)
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
                batch.delete(itemRef)
            }

            // Видалити саму категорію
            val categoryRef = collection.document(categoryId)
            batch.delete(categoryRef)

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
}
