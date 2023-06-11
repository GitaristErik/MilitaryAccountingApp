package com.example.militaryaccountingapp.presenter.utils.ui

import android.util.Patterns
import com.example.militaryaccountingapp.domain.helper.Results

sealed interface AuthValidator<T> {

    fun validate(data: T): Results<T>


    object NameValidator : AuthValidator<String> {
        private const val MIN_NAME_LENGTH = 4

        override fun validate(data: String): Results<String> {
            val name = data.trim()

            return when {
                name.isEmpty() ->
                    Results.Failure(Exception("Name is empty"))

                name.length < MIN_NAME_LENGTH ->
                    Results.Failure(Exception("Use at least $MIN_NAME_LENGTH characters"))

                else -> Results.Success(name)
            }
        }
    }

    object EmailValidator : AuthValidator<String> {
        private const val MIN_EMAIL_LENGTH = 6

        override fun validate(data: String): Results<String> {
            val email = data.trim()

            return when {
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                    Results.Failure(Exception("Enter a valid email"))

                email.isEmpty() ->
                    Results.Failure(Exception("Email is empty"))

                email.length < MIN_EMAIL_LENGTH ->
                    Results.Failure(Exception("Use at least $MIN_EMAIL_LENGTH characters"))

                else -> Results.Success(email)
            }
        }

    }

    object PasswordValidator : AuthValidator<String> {

        private const val MIN_PASSWORD_LENGTH = 4

        var password: String = ""
            private set

        override fun validate(data: String): Results<String> {
            this.password = data.trim()

            return when {
                password.length < MIN_PASSWORD_LENGTH ->
                    Results.Failure(Exception("Use at least $MIN_PASSWORD_LENGTH characters"))

                password.isEmpty() ->
                    Results.Failure(Exception("Password is empty"))

                else -> Results.Success(password)
            }
        }
    }

    object RepasswordValidator : AuthValidator<String> {

        override fun validate(data: String): Results<String> {
            val repassword = data.trim()

            return when {
                repassword != PasswordValidator.password ->
                    Results.Failure(Exception("Passwords do not match"))

                else -> PasswordValidator.validate(repassword)
            }
        }
    }

    object LoginValidator : AuthValidator<String> {
        private const val MIN_LOGIN_LENGTH = 4

        override fun validate(data: String): Results<String> {
            val login = data.trim()

            return when {
                login.length < MIN_LOGIN_LENGTH ->
                    Results.Failure(Exception("Use at least $MIN_LOGIN_LENGTH characters"))

                login.isEmpty() ->
                    Results.Failure(Exception("Login is empty"))

                login.matches(Regex("[^A-Za-z0-9]")) ->
                    Results.Failure(Exception("Login contains special characters"))

                else -> Results.Success(login)
            }
        }
    }

    object RankValidator : AuthValidator<String> {
        private const val MAX_RANK_LENGTH = 100

        override fun validate(data: String): Results<String> {
            val rank = data.trim()

            return when {
                rank.length > MAX_RANK_LENGTH ->
                    Results.Failure(Exception("Use at least $MAX_RANK_LENGTH characters"))

                rank.matches(Regex("[^A-Za-z0-9]")) ->
                    Results.Failure(Exception("Rank contains special characters"))

                else -> Results.Success(rank)
            }
        }
    }

    object FullNameValidator : AuthValidator<String> {
        private const val MAX_FULL_NAME_LENGTH = 100

        override fun validate(data: String): Results<String> {
            val fullName = data.trim()

            return when {
                fullName.length > MAX_FULL_NAME_LENGTH ->
                    Results.Failure(Exception("Use at least $MAX_FULL_NAME_LENGTH characters"))

                fullName.matches(Regex("[^A-Za-z0-9]")) ->
                    Results.Failure(Exception("Full name contains special characters"))

                else -> Results.Success(fullName)
            }
        }
    }

    object PhoneValidator : AuthValidator<String> {
        private const val MAX_PHONE_LENGTH = 11

        override fun validate(data: String): Results<String> {
            val phone = data.trim()

            return when {
                phone.length > MAX_PHONE_LENGTH ->
                    Results.Failure(Exception("Use at least $MAX_PHONE_LENGTH characters"))

                // match do not contain special characters. Only '+', '-', '(', ')', ' ' and numbers
                phone.matches(Regex("[^+\\-()0-9 ]")) ->
                    Results.Failure(Exception("Phone contains special characters"))

                else -> Results.Success(phone)
            }
        }
    }

    object PhonesValidator {
        fun validate(phones: List<String>): List<Results<String>> {
            return phones.map { phone ->
                PhoneValidator.validate(phone)
            }
        }
    }
}