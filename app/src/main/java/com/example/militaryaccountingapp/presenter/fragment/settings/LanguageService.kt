package com.example.militaryaccountingapp.presenter.fragment.settings

import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.preference.PreferenceManager
import com.example.militaryaccountingapp.R
import org.xmlpull.v1.XmlPullParser
import java.util.Locale

class LanguageService(private val resources: Resources) {

    /*    @JvmStatic
    var resources: Resources? = null
        set(value) {
            field = value
            if (field != null) {
                languageTags = parseAppLocales(field!!)
                languageNames = getLanguageNames(languageTags)
            } else {
                languageTags = emptyList()
                languageNames = emptyList()
            }
        }*/

    var languageTags: List<String> = emptyList<String>()
        get() {
            if (field.isEmpty()) {
                field = parseAppLocales(resources)
            }
            return field
        }
        private set

    var languageNames: List<String> = emptyList<String>()
        get() {
            if (field.isEmpty()) {
                field = getLanguageNames(languageTags)
            }
            return field
        }
        private set

//    val defaultLanguageTag: String get() = Locale.getDefault().toLanguageTag()
    val defaultLanguage: Locale get() = resources.configuration.locales[0]


    private fun getLanguageNames(languageTags: List<String>): List<String> = languageTags.map {
        Locale.forLanguageTag(it).displayName
    }

    /*    private fun getLanguagesTags(config: Configuration): List<String> = config
            .locales
            .toLanguageTags()
            .split(",".toRegex())
            .dropLastWhile { it.isEmpty() }*/

    private fun parseAppLocales(resources: Resources): List<String> {
        val res = mutableListOf<String>()
        val parser = resources.getXml(R.xml.locales_config)
        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG && parser.name == "locale") {
                val locale = Locale(parser.getAttributeValue(0))
                res.add(locale.toString())
            }
            parser.next()
        }
        return res
    }

    companion object {
        private const val KEY = "language"

        /**
         * @param context
         * @param languageTag
         * Save language tag to shared preferences and set language
         */
        @JvmStatic
        fun saveAndSetLanguage(
            context: Context,
            languageTag: String
        ) {
            saveLanguage(context, languageTag)
            setLanguage(languageTag)
        }

        /**
         * @param context
         * Load language tag from shared preferences and set language
         */
        @JvmStatic
        fun loadAndSetLanguage(context: Context) {
            val languageTag = loadLanguage(context)
            setLanguage(languageTag)
        }

        /**
         * @param languageTag
         * Set language by language tag (e.g. "en", "ru", "en-US")
         */
        fun setLanguage(languageTag: String) {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(languageTag)
            )
        }

        /**
         * @param context
         * @param languageTag
         * Save language tag to shared preferences
         */
        @JvmStatic
        fun saveLanguage(context: Context, languageTag: String) = PreferenceManager
            .getDefaultSharedPreferences(context)
            .edit().run {
                putString(KEY, languageTag)
                apply()
            }

        /**
         * @param context
         * @return language tag from shared preferences
         * Load language tag from shared preferences
         */
        @JvmStatic
        fun loadLanguage(context: Context): String = PreferenceManager
            .getDefaultSharedPreferences(context)
            .getString(KEY, "")!!
    }
}