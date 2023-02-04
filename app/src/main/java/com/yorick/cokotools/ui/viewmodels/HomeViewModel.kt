package com.yorick.cokotools.ui.viewmodels

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yorick.cokotools.data.model.Category
import com.yorick.cokotools.data.model.CategoryWithTools
import com.yorick.cokotools.data.model.Tool
import com.yorick.cokotools.data.network.ToolApi
import com.yorick.cokotools.data.repository.CategoryRepository
import com.yorick.cokotools.data.repository.ToolRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val toolRepository: ToolRepository,
    private val cateRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(loading = true))
    val uiState: StateFlow<HomeUiState> = _uiState

    var isSuccess by mutableStateOf(true)

    init {
        observeCategories()
        observeCategoryWithTools()
        observeTools()
        // 加载完成
        _uiState.value = _uiState.value.copy(loading = false)
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
            }
        }
    }

    fun addNewTool(tool: Tool, context: Context) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                toolRepository.addNewTool(tool)
            }
            successToast("添加成功", context = context)
        } catch (e: Exception) {
            e.printStackTrace()
            successToast("添加失败", context = context)
        }
    }

    fun downloadTool(tool: Tool): Boolean {
        // 本地查重
        if (_uiState.value.tools.any { it.name == tool.name && it.category == tool.category }) {
            viewModelScope.launch(Dispatchers.IO) {
                toolRepository.addNewTool(tool)
            }
            return true
        }
        return false
    }

    fun deleteTool(tool: Tool) {
        viewModelScope.launch(Dispatchers.IO) {
            toolRepository.deleteTool(tool)
        }
    }

    fun uploadTool(tool: Tool, context: Context) {
        // 远程查重
        if (_uiState.value.remoteTools.any { it.name == tool.name && it.category == tool.category }) {
            successToast("与云端仓库重复", context)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            var callBack = ""
            try {
                callBack = withContext(Dispatchers.IO) {
                    ToolApi.toolApiService.addNewTool(tool).string()
                }
                getAllRemoteTools()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                successToast(if (callBack == "Tool stored correctly") "上传成功" else "上传失败", context)
            }
        }
    }

    // 打开Activity
    fun startActivity(context: Context, packageName: String, activityName: String, okMsg: String?) {
        try {
            val intent = Intent()
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK 打开后是否返回原程序
            context.startActivity(
                intent.setClassName(packageName, activityName)
            )
            successToast(okMsg, context)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            isSuccess = false
        }
    }

    private fun successToast(okMsg: String?, context: Context) {
        if (okMsg != null && okMsg != "") {
            Toast.makeText(context, okMsg, Toast.LENGTH_SHORT).show()
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

