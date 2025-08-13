package io.bashpsk.emptylibs.imagekrop.crop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntSize
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

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
 * @param shapeList An immutable list of [KropShape] options available for cropping.
 * Defaults to [KropShape.Basic].
 * @param aspectList An immutable list of [KropAspectRatio] options available for cropping.
 * Defaults to [KropAspectRatio.Basic].
 * @return An instance of [ImageKropState].
 */
@Composable
fun rememberImageKropState(
    imageBitmap: ImageBitmap,
    config: KropConfig = KropConfig.surfaceBased(),
    shapeList: ImmutableList<KropShape> = KropShape.Basic,
    aspectList: ImmutableList<KropAspectRatio> = KropAspectRatio.Basic
): ImageKropState {

    return rememberSaveable(
        imageBitmap,
        config,
        shapeList,
        aspectList,
        saver = ImageKropState.StateSaver
    ) {
        ImageKropState(
            imageBitmap = imageBitmap,
            config = config,
            shapeList = shapeList,
            aspectList = aspectList
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
 * @param shapeList An immutable list of [KropShape] options available for cropping.
 * @param aspectList An immutable list of [KropAspectRatio] options available for cropping.
 */
class ImageKropState(
    val imageBitmap: ImageBitmap,
    val config: KropConfig,
    val shapeList: ImmutableList<KropShape>,
    val aspectList: ImmutableList<KropAspectRatio>
) {

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
     * The default value is [KropShape.SharpeCorner].
     */
    var kropShape by mutableStateOf(KropShape.SharpeCorner)
        private set

    /**
     * The corner of the crop rectangle that is currently being dragged, or null if no corner is
     * being dragged.
     * This is used to determine which corner to resize when the user drags the crop rectangle.
     */
    internal var kropCorner by mutableStateOf<KropCorner?>(null)

    /**
     * Flag indicating whether the crop rectangle is currently being moved.
     * This is true when the user is dragging the entire crop rectangle to a new position.
     */
    internal var isMovingCropRect by mutableStateOf(false)

    /**
     * The size of the canvas where the image is drawn.
     * This is used to calculate the correct scaling and positioning of the crop rectangle.
     */
    internal var canvasSize by mutableStateOf(IntSize.Zero)

    /**
     * The offset of the top-left corner of the crop rectangle.
     */
    internal var topLeft by mutableStateOf(Offset.Zero)

    /**
     * Offset of the top-right corner of the crop rectangle.
     */
    internal var topRight by mutableStateOf(Offset.Zero)

    /**
     * The offset of the bottom-left corner of the crop rectangle.
     */
    internal var bottomLeft by mutableStateOf(Offset.Zero)

    /**
     * Offset of the bottom-right corner of the crop rectangle.
     */
    internal var bottomRight by mutableStateOf(Offset.Zero)

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
     * @param shape The new [KropShape] to set.
     */
    fun updateKropShape(shape: KropShape) {

        kropShape = shape
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
     * @param imageShape The [KropShape] to apply to the cropped image. Defaults to
     * [KropShape.SharpeCorner].
     * @return A [KropResult] object which can be either [KropResult.Success] containing the
     * cropped [ImageBitmap] or [KropResult.Failed] if an error occurred during cropping.
     */
    internal suspend fun getCroppedImageBitmap(
        imageRect: Rect? = null,
        imageCanvasSize: IntSize? = null,
        imageFlip: KropImageFlip? = null,
        imageShape: KropShape? = null
    ): KropResult {

        return originalImage.getCroppedImageBitmap(
            cropRect = imageRect ?: Rect(topLeft = topLeft, bottomRight = bottomRight),
            canvasSize = imageCanvasSize ?: canvasSize,
            imageFlip = imageFlip,
            kropShape = imageShape ?: kropShape
        )
    }

    companion object {

        val StateSaver: Saver<ImageKropState, List<Any?>> = Saver(
            save = { state ->

                listOf(
                    state.imageBitmap,
                    state.config,
                    state.shapeList,
                    state.aspectList,
                    state.originalImage,
                    state.modifiedImage,
                    state.previewImage,
                    state.imageList,
                    state.kropAspectRatio,
                    state.isAspectLocked,
                    state.kropShape,
                    state.kropCorner,
                    state.isMovingCropRect,
                    state.canvasSize,
                    state.topLeft,
                    state.topRight,
                    state.bottomLeft,
                    state.bottomRight,
                    state.isAspectRatioMenuExpanded,
                    state.isShapeMenuExpanded
                )
            },
            restore = { elements ->

                val savedImageBitmap = elements[0] as ImageBitmap
                val savedConfig = elements[1] as KropConfig
                val savedShapeList = elements[2] as ImmutableList<KropShape>
                val savedAspectList = elements[3] as ImmutableList<KropAspectRatio>
                val savedOriginalImage = elements[4] as ImageBitmap
                val savedModifiedImage = elements[5] as? ImageBitmap
                val savedPreviewImage = elements[6] as? ImageBitmap

                val savedImageList = (elements[7] as? PersistentList<ImageBitmap>)
                    ?: persistentListOf(savedImageBitmap)

                val savedKropAspectRatio = elements[8] as KropAspectRatio
                val savedIsAspectLocked = elements[9] as Boolean
                val savedKropShape = elements[10] as KropShape
                val savedKropCorner = elements[11] as? KropCorner
                val savedIsMovingCropRect = elements[12] as Boolean
                val savedCanvasSize = elements[13] as IntSize
                val savedTopLeft = elements[14] as Offset
                val savedTopRight = elements[15] as Offset
                val savedBottomLeft = elements[16] as Offset
                val savedBottomRight = elements[17] as Offset
                val savedIsAspectRatioMenuExpanded = elements[18] as Boolean
                val savedIsShapeMenuExpanded = elements[19] as Boolean

                ImageKropState(
                    imageBitmap = savedImageBitmap,
                    config = savedConfig,
                    shapeList = savedShapeList,
                    aspectList = savedAspectList
                ).apply {

                    originalImage = savedOriginalImage
                    modifiedImage = savedModifiedImage
                    previewImage = savedPreviewImage
                    imageList = savedImageList
                    kropAspectRatio = savedKropAspectRatio
                    isAspectLocked = savedIsAspectLocked
                    kropShape = savedKropShape
                    kropCorner = savedKropCorner
                    isMovingCropRect = savedIsMovingCropRect
                    canvasSize = savedCanvasSize
                    topLeft = savedTopLeft
                    topRight = savedTopRight
                    bottomLeft = savedBottomLeft
                    bottomRight = savedBottomRight
                    isAspectRatioMenuExpanded = savedIsAspectRatioMenuExpanded
                    isShapeMenuExpanded = savedIsShapeMenuExpanded
                }
            }
        )
    }
}