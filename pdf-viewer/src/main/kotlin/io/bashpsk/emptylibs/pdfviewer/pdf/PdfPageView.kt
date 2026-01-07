package io.bashpsk.emptylibs.pdfviewer.pdf

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onFirstVisible
import androidx.compose.ui.unit.round
import io.bashpsk.emptylibs.jetpackui.layout.ZoomableLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * A composable that displays a single page of a PDF file.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param state The state of the PDF viewer.
 * @param pageData The data for the page to be displayed.
 * @param isScrolling Whether the user is currently scrolling.
 */
@OptIn(FlowPreview::class)
@Composable
internal fun PdfPageView(
    modifier: Modifier = Modifier,
    state: PdfLazyColumnState,
    pageData: PdfPageData = PdfPageData(),
    isScrolling: Boolean = false,
    placeholder: Color = MaterialTheme.colorScheme.surface,
    colorFilter: ColorFilter? = null
) {

    var highQualityBitmap by retain { mutableStateOf<ImageBitmap?>(null) }

    val imageBitmap by remember(highQualityBitmap, pageData) {
        derivedStateOf { highQualityBitmap ?: pageData.bitmap }
    }

    val aspectRatio by remember(pageData) {
        derivedStateOf {
            when (pageData.width > 0 && pageData.height > 0) {

                true -> pageData.width.toFloat() / pageData.height.toFloat()
                false -> 1F / 1.41F
            }
        }
    }

    val layoutPosition by remember(state.transformableState) {
        derivedStateOf { state.transformableState.position.round().copy(y = 0) }
    }

    val firstVisibleModifier = Modifier.onFirstVisible(
        minDurationMs = 300,
        minFractionVisible = 0.05F
    ) {

        state.coroutineScope.launch(context = Dispatchers.IO) {

            highQualityBitmap = state.getHighQualityBitmap(pageIndex = pageData.page)
        }
    }

    LaunchedEffect(state.transformableState) {

        snapshotFlow {

            state.transformableState.zoom
        }.debounce(timeout = 500.milliseconds).distinctUntilChanged().collectLatest {

            if (isScrolling.not()) state.coroutineScope.launch(context = Dispatchers.IO) {

                highQualityBitmap = state.getHighQualityBitmap(pageIndex = pageData.page)
            }
        }
    }

    imageBitmap?.let { bitmap ->

        ZoomableLayout(
            modifier = modifier
                .fillMaxWidth()
                .then(firstVisibleModifier)
                .offset { layoutPosition },
            zoomScale = state.transformableState.zoom
        ) {

            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio),
                bitmap = bitmap,
                contentScale = ContentScale.Fit,
                colorFilter = colorFilter,
                contentDescription = "Page ${pageData.page}"
            )
        }
    } ?: run {

        Box(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(ratio = aspectRatio)
                .background(color = placeholder)
                .then(firstVisibleModifier),
            contentAlignment = Alignment.Center
        ) {

            CircularProgressIndicator()
        }
    }
}