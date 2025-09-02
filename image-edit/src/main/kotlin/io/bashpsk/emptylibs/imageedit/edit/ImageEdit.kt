package io.bashpsk.emptylibs.imageedit.edit

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ImageEdit(
    modifier: Modifier = Modifier,
    state: ImageEditState,
    onDoneClick: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {

    val editToolInputSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(editToolInputSheetState.isVisible) {

        state.onRefreshEditItem()
    }

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        ImageEditInputSheet(editToolInputSheetState = editToolInputSheetState, state = state)

        Column(
            modifier = Modifier.matchParentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            ImageEditTopBar(
                state = state,
                editToolInputSheetState = editToolInputSheetState,
                onDoneClick = onDoneClick,
                onNavigateBack = onNavigateBack
            )

            ImageEditUI(
                modifier = Modifier.weight(weight = 1.0F),
                state = state
            )

            ImageEditBottomBar(state = state)
        }
    }
}