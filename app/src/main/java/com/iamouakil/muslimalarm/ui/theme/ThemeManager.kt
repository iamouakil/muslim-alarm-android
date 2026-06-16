package com.iamouakil.muslimalarm.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = PreferenceDataStoreFactory.create {
        context.preferencesDataStoreFile("app_settings")
    }

    private val themeKey = stringPreferencesKey("app_theme")

    val currentTheme: Flow<String> = dataStore.data.map { preferences ->
        preferences[themeKey] ?: "الأخضر الإسلامي"
    }

    suspend fun setTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[themeKey] = theme
        }
    }
}
