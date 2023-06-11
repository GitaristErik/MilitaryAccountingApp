package com.example.militaryaccountingapp.data.repository

import com.example.militaryaccountingapp.data.helper.ResultHelper.resultWrapper
import com.example.militaryaccountingapp.data.helper.ResultHelper.safetyResultWrapper
import com.example.militaryaccountingapp.data.storage.Storage
import com.example.militaryaccountingapp.domain.entity.extension.await
import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Result
import com.example.militaryaccountingapp.domain.repository.AuthRepository
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val storage: Storage<String, User>
) : AuthRepository {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun login(
        email: String,
        password: String
    ): Result<User> = safetyResultWrapper(
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
    ) {
        it.user?.let { firebaseUser ->
            Timber.i("Login Successful!")
            return@safetyResultWrapper loadUserInStorage(firebaseUser.uid)
        }
        Result.Failure(RuntimeException("User not loaded"))
    }

    override suspend fun register(
        email: String,
        password: String,
        login: String,
        name: String,
        fullName: String,
        rank: String,
        phones: List<String>,
    ): Result<User> = safetyResultWrapper(
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
    ) {
        it.user?.let { firebaseUser ->
            val user = User(
                id = firebaseUser.uid,
                login = login,
                email = email,
                name = name,
                fullName = fullName,
                rank = rank,
//            imageUrl= null,
                phones = phones,
                createdAt = firebaseUser.metadata?.creationTimestamp ?: System.currentTimeMillis(),
                updatedAt = firebaseUser.metadata?.lastSignInTimestamp
                    ?: System.currentTimeMillis(),
                deletedAt = null
            )
            Timber.i("Registration Successful!")
            return@safetyResultWrapper saveUserInStorage(user)
        }
        Result.Failure(RuntimeException("Sign in is successfully, but response model is empty"))
    }


    override suspend fun signInGoogle(
        idToken: String,
        accessToken: String?
    ): Result<User> = safetyResultWrapper(
        signInWithCredential(
            GoogleAuthProvider.getCredential(idToken, accessToken)
        )
    ) {
        Timber.d("signInWithCredential Google Success - " + it?.user?.uid)
        saveUserInStorageAtApi(it)
    }

    override suspend fun signInFacebook(
        token: String
    ): Result<User> = safetyResultWrapper(
        signInWithCredential(FacebookAuthProvider.getCredential(token))
    ) {
        Timber.i("signInWithCredential Facebook Success - " + it?.user?.uid)
        saveUserInStorageAtApi(it)
    }


    override suspend fun resetPassword(email: String): Result<Void?> = try {
        firebaseAuth.sendPasswordResetEmail(email).await()
    } catch (e: Exception) {
        Result.Failure(e)
    }

    override suspend fun currentUser(): User? {
        val currentUser = firebaseAuth.currentUser
        return if (currentUser == null) null
        else when (val result = storage.load(currentUser.uid)) {
            is Result.Success -> {
                result.data
            }

            is Result.Failure -> {
                Timber.e(result.throwable)
                null
            }

            is Result.Canceled -> {
                Timber.e(result.throwable)
                null
            }

            is Result.Loading -> {
                Timber.d("result loading: ${result.oldData}")
                result.oldData
            }
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
//        Firebase.auth.signOut()
    }


    private suspend fun signInWithCredential(
        authCredential: AuthCredential
    ): Result<AuthResult?> = firebaseAuth
        .signInWithCredential(authCredential).await()


    private suspend fun saveUserInStorageAtApi(
        result: AuthResult?
    ): Result<User> {
        result?.user?.providerData?.get(1)?.let {
            it.displayName?.let { name ->
                val email: String = if (it.email.isNullOrEmpty()) {
                    Timber.e("user email is empty")
                    ""
                } else {
                    it.email.toString()
                }

                val pict: String = if (it.photoUrl == null) "" else it.photoUrl.toString()

                val generateRandomLogin: () -> String = {
                    val login = (email.substringBefore("@") + result.user!!.uid.takeLast(4))
                        .replace("[^A-Za-z0-9]".toRegex(), "")
                    Timber.d("randomLogin: $login")
                    login
                }

                return saveUserInStorage(
                    User(
                        id = result.user!!.uid,
                        name = name,
                        login = generateRandomLogin(),
                        rank = "",
                        email = email,
                        fullName = name,
                        imageUrl = pict,
                        phones = listOfNotNull(it.phoneNumber),
                        createdAt = result.user!!.metadata?.creationTimestamp
                            ?: System.currentTimeMillis(),
                        updatedAt = result.user!!.metadata?.lastSignInTimestamp
                            ?: System.currentTimeMillis(),
                        deletedAt = null
                    )
                )
//                throw NullPointerException("user email is empty")
            }
            Timber.e("user name is empty")
            throw NullPointerException("user name is empty")
        }
        Timber.e("user model is empty")
        throw NullPointerException("user model is empty")
    }

    private suspend fun loadUserInStorage(userId: String): Result<User> = resultWrapper(
        storage.load(userId)
    ) {
        Timber.d(it.toString())
        Result.Success(it)
    }

    private suspend fun saveUserInStorage(user: User): Result<User> = resultWrapper(
        storage.save(user.id, user)
    ) {
        Timber.i("User saved!")
        Result.Success(user)
    }
}