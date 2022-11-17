package com.yorick.cokotools

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.yorick.cokotools.databinding.ActivityMainBinding
import com.yorick.cokotools.util.Utils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        preferences = getSharedPreferences("count", MODE_PRIVATE)
        var count: Int = preferences.getInt("count", 0)
        //判断程序与第几次运行，如果是第一次运行则开启弹窗
        val editor: SharedPreferences.Editor
        if (count == 0) {
            editor = preferences.edit()
            Utils.baseDialog(
                this,
                title = resources.getString(R.string.exceptions_title),
                msg = resources.getString(R.string.exceptions_message),
                neutral = resources.getString(R.string.exceptions_read_help_doc),
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

        binding.fab.setOnClickListener {
            val uri: Uri = Uri.parse(resources.getString(R.string.feedback_url))
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_donate -> {
                startActivity(Intent(this, DonateActivity::class.java))
                return true
            }
            R.id.action_helps -> {
                Utils.openDoc(this)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}