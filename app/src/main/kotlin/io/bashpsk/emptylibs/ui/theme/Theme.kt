package io.bashpsk.emptylibs.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.datastore.preferences.core.stringPreferencesKey
import io.bashpsk.emptylibs.datastoreui.datastore.LocalDatastore
import io.bashpsk.emptylibs.datastoreui.extension.getPreference
import io.bashpsk.emptylibs.component.datastoreui.AppFont

@Composable
fun EmptyLibsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {

    val context = LocalContext.current
    val datastore = LocalDatastore.current

    val getBodyFont by datastore.getPreference(
        key = stringPreferencesKey("FONT-PREFERENCE"),
        entities = AppFont.fontEntities
    ).collectAsState(initial = null)

    val colorScheme = when {

        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val typography = getBodyFont?.resId?.let { bodyFont ->

        Typography.copy(
            titleLarge = Typography.titleLarge.copy(fontFamily = FontFamily(Font(bodyFont))),
            titleMedium = Typography.titleMedium.copy(fontFamily = FontFamily(Font(bodyFont))),
            titleSmall = Typography.titleSmall.copy(fontFamily = FontFamily(Font(bodyFont)))
        )
    } ?: Typography

    MaterialTheme(colorScheme = colorScheme, typography = typography, content = content)
}