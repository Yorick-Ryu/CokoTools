package com.yorick.cokotools.util.shell

import android.os.Build
import android.util.Log
import com.yorick.cokotools.util.IOUtils
import com.yorick.cokotools.util.ResultUtil
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuRemoteProcess
import java.io.InputStream
import java.io.OutputStream

class ShizukuShell private constructor() : Shell {
    init {
        sInstance = this
    }

    override val isAvailable: Boolean
        get() = if (!Shizuku.pingBinder()) {
            false
        } else try {
            exec(Shell.Command("echo", "test")).isSuccessful
        } catch (e: Exception) {
            Log.w(TAG, "Unable to access shizuku: ")
            Log.w(TAG, e)
            false
        }

    override fun exec(command: Shell.Command): Shell.Result {
        return execInternal(command, null)
    }

    override fun exec(command: Shell.Command, inputPipe: InputStream): Shell.Result {
        return execInternal(command, inputPipe)
    }

    override fun makeLiteral(arg: String): String {
        return "'" + arg.replace("'", "'\\''") + "'"
    }

    private fun execInternal(command: Shell.Command, inputPipe: InputStream?): Shell.Result {
        val stdOutSb = StringBuilder()
        val stdErrSb = StringBuilder()
        return try {
            val process: ShizukuRemoteProcess =
                Shizuku.newProcess(arrayOf("sh"), null, null)
            val stdOutD: Thread =
                IOUtils.writeStreamToStringBuilder(stdOutSb, process.inputStream)
            val stdErrD: Thread =
                IOUtils.writeStreamToStringBuilder(stdErrSb, process.errorStream)
            val outputStream: OutputStream = process.getOutputStream()
            outputStream.write(command.toString().toByteArray())
            outputStream.flush()
            if (inputPipe != null && process.alive()) {
                try {
                    inputPipe.use { inputStream -> IOUtils.copyStream(inputStream, outputStream) }
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }
            outputStream.close()
            process.waitFor()
            stdOutD.join()
            stdErrD.join()
            val exitValue: Int = process.exitValue()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                process.destroyForcibly()
            } else {
                process.destroy()
            }
            Shell.Result(
                command,
                exitValue,
                stdOutSb.toString().trim { it <= ' ' },
                stdErrSb.toString().trim { it <= ' ' })
        } catch (e: Exception) {
            Log.w(TAG, "Unable execute command: ")
            Log.w(TAG, e)
            Shell.Result(
                command, -1, "", """

<!> SAI ShizukuShell Java exception: ${ResultUtil.throwableToString(e)}"""
            )
        }
    }

    companion object {
        private const val TAG = "ShizukuShell"
        private var sInstance: ShizukuShell? = null
        val instance: ShizukuShell?
            get() {
                synchronized(ShizukuShell::class.java) { return if (sInstance != null) sInstance else ShizukuShell() }
            }
    }
}