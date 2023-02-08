package com.yorick.cokotools.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.yorick.cokotools.R

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
}