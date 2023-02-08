package com.yorick.cokotools.ui.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yorick.cokotools.data.datastore.UserPreferencesRepository
import com.yorick.cokotools.data.model.DarkThemeConfig
import com.yorick.cokotools.data.model.UserData
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
                // Starting eagerly means the user data is ready when the SettingsDialog is laid out
                // for the first time. Without this, due to b/221643630 the layout is done using the
                // "Loading" text, then replaced with the user editable fields once loaded, however,
                // the layout height doesn't change meaning all the fields are squashed into a small
                // scrollable column.
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
        try {
            context.startActivity(
                Intent(
                    android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Toast.makeText(context, "点击清除应用数据", Toast.LENGTH_SHORT).show()
    }
}

sealed interface SettingsUiState {
    object Loading : SettingsUiState
    data class Success(val settings: UserData) : SettingsUiState
}