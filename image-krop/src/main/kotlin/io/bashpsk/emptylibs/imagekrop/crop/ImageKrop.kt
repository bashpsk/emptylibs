package io.bashpsk.emptylibs.imagekrop.crop

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import io.bashpsk.emptylibs.imagekrop.offset.hasNeared
import kotlin.math.abs

/**
 * A Composable function that provides an image cropping interface.
 *
 * This function allows users to crop an image with various configurations,
 * including aspect ratios and crop shapes. It provides a visual interface
 * for selecting the crop area and applying the crop.
 *
 * @param modifier Optional [Modifier] for the root Composable.
 * @param state The [ImageKropState] that holds the current state of the cropping UI,
 * including the image bitmap, configuration, and crop selection.
 * @param onNavigateBack A callback function that is invoked when the user initiates a back
 * navigation action, typically by pressing a back button in the UI.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ImageKrop(
    modifier: Modifier = Modifier,
    state: ImageKropState,
    onKropFinished: () -> Unit,
    onNavigateBack: () -> Unit
) {

    val density = LocalDensity.current
    val imagePreviewSheetState = rememberModalBottomSheetState()

    val topCenter by remember(state) {
        derivedStateOf { Offset((state.topLeft.x + state.topRight.x) / 2, state.topLeft.y) }
    }

    val bottomCenter by remember(state) {
        derivedStateOf {
            Offset((state.bottomLeft.x + state.bottomRight.x) / 2, state.bottomLeft.y)
        }
    }

    val leftCenter by remember(state) {
        derivedStateOf { Offset(state.topLeft.x, (state.topLeft.y + state.bottomLeft.y) / 2) }
    }

    val rightCenter by remember(state) {
        derivedStateOf { Offset(state.topRight.x, (state.topRight.y + state.bottomRight.y) / 2) }
    }

    val rectSize by remember {
        derivedStateOf {
            Size(
                width = state.topRight.x - state.topLeft.x,
                height = state.bottomLeft.y - state.topLeft.y
            )
        }
    }

    val threshold by remember(state, density) {
        derivedStateOf {
            maxOf(state.config.handleWidth, state.config.handleHeight).toPixel(density = density)
        }
    }

    val cropSizeLimit by remember(state.config, density) {
        derivedStateOf { state.config.minimumCropSize.toPixel(density = density) }
    }

    val imagePlacedModifier = Modifier.onPlaced { layoutCoordinates ->

        val imageWidth = layoutCoordinates.size.width.toFloat()
        val imageHeight = layoutCoordinates.size.height.toFloat()

        state.topLeft = Offset(imageWidth * 0.05F, imageHeight * 0.05F)
        state.topRight = Offset(imageWidth * 0.95F, imageHeight * 0.05F)
        state.bottomLeft = Offset(imageWidth * 0.05F, imageHeight * 0.95F)
        state.bottomRight = Offset(imageWidth * 0.95F, imageHeight * 0.95F)
        state.canvasSize = layoutCoordinates.size
    }

    val pointerInputWithoutAspect = Modifier.pointerInput(
        state.kropAspectRatio,
        state.canvasSize,
        cropSizeLimit
    ) {

        detectDragGestures(
            onDragStart = { offset ->

                state.kropCorner = when {

                    offset.hasNeared(
                        point = state.topLeft,
                        threshold = threshold
                    ) -> KropCorner.TOP_LEFT

                    offset.hasNeared(
                        point = state.topRight,
                        threshold = threshold
                    ) -> KropCorner.TOP_RIGHT

                    offset.hasNeared(
                        point = state.bottomLeft,
                        threshold = threshold
                    ) -> KropCorner.BOTTOM_LEFT

                    offset.hasNeared(
                        point = state.bottomRight,
                        threshold = threshold
                    ) -> KropCorner.BOTTOM_RIGHT

                    offset.hasNeared(
                        point = topCenter,
                        threshold = threshold
                    ) -> KropCorner.TOP_CENTRE

                    offset.hasNeared(
                        point = bottomCenter,
                        threshold = threshold
                    ) -> KropCorner.BOTTOM_CENTRE

                    offset.hasNeared(
                        point = leftCenter,
                        threshold = threshold
                    ) -> KropCorner.LEFT_CENTRE

                    offset.hasNeared(
                        point = rightCenter,
                        threshold = threshold
                    ) -> KropCorner.RIGHT_CENTRE

                    else -> null
                }

                state.isMovingCropRect = state.kropCorner == null && Rect(
                    topLeft = state.topLeft,
                    bottomRight = state.bottomRight
                ).contains(offset)
            },
            onDragEnd = {

                state.kropCorner = null
                state.isMovingCropRect = false
            },
            onDragCancel = {

                state.kropCorner = null
                state.isMovingCropRect = false
            }
        ) { change, dragAmount ->

            change.consume()

            val minX = 0F
            val minY = 0F
            val maxX = state.canvasSize.width.toFloat()
            val maxY = state.canvasSize.height.toFloat()

            when (state.kropCorner) {

                KropCorner.TOP_LEFT -> {

                    val potentialWidth = state.bottomRight.x - (state.topLeft.x + dragAmount.x)
                    val potentialHeight = state.bottomRight.y - (state.topLeft.y + dragAmount.y)

                    if (potentialWidth >= cropSizeLimit && potentialHeight >= cropSizeLimit) {

                        val newX = (state.topLeft.x + dragAmount.x).coerceIn(
                            minX..state.bottomRight.x - cropSizeLimit
                        )

                        val newY = (state.topLeft.y + dragAmount.y).coerceIn(
                            minY..state.bottomRight.y - cropSizeLimit
                        )

                        state.topLeft = Offset(newX, newY)
                        state.topRight = state.topRight.copy(y = newY)
                        state.bottomLeft = state.bottomLeft.copy(x = newX)
                    }
                }

                KropCorner.TOP_RIGHT -> {

                    val potentialWidth = (state.topRight.x + dragAmount.x) - state.bottomLeft.x
                    val potentialHeight = state.bottomRight.y
                    -(state.topRight.y + dragAmount.y)

                    if (potentialWidth >= cropSizeLimit && potentialHeight >= cropSizeLimit) {

                        val newX = (state.topRight.x + dragAmount.x).coerceIn(
                            state.topLeft.x + cropSizeLimit..maxX
                        )

                        val newY = (state.topRight.y + dragAmount.y).coerceIn(
                            minY..state.bottomLeft.y - cropSizeLimit
                        )

                        state.topRight = Offset(newX, newY)
                        state.topLeft = state.topLeft.copy(y = newY)
                        state.bottomRight = state.bottomRight.copy(x = newX)
                    }
                }

                KropCorner.BOTTOM_LEFT -> {

                    val potentialWidth = state.bottomRight.x
                    -(state.bottomLeft.x + dragAmount.x)
                    val potentialHeight = (state.bottomLeft.y + dragAmount.y) - state.topRight.y

                    if (potentialWidth >= cropSizeLimit && potentialHeight >= cropSizeLimit) {

                        val newX = (state.bottomLeft.x + dragAmount.x).coerceIn(
                            minX..state.bottomRight.x - cropSizeLimit
                        )

                        val newY = (state.bottomLeft.y + dragAmount.y).coerceIn(
                            state.topLeft.y + cropSizeLimit..maxY
                        )

                        state.bottomLeft = Offset(newX, newY)
                        state.topLeft = state.topLeft.copy(x = newX)
                        state.bottomRight = state.bottomRight.copy(y = newY)
                    }
                }

                KropCorner.BOTTOM_RIGHT -> {

                    val potentialWidth = (state.bottomRight.x + dragAmount.x) - state.topLeft.x
                    val potentialHeight = (state.bottomRight.y + dragAmount.y) - state.topLeft.y

                    if (potentialWidth >= cropSizeLimit && potentialHeight >= cropSizeLimit) {

                        val newX = (state.bottomRight.x + dragAmount.x).coerceIn(
                            state.bottomLeft.x + cropSizeLimit..maxX
                        )

                        val newY = (state.bottomRight.y + dragAmount.y).coerceIn(
                            state.topRight.y + cropSizeLimit..maxY
                        )

                        state.bottomRight = Offset(newX, newY)
                        state.topRight = state.topRight.copy(x = newX)
                        state.bottomLeft = state.bottomLeft.copy(y = newY)
                    }
                }

                KropCorner.TOP_CENTRE -> {

                    val potentialHeight = state.bottomLeft.y - (state.topLeft.y + dragAmount.y)

                    if (potentialHeight >= cropSizeLimit) {

                        val newY = (state.topLeft.y + dragAmount.y).coerceIn(
                            minY..state.bottomLeft.y - cropSizeLimit
                        )

                        state.topLeft = state.topLeft.copy(y = newY)
                        state.topRight = state.topRight.copy(y = newY)
                    }
                }

                KropCorner.LEFT_CENTRE -> {

                    val potentialWidth = state.bottomRight.x - (state.topLeft.x + dragAmount.x)

                    if (potentialWidth >= cropSizeLimit) {

                        val newX = (state.topLeft.x + dragAmount.x).coerceIn(
                            minX..state.bottomRight.x - cropSizeLimit
                        )

                        state.topLeft = state.topLeft.copy(x = newX)
                        state.bottomLeft = state.bottomLeft.copy(x = newX)
                    }
                }

                KropCorner.RIGHT_CENTRE -> {

                    val potentialWidth = (state.topRight.x + dragAmount.x) - state.topLeft.x

                    if (potentialWidth >= cropSizeLimit) {

                        val newX = (state.topRight.x + dragAmount.x).coerceIn(
                            state.topLeft.x + cropSizeLimit..maxX
                        )

                        state.topRight = state.topRight.copy(x = newX)
                        state.bottomRight = state.bottomRight.copy(x = newX)
                    }
                }

                KropCorner.BOTTOM_CENTRE -> {

                    val potentialHeight = (state.bottomLeft.y + dragAmount.y) - state.topLeft.y

                    if (potentialHeight >= cropSizeLimit) {

                        val newY = (state.bottomLeft.y + dragAmount.y).coerceIn(
                            state.topLeft.y + cropSizeLimit..maxY
                        )

                        state.bottomLeft = state.bottomLeft.copy(y = newY)
                        state.bottomRight = state.bottomRight.copy(y = newY)
                    }

                }

                null -> if (state.isMovingCropRect) {

                    val newTopLeftX = (state.topLeft.x + dragAmount.x).coerceIn(
                        minX..maxX - rectSize.width
                    )

                    val newTopLeftY = (state.topLeft.y + dragAmount.y).coerceIn(
                        minY..maxY - rectSize.height
                    )

                    val newTopRightX = newTopLeftX + rectSize.width
                    val newBottomLeftY = newTopLeftY + rectSize.height

                    state.topLeft = Offset(newTopLeftX, newTopLeftY)
                    state.topRight = Offset(newTopRightX, newTopLeftY)
                    state.bottomLeft = Offset(newTopLeftX, newBottomLeftY)
                    state.bottomRight = Offset(newTopRightX, newBottomLeftY)
                }
            }
        }
    }

    val pointerInputWithAspect = Modifier.pointerInput(
        state.kropAspectRatio,
        state.canvasSize,
        cropSizeLimit
    ) {

        detectDragGestures(
            onDragStart = { offset ->

                state.kropCorner = when {

                    offset.hasNeared(state.topLeft, threshold) -> KropCorner.TOP_LEFT
                    offset.hasNeared(state.topRight, threshold) -> KropCorner.TOP_RIGHT
                    offset.hasNeared(state.bottomLeft, threshold) -> KropCorner.BOTTOM_LEFT
                    offset.hasNeared(state.bottomRight, threshold) -> KropCorner.BOTTOM_RIGHT
                    offset.hasNeared(topCenter, threshold) -> KropCorner.TOP_CENTRE
                    offset.hasNeared(bottomCenter, threshold) -> KropCorner.BOTTOM_CENTRE
                    offset.hasNeared(leftCenter, threshold) -> KropCorner.LEFT_CENTRE
                    offset.hasNeared(rightCenter, threshold) -> KropCorner.RIGHT_CENTRE
                    else -> null
                }

                state.isMovingCropRect = state.kropCorner == null && Rect(
                    topLeft = state.topLeft,
                    bottomRight = state.bottomRight
                ).contains(offset)
            },
            onDragEnd = {

                state.kropCorner = null
                state.isMovingCropRect = false
            },
            onDragCancel = {

                state.kropCorner = null
                state.isMovingCropRect = false
            }
        ) { change, dragAmount ->

            change.consume()

            if (state.canvasSize == IntSize.Zero) return@detectDragGestures

            val aspectRatio = state.kropAspectRatio.ratio ?: return@detectDragGestures

            val minX = 0.0F
            val minY = 0.0F
            val maxX = state.canvasSize.width.toFloat()
            val maxY = state.canvasSize.height.toFloat()

            when (state.kropCorner) {

                KropCorner.TOP_LEFT, KropCorner.TOP_RIGHT, KropCorner.BOTTOM_LEFT,
                KropCorner.BOTTOM_RIGHT -> {

                    val fixedCorner = when (state.kropCorner) {

                        KropCorner.TOP_LEFT -> state.bottomRight
                        KropCorner.TOP_RIGHT -> state.bottomLeft
                        KropCorner.BOTTOM_LEFT -> state.topRight
                        KropCorner.BOTTOM_RIGHT -> state.topLeft
                        else -> return@detectDragGestures
                    }

                    val draggedCorner = when (state.kropCorner) {

                        KropCorner.TOP_LEFT -> state.topLeft
                        KropCorner.TOP_RIGHT -> state.topRight
                        KropCorner.BOTTOM_LEFT -> state.bottomLeft
                        KropCorner.BOTTOM_RIGHT -> state.bottomRight
                        else -> return@detectDragGestures
                    }

                    state.kropCorner?.let { corner ->

                        calculateNewCropRect(
                            draggedCorner = draggedCorner,
                            fixedCorner = fixedCorner,
                            dragDelta = dragAmount,
                            cornerType = corner,
                            aspectRatio = aspectRatio,
                            minSize = cropSizeLimit,
                            canvasWidth = maxX,
                            canvasHeight = maxY
                        )?.let { (newTopLeft, newBottomRight) ->

                            val boundedTopLeftX = newTopLeft.x.coerceIn(
                                minX..maxX - cropSizeLimit
                            )

                            val boundedTopLeftY = newTopLeft.y.coerceIn(
                                minY..maxY - cropSizeLimit
                            )

                            val boundedBottomRightX = newBottomRight.x.coerceIn(
                                minX + cropSizeLimit..maxX
                            )

                            val boundedBottomRightY = newBottomRight.y.coerceIn(
                                minY + cropSizeLimit..maxY
                            )

                            var finalWidth = boundedBottomRightX - boundedTopLeftX
                            var finalHeight = boundedBottomRightY - boundedTopLeftY

                            if (finalWidth / aspectRatio > finalHeight) {

                                finalWidth = finalHeight * aspectRatio
                            } else {

                                finalHeight = finalWidth / aspectRatio
                            }

                            if (finalWidth >= cropSizeLimit && finalHeight >= cropSizeLimit) {

                                when (state.kropCorner) {

                                    KropCorner.TOP_LEFT -> {

                                        state.topLeft = Offset(
                                            x = boundedBottomRightX - finalWidth,
                                            y = boundedBottomRightY - finalHeight
                                        )

                                        state.bottomRight = Offset(
                                            x = boundedBottomRightX,
                                            y = boundedBottomRightY
                                        )
                                    }

                                    KropCorner.TOP_RIGHT -> {

                                        state.topLeft = Offset(
                                            x = boundedTopLeftX,
                                            y = boundedBottomRightY - finalHeight
                                        )

                                        state.bottomRight = Offset(
                                            x = boundedTopLeftX + finalWidth,
                                            y = boundedBottomRightY
                                        )
                                    }

                                    KropCorner.BOTTOM_LEFT -> {

                                        state.topLeft = Offset(
                                            x = boundedBottomRightX - finalWidth,
                                            y = boundedTopLeftY
                                        )

                                        state.bottomRight = Offset(
                                            x = boundedBottomRightX,
                                            y = boundedTopLeftY + finalHeight
                                        )
                                    }

                                    KropCorner.BOTTOM_RIGHT -> {

                                        state.topLeft = Offset(
                                            x = boundedTopLeftX,
                                            y = boundedTopLeftY
                                        )

                                        state.bottomRight = Offset(
                                            x = boundedTopLeftX + finalWidth,
                                            y = boundedTopLeftY + finalHeight
                                        )
                                    }

                                    else -> Unit
                                }

                                state.topRight = Offset(state.bottomRight.x, state.topLeft.y)
                                state.bottomLeft = Offset(state.topLeft.x, state.bottomRight.y)
                            }
                        }
                    }
                }

                KropCorner.TOP_CENTRE -> {

                    val potentialNewTopY = state.topLeft.y + dragAmount.y
                    val currentHeight = state.bottomRight.y - state.topLeft.y
                    val currentWidth = state.bottomRight.x - state.topLeft.x

                    var newHeight = (state.bottomRight.y - potentialNewTopY).coerceAtLeast(
                        cropSizeLimit
                    )

                    var newWidth = newHeight * aspectRatio

                    if (state.topLeft.x + newWidth > maxX) {

                        newWidth = maxX - state.topLeft.x
                        newHeight = newWidth / aspectRatio
                    }

                    if (newWidth < cropSizeLimit) {

                        newWidth = cropSizeLimit
                        newHeight = newWidth / aspectRatio
                    }

                    val newTopY = (state.bottomRight.y - newHeight).coerceIn(
                        minY..state.bottomRight.y - cropSizeLimit
                    )

                    val finalHeight = state.bottomRight.y - newTopY
                    val finalWidth = finalHeight * aspectRatio

                    if (finalHeight >= cropSizeLimit && finalWidth >= cropSizeLimit
                        && state.topLeft.x + finalWidth <= maxX
                    ) {

                        val horizontalShift = (currentWidth - finalWidth) / 2

                        state.topLeft = Offset(state.topLeft.x + horizontalShift, newTopY)
                        state.topRight = Offset(state.bottomRight.x - horizontalShift, newTopY)
                        state.bottomLeft = state.bottomLeft.copy(x = state.topLeft.x)
                        state.bottomRight = state.bottomRight.copy(x = state.topRight.x)
                    }
                }

                KropCorner.BOTTOM_CENTRE -> {

                    val potentialNewBottomY = state.bottomRight.y + dragAmount.y
                    val currentHeight = state.bottomRight.y - state.topLeft.y
                    val currentWidth = state.bottomRight.x - state.topLeft.x

                    var newHeight = (potentialNewBottomY - state.topLeft.y).coerceAtLeast(
                        cropSizeLimit
                    )

                    var newWidth = newHeight * aspectRatio

                    if (state.topLeft.x + newWidth > maxX) {

                        newWidth = maxX - state.topLeft.x
                        newHeight = newWidth / aspectRatio
                    }

                    if (newWidth < cropSizeLimit) {

                        newWidth = cropSizeLimit
                        newHeight = newWidth / aspectRatio
                    }

                    val newBottomY = (state.topLeft.y + newHeight).coerceIn(
                        state.topLeft.y + cropSizeLimit..maxY
                    )

                    val finalHeight = newBottomY - state.topLeft.y
                    val finalWidth = finalHeight * aspectRatio

                    if (finalHeight >= cropSizeLimit && finalWidth >= cropSizeLimit
                        && state.topLeft.x + finalWidth <= maxX
                    ) {

                        val horizontalShift = (currentWidth - finalWidth) / 2

                        state.bottomLeft = Offset(state.topLeft.x + horizontalShift, newBottomY)

                        state.bottomRight = Offset(
                            x = state.bottomRight.x - horizontalShift,
                            y = newBottomY
                        )

                        state.topLeft = state.topLeft.copy(x = state.bottomLeft.x)
                        state.topRight = state.topRight.copy(x = state.bottomRight.x)

                    }
                }

                KropCorner.LEFT_CENTRE -> {

                    val potentialNewLeftX = state.topLeft.x + dragAmount.x
                    val currentWidth = state.bottomRight.x - state.topLeft.x
                    val currentHeight = state.bottomRight.y - state.topLeft.y

                    var newWidth = (state.bottomRight.x - potentialNewLeftX).coerceAtLeast(
                        cropSizeLimit
                    )

                    var newHeight = newWidth / aspectRatio

                    if (state.topLeft.y + newHeight > maxY) {

                        newHeight = maxY - state.topLeft.y
                        newWidth = newHeight * aspectRatio
                    }

                    if (newHeight < cropSizeLimit) {

                        newHeight = cropSizeLimit
                        newWidth = newHeight * aspectRatio
                    }

                    val newLeftX = (state.bottomRight.x - newWidth).coerceIn(
                        minX..state.bottomRight.x - cropSizeLimit
                    )

                    val finalWidth = state.bottomRight.x - newLeftX
                    val finalHeight = finalWidth / aspectRatio

                    if (finalWidth >= cropSizeLimit && finalHeight >= cropSizeLimit
                        && state.topLeft.y + finalHeight <= maxY
                    ) {

                        val verticalShift = (currentHeight - finalHeight) / 2

                        state.topLeft = Offset(newLeftX, state.topLeft.y + verticalShift)
                        state.bottomLeft = Offset(newLeftX, state.bottomRight.y - verticalShift)
                        state.topRight = state.topRight.copy(y = state.topLeft.y)
                        state.bottomRight = state.bottomRight.copy(y = state.bottomLeft.y)
                    }
                }

                KropCorner.RIGHT_CENTRE -> {

                    val potentialNewRightX = state.bottomRight.x + dragAmount.x
                    val currentWidth = state.bottomRight.x - state.topLeft.x
                    val currentHeight = state.bottomRight.y - state.topLeft.y

                    var newWidth = (potentialNewRightX - state.topLeft.x).coerceAtLeast(
                        cropSizeLimit
                    )

                    var newHeight = newWidth / aspectRatio

                    if (state.topLeft.y + newHeight > maxY) {

                        newHeight = maxY - state.topLeft.y
                        newWidth = newHeight * aspectRatio
                    }

                    if (newHeight < cropSizeLimit) {

                        newHeight = cropSizeLimit
                        newWidth = newHeight * aspectRatio
                    }

                    val newRightX = (state.topLeft.x + newWidth).coerceIn(
                        state.topLeft.x + cropSizeLimit..maxX
                    )

                    val finalWidth = newRightX - state.topLeft.x
                    val finalHeight = finalWidth / aspectRatio

                    if (finalWidth >= cropSizeLimit && finalHeight >= cropSizeLimit
                        && state.topLeft.y + finalHeight <= maxY
                    ) {

                        val verticalShift = (currentHeight - finalHeight) / 2

                        state.topRight = Offset(newRightX, state.topLeft.y + verticalShift)

                        state.bottomRight = Offset(
                            x = newRightX,
                            y = state.bottomRight.y - verticalShift
                        )

                        state.topLeft = state.topLeft.copy(y = state.topRight.y)
                        state.bottomLeft = state.bottomLeft.copy(y = state.bottomRight.y)
                    }
                }

                null -> if (state.isMovingCropRect) {

                    val rectWidth = (state.bottomRight.x - state.topLeft.x).coerceAtLeast(0F)
                    val rectHeight = (state.bottomRight.y - state.topLeft.y).coerceAtLeast(0F)

                    val newTopLeftX = (state.topLeft.x + dragAmount.x).coerceIn(
                        minX..maxX - rectWidth
                    )

                    val newTopLeftY = (state.topLeft.y + dragAmount.y).coerceIn(
                        minY..maxY - rectHeight
                    )

                    state.topLeft = Offset(newTopLeftX, newTopLeftY)
                    state.topRight = Offset(newTopLeftX + rectWidth, newTopLeftY)
                    state.bottomLeft = Offset(newTopLeftX, newTopLeftY + rectHeight)

                    state.bottomRight = Offset(
                        x = newTopLeftX + rectWidth,
                        y = newTopLeftY + rectHeight
                    )
                }
            }
        }
    }

    val pointerInputModifier by remember(state) {
        derivedStateOf {
            if (state.isAspectLocked) pointerInputWithAspect else pointerInputWithoutAspect
        }
    }

    val cropCanvasModifier = Modifier.drawWithContent {

        drawContent()

        drawIntoCanvas {

            drawKropOverlay(
                kropShape = state.kropShape,
                topLeft = state.topLeft,
                bottomRight = state.bottomRight,
                kropConfig = state.config
            )

            drawKropShapeBorder(
                kropShape = state.kropShape,
                topLeft = state.topLeft,
                bottomRight = state.bottomRight,
                kropConfig = state.config
            )

            drawKropBorder(
                topLeft = state.topLeft,
                rectSize = rectSize,
                kropConfig = state.config
            )

            drawPlus(
                topLeft = state.topLeft,
                rectSize = rectSize,
                kropConfig = state.config
            )

            drawHandle(
                corner = KropCorner.TOP_LEFT,
                center = state.topLeft,
                kropConfig = state.config
            )

            drawHandle(
                corner = KropCorner.TOP_RIGHT,
                center = state.topRight,
                kropConfig = state.config
            )

            drawHandle(
                corner = KropCorner.BOTTOM_LEFT,
                center = state.bottomLeft,
                kropConfig = state.config
            )

            drawHandle(
                corner = KropCorner.BOTTOM_RIGHT,
                center = state.bottomRight,
                kropConfig = state.config
            )

            drawHandle(
                corner = KropCorner.TOP_CENTRE,
                center = topCenter,
                kropConfig = state.config
            )

            drawHandle(
                corner = KropCorner.BOTTOM_CENTRE,
                center = bottomCenter,
                kropConfig = state.config
            )

            drawHandle(
                corner = KropCorner.LEFT_CENTRE,
                center = leftCenter,
                kropConfig = state.config
            )

            drawHandle(
                corner = KropCorner.RIGHT_CENTRE,
                center = rightCenter,
                kropConfig = state.config
            )
        }
    }

    LaunchedEffect(
        state.canvasSize,
        state.originalImage,
        state.kropAspectRatio,
        state.isAspectLocked
    ) {

        val isCanvasSizeValid = state.canvasSize != IntSize.Zero
        val isImageSizeValid = state.originalImage.width > 0 && state.originalImage.height > 0

        if (isCanvasSizeValid && isImageSizeValid) {

            val canvasWidth = state.canvasSize.width.toFloat()
            val canvasHeight = state.canvasSize.height.toFloat()

            var rectW: Float
            var rectH: Float
            val currentSelectedRatio = state.kropAspectRatio.ratio

            if (currentSelectedRatio != null) {

                if (canvasWidth / canvasHeight > currentSelectedRatio) {

                    rectH = canvasHeight * 0.8F
                    rectW = rectH * currentSelectedRatio
                } else {

                    rectW = canvasWidth * 0.8F
                    rectH = rectW / currentSelectedRatio
                }
            } else {

                rectW = canvasWidth.coerceAtMost(canvasHeight) * 0.8F
                rectH = rectW
            }

            rectW = rectW.coerceAtLeast(cropSizeLimit)
            rectH = rectH.coerceAtLeast(cropSizeLimit)

            if (state.isAspectLocked && currentSelectedRatio != null) {

                val checkH = rectW / currentSelectedRatio

                if (abs(checkH - rectH) > 1.0F) {

                    rectH = checkH

                    if (rectH < cropSizeLimit) {

                        rectH = cropSizeLimit
                        rectW = rectH * currentSelectedRatio
                    }
                }

                if (rectW > canvasWidth) {

                    rectW = canvasWidth
                    rectH = rectW / currentSelectedRatio
                }

                if (rectH > canvasHeight) {

                    rectH = canvasHeight
                    rectW = rectH * currentSelectedRatio
                }
            }

            if (rectW > canvasWidth) {

                rectW = canvasWidth

                rectH = if (currentSelectedRatio != null) {

                    (rectW / currentSelectedRatio).coerceAtMost(canvasHeight)
                } else {

                    rectH.coerceAtMost(canvasHeight)
                }
            }

            if (rectH > canvasHeight) {

                rectH = canvasHeight

                rectW = if (currentSelectedRatio != null) {

                    (rectH * currentSelectedRatio).coerceAtMost(canvasWidth)
                } else {

                    rectW.coerceAtMost(canvasWidth)
                }
            }

            rectW = rectW.coerceAtLeast(cropSizeLimit)
            rectH = rectH.coerceAtLeast(cropSizeLimit)

            if (state.isAspectLocked && currentSelectedRatio != null) {

                if (abs(rectW / rectH - currentSelectedRatio) > 0.01f) {

                    rectH = (rectW / currentSelectedRatio).coerceAtLeast(cropSizeLimit)

                    if (rectH > canvasHeight) {

                        rectH = canvasHeight
                        rectW = (rectH * currentSelectedRatio).coerceAtLeast(cropSizeLimit)
                    }
                }
            }

            val initialTopLeftX = (canvasWidth - rectW) / 2
            val initialTopLeftY = (canvasHeight - rectH) / 2

            state.topLeft = Offset(initialTopLeftX, initialTopLeftY)
            state.topRight = Offset(initialTopLeftX + rectW, initialTopLeftY)
            state.bottomLeft = Offset(initialTopLeftX, initialTopLeftY + rectH)
            state.bottomRight = Offset(initialTopLeftX + rectW, initialTopLeftY + rectH)
        }
    }

    KropImagePreview(
        sheetState = imagePreviewSheetState,
        state = state
    )

    KropShapeCustomizationDialog(state = state)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        ImageKropTopBar(
            modifier = Modifier.fillMaxWidth(),
            state = state,
            imagePreviewSheetState = imagePreviewSheetState,
            onKropFinished = onKropFinished,
            onNavigateBack = onNavigateBack
        )

        BoxWithConstraints(
            modifier = Modifier.weight(weight = 1.0F),
            contentAlignment = Alignment.Center
        ) {

            Image(
                modifier = Modifier
                    .then(imagePlacedModifier)
                    .then(pointerInputModifier)
                    .then(cropCanvasModifier),
                bitmap = state.originalImage,
                contentScale = ContentScale.Fit,
                contentDescription = "Image View"
            )
        }

        ImageKropBottomBar(
            modifier = Modifier.fillMaxWidth(),
            state = state
        )
    }
}