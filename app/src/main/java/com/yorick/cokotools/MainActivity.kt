package com.yorick.cokotools

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.yorick.cokotools.data.model.DarkThemeConfig
import com.yorick.cokotools.ui.CokoToolsApp
import com.yorick.cokotools.ui.components.BaseAlterDialog
import com.yorick.cokotools.ui.theme.CokoToolsTheme
import com.yorick.cokotools.ui.viewmodels.CokoViewModelFactory
import com.yorick.cokotools.ui.viewmodels.ContributorViewModel
import com.yorick.cokotools.ui.viewmodels.HomeViewModel
import com.yorick.cokotools.ui.viewmodels.SettingViewModel
import com.yorick.cokotools.ui.viewmodels.SettingsUiState
import com.yorick.cokotools.ui.viewmodels.ShellViewModel
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
        val shellViewModel: ShellViewModel by viewModels()
        val settingViewModel: SettingViewModel by viewModels { CokoViewModelFactory }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingViewModel.settingsUiState
                    .onEach {
                        uiState = it
                    }
                    .collect()
            }
        }

        enableEdgeToEdge()

        setContent {
            val darkTheme = shouldUseDarkTheme(uiState)
            var isFirst by remember {
                mutableStateOf(count == 0)
            }
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim,
                        darkScrim,
                    ) { darkTheme },
                )
                onDispose {}
            }
            CokoToolsTheme(
                darkTheme = darkTheme,
                dynamicColor = useDynamicTheming(uiState)
            ) {
                CokoToolsApp(
                    homeViewModel = homeViewModel,
                    contributorViewModel = contributorViewModel,
                    shellViewModel = shellViewModel,
                    settingViewModel = settingViewModel
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

private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)