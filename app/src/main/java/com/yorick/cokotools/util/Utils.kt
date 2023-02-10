package com.yorick.cokotools.util

import android.content.*
import android.content.pm.IPackageInstaller
import android.content.pm.IPackageInstallerSession
import android.content.pm.PackageInstaller
import android.net.Uri
import android.os.Build
import android.os.Process
import android.widget.Toast
import com.yorick.cokotools.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper
import java.io.IOException
import java.net.URLEncoder
import java.util.concurrent.CountDownLatch

object Utils {

    private const val QQ_GROUP_URL =
        "mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D"
    private const val QQ_GROUP_KEY = "lFuzgAHN-Q_4j7fodzBaOtKrc_q6NYg9"
    private const val SHIZUKU_PACKAGE = "moe.shizuku.privileged.api"
    private const val SHIZUKU_ACTIVITY = "moe.shizuku.manager.MainActivity"
    const val HELP_DOC_URL = "https://shimo.im/docs/R13j8x5vQ1ieogk5"
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
        return try {
            context.startActivity(
                Intent().setClassName(packageName, activityName)
            )
            true
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            false
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
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
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
        try {
            startActivity(context, SHIZUKU_PACKAGE, SHIZUKU_ACTIVITY)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    suspend fun doInstallApks(uris: List<Uri>, context: Context): Boolean {
        withContext(Dispatchers.IO) {
            val res = StringBuilder()
            val packageInstaller: PackageInstaller
            var session: PackageInstaller.Session? = null
            val cr: ContentResolver = context.contentResolver
            val installerPackageName: String
            var installerAttributionTag: String? = null
            val userId: Int
            val isRoot: Boolean
            try {
                val _packageInstaller: IPackageInstaller =
                    ShizukuSystemServerApi.PackageManager_getPackageInstaller()
                isRoot = Shizuku.getUid() == 0

                // the reason for use "com.android.shell" as installer package under adb is that getMySessions will check installer package's owner
                installerPackageName = if (isRoot) context.packageName else "com.android.shell"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    installerAttributionTag = context.attributionTag
                }
                userId = if (isRoot) Process.myUserHandle().hashCode() else 0
                packageInstaller = PackageInstallerUtils.createPackageInstaller(
                    _packageInstaller,
                    installerPackageName,
                    installerAttributionTag,
                    userId
                )
                val sessionId: Int
                res.append("createSession: ")
                val params =
                    PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
                var installFlags: Int = PackageInstallerUtils.getInstallFlags(params)
                installFlags =
                    installFlags or (0x00000004 /*PackageManager.INSTALL_ALLOW_TEST*/ or 0x00000002) /*PackageManager.INSTALL_REPLACE_EXISTING*/
                PackageInstallerUtils.setInstallFlags(params, installFlags)
                sessionId = packageInstaller.createSession(params)
                res.append(sessionId).append('\n')
                res.append('\n').append("write: ")
                val _session: IPackageInstallerSession = IPackageInstallerSession.Stub.asInterface(
                    _packageInstaller.openSession(sessionId)?.asBinder()?.let {
                        ShizukuBinderWrapper(
                            it
                        )
                    }
                )
                session = PackageInstallerUtils.createSession(_session)
                var i = 0
                for (uri in uris) {
                    val name = "$i.apk"
                    val `is` = cr.openInputStream(uri)
                    val os = session.openWrite(name, 0, -1)
                    val buf = ByteArray(8192)
                    var len: Int
                    try {
                        while (`is`!!.read(buf).also { len = it } > 0) {
                            os.write(buf, 0, len)
                            os.flush()
                            session.fsync(os)
                        }
                    } finally {
                        try {
                            `is`!!.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        try {
                            os.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    i++
                }
                res.append('\n').append("commit: ")
                val results = arrayOf<Intent?>(null)
                val countDownLatch = CountDownLatch(1)
                val intentSender: IntentSender =
                    IntentSenderUtils.newInstance(object : IIntentSenderAdaptor() {
                        override fun send(intent: Intent?) {
                            results[0] = intent
                            countDownLatch.countDown()
                        }
                    })
                session.commit(intentSender)
                countDownLatch.await()
                val result = results[0]
                val status =
                    result!!.getIntExtra(
                        PackageInstaller.EXTRA_STATUS,
                        PackageInstaller.STATUS_FAILURE
                    )
                val message = result.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
                res.append('\n').append("status: ").append(status).append(" (").append(message)
                    .append(")")
            } catch (tr: Throwable) {
                tr.printStackTrace()
                res.append(tr)
                return@withContext false
            } finally {
                if (session != null) {
                    try {
                        session.close()
                    } catch (tr: Throwable) {
                        res.append(tr)
                    }
                }
            }
        }
        return true
    }
}