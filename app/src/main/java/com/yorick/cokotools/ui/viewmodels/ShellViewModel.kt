package com.yorick.cokotools.ui.viewmodels

import android.app.Activity.RESULT_OK
import android.app.Application
import android.content.ClipData
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yorick.cokotools.R
import com.yorick.cokotools.util.FreezeUtils
import com.yorick.cokotools.util.InstallUtils
import com.yorick.cokotools.util.Utils
import com.yorick.cokotools.util.Utils.mToast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import rikka.shizuku.Shizuku.OnBinderDeadListener
import rikka.shizuku.Shizuku.OnBinderReceivedListener
import rikka.shizuku.Shizuku.OnRequestPermissionResultListener
import rikka.shizuku.Shizuku.addBinderDeadListener
import rikka.shizuku.Shizuku.addBinderReceivedListenerSticky
import rikka.shizuku.Shizuku.addRequestPermissionResultListener
import rikka.shizuku.Shizuku.checkSelfPermission
import rikka.shizuku.Shizuku.getVersion
import rikka.shizuku.Shizuku.isPreV11
import rikka.shizuku.Shizuku.removeBinderDeadListener
import rikka.shizuku.Shizuku.removeBinderReceivedListener
import rikka.shizuku.Shizuku.removeRequestPermissionResultListener

class ShellViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ShellUiState(shizukuState = ShizukuState.NOT_RUNNING))
    val uiState: StateFlow<ShellUiState> = _uiState

    private val binderReceivedListener = OnBinderReceivedListener {
        if (isPreV11()) {
            _uiState.value = _uiState.value.copy(
                shizukuState = ShizukuState.RUNNING_BUT_LOW_VERSION
            )
        } else {
            if (checkSelfPermission() == PERMISSION_GRANTED) {
                _uiState.value = _uiState.value.copy(
                    shizukuState = ShizukuState.RUNNING_AND_GRANTED,
                    shizukuVersion = getVersion().toString()
                )
                getFreezeList()
            } else {
                _uiState.value = _uiState.value.copy(
                    shizukuState = ShizukuState.RUNNING_BUT_NOT_GRANTED,
                )
            }
        }
    }

    private val binderDeadListener = OnBinderDeadListener {
        _uiState.value = _uiState.value.copy(shizukuState = ShizukuState.NOT_RUNNING)
    }

    private val requestPermissionResultListener =
        OnRequestPermissionResultListener { _, grantResult: Int ->
            if (grantResult == PERMISSION_GRANTED) {
                _uiState.value = _uiState.value.copy(
                    shizukuState = ShizukuState.RUNNING_AND_GRANTED,
                    shizukuVersion = getVersion().toString()
                )
                getFreezeList()
            }
        }

    init {
        addBinderReceivedListenerSticky(binderReceivedListener)
        addBinderDeadListener(binderDeadListener)
        addRequestPermissionResultListener(requestPermissionResultListener)
        getFreezeList()
    }

    fun openShizuku(context: Context): Boolean {
        return Utils.openShizuku(context)
    }

    fun installApks(result: ActivityResult, context: Context) {
        if (result.resultCode == RESULT_OK) {
            val uris = emptyList<Uri>().toMutableList()
            val clipData: ClipData? = result.data?.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    if (uri != null) {
                        uris += uri
                    }
                }
            } else {
                result.data?.data?.let { uris.add(it) }
            }
            _uiState.value = _uiState.value.copy(
                isInstallApk = true
            )
            mToast(R.string.install_start, context)
            viewModelScope.launch {
                mToast(
                    if (InstallUtils.installApkByShell(uris, context))
                        R.string.install_done else R.string.install_err, context
                )
                _uiState.value = _uiState.value.copy(
                    isInstallApk = false
                )
            }
        }
    }

    fun freezePackage(packageName: String, context: Context) {
        mToast(
            if (FreezeUtils.freezeApk(packageName))
                R.string.freeze_ok else R.string.freeze_err,
            context
        )
        getFreezeList()
    }

    fun unFreezePackage(packageName: String, context: Context) {
        mToast(
            if (FreezeUtils.unFreezeApk(packageName))
                R.string.unfreeze_ok else R.string.unfreeze_err,
            context
        )
        getFreezeList()
    }


    private fun getFreezeList() {
        if (_uiState.value.shizukuState == ShizukuState.RUNNING_AND_GRANTED) {
            _uiState.value = _uiState.value.copy(
                freezeList = FreezeUtils.getFreezeList()
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        removeBinderReceivedListener(binderReceivedListener)
        removeBinderDeadListener(binderDeadListener)
        removeRequestPermissionResultListener(requestPermissionResultListener)
    }
}

data class ShellUiState(
    val shizukuState: ShizukuState = ShizukuState.NOT_RUNNING,
    val shizukuVersion: String? = null,
    val isInstallApk: Boolean = false,
    val freezeList: List<String> = emptyList(),
    val error: String? = null
)

enum class ShizukuState {
    RUNNING_AND_GRANTED,
    RUNNING_BUT_NOT_GRANTED,
    RUNNING_BUT_LOW_VERSION,
    NOT_RUNNING,
}