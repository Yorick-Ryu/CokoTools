package com.yorick.cokotools.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.yorick.cokotools.R

// 失败弹窗组件
@Composable
fun ErrorDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    title: String = stringResource(id = R.string.title_error),
    text: String = stringResource(id = R.string.common_tips),
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseAlterDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = title,
        text = text,
        negativeText = R.string.exceptions_read_help_doc,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

// 基础弹窗组件
@Composable
fun BaseAlterDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    title: String,
    text: String,
    @StringRes positiveText: Int = R.string.accept,
    @StringRes negativeText: Int = R.string.cancel,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    cancelable: Boolean = true
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CokoToolsLogo()
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = positiveText))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = negativeText))
            }
        },
        properties = DialogProperties(
            dismissOnClickOutside = cancelable,
            dismissOnBackPress = cancelable
        )
    )
}

