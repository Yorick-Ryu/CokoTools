package com.yorick.cokotools.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun ShellScreen(
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.fillMaxSize()){
        Text(text = "Shell")
    }
}