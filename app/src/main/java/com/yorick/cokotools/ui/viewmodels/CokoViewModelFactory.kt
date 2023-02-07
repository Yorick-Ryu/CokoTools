package com.yorick.cokotools.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.yorick.cokotools.CokoApplication

@Suppress("UNCHECKED_CAST")
val CokoViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
        with(modelClass) {
            val application = checkNotNull(extras[APPLICATION_KEY]) as CokoApplication
            val toolRepository = application.toolRepository
            val categoryRepository = application.categoryRepository
            val contributorRepository = application.contributorRepository
            val userPreferencesRepository = application.userPreferencesRepository
            when {
                isAssignableFrom(HomeViewModel::class.java) -> {
                    HomeViewModel(toolRepository, categoryRepository)
                }
                isAssignableFrom(ContributorViewModel::class.java) -> {
                    ContributorViewModel(contributorRepository)
                }
                isAssignableFrom(SettingViewModel::class.java) -> {
                    SettingViewModel(userPreferencesRepository)
                }
                isAssignableFrom(ShellViewModel::class.java) -> {
                    ShellViewModel(application)
                }
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}