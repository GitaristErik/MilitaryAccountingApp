package com.example.militaryaccountingapp.data.storage

import com.example.militaryaccountingapp.data.helper.ResultHelper.safetyResultWrapper
import com.example.militaryaccountingapp.domain.entity.extension.await
import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Results
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class AuthStorage @Inject constructor() : Storage<String, User> {

    companion object {
        private const val USER_COLLECTION_NAME = "users"
    }

    private val firestoreInstance: FirebaseFirestore = FirebaseFirestore.getInstance()

    val collection = firestoreInstance.collection(USER_COLLECTION_NAME)

    override suspend fun save(key: String, data: User): Results<Void?> {
        return try {
            collection.document(key).set(data).await()
        } catch (throwable: Throwable) {
            Results.Failure(throwable)
        }
    }

    override suspend fun load(key: String): Results<User> = safetyResultWrapper({
        collection.document(key).get().await()
    }) {
        when (val user = it.toObject(User::class.java)) {
            null -> Results.Failure(ClassCastException("Error while casting user"))
            else -> Results.Success(user)
        }
    }
}