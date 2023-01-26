package com.yorick.cokotools.data.network

import com.yorick.cokotools.data.model.Category
import retrofit2.http.GET

interface CategoryApiService {
    @GET("category")
    suspend fun getAllCategories(): List<Category>
}

object CategoryApi {
    val categoryApiService: CategoryApiService by lazy {
        retrofit.create(CategoryApiService::class.java)
    }
}