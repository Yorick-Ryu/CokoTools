package com.yorick.cokotools.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yorick.cokotools.R


object Utils {


    private fun openActivity(context: Context, packageName: String, activityName: String): Boolean {
        try {
            val intent = Intent()
            context.startActivity(intent.setClassName(packageName, activityName))
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    fun joinQQGroup(context: Context, key: String): Boolean {
        val intent = Intent()
        intent.data =
            Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key")
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            context.startActivity(intent)
            true
        } catch (e: java.lang.Exception) {
            // 未安装手Q或安装的版本不支持
            false
        }
    }

    fun toastUtil(context: Context, desc: String) {
        Toast.makeText(context, desc, Toast.LENGTH_LONG).show()
    }

    private fun getAppVersionName(context: Context, packageName: String): List<String> {
        val verList = mutableListOf<String>()
        try {
            val packageInfo = context.applicationContext
                .packageManager
                .getPackageInfo(packageName, 0)
            verList.add(packageInfo.versionName) //版本号名称
            verList.add(packageInfo.longVersionCode.toString())
            Log.d("yu", "当前版本：$verList")
        } catch (e: NameNotFoundException) {
            e.printStackTrace()
        }
        return verList
    }

    private fun copy(context: Context, content: String) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = ClipData.newPlainText("yu", content)
        cm.setPrimaryClip(text)
    }

    fun testVer(context: Context, packageName: String) {
        // 测试
        val verList = getAppVersionName(context, packageName)
        copy(context, verList.toString())
        toastUtil(context, "版本为${verList},已复制")
        // 测试
    }

    private fun mDialog(
        flag: Boolean,
        context: Context,
        msg: String
    ) {
        val resources = context.resources
        if (!flag) {
            MaterialAlertDialogBuilder(context)
                .setIcon(R.drawable.ic_logo)
                .setTitle(resources.getString(R.string.compose_needed))
                .setMessage(msg)
                .setNeutralButton(resources.getString(R.string.cancel)) { _, _ ->

                }
                .setNegativeButton(resources.getString(R.string.decline)) { _, _ ->
                }
                .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                    val uri: Uri = Uri.parse(resources.getString(R.string.help_doc))
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
                .show()
        }
    }

    fun jumpActivity(
        context: Context,
        packageName: String,
        activityName: String,
        msg: String = context.resources.getString(R.string.common_tips)
    ) {
        mDialog(openActivity(context, packageName, activityName), context, msg)
    }
}