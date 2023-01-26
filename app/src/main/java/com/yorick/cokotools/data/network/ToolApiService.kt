package com.yorick.cokotools.data.network

import com.yorick.cokotools.data.model.Tool
import retrofit2.http.GET

interface ToolApiService {
    @GET("tool")
    suspend fun getAllTools(): List<Tool>
}

object ToolApi {
    val toolApiService: ToolApiService by lazy {
        retrofit.create(ToolApiService::class.java)
    }
}