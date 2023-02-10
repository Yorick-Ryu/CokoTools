package com.yorick.cokotools.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yorick.cokotools.R
import com.yorick.cokotools.data.datastore.UserPreferencesRepository
import com.yorick.cokotools.data.model.Category
import com.yorick.cokotools.data.model.CategoryWithTools
import com.yorick.cokotools.data.model.Tool
import com.yorick.cokotools.data.network.ToolApi
import com.yorick.cokotools.data.repository.CategoryRepository
import com.yorick.cokotools.data.repository.ToolRepository
import com.yorick.cokotools.util.Utils
import com.yorick.cokotools.util.Utils.mToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val toolRepository: ToolRepository,
    private val cateRepository: CategoryRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    companion object {
        const val CALL_BACK_SUCCESS = "Tool stored correctly"
    }

    private val _uiState = MutableStateFlow(HomeUiState(loading = true))
    val uiState: StateFlow<HomeUiState> = _uiState

    var isSuccess by mutableStateOf(true)

    private var okToast: Boolean = true

    init {
        observeUserPreferences()
        observeCategories()
        observeCategoryWithTools()
        observeTools()
        // 加载完成
        _uiState.value = _uiState.value.copy(loading = false)
    }

    private fun observeUserPreferences() {
        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow.collect { okToast = it.okToast }
        }
    }

    private fun observeTools() {
        viewModelScope.launch {
            toolRepository.getAllTools().catch { ex ->
                _uiState.value = HomeUiState(error = ex.message)
            }.collect {
                _uiState.value = _uiState.value.copy(
                    tools = it
                )
            }
        }
    }

    private fun observeCategories() {
        viewModelScope.launch {
            cateRepository.getAllCategory().catch { ex ->
                _uiState.value = HomeUiState(error = ex.message)
            }.collect {
                _uiState.value = _uiState.value.copy(
                    categories = it
                )
            }
        }
    }

    private fun observeCategoryWithTools() {
        viewModelScope.launch {
            cateRepository.getAllCategoryWithTools().catch { ex ->
                _uiState.value = HomeUiState(error = ex.message)
            }.collect {
                _uiState.value = _uiState.value.copy(
                    categoryWithTools = it
                )
            }
        }
    }

    fun getAllRemoteTools() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    remoteTools = ToolApi.toolApiService.getAllTools(),
                    showTools = ToolApi.toolApiService.getAllTools().filter { it.release }
                )
            } catch (ex: Exception) {
                _uiState.value = HomeUiState(error = ex.message)
                // 失败了要重新读取数据
                observeCategories()
                observeCategoryWithTools()
                observeTools()
            }
        }
    }

    fun addNewTool(tool: Tool, context: Context) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                toolRepository.addNewTool(tool)
            }
            mToast(R.string.add_success, context = context)
        } catch (e: Exception) {
            e.printStackTrace()
            mToast(R.string.add_err, context = context)
        }
    }

    fun downloadTool(tool: Tool, context: Context) {
        // 本地查重
        if (_uiState.value.tools.any { it.name == tool.name && it.category == tool.category }) {
            viewModelScope.launch(Dispatchers.IO) {
                toolRepository.addNewTool(tool)
            }
            mToast(R.string.download_success, context)
        }
        mToast(R.string.download_redundant, context)
    }

    fun deleteTool(tool: Tool) {
        viewModelScope.launch(Dispatchers.IO) {
            toolRepository.deleteTool(tool)
        }
    }

    fun uploadTool(tool: Tool, context: Context) {
        // 远程查重
        if (_uiState.value.remoteTools.any { it.name == tool.name && it.category == tool.category }) {
            mToast(R.string.upload_redundant, context)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            var callBack: String? = null
            try {
                callBack = withContext(Dispatchers.IO) {
                    ToolApi.toolApiService.addNewTool(tool).string()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                mToast(
                    if (callBack == CALL_BACK_SUCCESS) R.string.upload_success else R.string.upload_err,
                    context
                )
            }
        }
    }

    // 打开Activity
    fun startActivity(context: Context, packageName: String, activityName: String, okMsg: String?) {
        if (Utils.startActivity(context, packageName, activityName)) {
            if (okToast) mToast(okMsg, context)
        } else {
            isSuccess = false
        }
    }

    fun closeErrorDialog() {
        isSuccess = true
    }
}

data class HomeUiState(
    val categories: List<Category> = emptyList(),
    val tools: List<Tool> = emptyList(),
    val remoteTools: List<Tool> = emptyList(),
    val showTools: List<Tool> = emptyList(),
    val categoryWithTools: List<CategoryWithTools> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

