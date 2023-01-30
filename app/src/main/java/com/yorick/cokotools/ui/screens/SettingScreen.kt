package com.yorick.cokotools.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yorick.cokotools.R
import com.yorick.cokotools.ui.components.BaseAlterDialog

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    var dynamicColors by remember {
        mutableStateOf(true)
    }
    var confirmDialogState by remember {
        mutableStateOf(false)
    }
    if (confirmDialogState) {
        BaseAlterDialog(
            onDismissRequest = { confirmDialogState = false },
            title = stringResource(id = R.string.warning),
            text = stringResource(id = R.string.refresh_warning),
            onConfirm = { confirmDialogState = false },
            onDismiss = { confirmDialogState = false }
        )
    }
    Column(modifier = modifier.fillMaxWidth()) {
        CokoClassRow(className = stringResource(id = R.string.theme)) {
            CokoSingleRowListItem(
                modifier = Modifier.clickable {
                    isExpanded = true
                },
                icon = Icons.Outlined.DarkMode,
                name = stringResource(id = R.string.dark_mode)
            ) {
                Text(
                    text = stringResource(id = R.string.auto),
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.bodyMedium
                )
                DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.auto)) },
                        onClick = { isExpanded = false })
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.light)) },
                        onClick = { isExpanded = false })
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.dark)) },
                        onClick = { isExpanded = false })
                }
            }
            CokoDualRowListItem(
                icon = Icons.Outlined.ColorLens,
                name = stringResource(id = R.string.dynamic_color),
                desc = stringResource(id = R.string.dynamic_color_desc)
            ) {
                Switch(
                    checked = dynamicColors,
                    onCheckedChange = { dynamicColors = !dynamicColors })
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