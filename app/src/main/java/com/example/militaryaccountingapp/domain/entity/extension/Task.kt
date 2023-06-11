package com.example.militaryaccountingapp.domain.entity.extension

import com.example.militaryaccountingapp.domain.helper.Result
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// Task Extension
suspend fun <T> Task<T>.await(): Result<T> {
    if (isComplete) {
        val e = exception
        return if (e == null) {
            if (isCanceled) {
                Result.Canceled(CancellationException("Task $this was cancelled normally."))
            } else {
                Result.Success(result as T)
            }
        } else {
//            Result.Error(e)
            Result.Failure(e)
        }
    }

    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            val e = exception
            if (e == null) {
                @Suppress("UNCHECKED_CAST")
                if (isCanceled) {
                    cont.cancel()
                } else {
                    cont.resume(Result.Success(result as T))
                }
            } else {
                cont.resumeWithException(e)
            }
        }
    }
}