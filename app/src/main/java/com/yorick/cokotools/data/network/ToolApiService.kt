package com.yorick.cokotools.data.network

import com.yorick.cokotools.data.model.Tool
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ToolApiService {
    @GET("tool")
    suspend fun getAllTools(): List<Tool>

    @POST("tool")
    suspend fun addNewTool(@Body tool: Tool): ResponseBody
}

object ToolApi {
    val toolApiService: ToolApiService by lazy {
        retrofit.create(ToolApiService::class.java)
    }
}