package com.yorick.cokotools.data.repository

import com.yorick.cokotools.data.dao.CategoryDao
import com.yorick.cokotools.data.model.Category
import com.yorick.cokotools.data.model.CategoryWithTools
import kotlinx.coroutines.flow.Flow

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryRepository {
    override suspend fun getAllCategory(): Flow<List<Category>> = categoryDao.allCategories()

    override suspend fun getAllCategoryWithTools(): Flow<List<CategoryWithTools>> =
        categoryDao.allCategoryWithTools()

    override suspend fun addNewCategory(category: Category) = categoryDao.addNewCategory(category)

    override suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)
}