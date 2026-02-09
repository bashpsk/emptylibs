package io.bashpsk.emptylibs.imagekrop.crop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import io.bashpsk.emptylibs.composeutils.offset.OffsetData
import io.bashpsk.emptylibs.composeutils.offset.toOffsetData
import io.bashpsk.emptylibs.composeutils.shape.BasicPathShapes
import io.bashpsk.emptylibs.composeutils.shape.PathShape
import io.bashpsk.emptylibs.composeutils.size.SizeData
import io.bashpsk.emptylibs.composeutils.size.toSizeData
import io.bashpsk.emptylibs.imagekrop.cache.BitmapCacheManager
import io.bashpsk.emptylibs.imagekrop.cache.BitmapListCacheManager
import io.bashpsk.emptylibs.imagekrop.crop.KropCorner.Companion.hasCornerCenter
import io.bashpsk.emptylibs.imagekrop.offset.coerceAtLeast
import io.bashpsk.emptylibs.imagekrop.offset.getKropCorner
import io.bashpsk.emptylibs.imagekrop.offset.itemRect
import io.bashpsk.emptylibs.imageutils.extension.sameAs
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlin.math.abs
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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
 * @return An instance of [ImageKropState].
 */
@Composable
fun rememberImageKropState(
    imageBitmap: ImageBitmap,
    config: KropConfig = KropConfig.surfaceBased()
): ImageKropState {

    val density = LocalDensity.current

    return rememberSaveable(
        imageBitmap,
        config,
        density,
        saver = ImageKropState.StateSaver(
            imageBitmap = imageBitmap,
            config = config,
            density = density
        )
    ) {
        ImageKropState(imageBitmap = imageBitmap, config = config, density = density)
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
 */
@OptIn(ExperimentalUuidApi::class)
@Stable
class ImageKropState(val imageBitmap: ImageBitmap, val config: KropConfig, val density: Density) {

    /**
     * A persistent list of [PathShape] objects available for cropping.
     * This list defines the different shapes that can be used for the crop area.
     * It is initialized with [BasicPathShapes].
     * The list can be updated using the [updateShapeList] function.
     */
    var shapeList: PersistentList<PathShape> = BasicPathShapes

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
    var imageList by mutableStateOf(persistentListOf(KEY_ORIGINAL_IMAGE))
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

    /**
     * Whether the shape customization dialog is currently visible.
     * When `true`, the dialog is shown, allowing the user to customize the properties
     * of the selected shape. When `false`, the dialog is hidden.
     */
    internal var isShapeCustomizeDialog by mutableStateOf(false)

    init {

        BitmapListCacheManager.set(KEY_ORIGINAL_IMAGE, value = imageBitmap)
    }

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

            imageList.removeAt(index = index)
        } ?: imageList

        val newKey = generateImageKey()

        BitmapListCacheManager.resize(maxSize = imageList.size + 1)
        BitmapListCacheManager.set(newKey, value = bitmap)
        BitmapListCacheManager.contains(newKey).takeIf { hasAdded ->

            hasAdded
        }?.run {

            imageList = safeImageList.add(element = newKey)
        }
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

        imageList.lastOrNull()?.let { key ->

            BitmapListCacheManager[key]?.let { bitmap ->

                updateOriginalImage(bitmap)
            }
        }
    }

    /**
     * Clears all modified images and resets the image list to the original image.
     * This function is useful for undoing all changes and starting over with the original image.
     */
    fun clearImages() {

        imageList = persistentListOf(KEY_ORIGINAL_IMAGE)
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

        return imageList.indexOfFirst { key ->

            BitmapListCacheManager[key]?.sameAs(bitmap) == true
        }.takeIf { index -> index > 0 }
    }

    /**
     * Generates a unique key for an image.
     *
     * This function recursively calls [generateKey] until a unique key is found that is not
     * already present in the [imageList]. This ensures that each image in the list has a
     * distinct identifier.
     *
     * @return A unique [String] key for an image.
     */
    internal fun generateImageKey(): String {

        return generateKey().takeIf { newKey ->

            imageList.none { existingKey -> existingKey == newKey }
        } ?: generateImageKey()
    }

    /**
     * Generates a unique key using a random UUID.
     * This key is used for identifying images in the cache.
     *
     * @return A string representation of a randomly generated UUID.
     */
    internal fun generateKey(): String {

        return Uuid.random().toString()
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
     * Initializes the crop rectangle position and size based on the current aspect ratio,
     * canvas size, and minimum crop size configuration.
     *
     * This function calculates an initial size for the crop rectangle, aiming for 80% of the
     * smaller dimension of the canvas while respecting the configured aspect ratio and minimum
     * size. It then centers this rectangle within the canvas.
     *
     * The logic handles:
     * - Aspect ratio: If an aspect ratio is set, the rectangle's dimensions will adhere to it.
     * - No aspect ratio: If no aspect ratio is set (freeform), the rectangle will be a square
     *   initially, sized at 80% of the smaller canvas dimension.
     * - Minimum size: Ensures the rectangle's width and height are at least
     *   `config.minimumCropSize`.
     * - Canvas boundaries: Ensures the rectangle fits within the canvas. If the calculated size
     *   exceeds canvas dimensions (even after aspect ratio adjustments), it's clamped to the
     *   canvas bounds, and the other dimension is adjusted to maintain the aspect ratio if locked.
     * - Aspect lock refinement: If `isAspectLocked` is true, it performs additional checks
     *   and adjustments to ensure the final rectangle strictly adheres to the aspect ratio,
     *   especially after clamping to minimum size or canvas boundaries.
     *
     * Finally, it updates `kropRectPosition` and `kropRectSize` with the calculated values.
     */
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
            threshold = maxOf(config.handleWidth, config.handleHeight).toPixel(density)
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
     * Calculates the new position and size of the crop rectangle based on user interaction.
     *
     * This function determines the new crop rectangle dimensions and location after a drag
     * operation on one of its corners or edges, or when the entire rectangle is moved.
     * It considers the current corner being dragged, the drag amount, canvas boundaries,
     * minimum crop size, and locked aspect ratio.
     *
     * If a corner is being dragged ([currentCorner] is not null):
     * - For corner drags (TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT):
     *   It uses [calculateNewEditRect] to determine the new rectangle based on the dragged
     *   and fixed corners, applying aspect ratio constraints if locked.
     * - For edge drags (TOP_CENTRE, BOTTOM_CENTRE, LEFT_CENTRE, RIGHT_CENTRE):
     *   It adjusts the corresponding edge of the rectangle, ensuring it stays within canvas
     *   bounds and maintains the minimum size. If the aspect ratio is locked, it further
     *   adjusts the rectangle to fit the aspect ratio while staying within bounds and
     *   maintaining the minimum size.
     *
     * If no corner is being dragged ([currentCorner] is null), it means the entire crop
     * rectangle is being moved. In this case, it simply translates the rectangle by the
     * [amount], ensuring it stays within the canvas boundaries.
     *
     * After calculating the initial new position and size, it performs final adjustments
     * to ensure the rectangle:
     * - Adheres to the minimum size limit.
     * - Stays entirely within the canvas boundaries.
     * - If the aspect ratio is locked, it makes a final pass to ensure the aspect ratio is
     *   maintained while respecting the canvas boundaries and minimum size. This might involve
     *   shrinking one dimension to fit the other based on the aspect ratio and available space.
     *
     * @param position The current top-left position of the crop rectangle.
     * @param size The current size of the crop rectangle.
     * @param amount The offset representing the drag amount or movement.
     * @return A [Pair] containing the new [Offset] (top-left position) and [Size] of the
     */
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
     * after a corner or edge drag, maintaining a given aspect ratio if specified.
     *
     * This function handles two main scenarios:
     * 1. Dragging a corner ([KropCorner.TOP_LEFT], [KropCorner.TOP_RIGHT],
     * [KropCorner.BOTTOM_LEFT], [KropCorner.BOTTOM_RIGHT]):
     *    - Calculates the proposed new width and height based on the drag.
     *    - If an aspect ratio is provided, adjusts the height (or width if height adjustment leads
     *    to a size smaller than `minSize`) to maintain the ratio.
     *    - Determines the initial new top-left and bottom-right corners based on the fixed corner
     *    and adjusted dimensions.
     *    - Coerces the new rectangle within the canvas boundaries and `minSize`.
     *    - If an aspect ratio is provided, performs a final adjustment to ensure the aspect ratio
     *    is strictly maintained while respecting boundaries and `minSize`. This involves checking
     *    if adjusting width or height to match the ratio is more appropriate based on which
     *    dimension is currently further from the target ratio.
     *
     * 2. Dragging an edge center ([KropCorner.TOP_CENTRE], [KropCorner.BOTTOM_CENTRE],
     * [KropCorner.LEFT_CENTRE], [KropCorner.RIGHT_CENTRE]):
     *    - Calculates the initial new position of the dragged edge, coercing it within canvas
     *    bounds and ensuring it doesn't cross the opposite fixed edge minus `minSize`.
     *    - Updates the corresponding coordinate (top, bottom, left, or right) of the rectangle.
     *    - If the resulting dimension (width or height) is less than `minSize`, it adjusts the
     *    dragged edge to meet `minSize`.
     *    - Coerces the final rectangle within the canvas boundaries and `minSize`.
     *    - If an aspect ratio is *not* provided (freeform drag of an edge), this is the final step
     *    for edge drags.
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

        (((resultBottomRight.x - resultTopLeft.x) < minSize) ||
                ((resultBottomRight.y - resultTopLeft.y) < minSize)).takeIf { isNotValid ->

            isNotValid
        }?.run { return null }

        return Pair(resultTopLeft, resultBottomRight)
    }

    internal fun clearState() {

        BitmapCacheManager.evictAll()
        BitmapListCacheManager.evictAll()
        BitmapListCacheManager.set(KEY_ORIGINAL_IMAGE, value = imageBitmap)

        shapeList = BasicPathShapes
        originalImage = imageBitmap
        modifiedImage = null
        previewImage = null
        imageList = persistentListOf(KEY_ORIGINAL_IMAGE)
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

    companion object {

        private const val KEY_SHAPE_LIST = "IMAGE-KROP-SHAPE-LIST"
        private const val KEY_ORIGINAL_IMAGE = "IMAGE-KROP-ORIGINAL"
        private const val KEY_MODIFIED_IMAGE = "IMAGE-KROP-MODIFIED"
        private const val KEY_PREVIEW_IMAGE = "IMAGE-KROP-PREVIEW"
        private const val KEY_IMAGE_LIST = "IMAGE-KROP-IMAGE-LIST"
        private const val KEY_ASPECT_RATIO = "IMAGE-KROP-ASPECT-RATIO"
        private const val KEY_ASPECT_LOCKED = "IMAGE-KROP-ASPECT-LOCKED"
        private const val KEY_KROP_SHAPE = "IMAGE-KROP-SHAPE"
        private const val KEY_CURRENT_CORNER = "IMAGE-KROP-CURRENT-CORNER"
        private const val KEY_CANVAS_SIZE = "IMAGE-KROP-CANVAS-SIZE"
        private const val KEY_KROP_RECT_POSITION = "IMAGE-KROP-RECT-POSITION"
        private const val KEY_KROP_RECT_SIZE = "IMAGE-KROP-RECT-SIZE"
        private const val KEY_ASPECT_RATIO_MENU_EXPANDED = "IMAGE-KROP-ASPECT-RATIO-MENU-EXPANDED"
        private const val KEY_SHAPE_MENU_EXPANDED = "IMAGE-KROP-SHAPE-MENU-EXPANDED"
        private const val KEY_SHAPE_CUSTOMIZE_DIALOG = "IMAGE-KROP-SHAPE-CUSTOMIZE-DIALOG"

        @Suppress("UNCHECKED_CAST")
        fun StateSaver(
            imageBitmap: ImageBitmap,
            config: KropConfig,
            density: Density
        ): Saver<ImageKropState, Any> = mapSaver(
            save = { state ->

                BitmapCacheManager[KEY_ORIGINAL_IMAGE] = state.originalImage
                state.modifiedImage?.let { BitmapCacheManager[KEY_MODIFIED_IMAGE] = it }
                state.previewImage?.let { BitmapCacheManager[KEY_PREVIEW_IMAGE] = it }

                mapOf(
                    KEY_SHAPE_LIST to state.shapeList.toTypedArray(),
                    KEY_IMAGE_LIST to state.imageList.toTypedArray(),
                    KEY_ASPECT_RATIO to state.kropAspectRatio,
                    KEY_ASPECT_LOCKED to state.isAspectLocked,
                    KEY_KROP_SHAPE to state.kropShape,
                    KEY_CURRENT_CORNER to state.currentCorner,
                    KEY_CANVAS_SIZE to state.canvasSize.toSizeData(),
                    KEY_KROP_RECT_POSITION to state.kropRectPosition.toOffsetData(),
                    KEY_KROP_RECT_SIZE to state.kropRectSize.toSizeData(),
                    KEY_ASPECT_RATIO_MENU_EXPANDED to state.isAspectRatioMenuExpanded,
                    KEY_SHAPE_MENU_EXPANDED to state.isShapeMenuExpanded,
                    KEY_SHAPE_CUSTOMIZE_DIALOG to state.isShapeCustomizeDialog
                )
            },
            restore = { elements ->

                ImageKropState(
                    imageBitmap = imageBitmap,
                    config = config,
                    density = density
                ).apply {

                    shapeList = (elements.getOrElse(
                        KEY_SHAPE_LIST
                    ) { BasicPathShapes.toTypedArray() } as Array<PathShape>).toPersistentList()

                    originalImage = BitmapCacheManager[KEY_ORIGINAL_IMAGE] ?: imageBitmap
                    modifiedImage = BitmapCacheManager[KEY_MODIFIED_IMAGE]
                    previewImage = BitmapCacheManager[KEY_PREVIEW_IMAGE]

                    imageList = (elements.getOrElse(
                        KEY_IMAGE_LIST
                    ) { arrayOf(KEY_ORIGINAL_IMAGE) } as Array<String>).toPersistentList()

                    kropAspectRatio = elements.getOrElse(
                        KEY_ASPECT_RATIO
                    ) { KropAspectRatio.Ratio1to1 } as KropAspectRatio

                    isAspectLocked = elements.getOrElse(KEY_ASPECT_LOCKED) { false } as Boolean
                    kropShape = elements.getOrElse(KEY_KROP_SHAPE) { PathShape.None } as PathShape
                    currentCorner = elements.getOrElse(KEY_CURRENT_CORNER) { null } as KropCorner?

                    canvasSize = (elements.getOrElse(
                        KEY_CANVAS_SIZE
                    ) { Size.Zero.toSizeData() } as SizeData).toSize()

                    kropRectPosition = (elements.getOrElse(
                        KEY_KROP_RECT_POSITION
                    ) { Offset.Zero.toOffsetData() } as OffsetData).toOffset()

                    kropRectSize = (elements.getOrElse(
                        KEY_KROP_RECT_SIZE
                    ) { Size.Zero.toSizeData() } as SizeData).toSize()

                    isAspectRatioMenuExpanded = elements.getOrElse(
                        KEY_ASPECT_RATIO_MENU_EXPANDED
                    ) { false } as Boolean

                    isShapeMenuExpanded = elements.getOrElse(
                        KEY_SHAPE_MENU_EXPANDED
                    ) { false } as Boolean

                    isShapeCustomizeDialog = elements.getOrElse(
                        KEY_SHAPE_CUSTOMIZE_DIALOG
                    ) { false } as Boolean
                }
            }
        )
    }
}