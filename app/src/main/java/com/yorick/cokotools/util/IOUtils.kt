package com.yorick.cokotools.util

import android.util.Log
import java.io.*

object IOUtils {
    private const val TAG = "IOUtils"

    @Throws(IOException::class)
    fun copyStream(from: InputStream, to: OutputStream) {
        val buf = ByteArray(1024 * 1024)
        var len: Int
        while (from.read(buf).also { len = it } > 0) {
            to.write(buf, 0, len)
        }
    }

    fun writeStreamToStringBuilder(builder: StringBuilder, inputStream: InputStream?): Thread {
        val t = Thread {
            try {
                val buf = CharArray(1024)
                var len: Int
                val reader =
                    BufferedReader(InputStreamReader(inputStream))
                while (reader.read(buf).also { len = it } > 0) {
                    builder.append(buf, 0, len)
                }
                reader.close()
            } catch (e: Exception) {
                Log.wtf(TAG, e)
            }
        }
        t.start()
        return t
    }
}