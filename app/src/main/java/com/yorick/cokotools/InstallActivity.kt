package com.yorick.cokotools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.yorick.cokotools.ui.components.BaseAlterDialog
import com.yorick.cokotools.ui.theme.CokoToolsTheme
import com.yorick.cokotools.util.Utils
import kotlinx.coroutines.launch

class InstallActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent.data
        setContent {
            val scope = rememberCoroutineScope()
            var dialogState by remember { mutableStateOf(true) }
            val onDismissRequest = {
                dialogState = false
                finish()
            }
            CokoToolsTheme {
                if (dialogState) {
                    BaseAlterDialog(
                        onDismissRequest = onDismissRequest,
                        title = stringResource(id = R.string.install_component),
                        onConfirm = {
                            scope.launch {
                                Utils.mToast(
                                    if (Utils.doInstallApk(uri, applicationContext))
                                        R.string.install_done else R.string.install_err,
                                    applicationContext
                                )
                                onDismissRequest()
                            }
                        },
                        onDismiss = onDismissRequest,
                        cancelable = false
                    ) {
                        Text(text = uri.toString())
                    }
                }
            }
        }
    }
}