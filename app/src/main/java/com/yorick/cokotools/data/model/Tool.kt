package com.yorick.cokotools.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TOOLS")
data class Tool(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "NAME") val name: String,
    @ColumnInfo(name = "DESC") val desc: String? = "暂无描述",
    @ColumnInfo(name = "CATEGORY") val category: Int? = 1,
    @ColumnInfo(name = "TPACKAGE") val tPackage: String = "com.android.settings",
    @ColumnInfo(name = "ACTIVITY") val activity: String,
    @ColumnInfo(name = "OKMSG") val okMsg: String? = "",
    @ColumnInfo(name = "ERRMSG") val errMsg: String? = "这个功能可能不兼容你的设备，详情请查看文档。",
    @ColumnInfo(name = "RELEASE") val release: Boolean = false, // 是否发布，默认为否
)
