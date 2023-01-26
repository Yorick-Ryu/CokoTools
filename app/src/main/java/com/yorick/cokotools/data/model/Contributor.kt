package com.yorick.cokotools.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "CONTRIBUTORS", indices = [Index(value = ["NAME"], unique = true)])
data class Contributor(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "NAME") val name: String,
    @ColumnInfo(name = "AMOUNT") val amount: Double
)

// 本地数据，当网络连接失败时写入
val contributors = listOf(
    Contributor(
        id = 1,
        name = "张三",
        amount = 0.0,
    ),
    Contributor(
        id = 2,
        name = "小影",
        amount = 5.0,
    ),
    Contributor(
        id = 3,
        name = "小",
        amount = 1.0,
    ),
    Contributor(
        id = 4,
        name = "水清墨韵",
        amount = 6.0,
    ),
    Contributor(
        id = 5,
        name = "贤狼赫萝",
        amount = 3.0,
    ),
    Contributor(
        id = 6,
        name = "小陌",
        amount = 0.5,
    ),
    Contributor(
        id = 7,
        name = "此女子不温柔不体贴",
        amount = 3.0,
    ),
    Contributor(
        id = 8,
        name = "小恒",
        amount = 1.0,
    ),
    Contributor(
        id = 9,
        name = "无中生友",
        amount = 10.0,
    ),
    Contributor(
        id = 10,
        name = "6GHz",
        amount = 10.0,
    ),
    Contributor(
        id = 11,
        name = "铜锣烧",
        amount = 4.0,
    ),
    Contributor(
        id = 12,
        name = "黄琼海",
        amount = 10.0,
    ),
    Contributor(
        id = 13,
        name = "荞汐不摆烂",
        amount = 6.0,
    ),
    Contributor(
        id = 14,
        name = "自由风行者",
        amount = 6.0,
    ),
    Contributor(
        id = 15,
        name = "喻嘉乐",
        amount = 0.1,
    ),
    Contributor(
        id = 16,
        name = "雒韦超",
        amount = 1.0,
    ),
    Contributor(
        id = 17,
        name = "轩",
        amount = 3.0,
    ),
    Contributor(
        id = 18,
        name = "独角兽HMSUnicorn",
        amount = 3.0,
    ),
    Contributor(
        id = 19,
        name = "剑不jian很尖",
        amount = 10.0,
    ),
    Contributor(
        id = 20,
        name = "匿名",
        amount = 2.0 + 1.0 + 20.0 + 3.0 + 0.11 + 0.01,
    ),
)