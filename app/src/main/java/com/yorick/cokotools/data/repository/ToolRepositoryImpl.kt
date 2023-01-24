package com.yorick.cokotools.data.repository

import com.yorick.cokotools.data.dao.ToolDao
import com.yorick.cokotools.data.model.Tool
import kotlinx.coroutines.flow.Flow

class ToolRepositoryImpl(
    private val toolDao: ToolDao
) : ToolRepository {
    override suspend fun getAllTools(): Flow<List<Tool>> = toolDao.allTools()

    override suspend fun addNewTool(tool: Tool) = toolDao.addNewTool(tool)

    override suspend fun updateTool(tool: Tool) = toolDao.updateTool(tool)

    override suspend fun deleteTool(tool: Tool) = toolDao.deleteTool(tool)
}