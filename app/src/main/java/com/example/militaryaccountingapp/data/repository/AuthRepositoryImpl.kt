package com.example.militaryaccountingapp.data.repository

import com.example.militaryaccountingapp.data.helper.ResultHelper.resultWrapper
import com.example.militaryaccountingapp.data.helper.ResultHelper.safetyResultWrapper
import com.example.militaryaccountingapp.data.storage.Storage
import com.example.militaryaccountingapp.domain.entity.data.Category
import com.example.militaryaccountingapp.domain.entity.extension.await
import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.AuthRepository
import com.example.militaryaccountingapp.domain.repository.CategoryRepository
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val storage: Storage<String, User>,
    private val categoryRepository: CategoryRepository
) : AuthRepository {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun login(
        email: String,
        password: String
    ): Results<User> = safetyResultWrapper(
        { firebaseAuth.signInWithEmailAndPassword(email, password).await() }
    ) {
        it.user?.let { firebaseUser ->
            Timber.i("Login Successful!")
            return@safetyResultWrapper loadUserInStorage(firebaseUser.uid)
        }
        Results.Failure(RuntimeException("User not loaded"))
    }

    override suspend fun register(
        email: String,
        password: String,
        login: String,
        name: String,
        fullName: String,
        rank: String,
        phones: List<String>,
    ): Results<User> = safetyResultWrapper(
        { firebaseAuth.createUserWithEmailAndPassword(email, password).await() }
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
            resultWrapper(
                categoryRepository.createRootCategory(
                    Category(
                        id = firebaseUser.uid,
                        name = "all",
                        parentId = null,
                        userId = firebaseUser.uid
                    ),
                    firebaseUser.uid
                )
            ) { cat ->
                user.rootCategoryId = cat.id
                saveUserInStorage(user)
            }
        } ?: Results.Failure(
            RuntimeException("Sign in is successfully, but response model is empty")
        )
    }


    override suspend fun signInGoogle(
        idToken: String,
        accessToken: String?
    ): Results<User> = safetyResultWrapper({
        signInWithCredential(
            GoogleAuthProvider.getCredential(idToken, accessToken)
        )
    }) {
        Timber.d("signInWithCredential Google Success - " + it?.user?.uid)
        saveUserInStorageAtApi(it)
    }

    override suspend fun signInFacebook(
        token: String
    ): Results<User> = safetyResultWrapper({
        signInWithCredential(FacebookAuthProvider.getCredential(token))
    }) {
        Timber.i("signInWithCredential Facebook Success - " + it?.user?.uid)
        saveUserInStorageAtApi(it)
    }


    override suspend fun resetPassword(email: String): Results<Void?> = try {
        firebaseAuth.sendPasswordResetEmail(email).await()
    } catch (e: Exception) {
        Results.Failure(e)
    } finally {
        currentUser = null
    }

    private var currentUser: User? = null
    override suspend fun currentUser(): User? {
//        if (currentUser == null) {
            currentUser = firebaseAuth.currentUser?.let {
                when (val result = storage.load(it.uid)) {
                    is Results.Success -> {
                        result.data
                    }

                    is Results.Failure -> {
                        Timber.e(result.toString())
                        null
                    }

                    is Results.Canceled -> {
                        Timber.e(result.toString())
                        null
                    }

                    is Results.Loading -> {
                        Timber.d("result loading: ${result.oldData}")
                        result.oldData
                    }
                }
            }
//        }
        return currentUser
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
        currentUser = null
//        Firebase.auth.signOut()
    }

    override suspend fun editUserInfo(
        email: String,
        password: String?,
        login: String,
        name: String,
        fullName: String,
        rank: String,
        phones: List<String>
    ): Results<User> {
        val user = firebaseAuth.currentUser
        return if (user != null) {
            user.updateEmail(email)
            if (password != null) {
                user.updatePassword(password)
            }
            saveUserInStorage(
                User(
                    id = user.uid,
                    login = login,
                    email = user.email ?: email,
                    name = name,
                    fullName = fullName,
                    rank = rank,
                    phones = phones,
                    rootCategoryId = currentUser!!.rootCategoryId,
                    imageUrl = currentUser!!.imageUrl,
                    createdAt = currentUser!!.createdAt,
                    updatedAt = System.currentTimeMillis(),
                    deletedAt = currentUser!!.deletedAt,
                )
            )
        } else {
            Results.Failure(RuntimeException("User not found"))
        }
    }


    private suspend fun signInWithCredential(
        authCredential: AuthCredential
    ): Results<AuthResult?> = firebaseAuth
        .signInWithCredential(authCredential).await()


    private suspend fun saveUserInStorageAtApi(
        result: AuthResult?
    ): Results<User> {
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

    private suspend fun loadUserInStorage(userId: String): Results<User> = resultWrapper(
        storage.load(userId)
    ) {
        Timber.d(it.toString())
        currentUser = it
        Results.Success(it)
    }

    private suspend fun saveUserInStorage(user: User): Results<User> = safetyResultWrapper({
        storage.save(user.id, user)
    }) {
        Timber.i("User saved!")
        currentUser = user
        Results.Success(user)
    }
}