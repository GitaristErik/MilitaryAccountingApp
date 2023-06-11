package com.example.militaryaccountingapp.data.helper

import com.example.militaryaccountingapp.domain.helper.Result
import timber.log.Timber

object ResultHelper {

    /**
     * Wrapper for Result object
     * @param result [Result] object
     * @param onSuccessAction action for [Result.Success] object
     * @return [Result] object
     * @see Result
     * @see Result.Success
     * @see Result.Failure
     * @see Result.Canceled
     * @see Result.Loading
     */
    @JvmStatic
    suspend fun <D, O> resultWrapper(
        result: Result<D>,
        onSuccessAction: suspend (data: D) -> Result<O>
    ): Result<O> = when (result) {
        is Result.Success -> {
            Timber.i("$result")
            onSuccessAction(result.data)
        }

        is Result.Failure -> {
            Timber.e("${result.throwable}")
            Result.Failure(result.throwable, result.type)
        }

        is Result.Canceled -> {
            Timber.e("${result.throwable}")
            Result.Canceled(result.throwable)
        }

        is Result.Loading -> {
            Timber.e("${result.oldData}")
            Result.Loading()
        }
    }

    /**
     * Wrapper for [resultWrapper] with try-catch block
     * @param result [Result] object
     * @param onSuccessAction action for [Result.Success] object
     * @return [Result] object
     * @see resultWrapper
     * @see Result
     * @see Result.Success
     */
    @JvmStatic
    suspend fun <D, O> safetyResultWrapper(
        getResultAction: suspend () -> Result<D>,
        onSuccessAction: suspend (data: D) -> Result<O>
    ): Result<O> = try {
        resultWrapper(
            result = getResultAction(),
            onSuccessAction = onSuccessAction
        )
    } catch (throwable: Throwable) {
        Result.Failure(throwable)
    }

}