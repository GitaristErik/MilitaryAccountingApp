package com.example.militaryaccountingapp.domain.entity.extension

import com.example.militaryaccountingapp.domain.helper.Results
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// Task Extension
suspend fun <T> Task<T>.await(): Results<T> {
    if (isComplete) {
        val e = exception
        return if (e == null) {
            if (isCanceled) {
                Results.Canceled(CancellationException("Task $this was cancelled normally."))
            } else {
                Results.Success(result as T)
            }
        } else {
//            Result.Error(e)
            Results.Failure(e)
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
                    cont.resume(Results.Success(result as T))
                }
            } else {
                cont.resumeWithException(e)
            }
        }
    }
}