package com.yorick.cokotools.data.repository

import com.yorick.cokotools.data.model.Tool
import kotlinx.coroutines.flow.Flow

interface ToolRepository {
    suspend fun getAllTools(): Flow<List<Tool>>
    suspend fun addNewTool(tool: Tool)
    suspend fun updateTool(tool: Tool)
    suspend fun deleteTool(tool: Tool)
}