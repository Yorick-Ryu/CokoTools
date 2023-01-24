package com.yorick.cokotools.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "CATEGORIES")
data class Category(
    @PrimaryKey val categoryId: Int,
    @ColumnInfo(name = "NAME") val name: String,
    @ColumnInfo(name = "DESC") val desc: String? = "暂无描述"
)

data class CategoryWithTools(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "CATEGORY"
    )
    val tools: List<Tool>
)
