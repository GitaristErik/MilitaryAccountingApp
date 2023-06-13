package com.example.militaryaccountingapp.data.repository

import com.example.militaryaccountingapp.data.helper.ResultHelper.resultWrapper
import com.example.militaryaccountingapp.data.helper.ResultHelper.safetyResultWrapper
import com.example.militaryaccountingapp.domain.entity.data.Action
import com.example.militaryaccountingapp.domain.entity.data.ActionType
import com.example.militaryaccountingapp.domain.entity.data.Category
import com.example.militaryaccountingapp.domain.entity.data.Data
import com.example.militaryaccountingapp.domain.entity.data.Item
import com.example.militaryaccountingapp.domain.entity.extension.await
import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.entity.user.UserPermission
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.CategoryRepository
import com.example.militaryaccountingapp.domain.repository.DataRepository
import com.example.militaryaccountingapp.domain.repository.DataRepository.SortFilter
import com.example.militaryaccountingapp.domain.repository.ItemRepository
import com.example.militaryaccountingapp.domain.repository.UserRepository
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class DataRepositoryImpl @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val itemRepository: ItemRepository,
    private val userRepository: UserRepository
//    val currentUserUseCase: CurrentUserUseCase
) : DataRepository {

    companion object {
        private const val CATEGORIES = "categories"
        private const val PERMISSIONS = "permissions"
        private const val ITEMS = "items"
        private const val USERS = "users"
    }

    private val firestoreInstance: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val categoryCollection = firestoreInstance.collection(CATEGORIES)
    private val permissionCollection = firestoreInstance.collection(PERMISSIONS)
    private val itemsCollection = firestoreInstance.collection(ITEMS)
    private val usersCollection = firestoreInstance.collection(USERS)


    suspend fun getCategories(
        parentId: String?,
        userId: String
    ): Results<Map<Category, List<UserPermission>>> {
        val categoriesRes = resultWrapper(
            categoryCollection
                .whereEqualTo("parentId", parentId)
                .get().await()
        ) {
            val categories = it.toObjects(Category::class.java)
            Results.Success(categories)
        }

        if (categoriesRes !is Results.Success) return Results.Failure(
            Exception("Error while getting categories")
        )

        val categoriesIds = categoriesRes.data.map { it.id }

        return resultWrapper(
            permissionCollection
                .whereEqualTo("userId", userId)
                .whereIn("categoryId", categoriesIds)
                .get().await()
        ) { snapshot ->
            val groupedCategoriesWithPermissions = snapshot
                .toObjects(UserPermission::class.java)
                .groupBy { it.categoryId }
                .mapKeys { categoriesRes.data.first { c -> c.id == it.key } }
            Results.Success(groupedCategoriesWithPermissions)
        }
    }


    override suspend fun getDataByParent(
        clazz: Class<out Data>,
        parentId: String?,
        userId: String,
        sortType: SortFilter,
        isAscending: Boolean
    ): Results<Map<Data, List<UserPermission>>> {

        val collection = when (clazz) {
            Category::class.java -> categoryCollection
            Item::class.java -> itemsCollection
            else -> throw Exception("Unknown type")
        }

        val itemsRes = resultWrapper(
            collection
                .whereEqualTo("parentId", parentId)
                .get().await()
        ) {
            Results.Success(it.toObjects(clazz))
        }

        if (itemsRes !is Results.Success) return Results.Failure(
            Exception("Error while getting elements")
        )

        val itemsIds = itemsRes.data.map { it.id }.distinct()

        return safetyResultWrapper({
            permissionCollection
                .whereEqualTo("userId", userId)
                .let {
                    if (clazz == Category::class.java) {
                        it.whereIn("categoryId", itemsIds)
                    } else {
                        it.whereIn("itemId", itemsIds)
                    }
                }
                .get().await()
        }) { snapshot ->
            val groupedItemsWithPermissions = snapshot
                .toObjects(UserPermission::class.java)
                .groupBy {
                    if (clazz == Category::class.java) {
                        it.categoryId
                    } else {
                        it.itemId
                    }
                }.mapKeys {
                    itemsRes.data.first { c -> c.id == it.key }
                }
            Results.Success(groupedItemsWithPermissions)
        }
    }


    /**
     * @return Pair<List<Data>, List<String>> - list of items and list of users ids
     */
    override suspend fun getAllDataByUserId(
        userId: String
    ): Results<Pair<List<Data>, List<String>>> {
        val categoriesAndItemsRes = resultWrapper(
            permissionCollection
                .whereEqualTo("userId", userId)
                .get().await()
        ) { snapshot ->
            Results.Success(snapshot
                .toObjects(UserPermission::class.java).let {
                    it.mapNotNull(UserPermission::categoryId).distinct() to
                            it.mapNotNull(UserPermission::itemId).distinct()
                }
            )
        }
        if (categoriesAndItemsRes !is Results.Success) return Results.Failure(
            Exception("Error while getting ids for categories and items")
        )

        val (categoriesIds, itemsIds) = categoriesAndItemsRes.data

        val categoriesRes = resultWrapper(
            categoryCollection
                .whereIn("id", categoriesIds)
                .get().await()
        ) { Results.Success(it.toObjects(Category::class.java)) }
        if (categoriesRes !is Results.Success) return Results.Failure(
            Exception("Error while getting categories")
        )


        val itemsRes = resultWrapper(
            itemsCollection
                .whereIn("id", itemsIds)
                .get().await()
        ) { Results.Success(it.toObjects(Data::class.java)) }
        if (itemsRes !is Results.Success) return Results.Failure(
            Exception("Error while getting items")
        )

        val usersIdsRes = resultWrapper(
            permissionCollection.where(
                Filter.or(
                    Filter.inArray("categoryId", categoriesIds),
                    Filter.inArray("itemId", itemsIds)
                )
            ).get().await()
        ) {
            Results.Success(
                it.toObjects(UserPermission::class.java)
                    .map(UserPermission::userId).distinct()
            )
        }
        if (usersIdsRes !is Results.Success) return Results.Failure(
            Exception("Error while getting users ids")
        )

        return Results.Success(categoriesRes.data + itemsRes.data to usersIdsRes.data)
    }

    override suspend fun getHistory(
        limit: Int,
        page: Int,
        filters: Set<ActionType>,
    ): List<Triple<Action, Data?, User>> {
        return emptyList()
    }


}