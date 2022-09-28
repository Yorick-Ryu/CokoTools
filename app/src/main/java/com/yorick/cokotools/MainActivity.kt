package com.yorick.cokotools

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yorick.cokotools.databinding.ActivityMainBinding
import com.yorick.cokotools.util.Utils

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.buttonLockBands.setOnClickListener {
            Log.d("yu", "onCreate: lock")
            val activityExisting = Utils.isActivityExisting(
                this,
                "com.vivo.networkstate",
                "com.vivo.networkstate.MainActivity"
            )
            if (!activityExisting) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.compose_needed))
                    .setMessage(resources.getString(R.string.download_network_state))
                    .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                    }
                    .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->

                    }
                    .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->

                    }
                    .show()
            } else {
                Toast.makeText(
                    this, resources.getString(R.string.compose_satisfied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.fab.setOnClickListener {
            val uri: Uri = Uri.parse(resources.getString(R.string.feedback_url))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_helps -> {
                Log.d("yu", "onOptionsItemSelected: helps")
                return true
            }
            R.id.action_donate -> {
                Log.d("yu", "onOptionsItemSelected: donate")
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}