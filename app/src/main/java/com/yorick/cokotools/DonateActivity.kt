package com.yorick.cokotools

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.yorick.cokotools.util.Utils

class DonateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate)
        findViewById<Button>(R.id.button_join_group).setOnClickListener {
            Utils.joinQQGroup(this, "lFuzgAHN-Q_4j7fodzBaOtKrc_q6NYg9")
        }
        findViewById<Button>(R.id.button_coolapk_index).setOnClickListener {
            val uri: Uri = Uri.parse(resources.getString(R.string.coolapk_index_link))
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }
}