package com.yorick.cokotools.util

import android.util.Log
import com.yorick.cokotools.util.shell.Shell
import com.yorick.cokotools.util.shell.ShizukuShell

object FreezeUtils {

    fun freezeApk(packageName: String): Boolean {
        val result: Shell.Result?
        try {
            val shell = ShizukuShell
            val command = Shell.Command("pm", "disable-user", packageName)
            result = shell.exec(command)
            Log.d("FreezeUtils", "freezeApk: $result")
            return result.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun unFreezeApk(packageName: String): Boolean {
        val result: Shell.Result?
        try {
            val shell = ShizukuShell
            val command = Shell.Command("pm", "enable", packageName)
            result = shell.exec(command)
            Log.d("FreezeUtils", "freezeApk: $result")
            return result.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun getFreezeList(): List<String> {
        val freezeList: MutableList<String> = emptyList<String>().toMutableList()
        try {
            val shell = ShizukuShell
            val command = Shell.Command("pm", "list", "packages", "-d")
            val result = shell.exec(command)
            for (i in result.out.split("\n")) {
                freezeList += i.substring(8)
            }
            Log.d("FreezeUtils", "freezeApk: $freezeList")
            return freezeList
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return freezeList
    }
}