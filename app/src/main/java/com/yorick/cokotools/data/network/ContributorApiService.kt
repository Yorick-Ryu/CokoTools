package com.yorick.cokotools.data.network

import com.yorick.cokotools.data.model.Contributor
import retrofit2.http.GET

interface ContributorApiService {
    @GET("contributors.json")
    suspend fun getAllContributors(): List<Contributor>
}

object ContributorApi {
    val contributorApiService: ContributorApiService by lazy {
        retrofit.create(ContributorApiService::class.java)
    }
}

