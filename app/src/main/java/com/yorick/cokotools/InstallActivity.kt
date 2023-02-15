package com.yorick.cokotools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yorick.cokotools.ui.components.BaseAlterDialog
import com.yorick.cokotools.ui.theme.CokoToolsTheme
import com.yorick.cokotools.util.InstallUtils
import com.yorick.cokotools.util.Utils
import com.yorick.cokotools.util.shell.ShizukuShell
import kotlinx.coroutines.launch

class InstallActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uri = intent.data
        var apkPath: String? = null
        if (uri != null) {
            apkPath = uri.path
        }
        setContent {
            val scope = rememberCoroutineScope()
            var dialogState by remember { mutableStateOf(true) }
            val onDismissRequest = {
                dialogState = false
                finish()
            }
            var isInstall by remember { mutableStateOf(false) }
            CokoToolsTheme {
                if (dialogState) {
                    BaseAlterDialog(
                        modifier = Modifier.animateContentSize(),
                        onDismissRequest = onDismissRequest,
                        title = stringResource(id = R.string.install_component_down_grade),
                        onConfirm = {
                            if (ShizukuShell.isAvailable) {
                                isInstall = true
                                scope.launch {
                                    Utils.mToast(
                                        if (InstallUtils.doInstallApk(uri, applicationContext))
                                            R.string.install_done else R.string.install_err,
                                        applicationContext
                                    )
                                    onDismissRequest()
                                }
                            } else {
                                Utils.mToast(R.string.enable_it, applicationContext)
                                onDismissRequest()
                            }
                        },
                        onDismiss = onDismissRequest,
                        cancelable = false,
                        buttonEnable = !isInstall
                    ) {
                        if (isInstall) {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp, bottom = 10.dp)
                            )
                        } else {
                            Text(text = apkPath ?: stringResource(id = R.string.unknown))
                        }
                    }
                }
            }
        }
    }
}