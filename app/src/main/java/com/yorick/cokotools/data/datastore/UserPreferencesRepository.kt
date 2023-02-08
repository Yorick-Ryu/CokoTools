package com.yorick.cokotools.data.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.yorick.cokotools.data.model.DarkThemeConfig
import com.yorick.cokotools.data.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(
    private val userPreferencesStore: DataStore<UserPreferences>
) {
    private val TAG: String = "UserPreferencesRepo"

    val userPreferencesFlow: Flow<UserData> = userPreferencesStore.data.map {
        UserData(
            darkThemeConfig = when (it.darkThemeConfig) {
                null,
                DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
                DarkThemeConfigProto.UNRECOGNIZED,
                DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM,
                ->
                    DarkThemeConfig.FOLLOW_SYSTEM
                DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT ->
                    DarkThemeConfig.LIGHT
                DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> DarkThemeConfig.DARK
            },
            useDynamicColor = it.useDynamicColor,
            okToast = it.okToast
        )
    }
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(TAG, "Error reading sort order preferences.", exception)
            } else {
                throw exception
            }
        }

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        userPreferencesStore.updateData { preferences ->
            val darkThemeConfigProto: DarkThemeConfigProto = when (darkThemeConfig) {
                DarkThemeConfig.FOLLOW_SYSTEM -> DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
                DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
            }
            preferences.toBuilder().setDarkThemeConfig(darkThemeConfigProto).build()
        }
    }

    suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setUseDynamicColor(useDynamicColor).build()
        }
    }

    suspend fun setOkToastPreference(okToast: Boolean) {
        userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setOkToast(okToast).build()
        }
    }
}