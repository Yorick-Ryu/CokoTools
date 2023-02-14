package com.yorick.cokotools.util

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter

object ResultUtil {
    private const val TAG = "ResultUtil"
    fun throwableToString(throwable: Throwable): String {
        val sw = StringWriter(1024)
        val pw = PrintWriter(sw)
        throwable.printStackTrace(pw)
        pw.close()
        return sw.toString()
    }

    @get:SuppressLint("PrivateApi")
    private val systemProperty: String?
        get() = try {
            Class.forName("android.os.SystemProperties")
                .getDeclaredMethod("get", String::class.java)
                .invoke(null, "ro.miui.ui.version.name") as String
        } catch (e: Exception) {
            Log.w("SAIUtils", "Unable to use SystemProperties.get", e)
            null
        }
    val isMiui: Boolean
        get() = !TextUtils.isEmpty(systemProperty)
}