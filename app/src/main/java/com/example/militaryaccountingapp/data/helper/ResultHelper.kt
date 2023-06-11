package com.example.militaryaccountingapp.data.helper

import com.example.militaryaccountingapp.domain.helper.Results
import timber.log.Timber

object ResultHelper {

    /**
     * Wrapper for Result object
     * @param results [Results] object
     * @param onSuccessAction action for [Results.Success] object
     * @return [Results] object
     * @see Results
     * @see Results.Success
     * @see Results.Failure
     * @see Results.Canceled
     * @see Results.Loading
     */
    @JvmStatic
    suspend fun <D, O> resultWrapper(
        results: Results<D>,
        onSuccessAction: suspend (data: D) -> Results<O>
    ): Results<O> = when (results) {
        is Results.Success -> {
            Timber.i(results.toString())
            onSuccessAction(results.data)
        }

        is Results.Failure -> {
            Timber.e(results.toString())
            Results.Failure(results.throwable, results.type)
        }

        is Results.Canceled -> {
            Timber.e(results.toString())
            Results.Canceled(results.throwable)
        }

        is Results.Loading -> {
            Timber.e(results.toString())
            Results.Loading()
        }
    }

    /**
     * Wrapper for [resultWrapper] with try-catch block
     * @param result [Results] object
     * @param onSuccessAction action for [Results.Success] object
     * @return [Results] object
     * @see resultWrapper
     * @see Results
     * @see Results.Success
     */
    @JvmStatic
    suspend fun <D, O> safetyResultWrapper(
        getResultsAction: suspend () -> Results<D>,
        onSuccessAction: suspend (data: D) -> Results<O>
    ): Results<O> = try {
        resultWrapper(
            results = getResultsAction(),
            onSuccessAction = onSuccessAction
        )
    } catch (throwable: Throwable) {
        Results.Failure(throwable)
    }

}