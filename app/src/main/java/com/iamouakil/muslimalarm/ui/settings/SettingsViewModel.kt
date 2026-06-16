package com.iamouakil.muslimalarm.ui.settings

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val dataStore = PreferenceDataStoreFactory.create {
        context.preferencesDataStoreFile("app_settings")
    }

    private val languageKey = stringPreferencesKey("language")
    private val themeKey = stringPreferencesKey("theme")

    val selectedLanguage: StateFlow<String> = dataStore.data
        .map { it[languageKey] ?: "العربية" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "العربية")

    val selectedTheme: StateFlow<String> = dataStore.data
        .map { it[themeKey] ?: "الأخضر الإسلامي" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "الأخضر الإسلامي")

    fun setLanguage(lang: String) {
        viewModelScope.launch { dataStore.edit { it[languageKey] = lang } }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch { dataStore.edit { it[themeKey] = theme } }
    }
}
