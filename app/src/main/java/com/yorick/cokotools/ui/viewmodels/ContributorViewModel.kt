package com.yorick.cokotools.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yorick.cokotools.data.model.Contributor
import com.yorick.cokotools.data.repository.ContributorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class ContributorViewModel(
    private val contributorRepository: ContributorRepository
) : ViewModel() {
    var contributors by mutableStateOf(emptyList<Contributor>())

    init {
        getLatestContributor()
        observeContributors()
    }

    // 从本地数据库读取数据
    private fun observeContributors() {
        viewModelScope.launch {
            contributorRepository.getAllContributors().collect {
                contributors = it
            }
        }
    }

    // 获取网络数据库并更新本地数据库
    private fun getLatestContributor() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                contributorRepository.addNewContributors(*(contributorRepository.getContributorsFromRemote()).toTypedArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}