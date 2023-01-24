package com.yorick.cokotools.data.dao

import androidx.room.*
import com.yorick.cokotools.data.model.Contributor
import kotlinx.coroutines.flow.Flow

@Dao
interface ContributorDao {
    @Query("SELECT * FROM CONTRIBUTORS")
    fun allContributors(): Flow<List<Contributor>>

    @Query("SELECT * FROM CONTRIBUTORS WHERE id = :id")
    fun contributor(id: Int): Flow<Contributor?>

    // 增加贡献者，冲突则替换
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNewContributors(vararg contributors: Contributor)

    @Delete
    fun deleteContributors(vararg contributors: Contributor)
}