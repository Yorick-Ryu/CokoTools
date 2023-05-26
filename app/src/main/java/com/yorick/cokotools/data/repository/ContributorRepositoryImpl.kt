package com.yorick.cokotools.data.repository

import com.yorick.cokotools.data.dao.ContributorDao
import com.yorick.cokotools.data.model.Contributor
import com.yorick.cokotools.data.network.ContributorApi
import kotlinx.coroutines.flow.Flow

class ContributorRepositoryImpl(
    private val contributorDao: ContributorDao
) : ContributorRepository {
    override suspend fun getAllContributors(): Flow<List<Contributor>> =
        contributorDao.allContributors()

    override suspend fun addNewContributors(vararg contributors: Contributor) =
        contributorDao.addNewContributors(*contributors)

    override suspend fun deleteContributors(vararg contributors: Contributor) =
        contributorDao.deleteContributors(*contributors)

    override suspend fun getContributorsFromRemote() =
        ContributorApi.contributorApiService.getAllContributors()
}