package com.iamouakil.muslimalarm.ui.onboarding

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val dataStore = PreferenceDataStoreFactory.create {
        context.preferencesDataStoreFile("onboarding_prefs")
    }

    private val onboardingCompletedKey = booleanPreferencesKey("onboarding_completed")

    val isOnboardingCompleted: StateFlow<Boolean> = dataStore.data
        .map { it[onboardingCompletedKey] ?: false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setOnboardingCompleted() {
        viewModelScope.launch {
            dataStore.edit { it[onboardingCompletedKey] = true }
        }
    }
}
