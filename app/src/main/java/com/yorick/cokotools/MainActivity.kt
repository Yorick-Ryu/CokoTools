package com.yorick.cokotools

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.yorick.cokotools.ui.CokoToolsApp
import com.yorick.cokotools.ui.theme.CokoToolsTheme
import com.yorick.cokotools.util.Utils

class MainActivity : ComponentActivity() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

//        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        setContent {
            CokoToolsTheme {
                CokoToolsApp()
            }
        }

        preferences = getSharedPreferences("count", MODE_PRIVATE)
        var count: Int = preferences.getInt("count", 0)
        //判断程序与第几次运行，如果是第一次运行则开启弹窗
        val editor: SharedPreferences.Editor
        if (count == 0) {
            editor = preferences.edit()
            Utils.baseDialog(
                this,
                title = R.string.exceptions_title,
                msg = R.string.exceptions_message,
                neutral = R.string.exceptions_read_help_doc,
                neutralCallback = {
                    Utils.openDoc(this)
                },
                negativeCallback = {
                    editor.putInt("count", 0)
                    editor.apply()
                    finish()
                },
                positive = resources.getString(R.string.exceptions_accept),
                positiveCallback = {
                    editor.putInt("count", ++count)
                    editor.apply()
                },
                cancelable = false
            )
        }
    }
}