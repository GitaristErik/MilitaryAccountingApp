package com.example.militaryaccountingapp.data.storage

/*

class AvatarStorage @Inject constructor() : Storage<String, Uri> {

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

    override suspend fun load(key: String): Result<User> = safetyResultWrapper({
        collection.document(key).get().await()
    }) {
        when (val user = it.toObject(User::class.java)) {
            null -> Result.Failure(ClassCastException("Error while casting user"))
            else -> Result.Success(user)
        }
    }
}*/
