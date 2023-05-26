package com.yorick.cokotools.data.repository

import com.yorick.cokotools.data.model.Tool
import kotlinx.coroutines.flow.Flow

interface ToolRepository {
    suspend fun getAllTools(): Flow<List<Tool>>
    suspend fun addNewTool(vararg tools: Tool)
    suspend fun updateTool(vararg tools: Tool)
    suspend fun deleteTool(vararg tools: Tool)
    suspend fun getToolsFromRemote(): List<Tool>
}