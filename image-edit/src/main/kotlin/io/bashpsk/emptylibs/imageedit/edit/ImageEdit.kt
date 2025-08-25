package io.bashpsk.emptylibs.imageedit.edit

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ImageEdit(
    modifier: Modifier = Modifier,
    state: ImageEditState,
    config: ImageEditConfig,
    onBitmapSelect: () -> Unit,
    onDoneClick: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier.matchParentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            ImageEditTopBar(
                state = state,
                onDoneClick = onDoneClick,
                onNavigateBack = onNavigateBack
            )

            ImageEditUI(
                modifier = Modifier.weight(weight = 1.0F),
                state = state,
                config = config
            )

            ImageEditBottomBar(
                state = state,
                onBitmapSelect = onBitmapSelect
            )
        }
    }
}