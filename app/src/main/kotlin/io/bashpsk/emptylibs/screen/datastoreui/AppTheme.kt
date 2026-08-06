package io.bashpsk.emptylibs.screen.datastoreui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

enum class AppTheme {

    System,
    Dark,
    Light;

    companion object {

        @Composable
        fun getTheme(theme: String): Boolean {

            return when (valueOf(value = theme)) {

                System -> isSystemInDarkTheme()
                Dark -> true
                Light -> false
            }
        }
    }
}