package com.yorick.cokotools.data.repository

import com.yorick.cokotools.data.dao.ToolDao
import com.yorick.cokotools.data.model.Tool
import com.yorick.cokotools.data.network.ToolApi
import kotlinx.coroutines.flow.Flow

class ToolRepositoryImpl(
    private val toolDao: ToolDao
) : ToolRepository {
    override suspend fun getAllTools(): Flow<List<Tool>> = toolDao.allTools()

    override suspend fun addNewTool(vararg tools: Tool) = toolDao.addNewTool(*tools)

    override suspend fun updateTool(vararg tools: Tool) = toolDao.updateTool(*tools)

    override suspend fun deleteTool(vararg tools: Tool) = toolDao.deleteTool(*tools)
    override suspend fun getToolsFromRemote(): List<Tool> = ToolApi.toolApiService.getAllTools()
}