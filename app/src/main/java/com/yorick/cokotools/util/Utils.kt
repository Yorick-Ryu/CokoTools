package com.yorick.cokotools.util

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException

object Utils {
    fun isActivityExisting(context: Context, packageName: String, activityName: String): Boolean {
        try {
            val packageManager = context.packageManager
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            Class.forName(activityName)
        } catch (e: NameNotFoundException) {
            e.printStackTrace()
            return false
        }
        return true
    }
}