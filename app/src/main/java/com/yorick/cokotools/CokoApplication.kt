package com.yorick.cokotools

import android.app.Application
import com.yorick.cokotools.data.dao.AppDatabase
import com.yorick.cokotools.data.repository.CategoryRepositoryImpl
import com.yorick.cokotools.data.repository.ContributorRepositoryImpl
import com.yorick.cokotools.data.repository.ToolRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class CokoApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val db by lazy { AppDatabase.getInstance(this, applicationScope) }
    val toolRepository by lazy { ToolRepositoryImpl(db.toolDao()) }
    val categoryRepository by lazy { CategoryRepositoryImpl(db.categoryDao()) }
    val contributorRepository by lazy { ContributorRepositoryImpl(db.contributorDao()) }
}