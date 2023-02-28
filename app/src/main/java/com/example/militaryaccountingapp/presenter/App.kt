package com.example.militaryaccountingapp.presenter

import android.app.Application
import com.example.militaryaccountingapp.BuildConfig
import com.google.android.material.color.DynamicColors
import timber.log.Timber
import timber.log.Timber.DebugTree

open class App : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Timber.plant(DebugTree())
        Timber.d("onCreate")
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
