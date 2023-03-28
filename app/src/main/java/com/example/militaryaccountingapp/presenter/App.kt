package com.example.militaryaccountingapp.presenter

import android.app.Application
import com.example.militaryaccountingapp.BuildConfig
import timber.log.Timber
import timber.log.Timber.DebugTree

open class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(timberTree)
        Timber.d(" ")
    }

    private val timberTree: Timber.Tree
        get() = object : DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String =
                super.createStackElementTag(element)?.let { "$it::${element.methodName}" } ?: ""
        }
}
