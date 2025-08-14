package io.bashpsk.emptylibs.canvasslate.slate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CanvasSlate(
    modifier: Modifier = Modifier,
    state: CanvasSlateState = rememberCanvasSlateState(),
    onDoneClick: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 4.dp)
    ) {

        CanvasSlateTopBar(
            modifier = modifier,
            state = state,
            onDoneClick = onDoneClick,
            onNavigateBack = onNavigateBack
        )

        CanvasSlateUI(
            modifier = modifier.weight(weight = 1.0F),
            state = state
        )
    }
}