package com.tasneem.safwa.ui.theme

import androidx.compose.runtime.Composable

@Composable
fun SafwaTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    com.tasneem.safwa.core.SafwaTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        content = content
    )
}