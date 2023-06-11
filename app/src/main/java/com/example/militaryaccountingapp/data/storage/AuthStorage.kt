package com.example.militaryaccountingapp.data.storage

import com.example.militaryaccountingapp.data.helper.ResultHelper.safetyResultWrapper
import com.example.militaryaccountingapp.domain.entity.extension.await
import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Result
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class AuthStorage @Inject constructor() : Storage<String, User> {

    companion object {
        private const val USER_COLLECTION_NAME = "users"
    }

    private val firestoreInstance: FirebaseFirestore = FirebaseFirestore.getInstance()

    val collection = firestoreInstance.collection(USER_COLLECTION_NAME)

    override suspend fun save(key: String, data: User): Result<Void?> {
        return try {
            collection.document(key).set(data).await()
        } catch (throwable: Throwable) {
            Result.Failure(throwable)
        }
    }

    override suspend fun load(key: String): Result<User> = safetyResultWrapper(
        collection.document(key).get().await()
    ) {
        val user = it.toObject(User::class.java)!!
        Result.Success(user)
    }
}