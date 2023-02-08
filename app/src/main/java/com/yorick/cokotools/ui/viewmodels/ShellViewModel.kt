package com.yorick.cokotools.ui.viewmodels

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import rikka.shizuku.Shizuku.*

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
            } else {
                _uiState.value = _uiState.value.copy(
                    shizukuState = ShizukuState.RUNNING_BUT_NOT_GRANTED,
                )
            }
        }
    }

    private val binderDeadListener = OnBinderDeadListener {
        Log.d("yu111", "binderDeadListener:")
        _uiState.value = _uiState.value.copy(shizukuState = ShizukuState.NOT_RUNNING)
    }

    private val requestPermissionResultListener =
        OnRequestPermissionResultListener { _, grantResult: Int ->
            Log.d("yu111", "requestPermissionResultListener:$grantResult ")
            if (grantResult == PERMISSION_GRANTED) {
                _uiState.value = _uiState.value.copy(
                    shizukuState = ShizukuState.RUNNING_AND_GRANTED,
                    shizukuVersion = getVersion().toString()
                )
            }
        }

    init {
        addBinderReceivedListenerSticky(binderReceivedListener)
        addBinderDeadListener(binderDeadListener)
        addRequestPermissionResultListener(requestPermissionResultListener)
    }

    fun openShizuku(context: Context): Boolean {
        try {
            val intent = Intent(Intent.ACTION_MAIN)
            context.startActivity(
                intent.setClassName(
                    "moe.shizuku.privileged.api",
                    "moe.shizuku.manager.MainActivity"
                )
            )
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            return false
        }
        return true
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
    val error: String? = null
)

enum class ShizukuState {
    RUNNING_AND_GRANTED,
    RUNNING_BUT_NOT_GRANTED,
    RUNNING_BUT_LOW_VERSION,
    NOT_RUNNING,
}