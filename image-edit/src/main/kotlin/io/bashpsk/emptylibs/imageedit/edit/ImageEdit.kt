package io.bashpsk.emptylibs.imageedit.edit

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Composable function for the image editing screen.
 * This function provides the main UI for image editing, including top and bottom bars,
 * the image display area, and a modal bottom sheet for tool-specific inputs.
 *
 * @param modifier The modifier to be applied to the root container.
 * @param state The [ImageEditState] that holds the current state of the image editing process,
 * including the image being edited, selected tools, and applied transformations.
 * @param onDoneClick A lambda function to be invoked when the user clicks the "Done" button,
 * typically to save the edited image or confirm changes.
 * @param onNavigateBack A lambda function to be invoked when the user clicks the back button,
 * typically to navigate away from the editing screen.
 */
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

    ImageEditInputSheet(editToolInputSheetState = editToolInputSheetState, state = state)

    Column(
        modifier = modifier,
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