package io.bashpsk.emptylibs.pdfviewer.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.RetainedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onVisibilityChanged
import androidx.compose.ui.unit.round
import io.bashpsk.emptylibs.formatter.format.findAspectRatio
import io.bashpsk.emptylibs.imageview.tile.TileImageView
import io.bashpsk.emptylibs.layouts.zoomable.ZoomableLayout
import io.bashpsk.emptylibs.pdfviewer.pdf.PdfLazyColumnDefaults
import io.bashpsk.emptylibs.pdfviewer.pdf.PdfLazyColumnProperties
import io.bashpsk.emptylibs.pdfviewer.pdf.PdfLazyColumnState
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
 * @param placeholder The color to use for the placeholder.
 * @param colorFilter The color filter to apply to the image.
 * @param properties The properties for the PDF viewer.
 */
@OptIn(FlowPreview::class)
@Composable
internal fun PdfPageView(
    modifier: Modifier = Modifier,
    state: PdfLazyColumnState,
    pageData: PdfPageData = PdfPageData(),
    isScrolling: Boolean = false,
    placeholder: Color = MaterialTheme.colorScheme.surfaceVariant,
    colorFilter: ColorFilter? = null,
    properties: PdfLazyColumnProperties = PdfLazyColumnDefaults.properties()
) {

    val isImageZoomed by remember(state.transformable) {
        derivedStateOf { state.hasImageZoomed() }
    }

    val imageBitmap by remember(pageData, isImageZoomed) {
        derivedStateOf {
            if (isImageZoomed) {
                pageData.scaledImage?.bitmap ?: pageData.normalImage
            } else pageData.normalImage
        }
    }

    val aspectRatio by remember(pageData) {
        derivedStateOf {
            when (pageData.width > 0 && pageData.height > 0) {

                true -> findAspectRatio(width = pageData.width, height = pageData.height)
                false -> findAspectRatio(width = 1F, height = 1.41F)
            }
        }
    }

    val layoutPosition by remember(state.transformable) {
        derivedStateOf { state.transformable.position.round().copy(y = 0) }
    }

    val firstVisibleModifier = Modifier.onVisibilityChanged(
        minDurationMs = 50,
        minFractionVisible = 0.05F
    ) { visible ->

        if (visible) state.enqueueNormalRender(pageData = pageData)
    }

    RetainedEffect(state.transformable, isScrolling, isImageZoomed) {

        state.coroutineScope.launch(context = Dispatchers.IO) {

            snapshotFlow {

                state.transformable.zoom to (!isScrolling && isImageZoomed)
            }.distinctUntilChanged().debounce(
                timeout = 100.milliseconds
            ).collectLatest { (zoom, isFinished) ->

                if (isFinished) state.enqueueScaledRender(pageData = pageData, zoomLevel = zoom)
            }
        }

        onRetire {

            state.cancelEnqueueRender(pageIndex = pageData.page)
        }
    }

    imageBitmap?.let { bitmap ->

        ZoomableLayout(
            modifier = modifier
                .fillMaxWidth()
                .offset { layoutPosition },
            zoomScale = state.transformable.zoom
        ) {

            TileImageView(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio = aspectRatio),
                imageBitmap = bitmap,
                contentScale = ContentScale.Fit,
                colorFilter = colorFilter,
                tileSize = 1024
            )
        }
    } ?: Box(
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