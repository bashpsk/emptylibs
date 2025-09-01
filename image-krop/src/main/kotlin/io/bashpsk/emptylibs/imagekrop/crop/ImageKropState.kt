package io.bashpsk.emptylibs.imagekrop.crop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import io.bashpsk.emptylibs.imagekrop.crop.KropCorner.Companion.hasCornerCenter
import io.bashpsk.emptylibs.imagekrop.offset.coerceAtLeast
import io.bashpsk.emptylibs.imagekrop.offset.getKropCorner
import io.bashpsk.emptylibs.imagekrop.offset.itemRect
import io.bashpsk.emptylibs.imageutils.extension.sameAs
import io.bashpsk.emptylibs.imageutils.shape.BasicImageShapes
import io.bashpsk.emptylibs.imageutils.shape.ImageShape
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlin.math.abs

/**
 * Composable function to remember an [ImageKropState] instance.
 *
 * This function creates and remembers an [ImageKropState] which holds the state
 * for the image cropping functionality. It uses [rememberSaveable] to ensure
 * the state is preserved across configuration changes.
 *
 * @param imageBitmap The initial [ImageBitmap] to be cropped.
 * @param config The [KropConfig] to configure the cropping behavior. Defaults to
 * [KropConfig.surfaceBased].
 * @param aspectList An immutable list of [KropAspectRatio] options available for cropping.
 * Defaults to [KropAspectRatio.Basic].
 * @return An instance of [ImageKropState].
 */
@Composable
fun rememberImageKropState(
    imageBitmap: ImageBitmap,
    config: KropConfig = KropConfig.surfaceBased(),
    aspectList: ImmutableList<KropAspectRatio> = KropAspectRatio.Basic
): ImageKropState {

    val density = LocalDensity.current

    return rememberSaveable(
        imageBitmap,
        config,
        aspectList,
        density,
        saver = ImageKropState.StateSaver(density = density)
    ) {

        ImageKropState(
            imageBitmap = imageBitmap,
            config = config,
            aspectList = aspectList,
            density = density
        )
    }
}

/**
 * State object for managing image cropping operations.
 *
 * This class holds the state of the image being cropped, including the original image,
 * any modified versions, the current crop parameters (aspect ratio, shape), and UI state
 * related to cropping controls.
 *
 * It provides functions to update the image, aspect ratio, shape, and manage a list
 * of image versions for undo functionality.
 *
 * @param imageBitmap The initial [ImageBitmap] to be cropped. This is the base image.
 * @param config The [KropConfig] to be used for the cropping operations.
 * @param aspectList An immutable list of [KropAspectRatio] options available for cropping.
 */
