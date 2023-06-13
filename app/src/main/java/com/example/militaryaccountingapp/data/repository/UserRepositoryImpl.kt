package com.example.militaryaccountingapp.data.repository

import com.example.militaryaccountingapp.data.helper.ResultHelper.resultWrapper
import com.example.militaryaccountingapp.data.helper.ResultHelper.safetyResultWrapper
import com.example.militaryaccountingapp.domain.entity.extension.await
import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

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

    override suspend fun getUsers(ids: List<String>): Results<List<User>> = safetyResultWrapper({
        collection.whereIn("id", ids)
            .get().await()
    } ) {
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

}