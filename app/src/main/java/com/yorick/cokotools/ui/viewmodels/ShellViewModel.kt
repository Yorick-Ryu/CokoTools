package com.yorick.cokotools.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ShellViewModel : ViewModel() {
    private val _uiState =
        MutableStateFlow(ShellUiState(shizukuState = ShizukuState.RUNNING_AND_CONNECTING))
    val uiState: StateFlow<ShellUiState> = _uiState
}

data class ShellUiState(
    val shizukuState: ShizukuState = ShizukuState.NOT_RUNNING,
    val shizukuVersion: String = "12.12",
    val shizukuMode: String = ShizukuMode.ROOT
)

object ShizukuMode {
    const val ROOT = "Root"
    const val ADB = "ADB"
}

enum class ShizukuState {
    RUNNING_AND_CONNECTING,
    RUNNING_BUT_NOT_CONNECTING,
    NOT_RUNNING,
}