class ImageKropState(
    val imageBitmap: ImageBitmap,
    val config: KropConfig,
    val aspectList: ImmutableList<KropAspectRatio>,
    val density: Density
) {

    /**
     * A persistent list of [ImageShape] objects available for cropping.
     * This list defines the different shapes that can be used for the crop area.
     * It is initialized with [BasicImageShapes].
     * The list can be updated using the [updateShapeList] function.
     */
    var shapeList: PersistentList<ImageShape> = BasicImageShapes

    /**
     * The original image bitmap that is being cropped.
     * This is the image that was initially provided to the `ImageKropState`.
     * It can be updated using the [updateOriginalImage] function.
     */
    var originalImage by mutableStateOf(imageBitmap)
        private set

    /**
     * The image after applying modifications (e.g., cropping).
     * This is null if no modifications have been made yet.
     */
    var modifiedImage by mutableStateOf<ImageBitmap?>(null)
        private set

    /**
     * The current preview image, which is a temporarily cropped version of the image.
     * This is updated when the user interacts with the cropping UI but hasn't finalized the crop.
     * It can be null if no preview has been generated yet.
     */
    var previewImage by mutableStateOf<ImageBitmap?>(null)
        private set

    /**
     * A list of images that have been processed by the cropper.
     * The first image in the list is the original image.
     * Subsequent images are the results of cropping operations.
     * It used for image undo functionality.
     * It can be updated using the [addImage] function.
     */
    var imageList by mutableStateOf(persistentListOf(imageBitmap))
        private set

    /**
     * The aspect ratio of the crop selection.
     * This determines the shape of the cropping rectangle.
     * It is initialized with [KropAspectRatio.Ratio1to1].
     * The value can be changed using [updateAspectRatio].
     */
    var kropAspectRatio by mutableStateOf(KropAspectRatio.Ratio1to1)
        private set

    /**
     * Whether the aspect ratio of the crop rect is locked.
     * When locked, the crop rect can only be resized proportionally.
     * This property is observed by the UI to update the aspect ratio lock button.
     * The value can be updated using the [updateAspectLocked] method.
     */
    var isAspectLocked by mutableStateOf(false)
        private set

    /**
     * The current shape of the crop area.
     * This determines the visual appearance of the cropping rectangle.
     * It can be updated using [updateKropShape].
     * The default value is [ImageShape.None].
     */
    var kropShape by mutableStateOf<ImageShape>(ImageShape.None)
        private set

    /**
     * The corner of the crop rectangle that is currently being dragged, or null if no corner is
     * being dragged.
     * This is used to determine which corner to resize when the user drags the crop rectangle.
     */
    internal var currentCorner by mutableStateOf<KropCorner?>(null)

    /**
     * The size of the canvas where the image is drawn.
     * This is used to calculate the correct scaling and positioning of the crop rectangle.
     */
    internal var canvasSize by mutableStateOf(Size.Zero)

    /**
     * The current position of the crop rectangle, represented by its top-left corner's offset.
     * This is used internally to track the location of the crop rectangle on the canvas.
     */
    internal var kropRectPosition by mutableStateOf(Offset.Zero)

    /**
     * The size of the crop rectangle.
     * This is used to draw the crop rectangle on the canvas.
     */
    internal var kropRectSize by mutableStateOf(Size.Zero)

    /**
     * Whether the aspect ratio selection menu is currently expanded.
     * This is an internal state used to control the visibility of the aspect ratio options.
     */
    internal var isAspectRatioMenuExpanded by mutableStateOf(false)

    /**
     * Whether the shape selection menu is currently expanded.
     * When `true`, the menu is visible and allows the user to choose a crop shape.
     * When `false`, the menu is collapsed.
     */
    internal var isShapeMenuExpanded by mutableStateOf(false)

    internal var isKropShapeCustomizationDialog by mutableStateOf(false)

    /**
     * Updates the original image.
     *
     * This function is used to update the original image that will be used for cropping.
     *
     * @param bitmap The new original image.
     */
    fun updateOriginalImage(bitmap: ImageBitmap) {

        originalImage = bitmap
    }

    /**
     * Updates the modified image with the provided [bitmap] and adds it to the [imageList].
     *
     * @param bitmap The [ImageBitmap] to set as the modified image.
     */
    fun updateModifiedImage(bitmap: ImageBitmap) {

        modifiedImage = bitmap
        addImage(bitmap = bitmap)
    }

    /**
     * Updates the preview image with the given bitmap.
     *
     * @param bitmap The [ImageBitmap] to set as the preview image.
     */
    fun updatePreviewImage(bitmap: ImageBitmap) {

        previewImage = bitmap
    }

    /**
     * Adds an image to the list of images.
     *
     * If the image already exists in the list, it will be removed and re-added to ensure it's the
     * latest version.
     *
     * @param bitmap The [ImageBitmap] to add to the list.
     */
    fun addImage(bitmap: ImageBitmap) {

        val safeImageList = existImageIndex(bitmap = bitmap)?.let { index ->

            imageList.removeAt(index)
        } ?: imageList

        imageList = safeImageList.add(element = bitmap)
    }

    /**
     * Removes the last image from the list of images.
     * If the list is not empty after removal, the original image is updated to the new last image
     * in the list.
     * This function effectively provides an "undo" capability for image modifications.
     */
    fun removeLastImage() {

        imageList.lastIndex.takeIf { index -> index > 0 }?.let { index ->

            imageList = imageList.removeAt(index = index)
        }

        imageList.lastOrNull()?.let { bitmap ->

            updateOriginalImage(bitmap = bitmap)
        }
    }

    /**
     * Clears all modified images and resets the image list to the original image.
     * This function is useful for undoing all changes and starting over with the original image.
     */
    fun clearImages() {

        imageList = persistentListOf(imageBitmap)
    }

    /**
     * Updates the aspect ratio for cropping.
     *
     * @param aspect The new aspect ratio to be applied.
     */
    fun updateAspectRatio(aspect: KropAspectRatio) {

        kropAspectRatio = aspect
    }

    /**
     * Updates the aspect lock state.
     *
     * @param locked True if the aspect ratio should be locked, false otherwise.
     */
    fun updateAspectLocked(locked: Boolean) {

        isAspectLocked = locked
    }

    /**
     * Updates the current crop shape.
     *
     * @param shape The new [ImageShape] to set.
     */
    fun updateKropShape(shape: ImageShape) {

        kropShape = shape
    }

    /**
     * Updates an existing shape in the [shapeList] or adds it if it doesn't exist.
     *
     * If a shape with the same type as the provided [shape] already exists in the list,
     * it will be replaced with the new [shape]. Otherwise, the new [shape] is added to the list.
     *
     * This function is useful for managing a list of customizable shapes where users can modify
     * existing shapes or add new ones.
     *
     * @param shape The [ImageShape] to update or add to the list.
     */
    fun updateShapeList(shape: ImageShape) {

        shapeList = existShapeIndex(shape = shape)?.let { index ->

            shapeList.set(index = index, element = shape)
        } ?: shapeList
    }

    /**
     * Checks if an image bitmap already exists in the `imageList`.
     *
     * @param bitmap The [ImageBitmap] to search for.
     * @return The index of the existing bitmap in the `imageList` if found and the index is greater
     * than 0,
     * otherwise null. This ensures that the original image (at index 0) is not considered as an
     * existing image for replacement purposes.
     */
    internal fun existImageIndex(bitmap: ImageBitmap): Int? {

        return imageList.indexOfFirst { bitmapItem ->

            bitmapItem.sameAs(bitmap)
        }.takeIf { index -> index > 0 }
    }

    /**
     * Checks if a [ImageShape] already exists in the `shapeList`.
     *
     * This function iterates through the `shapeList` and compares the class of each `shapeItem`
     * with the class of the provided `shape`. If a shape with the same class is found, its index
     * is returned.
     *
     * @param shape The [ImageShape] to search for in the `shapeList`.
     * @return The index of the existing shape in the `shapeList` if found, otherwise null.
     */
    internal fun existShapeIndex(shape: ImageShape): Int? {

        return shapeList.indexOfFirst { shapeItem ->

            shapeItem::class == shape::class
        }.takeIf { index -> index >= 0 }
    }

    /**
     * Crops the original image based on the provided parameters.
     *
     * This function takes a crop rectangle, canvas size, optional image flip, and image shape
     * to produce a cropped version of the `originalImage`.
     *
     * @param imageRect The [Rect] defining the area to crop from the original image.
     * @param imageCanvasSize The [IntSize] of the canvas on which the image is displayed.
     * This is used for scaling calculations.
     * @param imageFlip An optional [KropImageFlip] value to flip the image horizontally or
     * vertically before cropping. Defaults to null (no flip).
     * @param imageShape The [ImageShape] to apply to the cropped image. Defaults to
     * [ImageShape.None].
     * @return A [KropResult] object which can be either [KropResult.Success] containing the
     * cropped [ImageBitmap] or [KropResult.Failed] if an error occurred during cropping.
     */
    internal suspend fun getCroppedImageBitmap(
        imageRect: Rect? = null,
        imageCanvasSize: Size? = null,
        imageFlip: KropImageFlip? = null,
        imageShape: ImageShape? = null
    ): KropResult {

        return originalImage.getCroppedImageBitmap(
            cropRect = imageRect ?: kropRectSize.itemRect(kropRectPosition),
            canvasSize = imageCanvasSize ?: canvasSize,
            imageFlip = imageFlip,
            kropShape = imageShape ?: kropShape
        )
    }

    internal fun onKropRectInitialized() {

        val sizeLimit = config.minimumCropSize.toPixel(density = density)

        var rectW: Float
        var rectH: Float

        kropAspectRatio.ratio?.let { ratio ->

            (canvasSize.width / canvasSize.height > ratio).takeIf { it }?.run {

                rectH = canvasSize.height * 0.8F
                rectW = rectH * ratio
            } ?: run {

                rectW = canvasSize.width * 0.8F
                rectH = rectW / ratio
            }
        } ?: run {

            rectW = canvasSize.width.coerceAtMost(canvasSize.height) * 0.8F
            rectH = rectW
        }

        rectW = rectW.coerceAtLeast(sizeLimit)
        rectH = rectH.coerceAtLeast(sizeLimit)

        kropAspectRatio.ratio?.takeIf { isAspectLocked }?.let { ratio ->

            (abs((rectW / ratio) - rectH) > 1.0F).takeIf { it }?.run {

                rectH = rectW / ratio

                (rectH < sizeLimit).takeIf { it }?.run {

                    rectH = sizeLimit
                    rectW = rectH * ratio
                }
            }

            (rectW > canvasSize.width).takeIf { it }?.run {

                rectW = canvasSize.width
                rectH = rectW / ratio
            }

            (rectH > canvasSize.height).takeIf { it }?.run {

                rectH = canvasSize.height
                rectW = rectH * ratio
            }
        }

        (rectW > canvasSize.width).takeIf { it }?.run {

            rectW = canvasSize.width

            rectH = kropAspectRatio.ratio?.let { ratio ->

                (rectW / ratio).coerceAtMost(canvasSize.height)
            } ?: rectH.coerceAtMost(canvasSize.height)
        }

        (rectH > canvasSize.height).takeIf { it }?.run {

            rectH = canvasSize.height

            rectW = kropAspectRatio.ratio?.let { ratio ->

                (rectH * ratio).coerceAtMost(canvasSize.width)
            } ?: rectW.coerceAtMost(canvasSize.width)
        }

        rectW = rectW.coerceAtLeast(sizeLimit)
        rectH = rectH.coerceAtLeast(sizeLimit)

        kropAspectRatio.ratio?.takeIf { isAspectLocked }?.let { ratio ->

            (abs(rectW / rectH - ratio) > 0.01F).takeIf { it }?.run {

                val targetH = (rectW / ratio).coerceAtLeast(sizeLimit)

                (targetH <= canvasSize.height).takeIf { it }?.run {

                    rectH = targetH
                } ?: run {

                    val targetW = (rectH * ratio).coerceAtLeast(sizeLimit)

                    (targetW <= canvasSize.width).takeIf { it }?.run {

                        rectW = targetW
                    }
                }
            }
        }

        val finalRectW = rectW.coerceIn(sizeLimit, canvasSize.width)
        val finalRectH = rectH.coerceIn(sizeLimit, canvasSize.height)

        val initialTopLeftX = (canvasSize.width - finalRectW) / 2
        val initialTopLeftY = (canvasSize.height - finalRectH) / 2

        kropRectPosition = Offset(initialTopLeftX, initialTopLeftY)
        kropRectSize = Size(finalRectW, finalRectH)
    }

    internal fun onKropStart(position: Offset) {

        currentCorner = kropRectSize.itemRect(position = kropRectPosition).getKropCorner(
            clickPosition = position,
            threshold = maxOf(config.handleWidth, config.handleHeight).toPixel(density)
        )
    }

    internal fun onKropEnd() {

        currentCorner = null
    }

    internal fun onKropChanges(position: Offset, amount: Offset) {

        getKropRectPositionSize(
            position = kropRectPosition,
            size = kropRectSize,
            amount = amount
        ).let { (newPosition, newSize) ->

            kropRectPosition = newPosition
            kropRectSize = newSize
        }
    }

    internal fun getKropRectPositionSize(
        position: Offset,
        size: Size,
        amount: Offset,
    ): Pair<Offset, Size> {

        val sizeLimit = config.minimumCropSize.toPixel(density = density)
        val minX = 0.0F
        val minY = 0.0F
        val maxX = canvasSize.width
        val maxY = canvasSize.height
        val initialRect = size.itemRect(position = position)

        var calculatedTopLeft = initialRect.topLeft
        var calculatedSize = initialRect.size

        val targetAspectRatio = kropAspectRatio.ratio?.takeIf { isAspectLocked }

        currentCorner.takeIf { corner -> corner != null }?.let { corner ->

            when (corner) {

                KropCorner.TOP_LEFT, KropCorner.TOP_RIGHT, KropCorner.BOTTOM_LEFT,
                KropCorner.BOTTOM_RIGHT -> {

                    val fixedCorner = when (currentCorner) {

                        KropCorner.TOP_LEFT -> initialRect.bottomRight
                        KropCorner.TOP_RIGHT -> Offset(initialRect.left, initialRect.bottom)
                        KropCorner.BOTTOM_LEFT -> Offset(initialRect.right, initialRect.top)
                        KropCorner.BOTTOM_RIGHT -> initialRect.topLeft
                        else -> initialRect.topLeft
                    }

                    val draggedPoint = when (currentCorner) {

                        KropCorner.TOP_LEFT -> initialRect.topLeft
                        KropCorner.TOP_RIGHT -> initialRect.topRight
                        KropCorner.BOTTOM_LEFT -> initialRect.bottomLeft
                        KropCorner.BOTTOM_RIGHT -> initialRect.bottomRight
                        else -> initialRect.topLeft
                    }

                    calculateNewEditRect(
                        draggedCorner = draggedPoint,
                        fixedCorner = fixedCorner,
                        dragDelta = amount,
                        cornerType = corner,
                        aspectRatio = targetAspectRatio,
                        minSize = sizeLimit,
                        canvasWidth = maxX,
                        canvasHeight = maxY
                    )?.let { (newRectTopLeft, newRectBottomRight) ->

                        calculatedTopLeft = newRectTopLeft
                        calculatedSize = Size(
                            width = (newRectBottomRight.x - newRectTopLeft.x).coerceAtLeast(
                                sizeLimit
                            ),
                            height = (newRectBottomRight.y - newRectTopLeft.y).coerceAtLeast(
                                sizeLimit
                            )
                        )
                    } ?: run {

                        calculatedTopLeft = initialRect.topLeft
                        calculatedSize = initialRect.size
                    }
                }

                KropCorner.TOP_CENTRE, KropCorner.BOTTOM_CENTRE, KropCorner.LEFT_CENTRE,
                KropCorner.RIGHT_CENTRE -> {

                    var tempRect = initialRect

                    when (currentCorner) {

                        KropCorner.TOP_CENTRE -> {

                            val newTop = (tempRect.top + amount.y).coerceIn(
                                minY..tempRect.bottom - sizeLimit
                            )

                            tempRect = tempRect.copy(top = newTop)
                        }

                        KropCorner.BOTTOM_CENTRE -> {

                            val newBottom = (tempRect.bottom + amount.y).coerceIn(
                                tempRect.top + sizeLimit..maxY
                            )

                            tempRect = tempRect.copy(bottom = newBottom)
                        }

                        KropCorner.LEFT_CENTRE -> {

                            val newLeft = (tempRect.left + amount.x).coerceIn(
                                minX..tempRect.right - sizeLimit
                            )

                            tempRect = tempRect.copy(left = newLeft)
                        }

                        KropCorner.RIGHT_CENTRE -> {

                            val newRight = (tempRect.right + amount.x).coerceIn(
                                tempRect.left + sizeLimit..maxX
                            )

                            tempRect = tempRect.copy(right = newRight)
                        }

                        else -> {}
                    }

                    targetAspectRatio?.let { ratio ->

                        var width = tempRect.width.coerceAtLeast(sizeLimit)
                        var height = tempRect.height.coerceAtLeast(sizeLimit)
                        val originalCenterX = tempRect.left + width / 2
                        val originalCenterY = tempRect.top + height / 2

                        when (currentCorner) {

                            KropCorner.TOP_CENTRE, KropCorner.BOTTOM_CENTRE -> {

                                height = tempRect.height.coerceAtLeast(sizeLimit)

                                val idealWidth = (height * ratio).coerceAtLeast(sizeLimit)
                                var newLeft = originalCenterX - idealWidth / 2
                                var newRight = newLeft + idealWidth

                                if (newLeft < minX) {

                                    newLeft = minX
                                    newRight = (newLeft + idealWidth).coerceAtMost(maxX)
                                } else if (newRight > maxX) {

                                    newRight = maxX
                                    newLeft = (newRight - idealWidth).coerceAtLeast(minX)
                                }

                                width = (newRight - newLeft).coerceAtLeast(sizeLimit)

                                val verticalAnchor = when (currentCorner) {
                                    KropCorner.TOP_CENTRE -> tempRect.bottom - tempRect.height
                                    else -> tempRect.top
                                }

                                height = (width / ratio).coerceIn(
                                    sizeLimit..maxY - verticalAnchor
                                )

                                tempRect = when (currentCorner) {

                                    KropCorner.TOP_CENTRE -> Rect(
                                        left = newLeft,
                                        top = (tempRect.bottom - height).coerceAtLeast(minY),
                                        right = newRight,
                                        bottom = tempRect.bottom
                                    )

                                    else -> Rect(
                                        left = newLeft,
                                        top = tempRect.top,
                                        right = newRight,
                                        bottom = (tempRect.top + height).coerceAtMost(maxY)
                                    )
                                }
                            }

                            KropCorner.LEFT_CENTRE, KropCorner.RIGHT_CENTRE -> {

                                width = tempRect.width.coerceAtLeast(sizeLimit)

                                val idealHeight = (width / ratio).coerceAtLeast(sizeLimit)
                                var newTop = originalCenterY - idealHeight / 2
                                var newBottom = newTop + idealHeight

                                if (newTop < minY) {

                                    newTop = minY
                                    newBottom = (newTop + idealHeight).coerceAtMost(maxY)
                                } else if (newBottom > maxY) {

                                    newBottom = maxY
                                    newTop = (newBottom - idealHeight).coerceAtLeast(minY)
                                }

                                height = (newBottom - newTop).coerceAtLeast(sizeLimit)

                                val horizontalAnchor = when (currentCorner) {

                                    KropCorner.LEFT_CENTRE -> tempRect.right - tempRect.width
                                    else -> tempRect.left
                                }

                                width = (height * ratio).coerceIn(
                                    sizeLimit, maxX - horizontalAnchor
                                )

                                tempRect = when (currentCorner) {

                                    KropCorner.LEFT_CENTRE -> Rect(
                                        left = (tempRect.right - width).coerceAtLeast(minX),
                                        top = newTop,
                                        right = tempRect.right,
                                        bottom = newBottom
                                    )

                                    else -> Rect(
                                        left = tempRect.left,
                                        top = newTop,
                                        right = (tempRect.left + width).coerceAtMost(maxX),
                                        bottom = newBottom
                                    )
                                }
                            }

                            else -> {}
                        }
                    }

                    calculatedTopLeft = tempRect.topLeft

                    calculatedSize = tempRect.size.coerceAtLeast(
                        width = sizeLimit,
                        height = sizeLimit
                    )
                }
            }

            var finalTopLeft = calculatedTopLeft
            var finalWidth = calculatedSize.width.coerceAtLeast(sizeLimit)
            var finalHeight = calculatedSize.height.coerceAtLeast(sizeLimit)

            finalTopLeft = finalTopLeft.copy(
                x = finalTopLeft.x.coerceIn(minX, (maxX - finalWidth).coerceAtLeast(minX)),
                y = finalTopLeft.y.coerceIn(minY, (maxY - finalHeight).coerceAtLeast(minY))
            )

            finalWidth = (maxX - finalTopLeft.x).coerceAtLeast(sizeLimit).coerceAtMost(finalWidth)
            finalHeight = (maxY - finalTopLeft.y).coerceAtLeast(sizeLimit).coerceAtMost(finalHeight)

            targetAspectRatio?.let { ratio ->

                val hBasedOnW = (finalWidth / ratio).coerceAtLeast(sizeLimit)
                val wBasedOnH = (finalHeight * ratio).coerceAtLeast(sizeLimit)

                val canFitHBasedOnW = finalTopLeft.y + hBasedOnW <= maxY + 0.01F
                val canFitWBasedOnH = finalTopLeft.x + wBasedOnH <= maxX + 0.01F

                when {

                    canFitHBasedOnW && (!canFitWBasedOnH || abs(finalWidth / hBasedOnW - ratio)
                            < abs(wBasedOnH / finalHeight - ratio)) -> finalHeight = hBasedOnW

                    canFitWBasedOnH -> finalWidth = wBasedOnH

                    else -> {

                        var h = (finalWidth / ratio).coerceIn(sizeLimit, maxY - finalTopLeft.y)
                        val w = (h * ratio).coerceIn(sizeLimit, maxX - finalTopLeft.x)

                        h = (w / ratio).coerceIn(sizeLimit, maxY - finalTopLeft.y)
                        finalWidth = w
                        finalHeight = h
                    }
                }
            }

            finalTopLeft = finalTopLeft.copy(
                x = finalTopLeft.x.coerceIn(minX, (maxX - finalWidth).coerceAtLeast(minX)),
                y = finalTopLeft.y.coerceIn(minY, (maxY - finalHeight).coerceAtLeast(minY))
            )

            calculatedSize = Size(
                width = finalWidth.coerceAtMost(maxX - finalTopLeft.x).coerceAtLeast(sizeLimit),
                height = finalHeight.coerceAtMost(maxY - finalTopLeft.y).coerceAtLeast(sizeLimit)
            )

            calculatedTopLeft = finalTopLeft.copy(
                x = (maxX - calculatedSize.width).takeIf { xPosition ->

                    finalTopLeft.x > xPosition && xPosition >= minX
                } ?: finalTopLeft.x,
                y = (maxY - calculatedSize.height).takeIf { yPosition ->

                    finalTopLeft.y > yPosition && yPosition >= minY
                } ?: finalTopLeft.y
            )

            calculatedTopLeft = calculatedTopLeft.copy(
                x = calculatedTopLeft.x.coerceAtLeast(minX),
                y = calculatedTopLeft.y.coerceAtLeast(minY)
            )
        } ?: run {

            val currentWidth = initialRect.width.coerceAtLeast(sizeLimit)
            val currentHeight = initialRect.height.coerceAtLeast(sizeLimit)

            val newLeft = (initialRect.left + amount.x).coerceIn(
                minX..(maxX - currentWidth).coerceAtLeast(minX)
            )
            val newTop = (initialRect.top + amount.y).coerceIn(
                minY..(maxY - currentHeight).coerceAtLeast(minY)
            )

            calculatedTopLeft = Offset(newLeft, newTop)
            calculatedSize = Size(currentWidth, currentHeight)
        }

        return Pair(calculatedTopLeft, calculatedSize)
    }


    /**
     * Calculates the new top-left and bottom-right points of a rectangle
     * after a corner drag, maintaining a given aspect ratio.
     *
     * @param draggedCorner The current position of the corner being dragged.
     * @param fixedCorner The position of the corner opposite to the dragged corner
     * (this corner stays fixed).
     * @param dragDelta The amount by which the draggedCornerCurrent has been moved.
     * @param cornerType The specific corner being dragged
     * (TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT).
     * @param aspectRatio The desired aspect ratio (width / height).
     * @param minSize The minimum allowed size (width or height) for the rectangle.
     * @param canvasWidth The maximum width of the canvas.
     * @param canvasHeight The maximum height of the canvas.
     * @return A Pair of new topLeft and bottomRight Offsets, or null if the drag is invalid.
     */
    private fun calculateNewEditRect(
        draggedCorner: Offset,
        fixedCorner: Offset,
        dragDelta: Offset,
        cornerType: KropCorner,
        aspectRatio: Float?,
        minSize: Float,
        canvasWidth: Float,
        canvasHeight: Float
    ): Pair<Offset, Offset>? {

        val newPosition = draggedCorner + dragDelta

        cornerType.takeIf { corner -> corner.hasCornerCenter() }?.let { corner ->

            var currentTopLeft = Offset(
                minOf(draggedCorner.x, fixedCorner.x),
                minOf(draggedCorner.y, fixedCorner.y)
            )
            var currentBottomRight = Offset(
                maxOf(draggedCorner.x, fixedCorner.x),
                maxOf(draggedCorner.y, fixedCorner.y)
            )

            when (corner) {

                KropCorner.TOP_CENTRE -> {

                    val newY = newPosition.y.coerceIn(0f, fixedCorner.y - minSize)

                    currentTopLeft = currentTopLeft.copy(y = newY)
                    currentBottomRight = currentBottomRight.copy(y = fixedCorner.y)
                }

                KropCorner.BOTTOM_CENTRE -> {

                    val newY = newPosition.y.coerceIn(fixedCorner.y + minSize, canvasHeight)

                    currentTopLeft = currentTopLeft.copy(y = fixedCorner.y)
                    currentBottomRight = currentBottomRight.copy(y = newY)
                }

                KropCorner.LEFT_CENTRE -> {

                    val newX = newPosition.x.coerceIn(0f, fixedCorner.x - minSize)

                    currentTopLeft = currentTopLeft.copy(x = newX)
                    currentBottomRight = currentBottomRight.copy(x = fixedCorner.x)
                }

                KropCorner.RIGHT_CENTRE -> {

                    val newX = newPosition.x.coerceIn(fixedCorner.x + minSize, canvasWidth)

                    currentTopLeft = currentTopLeft.copy(x = fixedCorner.x)
                    currentBottomRight = currentBottomRight.copy(x = newX)
                }

                else -> return null
            }

            (cornerType == KropCorner.TOP_CENTRE
                    || cornerType == KropCorner.BOTTOM_CENTRE).takeIf { isHorizontal ->

                isHorizontal && (currentBottomRight.y - currentTopLeft.y < minSize)
            }?.run {

                when (cornerType) {

                    KropCorner.TOP_CENTRE -> currentTopLeft = currentTopLeft.copy(
                        y = currentBottomRight.y - minSize
                    )

                    else -> currentBottomRight = currentBottomRight.copy(
                        y = currentTopLeft.y + minSize
                    )
                }
            } ?: (cornerType == KropCorner.LEFT_CENTRE
                    || cornerType == KropCorner.RIGHT_CENTRE).takeIf { isVertical ->

                isVertical && (currentBottomRight.x - currentTopLeft.x < minSize)
            }?.run {

                when (cornerType) {
                    KropCorner.LEFT_CENTRE -> currentTopLeft = currentTopLeft.copy(
                        x = currentBottomRight.x - minSize
                    )

                    else -> currentBottomRight = currentBottomRight.copy(
                        x = currentTopLeft.x + minSize
                    )
                }
            }

            var finalTopLeft = currentTopLeft.copy(
                x = currentTopLeft.x.coerceIn(0f, canvasWidth - minSize),
                y = currentTopLeft.y.coerceIn(0f, canvasHeight - minSize)
            )

            var finalBottomRight = currentBottomRight.copy(
                x = currentBottomRight.x.coerceIn(finalTopLeft.x + minSize, canvasWidth),
                y = currentBottomRight.y.coerceIn(finalTopLeft.y + minSize, canvasHeight)
            )

            val finalWidth = (finalBottomRight.x - finalTopLeft.x).coerceAtLeast(minSize)
            val finalHeight = (finalBottomRight.y - finalTopLeft.y).coerceAtLeast(minSize)

            finalTopLeft = finalTopLeft.copy(
                x = (finalBottomRight.x - finalWidth).coerceIn(0f, canvasWidth - minSize),
                y = (finalBottomRight.y - finalHeight).coerceIn(0f, canvasHeight - minSize)
            )
            finalBottomRight = finalBottomRight.copy(
                x = (finalTopLeft.x + finalWidth).coerceIn(finalTopLeft.x + minSize, canvasWidth),
                y = (finalTopLeft.y + finalHeight).coerceIn(finalTopLeft.y + minSize, canvasHeight)
            )

            ((finalBottomRight.x - finalTopLeft.x) < minSize
                    || (finalBottomRight.y - finalTopLeft.y) < minSize).takeIf { isNotValid ->

                isNotValid
            }?.run { return null }

            return Pair(finalTopLeft, finalBottomRight)
        }

        val proposedWidth = when (cornerType) {

            KropCorner.TOP_LEFT, KropCorner.BOTTOM_LEFT -> fixedCorner.x - newPosition.x
            KropCorner.TOP_RIGHT, KropCorner.BOTTOM_RIGHT -> newPosition.x - fixedCorner.x
            else -> return null
        }.coerceAtLeast(minSize)

        var adjustedWidth = proposedWidth
        var adjustedHeight = aspectRatio?.let { ratio ->

            (proposedWidth / ratio).coerceAtLeast(minSize)
        } ?: run {

            when (cornerType) {

                KropCorner.TOP_LEFT, KropCorner.TOP_RIGHT -> (fixedCorner.y - newPosition.y)
                KropCorner.BOTTOM_LEFT, KropCorner.BOTTOM_RIGHT -> (newPosition.y - fixedCorner.y)
                else -> return null
            }.coerceAtLeast(minSize)
        }

        aspectRatio?.takeIf { adjustedHeight < minSize }?.let { ratio ->

            adjustedHeight = minSize
            adjustedWidth = (adjustedHeight * ratio).coerceAtLeast(minSize)
        } ?: run {

            adjustedWidth = adjustedWidth.coerceAtLeast(minSize)
        }

        val (initialTopLeft, initialBottomRight) = when (cornerType) {

            KropCorner.TOP_LEFT -> Offset(
                x = fixedCorner.x - adjustedWidth,
                y = fixedCorner.y - adjustedHeight
            ) to fixedCorner

            KropCorner.TOP_RIGHT -> Offset(
                x = fixedCorner.x,
                y = fixedCorner.y - adjustedHeight
            ) to Offset(
                x = fixedCorner.x + adjustedWidth,
                y = fixedCorner.y
            )

            KropCorner.BOTTOM_LEFT -> Offset(
                x = fixedCorner.x - adjustedWidth,
                y = fixedCorner.y
            ) to Offset(
                x = fixedCorner.x,
                y = fixedCorner.y + adjustedHeight
            )

            KropCorner.BOTTOM_RIGHT -> fixedCorner to Offset(
                x = fixedCorner.x + adjustedWidth,
                y = fixedCorner.y + adjustedHeight
            )

            else -> return null
        }

        var finalTopLeft = initialTopLeft.copy(
            x = initialTopLeft.x.coerceIn(0.0F, canvasWidth - minSize),
            y = initialTopLeft.y.coerceIn(0.0F, canvasHeight - minSize)
        )

        var finalBottomRight = initialBottomRight.copy(
            x = initialBottomRight.x.coerceIn(finalTopLeft.x + minSize, canvasWidth),
            y = initialBottomRight.y.coerceIn(finalTopLeft.y + minSize, canvasHeight)
        )

        finalTopLeft = finalTopLeft.copy(
            x = finalTopLeft.x.coerceAtMost(finalBottomRight.x - minSize),
            y = finalTopLeft.y.coerceAtMost(finalBottomRight.y - minSize)
        )

        finalBottomRight = finalBottomRight.copy(
            x = finalBottomRight.x.coerceAtLeast(finalTopLeft.x + minSize),
            y = finalBottomRight.y.coerceAtLeast(finalTopLeft.y + minSize)
        )

        var currentWidth = (finalBottomRight.x - finalTopLeft.x).coerceAtLeast(minSize)
        var currentHeight = (finalBottomRight.y - finalTopLeft.y).coerceAtLeast(minSize)

        aspectRatio?.let { ratio ->

            (currentWidth / ratio > currentHeight).takeIf { isValid -> isValid }?.run {

                when (cornerType) {

                    KropCorner.TOP_LEFT, KropCorner.BOTTOM_LEFT -> {

                        currentWidth = currentHeight * ratio
                        finalTopLeft = finalTopLeft.copy(x = finalBottomRight.x - currentWidth)
                    }

                    KropCorner.TOP_RIGHT, KropCorner.BOTTOM_RIGHT -> {

                        currentWidth = currentHeight * ratio
                        finalBottomRight = finalBottomRight.copy(x = finalTopLeft.x + currentWidth)
                    }

                    else -> {}
                }
            } ?: (currentHeight > currentWidth / ratio).takeIf { isValid -> isValid }?.run {

                when (cornerType) {

                    KropCorner.TOP_LEFT, KropCorner.TOP_RIGHT -> {

                        currentHeight = currentWidth / ratio
                        finalTopLeft = finalTopLeft.copy(y = finalBottomRight.y - currentHeight)
                    }

                    KropCorner.BOTTOM_LEFT, KropCorner.BOTTOM_RIGHT -> {

                        currentHeight = currentWidth / ratio
                        finalBottomRight = finalBottomRight.copy(y = finalTopLeft.y + currentHeight)
                    }

                    else -> {}
                }
            }
        }

        finalTopLeft = finalTopLeft.copy(
            x = finalTopLeft.x.coerceIn(0.0F, canvasWidth - minSize),
            y = finalTopLeft.y.coerceIn(0.0F, canvasHeight - minSize)
        )

        currentWidth = (finalBottomRight.x - finalTopLeft.x).coerceAtLeast(minSize)
        currentHeight = (finalBottomRight.y - finalTopLeft.y).coerceAtLeast(minSize)

        finalBottomRight = finalBottomRight.copy(
            x = (finalTopLeft.x + currentWidth).coerceIn(finalTopLeft.x + minSize, canvasWidth),
            y = (finalTopLeft.y + currentHeight).coerceIn(finalTopLeft.y + minSize, canvasHeight)
        )
        finalTopLeft = finalTopLeft.copy(
            x = (finalBottomRight.x - currentWidth).coerceIn(0f, canvasWidth - minSize),
            y = (finalBottomRight.y - currentHeight).coerceIn(0f, canvasHeight - minSize)
        )

        val finalWidth = (finalBottomRight.x - finalTopLeft.x)
        val finalHeight = (finalBottomRight.y - finalTopLeft.y)

        (finalWidth < minSize || finalHeight < minSize).takeIf { inNotValid ->

            inNotValid
        }?.run { return null }

        val resultTopLeft = finalTopLeft
        val resultBottomRight = finalBottomRight

        (((resultBottomRight.x - resultTopLeft.x) < minSize - 0.001F) ||
                ((resultBottomRight.y - resultTopLeft.y) < minSize - 0.001F)).takeIf { isNotValid ->

            isNotValid
        }?.run { return null }

        return Pair(resultTopLeft, resultBottomRight)
    }

    companion object {

        fun StateSaver(density: Density): Saver<ImageKropState, List<Any?>> = Saver(
            save = { state ->

                listOf(
                    state.imageBitmap,
                    state.config,
                    state.aspectList,
                    state.shapeList,
                    state.originalImage,
                    state.modifiedImage,
                    state.previewImage,
                    state.imageList,
                    state.kropAspectRatio,
                    state.isAspectLocked,
                    state.kropShape,
                    state.currentCorner,
                    state.canvasSize,
                    state.kropRectPosition,
                    state.kropRectSize,
                    state.isAspectRatioMenuExpanded,
                    state.isShapeMenuExpanded,
                    state.isKropShapeCustomizationDialog
                )
            },
            restore = { elements ->

                val savedImageBitmap = elements[0] as ImageBitmap
                val savedConfig = elements[1] as KropConfig
                val savedAspectList = elements[2] as ImmutableList<KropAspectRatio>
                val savedShapeList = elements[3] as PersistentList<ImageShape>
                val savedOriginalImage = elements[4] as ImageBitmap
                val savedModifiedImage = elements[5] as? ImageBitmap
                val savedPreviewImage = elements[6] as? ImageBitmap

                val savedImageList = (elements[7] as? PersistentList<ImageBitmap>)
                    ?: persistentListOf(savedImageBitmap)

                val savedKropAspectRatio = elements[8] as KropAspectRatio
                val savedIsAspectLocked = elements[9] as Boolean
                val savedKropShape = elements[10] as ImageShape
                val savedKropCorner = elements[11] as? KropCorner
                val savedCanvasSize = elements[12] as Size
                val savedRectPosition = elements[13] as Offset
                val savedRectSize = elements[14] as Size
                val savedIsAspectRatioMenuExpanded = elements[15] as Boolean
                val savedIsShapeMenuExpanded = elements[16] as Boolean
                val savedIsKropShapeCustomizationDialog = elements[17] as Boolean

                ImageKropState(
                    imageBitmap = savedImageBitmap,
                    config = savedConfig,
                    aspectList = savedAspectList,
                    density = density
                ).apply {

                    shapeList = savedShapeList
                    originalImage = savedOriginalImage
                    modifiedImage = savedModifiedImage
                    previewImage = savedPreviewImage
                    imageList = savedImageList
                    kropAspectRatio = savedKropAspectRatio
                    isAspectLocked = savedIsAspectLocked
                    kropShape = savedKropShape
                    currentCorner = savedKropCorner
                    canvasSize = savedCanvasSize
                    kropRectPosition = savedRectPosition
                    kropRectSize = savedRectSize
                    isAspectRatioMenuExpanded = savedIsAspectRatioMenuExpanded
                    isShapeMenuExpanded = savedIsShapeMenuExpanded
                    isKropShapeCustomizationDialog = savedIsKropShapeCustomizationDialog
                }
            }
        )
    }
}