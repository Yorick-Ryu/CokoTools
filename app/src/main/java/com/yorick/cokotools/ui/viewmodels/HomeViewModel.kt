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

    private fun addNewTool(tool: Tool) {
        viewModelScope.launch {
            toolRepository.addNewTool(tool)
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
                remoteTools = ToolApi.toolApiService.getAllTools().filter { !it.release }
                println(remoteTools.size)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun uploadTool(tool: Tool): Boolean {
        var ctool: Tool? = null
        viewModelScope.launch {
            try {
                ctool = ToolApi.toolApiService.addNewTool(tool)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return ctool != null
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
            Toast.makeText(context, okMsg, Toast.LENGTH_LONG).show()
        }
    }

    fun closeErrorDialog() {
        isSuccess = true
    }
}