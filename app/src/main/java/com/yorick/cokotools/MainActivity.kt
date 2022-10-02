package com.yorick.cokotools

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yorick.cokotools.databinding.ActivityMainBinding
import com.yorick.cokotools.util.Utils
import com.yorick.cokotools.util.Utils.toastUtil

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        preferences = getSharedPreferences("count", MODE_PRIVATE)
        var count: Int = preferences.getInt("count", 0)
        //判断程序与第几次运行，如果是第一次运行则开启弹窗
        val editor: SharedPreferences.Editor
        if (count == 0) {
            editor = preferences.edit()
            val uri: Uri = Uri.parse(resources.getString(R.string.help_doc))
            MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.ic_logo)
                .setTitle(resources.getString(R.string.exceptions_title))
                .setMessage(resources.getString(R.string.exceptions_message))
                .setNeutralButton(resources.getString(R.string.exceptions_read_help_doc)) { _, _ ->
                    this.startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
                .setNegativeButton(resources.getString(R.string.decline)) { _, _ ->
                    editor.putInt("count", 0)
                    editor.commit()
                    finish()
                }
                .setPositiveButton(resources.getString(R.string.exceptions_accept)) { _, _ ->
                    editor.putInt("count", ++count)
                    editor.commit()
                }.setCancelable(false)
                .show()
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.buttonLockBands.setOnClickListener {
            // 尝试打开
            Utils.jumpActivity(
                this,
                resources.getString(R.string.lock_bands_package),
                resources.getString(R.string.lock_bands_activity),
                resources.getString(R.string.download_network_state)
            )
        }

        binding.buttonShowSeconds.setOnClickListener {
            Utils.jumpActivity(
                this,
                resources.getString(R.string.show_seconds_package),
                resources.getString(R.string.show_seconds_activity),
            )
            toastUtil(this, resources.getString(R.string.show_seconds_tips))
        }

        binding.buttonEngineerMode.setOnClickListener {
            Utils.jumpActivity(
                this,
                resources.getString(R.string.engineer_mode_package),
                resources.getString(R.string.engineer_mode_activity),
                resources.getString(R.string.download_engineer_model)
            )
        }
        binding.buttonFuelSummary.setOnClickListener {
            Utils.jumpActivity(
                this,
                resources.getString(R.string.fuel_summary_package),
                resources.getString(R.string.fuel_summary_activity),
            )
            toastUtil(this, resources.getString(R.string.fuel_summary_tips))
        }

        binding.buttonZenMode.setOnClickListener {
            Utils.jumpActivity(
                this,
                resources.getString(R.string.zen_mode_package),
                resources.getString(R.string.zen_mode_activity),
            )
        }

        binding.buttonShowWifiKeys.setOnClickListener {
            Utils.jumpActivity(
                this,
                resources.getString(R.string.show_wifi_keys_package),
                resources.getString(R.string.show_wifi_keys_activity),
            )
            toastUtil(this, resources.getString(R.string.show_wifi_key_tips))
        }

        binding.buttonMaxCharging.setOnClickListener {
            Utils.jumpActivity(
                this,
                resources.getString(R.string.max_charging_package),
                resources.getString(R.string.max_charging_activity),
                resources.getString(R.string.download_fuel_summary)
            )
            toastUtil(this, resources.getString(R.string.max_charging_tips))
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
                val uri: Uri = Uri.parse(resources.getString(R.string.help_doc))
                startActivity(Intent(Intent.ACTION_VIEW, uri))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}