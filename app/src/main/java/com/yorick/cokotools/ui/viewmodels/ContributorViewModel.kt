package com.yorick.cokotools.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yorick.cokotools.data.model.Contributor
import com.yorick.cokotools.data.repository.ContributorRepository
import kotlinx.coroutines.launch

class ContributorViewModel(
    private val contributorRepository: ContributorRepository
) : ViewModel() {
    var contributors by mutableStateOf(emptyList<Contributor>())

    init {
        observeContributors()
    }

    private fun observeContributors() {
        viewModelScope.launch {
            contributorRepository.getAllContributors().collect {
                contributors = it
            }
        }
    }
}