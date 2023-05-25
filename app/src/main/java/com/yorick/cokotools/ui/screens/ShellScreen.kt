package com.yorick.cokotools.ui.screens

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.HighlightOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yorick.cokotools.R
import com.yorick.cokotools.ui.components.CardWithTitle
import com.yorick.cokotools.ui.viewmodels.ShellUiState
import com.yorick.cokotools.ui.viewmodels.ShellViewModel
import com.yorick.cokotools.ui.viewmodels.ShizukuState
import com.yorick.cokotools.util.Utils
import com.yorick.cokotools.util.Utils.mToast
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
    var packageName by remember { mutableStateOf("") }
    val onPackageNameChange: (packageName: String) -> Unit = { packageName = it }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
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
        Spacer(modifier = Modifier.height(16.dp))
        FreezeComponentCard(
            uiState = uiState,
            scope = scope,
            hostState = hostState,
            freeze = shellViewModel::freezePackage,
            unfreeze = shellViewModel::unFreezePackage,
            context = context,
            packageName = packageName,
            onPackageNameChange = onPackageNameChange
        )
        if (uiState.freezeList.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            FreezeListCard(
                uiState = uiState,
                onPackageNameChange = onPackageNameChange,
                scope = scope,
                hostState = hostState,
                context = context
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
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
                    openUrl(Utils.SHIZUKU_INTRODUCTION_URL, context)
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
                    openUrl(Utils.SHIZUKU_DOWNLOAD_URL, context)
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
                        openUrl(Utils.SHIZUKU_DOWNLOAD_URL, context)
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
    installApks: (result: ActivityResult, context: Context) -> Unit,
    hostState: SnackbarHostState,
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
        Row {
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
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun FreezeComponentCard(
    modifier: Modifier = Modifier,
    uiState: ShellUiState,
    freeze: (packageName: String, context: Context) -> Unit,
    unfreeze: (packageName: String, context: Context) -> Unit,
    hostState: SnackbarHostState,
    scope: CoroutineScope,
    context: Context,
    packageName: String,
    onPackageNameChange: (packageName: String) -> Unit,
) {
    var res: Int
    val keyboardController = LocalSoftwareKeyboardController.current
    val enabled = uiState.shizukuState == ShizukuState.RUNNING_AND_GRANTED
    val confirmMessage = stringResource(id = R.string.freeze_confirm)
    val label = stringResource(id = R.string.accept)
    val onConfirmInfo: () -> Unit = {
        scope.launch {
            val result = hostState.showSnackbar(
                message = String.format(confirmMessage, packageName),
                duration = SnackbarDuration.Short,
                actionLabel = label,
                withDismissAction = true
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    res = Utils.matchPackageName(packageName.trim())
                    if (res == 0) freeze(packageName, context) else mToast(res, context)
                }

                SnackbarResult.Dismissed -> {}
            }
        }
    }
    val aboutMessage = stringResource(id = R.string.freeze_desc)
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
    CardWithTitle(
        modifier = modifier,
        cardTitle = stringResource(id = R.string.freeze_component),
        onClickInfo = onClickInfo
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 10.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 300.dp),
                value = packageName,
                label = {
                    Text(text = stringResource(id = R.string.input_package))
                },
                trailingIcon = {
                    if (packageName != "") {
                        IconButton(onClick = { onPackageNameChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(id = R.string.clear)
                            )
                        }
                    }
                },
                onValueChange = onPackageNameChange,
                enabled = enabled,
                singleLine = true,
                shape = MaterialTheme.shapes.small,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() })
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    modifier = Modifier,
                    enabled = enabled,
                    onClick = {
                        keyboardController?.hide()
                        res = Utils.matchPackageName(packageName.trim())
                        if (res == 0) unfreeze(packageName, context) else mToast(res, context)
                    }
                ) {
                    Text(text = stringResource(id = R.string.unfreeze_component))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    enabled = enabled,
                    onClick = {
                        keyboardController?.hide()
                        res = Utils.matchPackageName(packageName.trim())
                        if (res == 0) onConfirmInfo() else mToast(res, context)
                    }
                ) {
                    Text(text = stringResource(id = R.string.freeze_component))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FreezeListCard(
    modifier: Modifier = Modifier,
    uiState: ShellUiState,
    onPackageNameChange: (packageName: String) -> Unit,
    hostState: SnackbarHostState,
    scope: CoroutineScope,
    context: Context,
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val aboutMessage = stringResource(id = R.string.freeze_list_desc)
    val copyDone = stringResource(id = R.string.copy_done)
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
    CardWithTitle(
        cardTitle = stringResource(id = R.string.freeze_list),
        onClickInfo = onClickInfo
    ) {
        Column(
            modifier = modifier.height(300.dp)
        ) {
            LazyColumn(state = rememberLazyListState()) {
                items(items = uiState.freezeList) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = { onPackageNameChange(it) },
                                onLongClick = {
                                    clipboardManager.setText(AnnotatedString(it))
                                    mToast(String.format(copyDone, it), context)
                                }
                            )
                            .padding(horizontal = 16.dp, vertical = 3.dp),
                        text = it
                    )
                }
            }
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
            .padding(start = 16.dp),
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
        Spacer(modifier = Modifier.width(10.dp))
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
                    Spacer(modifier = Modifier.width(8.dp))
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
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stateDesc,
            color = MaterialTheme.colorScheme.onTertiary,
            style = MaterialTheme.typography.labelLarge
        )
    }
}