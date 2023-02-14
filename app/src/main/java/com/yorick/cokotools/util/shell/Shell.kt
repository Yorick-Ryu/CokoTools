package com.yorick.cokotools.util.shell

import android.annotation.SuppressLint
import java.io.InputStream
import java.util.*

interface Shell {
    val isAvailable: Boolean

    fun exec(command: Command): Result
    fun exec(command: Command, inputPipe: InputStream): Result
    fun makeLiteral(arg: String): String
    class Command(command: String, vararg args: String?) {
        private var mArgs = mutableListOf<String>()

        init {
            mArgs += command
            if (args.toMutableList().isNotEmpty()) {
                for (i in args) {
                    if (i != null) mArgs += i
                }
            }
        }

        fun toStringArray(): Array<String?> {
            val array = arrayOfNulls<String>(mArgs.size)
            for (i in mArgs.indices) {
                array[i] = mArgs[i]
            }
            return array
        }

        override fun toString(): String {
            val sb = StringBuilder()
            for (i in mArgs.indices) {
                val arg = mArgs[i]
                sb.append(arg)
                if (i < mArgs.size - 1) {
                    sb.append(" ")
                }
            }
            return sb.toString()
        }

        class Builder internal constructor(command: String, vararg args: String?) {
            private val mCommand: Command

            init {
                mCommand = Command(command, *args)
            }

            fun addArg(argument: String): Builder {
                mCommand.mArgs.plus(argument)
                return this
            }

            fun build(): Command {
                return mCommand
            }
        }
    }

    class Result internal constructor(
        val cmd: Command,
        val exitCode: Int,
        val out: String,
        val err: String
    ) {

        val isSuccessful: Boolean
            get() = exitCode == 0

        @SuppressLint("DefaultLocale")
        override fun toString(): String {
            return String.format(
                "Command: %s\nExit code: %d\nOut:\n%s\n=============\nErr:\n%s",
                cmd,
                exitCode,
                out,
                err
            )
        }
    }
}