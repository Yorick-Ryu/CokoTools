package com.yorick.cokotools.data.dao

import androidx.room.*
import com.yorick.cokotools.data.model.Tool
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolDao {
    @Query("SELECT * FROM TOOLS")
    fun allTools(): Flow<List<Tool>>

    @Query("SELECT * FROM TOOLS WHERE id = :id")
    fun tool(id: Int): Flow<Tool?>

    @Update
    fun updateTool(tool: Tool)

    // 增加工具，冲突则替换
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNewTool(tool: Tool)

    @Delete
    fun deleteTool(tool: Tool)
}