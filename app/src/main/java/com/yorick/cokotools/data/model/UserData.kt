package com.yorick.cokotools.data.model

import androidx.annotation.Keep

@Keep
data class UserData(
    val darkThemeConfig: DarkThemeConfig,
    val useDynamicColor: Boolean,
)
