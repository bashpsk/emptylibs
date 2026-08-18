package io.bashpsk.emptylibs.pdfviewer.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.RetainedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.roundToIntRect
import androidx.compose.ui.unit.toOffset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.bashpsk.emptylibs.imageview.lazy.LazyImage
import io.bashpsk.emptylibs.imageview.lazy.ScaledImageFragment
import io.bashpsk.emptylibs.layouts.zoomable.ZoomableLayout

/**
 * A Composable that displays a single [PdfPage]. It handles zoom, offset, and high-resolution
 * fragment rendering.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param page The [PdfPage] instance to display.
 * @param zoomScale The current zoom level.
 * @param layoutOffset The offset of the page within the viewer.
 * @param colorFilter Optional [ColorFilter] to apply to the page image (e.g., for night mode).
 */
@Composable
fun PdfPageView(
    modifier: Modifier = Modifier,
    page: PdfPage,
    zoomScale: Float = 1.0F,
    layoutOffset: IntOffset = IntOffset.Zero,
    colorFilter: ColorFilter? = null
) {

    val imageBitmap by page.imageBitmap.collectAsStateWithLifecycle(initialValue = null)
    val pdfScaledPage by page.pdfScaledPage.collectAsStateWithLifecycle(initialValue = null)

    ZoomableLayout(
        modifier = modifier.offset { layoutOffset },
        zoomScale = zoomScale
    ) {

        RetainedEffect(viewport, zoomScale) {

            val visibleFragment = if (zoomScale <= 1.2F) null else viewport.roundToIntRect()

            page.updateVisibleLayout(visibleFragment = visibleFragment, zoom = zoomScale)

            onRetire { }
        }

        val fragment by remember(pdfScaledPage) {
            derivedStateOf {
                pdfScaledPage?.let { scaledPage ->

                    ScaledImageFragment(
                        bitmap = scaledPage.bitmap,
                        topLeft = scaledPage.topLeft.toOffset(),
                        dstSize = scaledPage.dstSize
                    )
                }
            }
        }

        Layout(
            modifier = Modifier.onSizeChanged { size ->

                page.updateLayoutSize(size = size)
            },
            content = {

                imageBitmap?.let { bitmap ->

                    LazyImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(page.ratio),
                        baseImage = bitmap,
                        fragment = fragment,
                        colorFilter = colorFilter,
                        contentDescription = "Page ${page.index}"
                    )
                } ?: Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(page.ratio),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator(modifier = Modifier.size(35.dp))
                }
            },
            measurePolicy = { measurables, constraints ->

                val width: Int
                val height: Int

                if (constraints.hasBoundedWidth) {

                    width = constraints.maxWidth
                    height = (width * page.ratio).toInt()
                } else if (constraints.hasBoundedHeight) {

                    height = constraints.maxHeight
                    width = (height / page.ratio).toInt()
                } else {

                    width = constraints.minWidth
                    height = (width * page.ratio).toInt()
                }

                val placeables = measurables.map { measurable ->

                    measurable.measure(Constraints.fixed(width, height))
                }

                layout(width = width, height = height) {

                    placeables.forEach { placeable -> placeable.place(x = 0, y = 0) }
                }
            }
        )
    }
}