package com.example.militaryaccountingapp

import android.app.Application
import com.google.android.material.color.DynamicColors

open class App : Application() {
    override fun onCreate() {
        super.onCreate()

        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
