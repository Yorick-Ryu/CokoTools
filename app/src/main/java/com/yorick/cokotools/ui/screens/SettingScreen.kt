package com.yorick.cokotools.ui.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.SpeakerNotes
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.SpeakerNotesOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yorick.cokotools.R
import com.yorick.cokotools.data.model.DarkThemeConfig
import com.yorick.cokotools.data.model.UserData
import com.yorick.cokotools.ui.components.BaseAlterDialog
import com.yorick.cokotools.ui.theme.supportsDynamicTheming
import com.yorick.cokotools.ui.viewmodels.SettingViewModel
import com.yorick.cokotools.ui.viewmodels.SettingsUiState.Loading
import com.yorick.cokotools.ui.viewmodels.SettingsUiState.Success

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    settingViewModel: SettingViewModel
) {
    val settingsUiState by settingViewModel.settingsUiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        when (settingsUiState) {
            Loading -> {
                Text(text = stringResource(id = R.string.loading))
            }

            is Success -> {
                SettingList(
                    settings = (settingsUiState as Success).settings,
                    onChangeDynamicColorPreference = settingViewModel::updateDynamicColorPreference,
                    onChangeDarkThemeConfig = settingViewModel::updateDarkThemeConfig,
                    onChangeOkToastConfig = settingViewModel::updateOkToastPreference,
                    reloadLocalData = settingViewModel::reloadLocalData
                )
            }
        }
    }
}

@Composable
fun SettingList(
    modifier: Modifier = Modifier,
    settings: UserData,
    supportDynamicColor: Boolean = supportsDynamicTheming(),
    onChangeDynamicColorPreference: (useDynamicColor: Boolean) -> Unit,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
    onChangeOkToastConfig: (okToast: Boolean) -> Unit,
    reloadLocalData: (content: Context) -> Unit
) {
    val context = LocalContext.current
    var isExpanded by remember {
        mutableStateOf(false)
    }

    var confirmDialogState by remember {
        mutableStateOf(false)
    }
    if (confirmDialogState) {
        BaseAlterDialog(
            onDismissRequest = { confirmDialogState = false },
            title = stringResource(id = R.string.warning),
            text = stringResource(id = R.string.refresh_warning),
            onConfirm = {
                confirmDialogState = false
                reloadLocalData(context)
            },
            onDismiss = { confirmDialogState = false }
        )
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        CokoClassRow(className = stringResource(id = R.string.theme)) {
            CokoSingleRowListItem(
                modifier = Modifier.clickable {
                    isExpanded = true
                },
                icon = Icons.Outlined.DarkMode,
                name = stringResource(id = R.string.dark_mode)
            ) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                    LocalContentColor provides MaterialTheme.colorScheme.outline
                ) {
                    when (settings.darkThemeConfig) {
                        DarkThemeConfig.FOLLOW_SYSTEM -> {
                            Text(text = stringResource(id = R.string.auto))
                        }

                        DarkThemeConfig.LIGHT -> {
                            Text(text = stringResource(id = R.string.light))
                        }

                        DarkThemeConfig.DARK -> {
                            Text(text = stringResource(id = R.string.dark))
                        }
                    }
                }
                DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.auto)) },
                        onClick = {
                            isExpanded = false
                            onChangeDarkThemeConfig(DarkThemeConfig.FOLLOW_SYSTEM)
                        })
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.light)) },
                        onClick = {
                            isExpanded = false
                            onChangeDarkThemeConfig(DarkThemeConfig.LIGHT)
                        })
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.dark)) },
                        onClick = {
                            isExpanded = false
                            onChangeDarkThemeConfig(DarkThemeConfig.DARK)
                        })
                }
            }
            if (supportDynamicColor) {
                CokoDualRowListItem(
                    icon = Icons.Outlined.ColorLens,
                    name = stringResource(id = R.string.dynamic_color),
                    desc = stringResource(id = R.string.dynamic_color_desc)
                ) {
                    Switch(
                        checked = settings.useDynamicColor,
                        onCheckedChange = { onChangeDynamicColorPreference(!settings.useDynamicColor) }
                    )
                }
            }
        }
        CokoClassRow(className = stringResource(id = R.string.function)) {
            CokoSingleRowListItem(
                modifier = Modifier,
                icon = if (settings.okToast) Icons.AutoMirrored.Outlined.SpeakerNotes else Icons.Outlined.SpeakerNotesOff,
                name = stringResource(id = R.string.ok_toast),
            ) {
                Switch(
                    checked = settings.okToast,
                    onCheckedChange = { onChangeOkToastConfig(!settings.okToast) }
                )
            }
        }
        CokoClassRow(className = stringResource(id = R.string.data)) {
            CokoDualRowListItem(
                modifier = Modifier.clickable {
                    confirmDialogState = true
                },
                icon = Icons.Outlined.Refresh,
                name = stringResource(id = R.string.refresh_data),
                desc = stringResource(id = R.string.refresh_data_desc)
            )
        }
    }
}

@Composable
fun CokoClassRow(
    modifier: Modifier = Modifier,
    className: String,
    listContent: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = className,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }
        listContent()
    }
}

@Composable
fun CokoSingleRowListItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    name: String,
    option: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = name
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Box {
            option()
        }
    }
}

@Composable
fun CokoDualRowListItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    name: String,
    desc: String,
    option: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = name
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = desc,
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
        Box {
            option()
        }
    }
}