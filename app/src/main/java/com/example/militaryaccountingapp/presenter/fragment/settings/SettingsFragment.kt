package com.example.militaryaccountingapp.presenter.fragment.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentSettingsBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseFragment
import kotlinx.coroutines.launch

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSettingsBinding
        get() = FragmentSettingsBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            this.childFragmentManager
                .beginTransaction()
                .replace(R.id.container_root_preferences, PreferenceFragment())
                .commit()
        }
    }

    override fun initializeView() {}
}

class PreferenceFragment : PreferenceFragmentCompat() {

    private val languageService: LanguageService by lazy { LanguageService(resources) }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        lifecycleScope.launch {
            setupLanguagePreference()
            setupThemePreference()
        }
    }

    private fun setupLanguagePreference() {
        findPreference<ListPreference>("language")!!.apply {
            entries = languageService.languageNames.toTypedArray()
            entryValues = languageService.languageTags.toTypedArray()
            languageService.defaultLanguage.let {
                setDefaultValue(it.toLanguageTag())
                summary = it.displayName
            }
        }.setOnPreferenceChangeListener { _, value ->
//            LanguageService.saveAndSetLanguage(requireContext(), value as String)
            LanguageService.setLanguage(value as String)
            true
        }
    }

    private fun setupThemePreference() {
        findPreference<ListPreference>("theme")!!.apply {
            ThemeService.ThemeType.values().let { themes ->
                entries = themes.map { it.displayName }.toTypedArray()
                entryValues = themes.map { it.toString() }.toTypedArray()
            }
            ThemeService.theme.value.let {
                setDefaultValue(it.toString())
                summary = it.displayName
            }
        }.setOnPreferenceChangeListener { _, value ->
            val themeType = ThemeService.ThemeType.valueOf(value.toString())
            ThemeService.saveAndSetTheme(requireActivity(), themeType)
            true
        }
    }
}