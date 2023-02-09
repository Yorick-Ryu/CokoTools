package com.yorick.cokotools.ui.screens

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.HighlightOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yorick.cokotools.R
import com.yorick.cokotools.ui.components.CardWithTitle
import com.yorick.cokotools.ui.viewmodels.ShellUiState
import com.yorick.cokotools.ui.viewmodels.ShellViewModel
import com.yorick.cokotools.ui.viewmodels.ShizukuState
import com.yorick.cokotools.util.Utils.openUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import rikka.shizuku.Shizuku

@Composable
fun ShellScreen(
    modifier: Modifier = Modifier,
    shellViewModel: ShellViewModel,
    scope: CoroutineScope,
    hostState: SnackbarHostState,
) {
    val context = LocalContext.current
    val uiState by shellViewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        ShizukuCard(
            uiState = uiState,
            openShizuku = shellViewModel::openShizuku,
            scope = scope,
            hostState = hostState,
            context = context
        )
        Spacer(modifier = Modifier.height(16.dp))
        InstallComponentCard(
            uiState = uiState,
            scope = scope,
            hostState = hostState,
            context = context,
            installApks = shellViewModel::installApks
        )
    }
}

@Composable
fun ShizukuCard(
    modifier: Modifier = Modifier,
    uiState: ShellUiState,
    openShizuku: (context: Context) -> Boolean = { _ -> true },
    scope: CoroutineScope,
    hostState: SnackbarHostState,
    context: Context
) {
    val aboutMessage = stringResource(id = R.string.about_shizuku_introduction)
    val aboutActionLabel = stringResource(id = R.string.about_more)
    val aboutInfo: () -> Unit = {
        scope.launch {
            val result = hostState.showSnackbar(
                message = aboutMessage,
                duration = SnackbarDuration.Short,
                actionLabel = aboutActionLabel,
                withDismissAction = true
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    openUrl("https://shizuku.rikka.app/zh-hans/introduction/", context)
                }
                SnackbarResult.Dismissed -> {}
            }
        }
    }
    val errMessage = stringResource(id = R.string.no_shizuku)
    val errActionLabel = stringResource(id = R.string.download_shizuku)
    val errInfo: () -> Unit = {
        scope.launch {
            val result = hostState.showSnackbar(
                message = errMessage,
                duration = SnackbarDuration.Short,
                actionLabel = errActionLabel,
                withDismissAction = true
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    openUrl("https://shizuku.rikka.app/zh-hans/download/", context)
                }
                SnackbarResult.Dismissed -> {}
            }
        }
    }
    CardWithTitle(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable {
                when (uiState.shizukuState) {
                    ShizukuState.NOT_RUNNING -> {
                        if (!openShizuku(context)) errInfo()
                    }
                    ShizukuState.RUNNING_BUT_LOW_VERSION -> {
                        openUrl("https://shizuku.rikka.app/zh-hans/download/", context)
                    }
                    ShizukuState.RUNNING_BUT_NOT_GRANTED -> {
                        Shizuku.requestPermission(0)
                    }
                    ShizukuState.RUNNING_AND_GRANTED -> {}
                }
            },
        cardTitle = stringResource(id = R.string.shizuku_state),
        onClickInfo = aboutInfo
    ) {
        when (uiState.shizukuState) {
            ShizukuState.NOT_RUNNING -> {
                ShizukuStateCardContent(
                    icon = Icons.Outlined.HighlightOff,
                    shizukuState = stringResource(id = R.string.not_running),
                    stateDesc = stringResource(id = R.string.not_running_tip),
                )
            }
            ShizukuState.RUNNING_BUT_LOW_VERSION -> {
                ShizukuStateCardContent(
                    icon = Icons.Outlined.ErrorOutline,
                    shizukuState = stringResource(id = R.string.running),
                    permissionState = stringResource(id = R.string.low_version),
                    stateDesc = stringResource(id = R.string.current_version) + " "
                            + uiState.shizukuVersion + " "
                            + stringResource(id = R.string.running_but_low_version_tip),
                )
            }
            ShizukuState.RUNNING_BUT_NOT_GRANTED -> {
                ShizukuStateCardContent(
                    icon = Icons.Outlined.ErrorOutline,
                    shizukuState = stringResource(id = R.string.running),
                    permissionState = stringResource(id = R.string.not_granted),
                    stateDesc = stringResource(id = R.string.click_to_grant),
                )
            }
            ShizukuState.RUNNING_AND_GRANTED -> {
                ShizukuStateCardContent(
                    icon = Icons.Outlined.CheckCircle,
                    shizukuState = stringResource(id = R.string.running),
                    permissionState = stringResource(id = R.string.granted),
                    stateDesc = stringResource(id = R.string.version) + " " + uiState.shizukuVersion,
                )
            }
        }
    }
}


@Composable
fun InstallComponentCard(
    modifier: Modifier = Modifier,
    uiState: ShellUiState,
    scope: CoroutineScope,
    hostState: SnackbarHostState,
    installApks: (result: ActivityResult, context: Context) -> Unit,
    context: Context
) {
    val aboutMessage = stringResource(id = R.string.install_component_desc)
    val onClickInfo: () -> Unit = {
        scope.launch {
            val result = hostState.showSnackbar(
                message = aboutMessage,
                duration = SnackbarDuration.Short,
                withDismissAction = true
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {}
                SnackbarResult.Dismissed -> {}
            }
        }
    }
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        type = "application/vnd.android.package-archive"
    }
    val launcherForActivityResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        installApks(it, context)
    }
    CardWithTitle(
        modifier = modifier.height(110.dp),
        cardTitle = stringResource(id = R.string.install_component),
        onClickInfo = onClickInfo
    ) {
        val enabled =
            uiState.shizukuState == ShizukuState.RUNNING_AND_GRANTED && !uiState.isInstallApk
        Button(
            modifier = Modifier.padding(start = 16.dp),
            enabled = enabled,
            onClick = {
                launcherForActivityResult.launch(intent)
            }
        ) {
            Text(
                text = stringResource(
                    id = if (uiState.isInstallApk) R.string.installing else R.string.install_component
                )
            )
        }
    }
}

@Composable
fun ShizukuStateCardContent(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    shizukuState: String,
    permissionState: String? = null,
    stateDesc: String = "",
) {
    val stateColor by animateColorAsState(
        if (shizukuState == stringResource(id = R.string.running) &&
            permissionState == stringResource(id = R.string.granted)
        ) {
            MaterialTheme.colorScheme.secondary
        } else {
            MaterialTheme.colorScheme.tertiary
        }
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(shape = CircleShape)
                .background(color = stateColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = modifier,
                imageVector = icon,
                contentDescription = shizukuState,
                tint = MaterialTheme.colorScheme.onTertiary
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.height(45.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.Center) {
                Text(
                    text = shizukuState,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (permissionState != null) {
                    Spacer(modifier = Modifier.width(10.dp))
                    StateDescTag(
                        stateDesc = permissionState,
                        stateColor = stateColor
                    )
                }
            }
            Text(
                text = stateDesc,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal)
            )
        }
    }
}

@Composable
fun StateDescTag(
    modifier: Modifier = Modifier,
    stateDesc: String,
    stateColor: Color,
) {
    Box(
        modifier = modifier
            .background(
                color = stateColor,
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 5.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stateDesc,
            color = MaterialTheme.colorScheme.onTertiary,
            style = MaterialTheme.typography.labelLarge
        )
    }
}