package com.yorick.cokotools.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yorick.cokotools.data.datastore.UserPreferencesRepository
import com.yorick.cokotools.data.model.DarkThemeConfig
import com.yorick.cokotools.data.model.UserData
import com.yorick.cokotools.util.Utils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val settingsUiState: StateFlow<SettingsUiState> =
        userPreferencesRepository.userPreferencesFlow
            .map { userData ->
                SettingsUiState.Success(settings = userData)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = SettingsUiState.Loading,
            )

    fun updateDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch {
            userPreferencesRepository.setDarkThemeConfig(darkThemeConfig)
        }
    }

    fun updateDynamicColorPreference(useDynamicColor: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setDynamicColorPreference(useDynamicColor)
        }
    }

    fun updateOkToastPreference(okToast: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setOkToastPreference(okToast)
        }
    }

    fun reloadLocalData(context: Context) {
        Utils.openAppDetail(context)
    }
}

sealed interface SettingsUiState {
    object Loading : SettingsUiState
    data class Success(val settings: UserData) : SettingsUiState
}