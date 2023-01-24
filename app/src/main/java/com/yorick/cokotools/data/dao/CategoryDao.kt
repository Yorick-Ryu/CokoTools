package com.yorick.cokotools.data.dao

import androidx.room.*
import com.yorick.cokotools.data.model.Category
import com.yorick.cokotools.data.model.CategoryWithTools
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM CATEGORIES")
    fun allCategories(): Flow<List<Category>>

    @Transaction
    @Query("SELECT * FROM CATEGORIES")
    fun allCategoryWithTools(): Flow<List<CategoryWithTools>>

    @Query("SELECT * FROM CATEGORIES WHERE categoryId = :id")
    fun category(id: Int): Flow<Category?>

    // 增加分类，冲突则替换
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNewCategory(category: Category)

    @Delete
    fun deleteCategory(category: Category)
}