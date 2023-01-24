package com.yorick.cokotools

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import com.yorick.cokotools.ui.CokoToolsApp
import com.yorick.cokotools.ui.components.BaseAlterDialog
import com.yorick.cokotools.ui.theme.CokoToolsTheme
import com.yorick.cokotools.ui.viewmodels.CokoViewModelFactory
import com.yorick.cokotools.ui.viewmodels.ContributorViewModel
import com.yorick.cokotools.ui.viewmodels.HomeViewModel

class MainActivity : ComponentActivity() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        preferences = getSharedPreferences("count", MODE_PRIVATE)
        var count: Int = preferences.getInt("count", 0)
        val editor: SharedPreferences.Editor = preferences.edit()

        val homeViewModel: HomeViewModel by viewModels { CokoViewModelFactory }
        val contributorViewModel: ContributorViewModel by viewModels { CokoViewModelFactory }

        setContent {
            var isFirst by remember {
                mutableStateOf(count == 0)
            }
            CokoToolsTheme {
                CokoToolsApp(
                    homeViewModel = homeViewModel,
                    contributorViewModel = contributorViewModel
                )
                // 判断程序与第几次运行，如果是第一次运行则开启弹窗
                if (isFirst) {
                    BaseAlterDialog(
                        onDismissRequest = { isFirst = false },
                        title = stringResource(id = R.string.exceptions_title),
                        text = stringResource(id = R.string.exceptions_message),
                        positiveText = R.string.exceptions_accept,
                        negativeText = R.string.decline,
                        onConfirm = {
                            editor.putInt("count", ++count)
                            editor.apply()
                            isFirst = false
                        },
                        onDismiss = {
                            editor.putInt("count", 0)
                            editor.apply()
                            finish()
                        },
                        cancelable = false
                    )
                }
            }
        }
    }
}