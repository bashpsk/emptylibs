package io.bashpsk.emptylibs.canvasslate.extension

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse

@Composable
@ReadOnlyComposable
internal fun findContentColorFor(backgroundColor: Color): Color {

    return MaterialTheme.colorScheme.findContentColorFor(backgroundColor).takeOrElse {
        LocalContentColor.current
    }
}

@Stable
private fun ColorScheme.findContentColorFor(backgroundColor: Color): Color {

    return when (backgroundColor) {

        primary -> onPrimary
        secondary -> onSecondary
        tertiary -> onTertiary
        background -> onBackground
        error -> onError
        primaryContainer -> onPrimaryContainer
        secondaryContainer -> onSecondaryContainer
        tertiaryContainer -> onTertiaryContainer
        errorContainer -> onErrorContainer
        inverseSurface -> inverseOnSurface
        surface -> onSurface
        surfaceVariant -> onSurfaceVariant
        surfaceBright -> onSurface
        surfaceContainer -> onSurface
        surfaceContainerHigh -> onSurface
        surfaceContainerHighest -> onSurface
        surfaceContainerLow -> onSurface
        surfaceContainerLowest -> onSurface
        onPrimary -> primary
        onSecondary -> secondary
        onTertiary -> tertiary
        onBackground -> background
        onError -> error
        onPrimaryContainer -> primaryContainer
        onSecondaryContainer -> secondaryContainer
        onTertiaryContainer -> tertiaryContainer
        onErrorContainer -> errorContainer
        inverseOnSurface -> inverseSurface
        onSurface -> surface
        onSurfaceVariant -> surfaceVariant
        else -> Color.Unspecified
    }
}