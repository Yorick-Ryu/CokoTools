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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val toolRepository: ToolRepository,
    private val cateRepository: CategoryRepository
) : ViewModel() {

    var tools by mutableStateOf(emptyList<Tool>())
    var remoteTools by mutableStateOf(emptyList<Tool>())
    var categoryWithTools by mutableStateOf(emptyList<CategoryWithTools>())
    var categories by mutableStateOf(emptyList<Category>())
    var isSuccess by mutableStateOf(true)

    init {
        observeCategories()
        observeCategoryWithTools()
        observeTools()
    }

    private fun observeTools() {
        viewModelScope.launch {
            toolRepository.getAllTools().collect {
                tools = it
            }
        }
    }

    private fun observeCategories() {
        viewModelScope.launch {
            cateRepository.getAllCategory().collect {
                categories = it
            }
        }
    }

    private fun observeCategoryWithTools() {
        viewModelScope.launch {
            cateRepository.getAllCategoryWithTools().collect {
                categoryWithTools = it
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
        val sameTools = tools.filter { it.name == tool.name && it.category == tool.category }
        // 本地查重
        if (sameTools.isEmpty()) {
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

    fun getAllRemoteTools() {
        viewModelScope.launch {
            try {
                remoteTools = ToolApi.toolApiService.getAllTools().filter { it.release }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun uploadTool(tool: Tool, context: Context) {
        // 远程查重
        if (remoteTools.any { it.name == tool.name && it.category == tool.category }) {
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