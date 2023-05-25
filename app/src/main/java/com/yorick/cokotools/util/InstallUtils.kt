package com.yorick.cokotools.util

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.IPackageInstaller
import android.content.pm.IPackageInstallerSession
import android.content.pm.PackageInstaller
import android.content.pm.PackageInstaller.Session
import android.net.Uri
import android.os.Build
import android.os.Process
import android.util.Log
import android.util.Pair
import com.tencent.bugly.crashreport.BuglyLog
import com.tencent.bugly.crashreport.CrashReport
import com.yorick.cokotools.BuildConfig
import com.yorick.cokotools.R
import com.yorick.cokotools.util.shell.Shell
import com.yorick.cokotools.util.shell.ShizukuShell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.regex.Matcher
import java.util.regex.Pattern

object InstallUtils {

    private val sessionIdPattern = Pattern.compile("(\\d+)")
    private const val TAG = "InstallUtils"
    private const val INSTALL_SUCCEEDED_FLAG = "INSTALL_SUCCEEDED"

    suspend fun doInstallApk(uri: Uri?, context: Context): Boolean {
        Utils.mToast(R.string.install_start, context)
        return if (uri == null) {
            false
        } else {
            installApkByShell(listOf(uri), context)
        }
    }

    suspend fun installApkByShell(uris: List<Uri>, context: Context): Boolean {
        try {
            val shell = ShizukuShell
            val sessionId = createSession(shell)
            val session = getSession(sessionId) ?: return false
            return doInstallApkBySession(uris, context, session)
        } catch (e: Exception) {
            e.printStackTrace()
            CrashReport.postCatchedException(e)
            return false
        }
    }

    private suspend fun doInstallApkBySession(
        uris: List<Uri>,
        context: Context,
        session: Session
    ): Boolean {
        val res = StringBuilder()
        val cr: ContentResolver = context.contentResolver
        return withContext(Dispatchers.IO) {
            try {
                for ((i, uri) in uris.withIndex()) {
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
                            `is`?.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        try {
                            os.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
                res.append('\n').append("commit: ")
                val results = mutableListOf<Intent?>(null)
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
                if (message != INSTALL_SUCCEEDED_FLAG) throw IllegalStateException(message)
                res.append('\n').append("status: ").append(status).append(" (").append(message)
                    .append(")")
            } catch (tr: Throwable) {
                tr.printStackTrace()
                res.append(tr)
                CrashReport.postCatchedException(tr)
                BuglyLog.e(TAG, res.toString())
                return@withContext false
            } finally {
                try {
                    session.close()
                } catch (tr: Throwable) {
                    res.append(tr)
                }
            }
            return@withContext true
        }
    }

    /**
     *  可以安装但是不能降级安装
     */
    suspend fun doInstallApks(uris: List<Uri>, context: Context): Boolean {
        val sessionId = getSessionID(context) ?: return false
        val session = getSession(sessionId) ?: return false
        return doInstallApkBySession(uris, context, session)
    }

    private fun getSession(sessionId: Int): Session? {
        var session: Session? = null
        try {
            val _packageInstaller: IPackageInstaller =
                ShizukuSystemServerApi.getPackageInstallerByPackageManager()

            val _session: IPackageInstallerSession = IPackageInstallerSession.Stub.asInterface(
                _packageInstaller.openSession(sessionId)?.asBinder()?.let {
                    ShizukuBinderWrapper(
                        it
                    )
                }
            )
            session = PackageInstallerUtils.createSession(_session)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return session
    }

    private fun getSessionID(context: Context): Int? {
        var sessionId: Int? = null
        val res = StringBuilder()
        val packageInstaller: PackageInstaller
        val installerPackageName: String
        var installerAttributionTag: String? = null
        val userId: Int
        val isRoot: Boolean
        try {
            val _packageInstaller: IPackageInstaller =
                ShizukuSystemServerApi.getPackageInstallerByPackageManager()
            isRoot = Shizuku.getUid() == 0

            // the reason for use "com.android.shell" as installer package under adb is that getMySessions will check installer package's owner
            installerPackageName = if (isRoot) context.packageName else "com.android.shell"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                installerAttributionTag = context.attributionTag
            }
            userId = if (isRoot) Process.myUserHandle().hashCode() else 0
            packageInstaller = PackageInstallerUtils.createPackageInstaller(
                _packageInstaller,
                installerPackageName,
                installerAttributionTag,
                userId
            )
            res.append("createSession: ")
            val params =
                PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
            var installFlags: Int = PackageInstallerUtils.getInstallFlags(params)
            installFlags =
                installFlags or (0x00000004 /*PackageManager.INSTALL_ALLOW_TEST*/ or 0x00000002) /*PackageManager.INSTALL_REPLACE_EXISTING*/
            PackageInstallerUtils.setInstallFlags(params, installFlags)
            sessionId = packageInstaller.createSession(params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sessionId
    }

    @Throws(RuntimeException::class)
    private fun createSession(shell: Shell): Int {
        val commandsToAttempt = mutableListOf<Shell.Command>()
        commandsToAttempt += Shell.Command(
            "pm", "install-create", "-r", "-d", "--user 0", "--install-location", "0", "-i",
            shell.makeLiteral(BuildConfig.APPLICATION_ID)
        )
        commandsToAttempt += Shell.Command(
            "pm", "install-create", "-r", "-d", "-- user 0", "-i",
            shell.makeLiteral(BuildConfig.APPLICATION_ID)
        )
        val attemptedCommands: MutableList<Pair<Shell.Command, String>> = mutableListOf()
        for (commandToAttempt in commandsToAttempt) {
            val result: Shell.Result = shell.exec(commandToAttempt)
            attemptedCommands.add(Pair(commandToAttempt, result.toString()))
            if (!result.isSuccessful) {
                Log.w(TAG, "Command failed: $commandToAttempt > $result")
                continue
            }
            val sessionId = extractSessionId(result.out)
            if (sessionId != null) {
                return sessionId
            } else {
                Log.w(TAG, "Command failed: $commandToAttempt > $result")
            }
        }
        val exceptionMessage = StringBuilder("Unable to create session, attempted commands: ")
        var i = 1
        for (attemptedCommand in attemptedCommands) {
            exceptionMessage.append("\n\n").append(i++).append(") ==========================\n")
                .append(attemptedCommand.first)
                .append("\nVVVVVVVVVVVVVVVV\n")
                .append(attemptedCommand.second)
        }
        exceptionMessage.append("\n")
        throw IllegalStateException(exceptionMessage.toString())
    }

    private fun extractSessionId(commandResult: String): Int? {
        return try {
            val sessionIdMatcher: Matcher =
                sessionIdPattern.matcher(
                    commandResult
                )
            sessionIdMatcher.find()
            sessionIdMatcher.group(1)?.toInt()
        } catch (e: Exception) {
            Log.w(TAG, commandResult, e)
            null
        }
    }
}