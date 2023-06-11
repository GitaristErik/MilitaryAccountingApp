package com.example.militaryaccountingapp.presenter.utils.ui

import android.util.Patterns
import com.example.militaryaccountingapp.domain.helper.Result
import timber.log.Timber

sealed interface AuthValidator<T> {

    fun validate(data: T): Result<T>


    object NameValidator : AuthValidator<String> {
        private const val MIN_NAME_LENGTH = 4

        override fun validate(data: String): Result<String> {
            val name = data.trim()

            return when {
                name.isEmpty() ->
                    Result.Failure(Exception("Name is empty"))

                name.length < MIN_NAME_LENGTH ->
                    Result.Failure(Exception("Use at least $MIN_NAME_LENGTH characters"))

                else -> Result.Success(name)
            }
        }
    }

    object EmailValidator : AuthValidator<String> {
        private const val MIN_EMAIL_LENGTH = 6

        override fun validate(data: String): Result<String> {
            val email = data.trim()

            return when {
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                    Result.Failure(Exception("Enter a valid email"))

                email.isEmpty() ->
                    Result.Failure(Exception("Email is empty"))

                email.length < MIN_EMAIL_LENGTH ->
                    Result.Failure(Exception("Use at least $MIN_EMAIL_LENGTH characters"))

                else -> Result.Success(email)
            }
        }

    }

    object PasswordValidator : AuthValidator<String> {

        private const val MIN_PASSWORD_LENGTH = 4

        var password: String = ""
            private set

        override fun validate(data: String): Result<String> {
            this.password = data.trim()

            return when {
                password.length < MIN_PASSWORD_LENGTH ->
                    Result.Failure(Exception("Use at least $MIN_PASSWORD_LENGTH characters"))

                password.isEmpty() ->
                    Result.Failure(Exception("Password is empty"))

                else -> Result.Success(password)
            }
        }
    }

    object RepasswordValidator : AuthValidator<String> {

        override fun validate(data: String): Result<String> {
            val repassword = data.trim()

            return when {
                repassword != PasswordValidator.password ->
                    Result.Failure(Exception("Passwords do not match"))

                else -> PasswordValidator.validate(repassword)
            }
        }
    }

    object LoginValidator : AuthValidator<String> {
        private const val MIN_LOGIN_LENGTH = 4

        override fun validate(data: String): Result<String> {
            val login = data.trim()

            return when {
                login.length < MIN_LOGIN_LENGTH ->
                    Result.Failure(Exception("Use at least $MIN_LOGIN_LENGTH characters"))

                login.isEmpty() ->
                    Result.Failure(Exception("Login is empty"))

                login.matches(Regex("[^A-Za-z0-9]")) ->
                    Result.Failure(Exception("Login contains special characters"))

                else -> Result.Success(login)
            }
        }
    }

    object RankValidator : AuthValidator<String> {
        private const val MAX_RANK_LENGTH = 100

        override fun validate(data: String): Result<String> {
            val rank = data.trim()

            return when {
                rank.length > MAX_RANK_LENGTH ->
                    Result.Failure(Exception("Use at least $MAX_RANK_LENGTH characters"))

                rank.matches(Regex("[^A-Za-z0-9]")) ->
                    Result.Failure(Exception("Rank contains special characters"))

                else -> Result.Success(rank)
            }
        }
    }

    object FullNameValidator : AuthValidator<String> {
        private const val MAX_FULL_NAME_LENGTH = 100

        override fun validate(data: String): Result<String> {
            val fullName = data.trim()

            return when {
                fullName.length > MAX_FULL_NAME_LENGTH ->
                    Result.Failure(Exception("Use at least $MAX_FULL_NAME_LENGTH characters"))

                fullName.matches(Regex("[A-Za-z0-9 ]+")) ->
                    Result.Failure(Exception("Full name contains special characters"))

                else -> Result.Success(fullName)
            }
        }
    }

    object PhoneValidator : AuthValidator<String> {
        private const val MAX_PHONE_LENGTH = 11

        override fun validate(data: String): Result<String> {
            val phone = data.trim()

            return when {
                phone.length > MAX_PHONE_LENGTH ->
                    Result.Failure(Exception("Use at least $MAX_PHONE_LENGTH characters"))

                // match do not contain special characters. Only '+', '-', '(', ')', ' ' and numbers
                phone.matches(Regex("^[()+\\-0-9\\s]+\$")) ->
                    Result.Failure(Exception("Phone contains special characters"))

                else -> Result.Success(phone)
            }
        }
    }

    object PhonesValidator {
        fun validate(phones: List<String>): List<Result<String>> {
            Timber.e("phones list: $phones")
            return phones.map { phone ->
                PhoneValidator.validate(phone)
            }
        }
    }
}