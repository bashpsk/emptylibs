package io.bashpsk.emptylibs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.bashpsk.emptylibs.canvasslate.slate.CanvasSlate
import io.bashpsk.emptylibs.canvasslate.slate.rememberCanvasSlateState
import kotlinx.collections.immutable.persistentListOf

@Composable
fun CanvasSlateScreen() {

    val colorList = persistentListOf(
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer,
        MaterialTheme.colorScheme.errorContainer,
        MaterialTheme.colorScheme.onPrimaryContainer,
        MaterialTheme.colorScheme.onSecondaryContainer,
        MaterialTheme.colorScheme.onTertiaryContainer,
        MaterialTheme.colorScheme.onErrorContainer,
    )

    val canvasSlateState = rememberCanvasSlateState(
        background = MaterialTheme.colorScheme.background,
        initial = MaterialTheme.colorScheme.onSurface,
        colorList = colorList
    )

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            CanvasSlate(
                modifier = Modifier.fillMaxSize(),
                state = canvasSlateState
            )
        }
    }
}