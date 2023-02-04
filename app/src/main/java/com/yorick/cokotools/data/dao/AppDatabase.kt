package com.yorick.cokotools.data.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yorick.cokotools.data.model.*
import com.yorick.cokotools.data.network.CategoryApi
import com.yorick.cokotools.data.network.ContributorApi
import com.yorick.cokotools.data.network.ToolApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Contributor::class, Category::class, Tool::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contributorDao(): ContributorDao
    abstract fun categoryDao(): CategoryDao
    abstract fun toolDao(): ToolDao

    private class AppDatabaseCallBack(
        private val scope: CoroutineScope
    ) : Callback() {
        // 首次打开应用填充数据
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    initCategories(database.categoryDao())
                    initTools(database.toolDao())
                    initContributor(database.contributorDao())
                }
            }
        }

        // 填充分类
        suspend fun initCategories(categoryDao: CategoryDao) {
            try {
                categoryDao.addNewCategory(
                    *CategoryApi.categoryApiService.getAllCategories().toTypedArray()
                )
            } catch (e: Exception) {
                e.printStackTrace()
                categoryDao.addNewCategory(*categories.toTypedArray())
            }
        }

        // 填充工具
        suspend fun initTools(toolDao: ToolDao) {
            try {
                toolDao.addNewTool(*ToolApi.toolApiService.getAllTools().filter { it.release }
                    .toTypedArray())
            } catch (e: Exception) {
                e.printStackTrace()
                toolDao.addNewTool(*tools.toTypedArray())
            }
        }

        // 填充赞助者
        suspend fun initContributor(contributorDao: ContributorDao) {
            try {
                contributorDao.addNewContributors(
                    *ContributorApi.contributorApiService.getAllContributors().toTypedArray()
                )
            } catch (e: Exception) {
                e.printStackTrace()
                contributorDao.addNewContributors(*contributors.toTypedArray())
            }
        }
    }

    // 单例
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getInstance(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database"
                )
//                    .createFromAsset("database/bus_schedule.db")
                    .addCallback(AppDatabaseCallBack(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}