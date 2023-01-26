package com.yorick.cokotools.data.model

import androidx.room.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "CATEGORIES")
data class Category(
    @SerialName("id")
    @PrimaryKey
    val categoryId: Int,
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

val categories = listOf(
    Category(1, "安卓通用", "调用原生安卓设置，只要系统没被厂家魔改就可以使用。"),
    Category(2, "OriginOS", "OriginOS专属黑科技功能，使用前请下载安装所需组件。"),
    Category(3, "ColorOS", "ColorOS专属黑科技功能，使用前请下载安装所需组件。"),
    Category(4, "MIUI", "MIUI专属黑科技功能，使用前请下载安装所需组件。"),
    Category(5, "Flyme", "Flyme专属黑科技功能，使用前请下载安装所需组件。"),
    Category(6, "OneUI", "OneUI专属黑科技功能，使用前请下载安装所需组件。"),
)