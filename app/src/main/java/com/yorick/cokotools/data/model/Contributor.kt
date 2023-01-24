package com.yorick.cokotools.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "CONTRIBUTORS", indices = [Index(value = ["NAME"], unique = true)])
data class Contributor(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "NAME") val name: String,
    @ColumnInfo(name = "AMOUNT") val amount: Double
)