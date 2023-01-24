//package com.yorick.cokotools.data
//
//import android.icu.lang.UScript.COMMON
//import android.os.Build
//import com.yorick.cokotools.R
//import com.yorick.cokotools.util.Utils
//
//data class Tool(
//    val toolName: String,
//    val toolDesc: String,
//    val toolClass: String,
//    val toolPackage: String = "com.android.settings",
//    val toolActivity: String,
//    val okMsg: String = "",
//    val errMsg: String = "",
//)
//
//val commonTools = listOf(
//    Tool(
//        toolName = "状态栏显秒",
//        toolDesc = "可以让状态栏显示秒数,点击状态栏下滑找到时间。",
//        toolClass = COMMON,
//        toolAction = { context ->
//            Utils.jumpActivity(
//                context,
//                R.string.show_seconds_package,
//                R.string.show_seconds_activity,
//                okMsg = R.string.show_seconds_tips
//            )
//        }
//    ),
//    Tool(
//        toolName = R.string.fuel_summary,
//        toolDesc = R.string.fuel_summary_tips,
//        toolAction = { context ->
//            Utils.jumpActivity(
//                context,
//                R.string.fuel_summary_package,
//                R.string.fuel_summary_activity,
//                okMsg = R.string.fuel_summary_tips
//            )
//        }
//    ),
//    Tool(
//        toolName = R.string.zen_mode,
//        toolDesc = R.string.zen_mode_tips,
//        toolAction = { context ->
//            Utils.jumpActivity(
//                context,
//                R.string.zen_mode_package,
//                R.string.zen_mode_activity,
//            )
//        }
//    ),
//    Tool(
//        toolName = R.string.storage_dashboard,
//        toolDesc = R.string.storage_dashboard_tips,
//        toolAction = { context ->
//            Utils.jumpActivity(
//                context,
//                R.string.fuel_summary_package,
//                R.string.storage_dashboard_activity,
//            )
//        }
//    ),
//    Tool(
//        toolName = R.string.show_wifi_keys,
//        toolDesc = R.string.show_wifi_key_tips,
//        toolAction = { context ->
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                Utils.baseDialog(
//                    context,
//                    title = R.string.title_tip,
//                    msg = R.string.msg_high_version,
//                    neutral = R.string.open_wifi_list,
//                    neutralCallback = {
//                        Utils.jumpActivity(
//                            context,
//                            R.string.show_wifi_keys_package,
//                            R.string.show_wifi_list_activity,
//                        )
//                    },
//                    positiveCallback = {},
//                    negative = ""
//                )
//            } else {
//                Utils.jumpActivity(
//                    context,
//                    R.string.show_wifi_keys_package,
//                    R.string.show_wifi_keys_activity,
//                    R.string.show_wifi_key_tips,
//                )
//            }
//        }
//    ),
//    Tool(
//        toolName = R.string.urls_manage,
//        toolDesc = R.string.urls_manage_tips,
//        toolAction = { context ->
//            Utils.jumpActivity(
//                context,
//                R.string.fuel_summary_package,
//                R.string.urls_manage_activity,
//            )
//        }
//    ),
//    Tool(
//        toolName = R.string.high_power,
//        toolDesc = R.string.high_power_tips,
//        toolAction = { context ->
//            Utils.jumpActivity(
//                context,
//                R.string.fuel_summary_package,
//                R.string.high_power_activity,
//            )
//        }
//    ),
//)
//
//val blueTools = listOf(
//    Tool(
//        toolName = R.string.lock_bands,
//        toolDesc = R.string.lock_bands_tips,
//        toolAction = { context ->
//            Utils.jumpActivity(
//                context,
//                R.string.lock_bands_package,
//                R.string.lock_bands_activity,
//                R.string.download_network_state,
//            )
//        }
//    ),
//    Tool(
//        toolName = R.string.max_charging,
//        toolDesc = R.string.max_charging_tips,
//        toolAction = { context ->
//            Utils.jumpActivity(
//                context,
//                R.string.max_charging_package,
//                R.string.max_charging_activity,
//                R.string.download_fuel_summary,
//                R.string.max_charging_tips
//            )
//        }
//    ),
//    Tool(
//        toolName = R.string.engineer_mode,
//        toolDesc = R.string.engineer_mode_tips,
//        toolAction = { context ->
//            Utils.jumpActivity(
//                context,
//                R.string.engineer_mode_package,
//                R.string.engineer_mode_activity,
//                R.string.download_engineer_model,
//            )
//        }
//    ),
//    Tool(
//        // TODO need testing
//        toolName = R.string.sub_setting,
//        toolDesc = R.string.sub_setting_tips,
//        toolAction = { context ->
//            Utils.jumpActivity(
//                context,
//                R.string.fuel_summary_package,
//                R.string.sub_setting_package,
//            )
//        }
//    ),
//)