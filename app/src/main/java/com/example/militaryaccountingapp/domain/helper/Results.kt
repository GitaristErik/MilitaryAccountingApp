package com.example.militaryaccountingapp.domain.helper

sealed class Results<out T> {
    data class Loading<out T>(val oldData: T? = null) : Results<T>()
    data class Success<out T>(val data: T) : Results<T>()
    data class Failure<out T>(
        val throwable: Throwable,
        val type: FailureType = FailureType.UNKNOWN,
        val oldData: T? = null,
    ) : Results<T>()

//    data class Error(val exception: Exception) : Result<Nothing>() // Status Error an error message

    data class Canceled(val throwable: Throwable?) : Results<Nothing>() // Status Canceled
    // string method to display a result for debugging
    override fun toString(): String {
        return "Result." + when (this) {
            is Success<*> -> "Success [data=$data]"
//            is Error -> "Error[exception=$exception]"
            is Canceled -> "Canceled [exception=$throwable]"
            is Loading -> "Loading [oldData=$oldData]"
            is Failure -> "Failure [throwable=$throwable, type=$type, oldData=$oldData]"
        }
    }

    enum class FailureType {
        NETWORK,
        SERVER,
        EMPTY,
        CLIENT,
        UNKNOWN,
        FACEBOOK,
        GOOGLE,
    }

    companion object {
        fun <T> Results<T>.anyData() = when (this) {
            is Success -> this.data
            is Loading -> this.oldData
            is Failure -> this.oldData
//            is Error -> this.exception
            is Canceled -> this.throwable ?: Unit
        }
    }
}
