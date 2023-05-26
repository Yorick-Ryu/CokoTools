package com.yorick.cokotools.data.repository

import com.yorick.cokotools.data.model.Category
import com.yorick.cokotools.data.model.CategoryWithTools
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun getAllCategory(): Flow<List<Category>>
    suspend fun getAllCategoryWithTools(): Flow<List<CategoryWithTools>>
    suspend fun addNewCategory(vararg categories: Category)
    suspend fun deleteCategory(vararg categories: Category)
    suspend fun getCategoryFromRemote(): List<Category>
}