package com.yorick.cokotools

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.yorick.cokotools.data.dao.AppDatabase
import com.yorick.cokotools.data.datastore.UserPreferences
import com.yorick.cokotools.data.datastore.UserPreferencesRepository
import com.yorick.cokotools.data.datastore.UserPreferencesSerializer
import com.yorick.cokotools.data.repository.CategoryRepositoryImpl
import com.yorick.cokotools.data.repository.ContributorRepositoryImpl
import com.yorick.cokotools.data.repository.ToolRepositoryImpl
import com.yorick.cokotools.util.ApplicationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.lsposed.hiddenapibypass.HiddenApiBypass

private const val DATA_STORE_FILE_NAME = "user_prefs.pb"

private val Context.userPreferencesStore: DataStore<UserPreferences> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = UserPreferencesSerializer
)

class CokoApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val db by lazy { AppDatabase.getInstance(this, applicationScope) }
    val toolRepository by lazy { ToolRepositoryImpl(db.toolDao()) }
    val categoryRepository by lazy { CategoryRepositoryImpl(db.categoryDao()) }
    val contributorRepository by lazy { ContributorRepositoryImpl(db.contributorDao()) }
    val userPreferencesRepository by lazy { UserPreferencesRepository(userPreferencesStore) }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.addHiddenApiExemptions("L")
        }
        ApplicationUtils.application = this
    }
}