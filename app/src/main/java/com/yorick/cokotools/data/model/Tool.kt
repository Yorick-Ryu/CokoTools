package com.yorick.cokotools.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
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

val tools = listOf(
    Tool(
        1,
        name = "状态栏显秒",
        desc = "让状态栏显示秒数等状态栏其他相关配置",
        category = 1,
        tPackage = "com.android.systemui",
        activity = "com.android.systemui.DemoMode",
        okMsg = "点击[状态栏] 下滑找到[时间]"
    ),
    Tool(
        2,
        name = "电量统计",
        desc = "原生安卓的电量统计，有曲线图。",
        category = 1,
        tPackage = "com.android.settings",
        activity = "com.android.settings.Settings\$PowerUsageSummaryActivity",
        okMsg = "请点击右上角三个点或者电池用量"
    ),
    Tool(
        3,
        name = "勿扰模式",
        desc = "原生安卓的勿扰模式",
        category = 1,
        tPackage = "com.android.settings",
        activity = "com.android.settings.Settings\$ZenModeSettingsActivity",
    ),
    Tool(
        4,
        name = "原生存储",
        desc = "原生安卓的存储",
        category = 1,
        tPackage = "com.android.settings",
        activity = "com.android.settings.Settings\$StorageDashboardActivity",
    ),
    Tool(
        5,
        name = "WIFI密码查看",
        desc = "查看已保存的WIFI密码，不支持Android12。",
        category = 1,
        tPackage = "com.android.settings",
        activity = "com.android.settings.Settings\$WifiSettingsActivity",
        okMsg = "点击需要查看密码的WiFi 点击[分享]后触摸指纹传感器"
    ),
    Tool(
        6,
        name = "链接管理",
        desc = "设置链接跳转到对应的应用界面",
        category = 1,
        tPackage = "com.android.settings",
        activity = "com.android.settings.Settings\$ManageDomainUrlsActivity",
        okMsg = "点击要查看密码的WiFi 点击[分享]后触摸指纹传感器"
    ),
    Tool(
        7,
        name = "电池优化",
        desc = "应用耗电管理",
        category = 1,
        tPackage = "com.android.settings",
        activity = "com.android.settings.Settings\$HighPowerApplicationsActivity",
    ),
    Tool(
        8,
        name = "锁频段",
        desc = "通过手动锁定Band来提高某些场景下的网络速度。需要NetworkState组件！",
        category = 2,
        tPackage = "com.vivo.networkstate",
        activity = "com.vivo.networkstate.MainActivity",
        errMsg = "缺少NetworkState组件或者组件版本不兼容"
    ),
    Tool(
        9,
        name = "满血充电",
        desc = "调用工厂调试模式，实现高功率充电，谨慎使用！需要FuelSummary组件，部分机型无效。",
        category = 2,
        tPackage = "com.vivo.fuelsummary",
        activity = "com.vivo.fuelsummary.FuelSummary",
        okMsg = "点击[循环充电] 弹出[记录频率设置]点确定 插充电器 点击开始",
        errMsg = "缺少FuelSummary组件或者组件版本不兼容"
    ),
    Tool(
        10,
        name = "工厂测试",
        desc = "隐藏的出厂测试，可以修改系统的一些参数，谨慎使用！需要工厂测试组件",
        category = 2,
        tPackage = "com.iqoo.engineermode",
        activity = "com.iqoo.engineermode.EngineerMode",
        errMsg = "缺少工厂测试组件或者组件版本不兼容"
    ),
    Tool(
        11,
        name = "原生设置",
        desc = "打开安卓原生设置，系统要求2.0及以上",
        category = 2,
        tPackage = "com.android.settings",
        activity = "com.vivo.settings.VivoSubSettings",
    )
)