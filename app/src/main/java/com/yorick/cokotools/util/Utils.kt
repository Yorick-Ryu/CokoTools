package com.yorick.cokotools.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.tencent.bugly.crashreport.CrashReport
import com.yorick.cokotools.R
import java.net.URLEncoder

object Utils {

    private const val QQ_GROUP_URL =
        "mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D"
    private const val QQ_GROUP_KEY = "lFuzgAHN-Q_4j7fodzBaOtKrc_q6NYg9"
    private const val SHIZUKU_PACKAGE = "moe.shizuku.privileged.api"
    private const val SHIZUKU_ACTIVITY = "moe.shizuku.manager.MainActivity"
    private const val REGEX_MATCH_PACKAGE = "^([A-Za-z]{1}[A-Za-z\\d_]*\\.)+[A-Za-z][A-Za-z\\d_]*\$"
    const val HELP_DOC_URL = "https://yorick.love/2023/02/11/Project/CookTools-doc/"
    const val ALIPAY_DONATE_URL = "https://qr.alipay.com/fkx17875qbw3mypdlenhee2"
    const val COOLAPK_URL = "http://www.coolapk.com/u/3774603"
    const val DONATE_CODE_URL = "https://yorick.love/img/qrcode/wechat_donate_code.png"
    const val BLOG_URL = "https://yorick.love"
    const val OPEN_SOURCE_URL = "https://github.com/Yorick-Ryu/CokoTools"
    const val SHIZUKU_DOWNLOAD_URL = "https://shizuku.rikka.app/zh-hans/download/"
    const val SHIZUKU_INTRODUCTION_URL = "https://shizuku.rikka.app/zh-hans/introduction/"

    fun joinQQGroup(context: Context): Boolean {
        val intent = Intent()
        intent.data =
            Uri.parse(QQ_GROUP_URL + QQ_GROUP_KEY)
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            context.startActivity(intent)
            true
        } catch (e: java.lang.Exception) {
            // 未安装手Q或安装的版本不支持
            mToast(R.string.not_find_qq, context)
            false
        }
    }

    fun mToast(msg: String?, context: Context) {
        if (msg != null && msg != "") {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun mToast(msg: Int, context: Context) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun openUrl(url: String, context: Context) {
        try {
            val uri: Uri = Uri.parse(url)
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startActivity(
        context: Context,
        packageName: String,
        activityName: String,
    ): Boolean {
        val intent = Intent().setClassName(packageName, activityName)
        return try {
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                true
            } catch (ex: Exception) {
                ex.printStackTrace()
                CrashReport.postCatchedException(ex)
                false
            }
        }
    }

    fun openWeChatScan(context: Context) {
        try {
            Intent(Intent.ACTION_MAIN).apply {
                putExtra("LauncherUI.From.Scaner.Shortcut", true)
                addCategory(Intent.CATEGORY_LAUNCHER)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                component = ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI")
                context.startActivity(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            CrashReport.postCatchedException(e)
        }
    }

    fun openAlipayPayPage(qrcode: String, context: Context): Boolean {
        var qrEncode = qrcode
        try {
            qrEncode = URLEncoder.encode(qrEncode, "utf-8")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            val alipayqr =
                "alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=$qrEncode"
            openUrl(alipayqr + "%3F_s%3Dweb-other&_t=" + System.currentTimeMillis(), context)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            CrashReport.postCatchedException(e)
        }
        return false
    }

    fun openAppDetail(context: Context) {
        try {
            context.startActivity(
                Intent(
                    android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
            )
            mToast(R.string.clean_app_data, context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openShizuku(context: Context): Boolean {
        return startActivity(context, SHIZUKU_PACKAGE, SHIZUKU_ACTIVITY)
    }

    /**
     *  正则匹配包名
     */
    fun matchPackageName(packageName: String): Int {
        if (packageName.trim() == "") {
            return R.string.not_empty
        }
        if (!Regex(pattern = REGEX_MATCH_PACKAGE).matches(packageName.trim())) {
            return R.string.illegal_package_name
        }
        return 0
    }
}