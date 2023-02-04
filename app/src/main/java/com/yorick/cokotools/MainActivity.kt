package com.yorick.cokotools

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.yorick.cokotools.data.model.DarkThemeConfig
import com.yorick.cokotools.ui.CokoToolsApp
import com.yorick.cokotools.ui.components.BaseAlterDialog
import com.yorick.cokotools.ui.theme.CokoToolsTheme
import com.yorick.cokotools.ui.viewmodels.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        var uiState: SettingsUiState by mutableStateOf(SettingsUiState.Loading)


        preferences = getSharedPreferences("count", MODE_PRIVATE)
        var count: Int = preferences.getInt("count", 0)
        val editor: SharedPreferences.Editor = preferences.edit()

        val homeViewModel: HomeViewModel by viewModels { CokoViewModelFactory }
        val contributorViewModel: ContributorViewModel by viewModels { CokoViewModelFactory }
        val settingsViewModel: SettingsViewModel by viewModels { CokoViewModelFactory }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsViewModel.settingsUiState
                    .onEach {
                        uiState = it
                    }
                    .collect()
            }
        }

        setContent {
            val systemUiController = rememberSystemUiController()
            val darkTheme = shouldUseDarkTheme(uiState)
            DisposableEffect(systemUiController, darkTheme) {
                systemUiController.systemBarsDarkContentEnabled = !darkTheme
                onDispose {}
            }
            var isFirst by remember {
                mutableStateOf(count == 0)
            }
            CokoToolsTheme(
                darkTheme = darkTheme,
                dynamicColor = useDynamicTheming(uiState)
            ) {
                CokoToolsApp(
                    homeViewModel = homeViewModel,
                    contributorViewModel = contributorViewModel,
                    settingsViewModel = settingsViewModel
                )
                // 判断程序与第几次运行，如果是第一次运行则开启弹窗
                if (isFirst) {
                    BaseAlterDialog(
                        onDismissRequest = { isFirst = false },
                        title = stringResource(id = R.string.exceptions_title),
                        text = stringResource(id = R.string.exceptions_message),
                        positiveText = R.string.exceptions_accept,
                        negativeText = R.string.decline,
                        onConfirm = {
                            editor.putInt("count", ++count)
                            editor.apply()
                            isFirst = false
                        },
                        onDismiss = {
                            editor.putInt("count", 0)
                            editor.apply()
                            finish()
                        },
                        cancelable = false
                    )
                }
            }
        }
    }
}

@Composable
private fun shouldUseDarkTheme(
    uiState: SettingsUiState,
): Boolean = when (uiState) {
    SettingsUiState.Loading -> isSystemInDarkTheme()
    is SettingsUiState.Success -> when (uiState.settings.darkThemeConfig) {
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
    }
}

@Composable
private fun useDynamicTheming(
    uiState: SettingsUiState,
): Boolean = when (uiState) {
    SettingsUiState.Loading -> false
    is SettingsUiState.Success -> uiState.settings.useDynamicColor
}