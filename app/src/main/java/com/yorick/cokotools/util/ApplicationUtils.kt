package com.yorick.cokotools.util

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import java.lang.reflect.InvocationTargetException

object ApplicationUtils {
    var application: Application? = null
    val processName: String
        get() = if (Build.VERSION.SDK_INT >= 28) Application.getProcessName() else {
            try {
                @SuppressLint("PrivateApi") val activityThread =
                    Class.forName("android.app.ActivityThread")
                @SuppressLint("DiscouragedPrivateApi") val method =
                    activityThread.getDeclaredMethod("currentProcessName")
                method.invoke(null) as String
            } catch (e: ClassNotFoundException) {
                throw RuntimeException(e)
            } catch (e: NoSuchMethodException) {
                throw RuntimeException(e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException(e)
            } catch (e: InvocationTargetException) {
                throw RuntimeException(e)
            }
        }
}