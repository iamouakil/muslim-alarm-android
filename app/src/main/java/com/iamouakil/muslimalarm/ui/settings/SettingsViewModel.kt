package com.iamouakil.muslimalarm.ui.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iamouakil.muslimalarm.ui.theme.ThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private val Context.appSettingsDataStore by preferencesDataStore(name = "app_settings")

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val themeManager: ThemeManager
) : ViewModel() {

    private val LANGUAGE_KEY = stringPreferencesKey("app_language")

    val selectedLanguage: StateFlow<String> = context.appSettingsDataStore.data
        .map { it[LANGUAGE_KEY] ?: "العربية" }
        .stateIn(viewModelScope, SharingStarted.Lazily, "العربية")

    val selectedTheme: StateFlow<String> = themeManager.currentTheme
        .stateIn(viewModelScope, SharingStarted.Lazily, "الأخضر الإسلامي")

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            context.appSettingsDataStore.edit { prefs ->
                prefs[LANGUAGE_KEY] = lang
            }
        }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch {
            themeManager.setTheme(theme)
        }
    }
}
