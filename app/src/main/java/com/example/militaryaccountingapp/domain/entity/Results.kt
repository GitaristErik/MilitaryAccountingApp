package com.example.militaryaccountingapp.domain.entity

sealed class Results<out T> {
    data class Loading<out T>(val oldData: T? = null) : Results<T>()
    data class Success<out T>(val data: T) : Results<T>()
    data class Failure<out T>(
        val throwable: Throwable,
        val type: FailureType = FailureType.UNKNOWN,
        val oldData: T? = null,
    ) : Results<T>()

    enum class FailureType {
        NETWORK,
        SERVER,
        EMPTY,
        CLIENT,
        UNKNOWN,
    }

    companion object {
        fun <T> Results<T>.anyData() = when (this) {
            is Loading -> this.oldData
            is Failure -> this.oldData
            is Success -> this.data
        }
    }
}
