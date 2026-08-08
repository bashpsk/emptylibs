package io.bashpsk.emptylibs.imagekrop.crop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import io.bashpsk.emptylibs.composeutils.offset.coerceAtLeast
import io.bashpsk.emptylibs.composeutils.offset.coerceIn
import io.bashpsk.emptylibs.composeutils.shape.PathShape
import io.bashpsk.emptylibs.composeutils.size.coerceIn
import io.bashpsk.emptylibs.imagekrop.offset.getKropCorner
import io.bashpsk.emptylibs.imagekrop.offset.itemRect
import io.bashpsk.emptylibs.imageutils.extension.sameAs
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

/**
 * Composable function to remember an [ImageKropState] instance.
 *
 * This function creates and remembers an [ImageKropState] which holds the state
 * for the image cropping functionality. It uses [retain] to ensure the state is preserved across
 * configuration changes.
 *
 * @param imageBitmap The initial [ImageBitmap] to be cropped.
 * @param config The [KropConfig] to configure the cropping behavior. Defaults to
 * [KropConfig.surfaceBased].
 * @return An instance of [ImageKropState].
 */
@Composable
fun rememberImageKropState(
    imageBitmap: ImageBitmap,
    config: KropConfig = KropConfig.surfaceBased()
): ImageKropState {

    val density = LocalDensity.current

    return retain(density, imageBitmap, config) {
        ImageKropState(density = density, imageBitmap = imageBitmap, config = config)
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
 * @param density The density of the display.
 * @param imageBitmap The initial [ImageBitmap] to be cropped. This is the base image.
 * @param config The [KropConfig] to be used for the cropping operations.
 */
@Stable
class ImageKropState(val density: Density, val imageBitmap: ImageBitmap, val config: KropConfig) {

    /**
     * A persistent list of [PathShape] objects available for cropping.
     * This list defines the different shapes that can be used for the crop area.
     * It is initialized with [PathShape.BasicPathShapes].
     * The list can be updated using the [updateShapeList] function.
     */
    var shapeList: PersistentList<PathShape> = PathShape.BasicPathShapes

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
     * The default value is [PathShape.None].
     */
    var kropShape by mutableStateOf<PathShape>(PathShape.None)
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
    internal var canvasSize by mutableStateOf(Size.Unspecified)

    /**
     * The current position of the crop rectangle, represented by its top-left corner's offset.
     * This is used internally to track the location of the crop rectangle on the canvas.
     */
    internal var kropRectPosition by mutableStateOf(Offset.Unspecified)

    /**
     * The size of the crop rectangle.
     * This is used to draw the crop rectangle on the canvas.
     */
    internal var kropRectSize by mutableStateOf(Size.Unspecified)

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

    /**
     * Whether the shape customization dialog is currently visible.
     * When `true`, the dialog is shown, allowing the user to customize the properties
     * of the selected shape. When `false`, the dialog is hidden.
     */
    internal var isShapeCustomizeDialog by mutableStateOf(false)

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

            imageList.removingAt(index = index)
        } ?: imageList

        imageList = safeImageList.adding(element = bitmap)
    }

    /**
     * Removes the last image from the list of images.
     * If the list is not empty after removal, the original image is updated to the new last image
     * in the list.
     * This function effectively provides an "undo" capability for image modifications.
     */
    fun removeLastImage() {

        imageList.lastIndex.takeIf { index -> index > 0 }?.let { index ->

            imageList = imageList.removingAt(index = index)
        }

        imageList.lastOrNull()?.let { bitmap ->

            updateOriginalImage(bitmap)
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
     * @param shape The new [PathShape] to set.
     */
    fun updateKropShape(shape: PathShape) {

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
     * @param shape The [PathShape] to update or add to the list.
     */
    fun updateShapeList(shape: PathShape) {

        shapeList = existShapeIndex(shape = shape)?.let { index ->

            shapeList.replacingAt(index = index, element = shape)
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

        return imageList.indexOfFirst { image ->

            image.sameAs(bitmap)
        }.takeIf { index -> index > 0 }
    }

    /**
     * Checks if a [PathShape] already exists in the `shapeList`.
     *
     * This function iterates through the `shapeList` and compares the class of each `shapeItem`
     * with the class of the provided `shape`. If a shape with the same class is found, its index
     * is returned.
     *
     * @param shape The [PathShape] to search for in the `shapeList`.
     * @return The index of the existing shape in the `shapeList` if found, otherwise null.
     */
    internal fun existShapeIndex(shape: PathShape): Int? {

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
     * @param imageShape The [PathShape] to apply to the cropped image. Defaults to
     * [PathShape.None].
     * @return A [KropResult] object which can be either [KropResult.Success] containing the
     * cropped [ImageBitmap] or [KropResult.Failed] if an error occurred during cropping.
     */
    internal suspend fun getCroppedImageBitmap(
        imageRect: Rect? = null,
        imageCanvasSize: Size? = null,
        imageFlip: KropImageFlip? = null,
        imageShape: PathShape? = null
    ): KropResult {

        return originalImage.getCroppedImageBitmap(
            cropRect = imageRect ?: kropRectSize.itemRect(kropRectPosition),
            canvasSize = imageCanvasSize ?: canvasSize,
            imageFlip = imageFlip,
            kropShape = imageShape ?: kropShape
        )
    }

    /**
     * Handles the start of a crop operation.
     *
     * This function is called when the user starts interacting with the crop rectangle
     * (e.g., by clicking on a corner or edge).
     * It determines which corner of the crop rectangle the user
     * has clicked on and sets [currentCorner] accordingly.
     *
     * @param position The [Offset] representing the position where the user clicked on the canvas.
     */
    internal fun onKropStart(position: Offset) {

        currentCorner = kropRectSize.itemRect(position = kropRectPosition).getKropCorner(
            clickPosition = position,
            threshold = maxOf(config.handleWidth, config.handleHeight).toPixel(density = density)
        )
    }

    /**
     * Called when the cropping gesture ends.
     * This function resets the `currentCorner` to null, indicating that no corner is
     * currently being dragged.
     */
    internal fun onKropEnd() {

        currentCorner = null
    }

    /**
     * Handles changes to the crop rectangle's position and size.
     *
     * This function is called when the user drags a corner or edge of the crop rectangle,
     * or when the crop rectangle is moved. It calculates the new position and size of the
     * crop rectangle based on the drag amount and updates the [kropRectPosition] and
     * [kropRectSize] accordingly.
     *
     * @param position The current position of the pointer (e.g., finger or mouse).
     * @param amount The amount by which the pointer has moved since the last update.
     */
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

    /**
     * Initializes the crop rectangle using the current canvas size, aspect ratio, and minimum crop
     * size configuration.
     *
     * When an aspect ratio is configured, the crop rectangle is initialized to approximately 80% of
     * the available canvas area while maintaining the requested aspect ratio. When no aspect ratio
     * is configured, a square crop rectangle is created using 80% of the smaller canvas dimension.
     *
     * The resulting rectangle is constrained by [config.minimumCropSize] and the canvas boundaries,
     * and is positioned at the center of the canvas.
     */
    internal fun onKropRectInitialized() {

        val minSize = config.minimumCropSize.toPixel(density = density)

        kropRectSize = kropAspectRatio.ratio?.let { ratio ->

            fitInitialSize(canvasSize = canvasSize, aspectRatio = ratio, minSize = minSize)
        } ?: run {

            val value = minOf(a = canvasSize.width, b = canvasSize.height) * 0.8F

            Size(width = value, height = value).coerceIn(
                minimum = Size(width = minSize, height = minSize),
                maximum = canvasSize
            )
        }

        kropRectPosition = Offset(
            x = (canvasSize.width - kropRectSize.width) / 2F,
            y = (canvasSize.height - kropRectSize.height) / 2F
        )
    }

    /**
     * Calculates the crop rectangle position and size after a user interaction.
     *
     * The operation performed depends on [currentCorner]:
     *
     * - When no corner or edge is active, the entire crop rectangle is moved.
     * - When a corner is active, the corresponding corner is resized while the opposite corner
     *   remains fixed.
     * - When an edge center is active, the corresponding edge is resized.
     *
     * When [isAspectLocked] is enabled and an aspect ratio is configured, resizing maintains the
     * selected aspect ratio. All resulting rectangles are constrained by the configured minimum
     * crop size and the canvas boundaries.
     *
     * @param position The current top-left position of the crop rectangle.
     * @param size The current size of the crop rectangle.
     * @param amount The change in pointer position since the previous gesture event.
     * @return A [Pair] containing the new top-left position and size of the crop rectangle.
     */
    internal fun getKropRectPositionSize(
        position: Offset,
        size: Size,
        amount: Offset,
    ): Pair<Offset, Size> {

        val minSize = config.minimumCropSize.toPixel(density = density)
        val rect = size.itemRect(position = position)
        val aspectRatio = kropAspectRatio.ratio?.takeIf { isAspectLocked }

        return when (val corner = currentCorner) {

            null -> moveRect(rect = rect, amount = amount, canvasSize = canvasSize)

            KropCorner.TOP_LEFT, KropCorner.TOP_RIGHT, KropCorner.BOTTOM_LEFT,
            KropCorner.BOTTOM_RIGHT -> resizeFromCorner(
                rect = rect,
                corner = corner,
                amount = amount,
                aspectRatio = aspectRatio,
                minSize = minSize,
                canvasSize = canvasSize
            )

            KropCorner.TOP_CENTRE, KropCorner.BOTTOM_CENTRE, KropCorner.LEFT_CENTRE,
            KropCorner.RIGHT_CENTRE -> resizeFromEdge(
                rect = rect,
                edge = corner,
                amount = amount,
                aspectRatio = aspectRatio,
                minSize = minSize,
                canvasSize = canvasSize
            )
        }
    }

    /**
     * Moves the crop rectangle by the supplied offset.
     *
     * The rectangle is constrained so that it remains completely inside the canvas boundaries.
     *
     * @param rect The current crop rectangle.
     * @param amount The amount by which the rectangle should be moved.
     * @param canvasSize The size of the canvas containing the crop rectangle.
     * @return A [Pair] containing the new top-left position and the unchanged rectangle size.
     */
    private fun moveRect(rect: Rect, amount: Offset, canvasSize: Size): Pair<Offset, Size> {

        val newPosition = (rect.topLeft + amount).coerceIn(
            minimum = Offset.Zero,
            maximum = Offset(
                x = canvasSize.width - rect.width,
                y = canvasSize.height - rect.height
            ).coerceAtLeast(minimum = Offset.Zero)
        )

        return newPosition to rect.size
    }

    /**
     * Resizes the crop rectangle from one of its four corners.
     *
     * The corner specified by [corner] follows the pointer movement while the opposite corner
     * remains fixed. The resulting rectangle is constrained by the minimum crop size and canvas
     * boundaries.
     *
     * When [aspectRatio] is not null, the crop rectangle maintains the specified aspect ratio
     * during resizing.
     *
     * @param rect The current crop rectangle.
     * @param corner The corner being dragged.
     * @param amount The pointer movement since the previous gesture event.
     * @param aspectRatio The target aspect ratio, or null for freeform resizing.
     * @param minSize The minimum allowed width and height of the crop rectangle.
     * @param canvasSize The size of the canvas containing the crop rectangle.
     * @return A [Pair] containing the resized rectangle's top-left position and size.
     */
    private fun resizeFromCorner(
        rect: Rect,
        corner: KropCorner,
        amount: Offset,
        aspectRatio: Float?,
        minSize: Float,
        canvasSize: Size
    ): Pair<Offset, Size> {

        val dragged = cornerPoint(rect = rect, corner = corner) + amount
        val fixed = oppositeCorner(rect = rect, corner = corner)

        var width = when (corner) {

            KropCorner.TOP_LEFT, KropCorner.BOTTOM_LEFT -> fixed.x - dragged.x
            KropCorner.TOP_RIGHT, KropCorner.BOTTOM_RIGHT -> dragged.x - fixed.x
            else -> return rect.topLeft to rect.size
        }.coerceAtLeast(minimumValue = minSize)

        var height = when (corner) {

            KropCorner.TOP_LEFT, KropCorner.TOP_RIGHT -> fixed.y - dragged.y
            KropCorner.BOTTOM_LEFT, KropCorner.BOTTOM_RIGHT -> dragged.y - fixed.y
        }.coerceAtLeast(minimumValue = minSize)

        aspectRatio?.let { ratio ->

            val fitted = fitSizeToAspect(
                width = width,
                height = height,
                aspectRatio = ratio,
                minSize = minSize,
                maxWidth = canvasSize.width,
                maxHeight = canvasSize.height
            )

            width = fitted.width
            height = fitted.height
        }

        val result = when (corner) {

            KropCorner.TOP_LEFT -> Rect(
                left = fixed.x - width,
                top = fixed.y - height,
                right = fixed.x,
                bottom = fixed.y
            )

            KropCorner.TOP_RIGHT -> Rect(
                left = fixed.x,
                top = fixed.y - height,
                right = fixed.x + width,
                bottom = fixed.y
            )

            KropCorner.BOTTOM_LEFT -> Rect(
                left = fixed.x - width,
                top = fixed.y,
                right = fixed.x,
                bottom = fixed.y + height
            )

            KropCorner.BOTTOM_RIGHT -> Rect(
                left = fixed.x,
                top = fixed.y,
                right = fixed.x + width,
                bottom = fixed.y + height
            )
        }

        val clamped = clampRectToCanvas(rect = result, canvasSize = canvasSize, minSize = minSize)

        return clamped.topLeft to clamped.size
    }

    /**
     * Resizes the crop rectangle from one of its four edge centers.
     *
     * The selected edge follows the pointer movement while the opposite edge remains anchored.
     * When [aspectRatio] is provided, the other dimension is adjusted to maintain the requested
     * aspect ratio.
     *
     * The resulting rectangle is constrained by the minimum crop size and canvas boundaries.
     *
     * @param rect The current crop rectangle.
     * @param edge The edge center being dragged.
     * @param amount The pointer movement since the previous gesture event.
     * @param aspectRatio The target aspect ratio, or null for Freeform resizing.
     * @param minSize The minimum allowed width and height of the crop rectangle.
     * @param canvasSize The size of the canvas containing the crop rectangle.
     * @return A [Pair] containing the resized rectangle's top-left position and size.
     */
    private fun resizeFromEdge(
        rect: Rect,
        edge: KropCorner,
        amount: Offset,
        aspectRatio: Float?,
        minSize: Float,
        canvasSize: Size
    ): Pair<Offset, Size> {

        var result = when (edge) {

            KropCorner.TOP_CENTRE -> rect.copy(
                top = (rect.top + amount.y).coerceIn(0F..rect.bottom - minSize)
            )

            KropCorner.BOTTOM_CENTRE -> rect.copy(
                bottom = (rect.bottom + amount.y).coerceIn(
                    rect.top + minSize..canvasSize.height
                )
            )

            KropCorner.LEFT_CENTRE -> rect.copy(
                left = (rect.left + amount.x).coerceIn(0F..rect.right - minSize)
            )

            KropCorner.RIGHT_CENTRE -> rect.copy(
                right = (rect.right + amount.x).coerceIn(
                    rect.left + minSize..canvasSize.width
                )
            )

            else -> rect
        }

        aspectRatio?.let { ratio ->

            result = resizeEdgeWithAspect(
                rect = result,
                edge = edge,
                aspectRatio = ratio,
                minSize = minSize,
                canvasSize = canvasSize
            )
        }

        result = clampRectToCanvas(rect = result, canvasSize = canvasSize, minSize = minSize)

        return result.topLeft to result.size
    }

    /**
     * Adjusts an edge-resized crop rectangle to maintain a specific aspect ratio.
     *
     * For vertical edge operations, the height is derived from the user's drag and the width is
     * calculated from the aspect ratio. For horizontal edge operations, the width is derived from
     * the user's drag and the height is calculated from the aspect ratio.
     *
     * The resulting rectangle is positioned so that the resized edge remains anchored while the
     * opposite dimension is adjusted around the rectangle's center.
     *
     * @param rect The crop rectangle after the initial edge resize.
     * @param edge The edge center being dragged.
     * @param aspectRatio The target aspect ratio.
     * @param minSize The minimum allowed crop dimension.
     * @param canvasSize The size of the canvas containing the crop rectangle.
     * @return A crop rectangle adjusted to the requested aspect ratio.
     */
    private fun resizeEdgeWithAspect(
        rect: Rect,
        edge: KropCorner,
        aspectRatio: Float,
        minSize: Float,
        canvasSize: Size
    ): Rect {

        return when (edge) {

            KropCorner.TOP_CENTRE, KropCorner.BOTTOM_CENTRE -> {

                val height = rect.height.coerceAtLeast(minimumValue = minSize)
                val maxHeightByWidth = canvasSize.width / aspectRatio
                val finalHeight = height.coerceAtMost(maximumValue = maxHeightByWidth)
                val width = (finalHeight * aspectRatio).coerceIn(minSize..canvasSize.width)
                val centerX = rect.center.x
                val left = (centerX - width / 2F).coerceIn(0F..canvasSize.width - width)
                val right = left + width

                when (edge) {

                    KropCorner.TOP_CENTRE -> Rect(
                        left = left,
                        top = rect.bottom - finalHeight,
                        right = right,
                        bottom = rect.bottom
                    )

                    KropCorner.BOTTOM_CENTRE -> Rect(
                        left = left,
                        top = rect.top,
                        right = right,
                        bottom = rect.top + finalHeight
                    )
                }
            }

            KropCorner.LEFT_CENTRE, KropCorner.RIGHT_CENTRE -> {

                val width = rect.width.coerceAtLeast(minimumValue = minSize)
                val maxWidthByHeight = canvasSize.height * aspectRatio
                val finalWidth = width.coerceAtMost(maximumValue = maxWidthByHeight)
                val height = (finalWidth / aspectRatio).coerceIn(minSize..canvasSize.height)
                val centerY = rect.center.y
                val top = (centerY - height / 2F).coerceIn(0F..canvasSize.height - height)
                val bottom = top + height

                when (edge) {

                    KropCorner.LEFT_CENTRE -> Rect(
                        left = rect.right - finalWidth,
                        top = top,
                        right = rect.right,
                        bottom = bottom
                    )

                    KropCorner.RIGHT_CENTRE -> Rect(
                        left = rect.left,
                        top = top,
                        right = rect.left + finalWidth,
                        bottom = bottom
                    )
                }
            }

            else -> rect
        }
    }

    /**
     * Calculates a crop size that satisfies the requested aspect ratio.
     *
     * The returned size respects the minimum crop size and the maximum width and height available
     * on the canvas. If the requested dimensions exceed the available canvas area, the size is
     * reduced while preserving the aspect ratio.
     *
     * @param width The preferred crop width.
     * @param height The preferred crop height.
     * @param aspectRatio The desired width-to-height aspect ratio.
     * @param minSize The minimum allowed width and height.
     * @param maxWidth The maximum allowed width.
     * @param maxHeight The maximum allowed height.
     * @return A [Size] that satisfies the aspect ratio and available bounds as closely as possible.
     */
    private fun fitSizeToAspect(
        width: Float,
        height: Float,
        aspectRatio: Float,
        minSize: Float,
        maxWidth: Float,
        maxHeight: Float
    ): Size {

        var finalWidth = width.coerceAtLeast(minimumValue = minSize)
        var finalHeight = finalWidth / aspectRatio

        if (finalHeight < minSize) {
            finalHeight = minSize
            finalWidth = finalHeight * aspectRatio
        }

        if (finalWidth > maxWidth) {
            finalWidth = maxWidth
            finalHeight = finalWidth / aspectRatio
        }

        if (finalHeight > maxHeight) {
            finalHeight = maxHeight
            finalWidth = finalHeight * aspectRatio
        }

        return Size(width = finalWidth, height = finalHeight).coerceIn(
            minimum = Size(width = minSize, height = minSize),
            maximum = Size(width = maxWidth, height = maxHeight)
        )
    }

    /**
     * Calculates the initial crop rectangle size for an aspect-ratio-constrained crop.
     *
     * The crop size is based on approximately 80% of the available canvas area while preserving the
     * requested aspect ratio. The resulting size is also constrained by the minimum crop size and
     * canvas dimensions.
     *
     * @param canvasSize The size of the canvas.
     * @param aspectRatio The desired width-to-height aspect ratio.
     * @param minSize The minimum allowed crop dimension.
     * @return The initial crop rectangle size.
     */
    private fun fitInitialSize(canvasSize: Size, aspectRatio: Float, minSize: Float): Size {

        val maxWidth = canvasSize.width * 0.8F
        val maxHeight = canvasSize.height * 0.8F

        val initialSize = if (maxWidth / maxHeight > aspectRatio) Size(
            width = maxHeight * aspectRatio,
            height = maxHeight
        ) else Size(width = maxWidth, height = maxWidth / aspectRatio)

        return initialSize.coerceIn(
            minimum = Size(width = minSize, height = minSize),
            maximum = canvasSize
        )
    }

    /**
     * Constrains a crop rectangle to the canvas boundaries and minimum crop size.
     *
     * The rectangle's width and height are first adjusted to satisfy the minimum size and canvas
     * dimensions. Its position is then clamped so the complete rectangle remains inside the canvas.
     *
     * @param rect The crop rectangle to constrain.
     * @param canvasSize The size of the canvas.
     * @param minSize The minimum allowed width and height.
     * @return A rectangle that fits within the canvas and satisfies the minimum size constraints.
     */
    private fun clampRectToCanvas(rect: Rect, canvasSize: Size, minSize: Float): Rect {

        val size = rect.size.coerceIn(
            minimum = Size(width = minSize, height = minSize),
            maximum = canvasSize
        )

        val position = rect.topLeft.coerceIn(
            minimum = Offset.Zero,
            maximum = Offset(
                x = canvasSize.width - size.width,
                y = canvasSize.height - size.height
            ).coerceAtLeast(minimum = Offset.Zero)
        )

        return Rect(offset = position, size = size)
    }

    /**
     * Returns the point represented by the specified corner of a rectangle.
     *
     * @param rect The rectangle whose corner should be returned.
     * @param corner The corner to retrieve.
     * @return The [Offset] corresponding to the requested corner.
     */
    private fun cornerPoint(rect: Rect, corner: KropCorner): Offset {

        return when (corner) {

            KropCorner.TOP_LEFT -> rect.topLeft
            KropCorner.TOP_RIGHT -> rect.topRight
            KropCorner.BOTTOM_LEFT -> rect.bottomLeft
            KropCorner.BOTTOM_RIGHT -> rect.bottomRight
            else -> rect.topLeft
        }
    }

    /**
     * Returns the corner opposite to the specified corner of a rectangle.
     *
     * This is used during crop resizing to keep the opposite corner fixed while the selected corner
     * follows the pointer.
     *
     * @param rect The rectangle whose opposite corner should be returned.
     * @param corner The corner being resized.
     * @return The [Offset] corresponding to the opposite corner.
     */
    private fun oppositeCorner(rect: Rect, corner: KropCorner): Offset {

        return when (corner) {

            KropCorner.TOP_LEFT -> rect.bottomRight
            KropCorner.TOP_RIGHT -> rect.bottomLeft
            KropCorner.BOTTOM_LEFT -> rect.topRight
            KropCorner.BOTTOM_RIGHT -> rect.topLeft
            else -> rect.topLeft
        }
    }

    internal fun clearState() {

        shapeList = PathShape.BasicPathShapes
        originalImage = imageBitmap
        modifiedImage = null
        previewImage = null
        imageList = persistentListOf(imageBitmap)
        kropAspectRatio = KropAspectRatio.Ratio1to1
        isAspectLocked = false
        kropShape = PathShape.None

        currentCorner = null
        canvasSize = Size.Zero
        kropRectPosition = Offset.Zero
        kropRectSize = Size.Zero
        isAspectRatioMenuExpanded = false
        isShapeMenuExpanded = false
        isShapeCustomizeDialog = false
    }
}