package com.example.militaryaccountingapp.presenter.fragment.settings

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.example.militaryaccountingapp.R
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

object ThemeService {

    private const val KEY = "theme"

    enum class ThemeType(val displayName: String) {
        SYSTEM("System default"),
        MONET("Monet (Material 3 You)"),
        LIGHT("Light"),
        DARK("Dark");

        private val nightMode: Int
            get() = when (this) {
                SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                MONET -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                DARK -> AppCompatDelegate.MODE_NIGHT_YES
                LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            }

        private val styleId: Int
            get() = when (this) {
                MONET -> R.style.Theme_MilitaryAccountingApp_Monet
                else -> R.style.Theme_MilitaryAccountingApp
            }

        private fun setNightMode() {
            AppCompatDelegate.setDefaultNightMode(this.nightMode)
        }

        fun setTheme(activity: Activity) {
            this.setNightMode()
            activity.setTheme(this.styleId)
            if (this == MONET) {
                DynamicColors.applyToActivitiesIfAvailable(activity.application)
            }
        }
    }

    private val _theme: MutableStateFlow<ThemeType> = MutableStateFlow(ThemeType.MONET)
    val theme: StateFlow<ThemeType> = _theme.asStateFlow()

    @JvmStatic
    private fun reloadTheme(activity: Activity) {
        activity.recreate()
    }

    @JvmStatic
    fun saveAndSetTheme(
        activity: Activity,
        themeType: ThemeType,
    ) {
        saveTheme(activity.baseContext, themeType)
        _theme.value =
            themeType.also {
                it.setTheme(activity)
            }
        reloadTheme(activity)
    }

    @JvmStatic
    fun loadAndSetTheme(activity: Activity) {
        loadTheme(activity.baseContext).setTheme(activity)
    }

    @JvmStatic
    fun saveTheme(context: Context, themeType: ThemeType) = PreferenceManager
        .getDefaultSharedPreferences(context)
        .edit().run {
            putString(KEY, themeType.toString())
            apply()
        }

    @JvmStatic
    fun loadTheme(context: Context): ThemeType {
        val themeType = PreferenceManager
            .getDefaultSharedPreferences(context)
            .getString(KEY, ThemeType.MONET.toString())!!
        Timber.d("themeType: $themeType")
        return ThemeType.valueOf(themeType)
    }
}
