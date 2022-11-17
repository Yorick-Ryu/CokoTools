package com.yorick.cokotools.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yorick.cokotools.R

object Utils {

    private fun openActivity(context: Context, packageName: String, activityName: String): Boolean {
        try {
            val intent = Intent()
            context.startActivity(intent.setClassName(packageName, activityName))
        } catch (e: ActivityNotFoundException) {
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

    fun openDoc(context: Context) {
        val uri: Uri = Uri.parse(context.resources.getString(R.string.help_doc))
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    fun toastUtil(context: Context, desc: String) {
        if (desc != "") {
            Toast.makeText(context, desc, Toast.LENGTH_LONG).show()
        }
    }

    private fun mDialog(
        flag: Boolean,
        context: Context,
        errMsg: String,
        okMsg: String
    ) {
        if (!flag) {
            baseDialog(context, errMsg)
        } else {
            toastUtil(context, okMsg)
        }
    }

    fun baseDialog(
        context: Context,
        msg: String,
        title: String = context.resources.getString(R.string.compose_needed),
        neutral: String = context.resources.getString(R.string.cancel),
        neutralCallback: () -> Unit = {},
        negative: String = context.resources.getString(R.string.decline),
        negativeCallback: () -> Unit = {},
        positive: String = context.resources.getString(R.string.accept),
        positiveCallback: () -> Unit = {
            openDoc(context)
        },
        cancelable: Boolean = true
    ) {
        MaterialAlertDialogBuilder(context)
            .setIcon(R.drawable.ic_logo)
            .setTitle(title)
            .setMessage(msg)
            .setNeutralButton(neutral) { _, _ ->
                neutralCallback()
            }
            .setNegativeButton(negative) { _, _ ->
                negativeCallback()
            }
            .setPositiveButton(positive) { _, _ ->
                positiveCallback()
            }.setCancelable(cancelable)
            .show()
    }

    fun jumpActivity(
        context: Context,
        packageName: String,
        activityName: String,
        errMsg: String = context.resources.getString(R.string.common_tips),
        okMsg: String = ""
    ) {
        mDialog(openActivity(context, packageName, activityName), context, errMsg, okMsg)
    }
}