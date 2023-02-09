package com.yorick.cokotools.util

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.IntentSender
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
import java.util.concurrent.CountDownLatch

object Utils {

    fun joinQQGroup(context: Context): Boolean {
        val intent = Intent()
        intent.data =
            Uri.parse(
                "mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D"
                        + "lFuzgAHN-Q_4j7fodzBaOtKrc_q6NYg9"
            )
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

    fun openDoc(context: Context) {
        val uri: Uri = Uri.parse(context.resources.getString(R.string.help_doc))
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    fun openUrl(url: String, context: Context) {
        val uri: Uri = Uri.parse(url)
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
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