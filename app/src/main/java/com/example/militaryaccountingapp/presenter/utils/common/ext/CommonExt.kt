package com.example.militaryaccountingapp.presenter.utils.common.ext

import com.example.militaryaccountingapp.BuildConfig
import timber.log.Timber

fun <T> debugTry(block: () -> T?): T? = try {
    block()
} catch (e: Exception) {
    if (BuildConfig.DEBUG) Timber.e(e)
    null
}

suspend fun <T> debugTrySuspend(block: suspend () -> T?): T? = try {
    block()
} catch (e: Exception) {
    if (BuildConfig.DEBUG) Timber.e(e)
    null
}