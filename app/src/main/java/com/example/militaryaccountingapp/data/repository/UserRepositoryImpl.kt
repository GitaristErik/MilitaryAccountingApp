package com.example.militaryaccountingapp.data.repository

import com.example.militaryaccountingapp.data.helper.ResultHelper.resultWrapper
import com.example.militaryaccountingapp.data.helper.ResultHelper.safetyResultWrapper
import com.example.militaryaccountingapp.domain.entity.extension.await
import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlinx.coroutines.tasks.await as awaitCoroutine

class UserRepositoryImpl @Inject constructor() : UserRepository {

    companion object {
        private const val USERS = "users"
    }

    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val collection = firestoreInstance.collection(USERS)

    override suspend fun getUser(id: String): Results<User> = resultWrapper(
        collection.document(id).get().await()
    ) {
        it.toObject(User::class.java)?.let { user ->
            Results.Success(user)
        } ?: Results.Failure(Exception("Error while cast user"))
    }

    override suspend fun getUsers(ids: List<String>): Results<List<User>> = if (ids.isEmpty())
        Results.Success(emptyList()) else safetyResultWrapper({
        collection
            .whereIn("id", ids)
            .get().await()
    }) {
        Results.Success(it.toObjects(User::class.java))
    }

    override suspend fun getUsersAvatars(
        usersIds: List<String>
    ): Map<String, String> = (resultWrapper(
        getUsers(usersIds)
    ) {
        Results.Success(
            it.associate { user ->
                user.id to (user.imageUrl ?: "")
            }
        )
    } as? Results.Success)?.data ?: emptyMap()

    /*

        override suspend fun searchUsers(
            cleanText: String
        ): Results<List<User>> = safetyResultWrapper({
            collection.whereGreaterThanOrEqualTo("name", cleanText)
                .whereLessThanOrEqualTo("name", cleanText + "\uf8ff")
                .whereGreaterThanOrEqualTo("email", cleanText)
                .whereLessThanOrEqualTo("email", cleanText + "\uf8ff")
                .whereGreaterThanOrEqualTo("fullName", cleanText)
                .whereLessThanOrEqualTo("fullName", cleanText + "\uf8ff")
                .whereArrayContainsAny("phones", listOf(cleanText))
                .get().await()
        }) { snapshot ->
            val results = mutableListOf<User>()
            for (document in snapshot.documents) {
                val user = document.toObject(User::class.java)
                user?.let { results.add(it) }
            }
            Results.Success(results)
        }    override suspend fun searchUsers(
            cleanText: String
        ): Results<List<User>> = safetyResultWrapper({
            collection.whereGreaterThanOrEqualTo("name", cleanText)
                .whereLessThanOrEqualTo("name", cleanText + "\uf8ff")
                .whereGreaterThanOrEqualTo("email", cleanText)
                .whereLessThanOrEqualTo("email", cleanText + "\uf8ff")
                .whereGreaterThanOrEqualTo("fullName", cleanText)
                .whereLessThanOrEqualTo("fullName", cleanText + "\uf8ff")
                .whereArrayContainsAny("phones", listOf(cleanText))
                .get().await()
        }) { snapshot ->
            val results = mutableListOf<User>()
            for (document in snapshot.documents) {
                val user = document.toObject(User::class.java)
                user?.let { results.add(it) }
            }
            Results.Success(results)
        }
    */


    override suspend fun searchUsers(cleanText: String): Results<List<User>> {
        val query1 = collection
            .orderBy("name")
            .startAt(cleanText)
            .endAt(cleanText + "\uf8ff")
//            .whereGreaterThanOrEqualTo("name", cleanText)
//            .whereLessThanOrEqualTo("name", cleanText)

        val query2 = collection
            .orderBy("email")
            .startAt(cleanText)
            .endAt(cleanText + "\uf8ff")
//            .whereGreaterThanOrEqualTo("email", cleanText)
//            .whereLessThanOrEqualTo("email", cleanText)

        val snapshot1 = query1.get().awaitCoroutine()
        val snapshot2 = query2.get().awaitCoroutine()
        val results = mutableListOf<User>()

        for (document in snapshot1.documents) {
            val user = document.toObject(User::class.java)
            user?.let { results.add(it) }
        }

        for (document in snapshot2.documents) {
            val user = document.toObject(User::class.java)
            if (!results.contains(user)) {
                user?.let { results.add(it) }
            }
        }

        return Results.Success(results)
    }

    override suspend fun updateCurrentUserInfo(
        id: String,
        mapOf: Map<String, List<Any>>
    ): Results<Unit> = safetyResultWrapper({
        collection
            .document(id)
            .update(mapOf)
            .await()
    }) {
        Results.Success(Unit)
    }
}