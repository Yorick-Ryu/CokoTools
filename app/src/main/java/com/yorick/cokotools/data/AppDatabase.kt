package com.yorick.cokotools.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yorick.cokotools.data.dao.CategoryDao
import com.yorick.cokotools.data.dao.ContributorDao
import com.yorick.cokotools.data.dao.ToolDao
import com.yorick.cokotools.data.model.Category
import com.yorick.cokotools.data.model.Contributor
import com.yorick.cokotools.data.model.Tool
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
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val contributorDao = database.contributorDao()
                    val categoryDao = database.categoryDao()
                    val toolDao = database.toolDao()
                    contributorDao.addNewContributors(Contributor(1, "测试", 100.0))
                    categoryDao.addNewCategory(Category(1, "安卓通用", "安卓通用"))
                    toolDao.addNewTool(
                        Tool(
                            id = 1,
                            name = "状态栏显秒",
                            desc = "让状态栏显示秒数等状态栏其他相关配置",
                            category = 1,
                            tPackage = "com.android.systemui",
                            activity = "com.android.systemui.DemoMode",
                            okMsg = "点击[状态栏] 下滑找到[时间]"
                        )
                    )
                }
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