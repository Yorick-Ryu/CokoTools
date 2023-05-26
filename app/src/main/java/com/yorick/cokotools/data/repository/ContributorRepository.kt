package com.yorick.cokotools.data.repository

import com.yorick.cokotools.data.model.Contributor
import kotlinx.coroutines.flow.Flow

interface ContributorRepository {
    suspend fun getAllContributors(): Flow<List<Contributor>>
    suspend fun addNewContributors(vararg contributors: Contributor)
    suspend fun deleteContributors(vararg contributors: Contributor)
    suspend fun getContributorsFromRemote(): List<Contributor>
}