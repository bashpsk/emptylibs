package io.bashpsk.emptylibs.imageedit.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.toIntSize
import androidx.compose.ui.unit.toSize
import io.bashpsk.emptylibs.composeutils.size.SizeData
import io.bashpsk.emptylibs.composeutils.size.toSizeData
import io.bashpsk.emptylibs.imageedit.cache.ImageEditCacheManager
import io.bashpsk.emptylibs.imageedit.cache.ImageEditListCacheManager
import io.bashpsk.emptylibs.imageedit.cache.ImageInputCacheManager
import io.bashpsk.emptylibs.imageedit.edit.EditItemCorner.Companion.hasCornerEdge
import io.bashpsk.emptylibs.imageedit.extension.getEditItemCorner
import io.bashpsk.emptylibs.imageedit.extension.hasEditItemClicked
import io.bashpsk.emptylibs.imageedit.extension.toBottomRight
import io.bashpsk.emptylibs.imageedit.extension.toPixel
import io.bashpsk.emptylibs.imageedit.extension.toRect
import io.bashpsk.emptylibs.imageutils.extension.fittedImageSize
import io.bashpsk.emptylibs.imageutils.extension.toSize
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Remembers the state for image editing.
 *
 * This function creates and remembers an [ImageEditState] instance, which holds the current state
 * of the image editing process. This includes the list of edits, the currently selected tool,
 * and other relevant information.
 *
 * The state is automatically saved and restored across configuration changes
 * (e.g., screen rotation)
 * using [rememberSaveable].
 *
 * @param imageBitmap The initial [ImageBitmap] to be edited. Can be null if no image is loaded
 * initially.
 * @param config The [ImageEditConfig] to configure the behavior of the image editor.
 * Defaults to [ImageEditConfig.surfaceBased].
 * @return An [ImageEditState] instance that can be used to control and observe the image editing
 * process.
 */
@Composable
fun rememberImageEditState(
    imageBitmap: ImageBitmap?,
    config: ImageEditConfig = ImageEditConfig.surfaceBased()
): ImageEditState {

    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    return rememberSaveable(
        imageBitmap,
        config,
        density,
        textMeasurer,
        saver = ImageEditState.StateSaver(
            imageBitmap = imageBitmap,
            config = config,
            density = density,
            textMeasurer = textMeasurer
        )
    ) {
        ImageEditState(
            imageBitmap = imageBitmap,
            config = config,
            density = density,
            textMeasurer = textMeasurer
        )
    }
}

/**
 * Represents the state of an image editing session.
 *
 * This class holds all the information related to the image being edited,
 * the list of edits applied, the current edit item, and various input states
 * for different editing tools (brush, erase, image, shape, text).
 * It also manages the canvas size, toolbar state, and selected corner for resizing/moving items.
 *
 * This class is designed to be used with Jetpack Compose and provides a `Saver`
 * for state restoration across configuration changes or process death.
 *
 * @property imageBitmap The original [ImageBitmap] being edited. Can be null if no image is loaded.
 * @property config The [ImageEditConfig] containing configuration options for the editor.
 * @property density The [Density] of the current display, used for pixel conversions.
 * @property textMeasurer A [TextMeasurer] used for calculating the size of text items.
 *
 * @property imageEditItemList A persistent list of [ImageEditItems] representing all edits applied
 * to the image.
 * @property currentImageEditItem The currently selected or active [ImageEditItems] for editing.
 * @property isToolBarMenuExpanded A boolean indicating whether the toolbar menu is currently
 * expanded.
 * @property canvasSize The current [Size] of the editing canvas.
 * @property currentCorner The currently selected [EditItemCorner] for an item, used for resizing or
 * moving.
 * @property brushEditInput The current input state for the brush tool ([ImageEditInput.BrushItem]).
 * @property eraseEditInput The current input state for the erase tool ([ImageEditInput.EraseItem]).
 * @property imageEditInput The current input state for adding an image item
 * ([ImageEditInput.ImageItem]).
 * @property shapeEditInput The current input state for adding a shape item
 * ([ImageEditInput.ShapeItem]).
 * @property textEditInput The current input state for adding a text item
 * ([ImageEditInput.TextItem]).
 */
@OptIn(ExperimentalTime::class)
@Stable
class ImageEditState(
    val imageBitmap: ImageBitmap?,
    val config: ImageEditConfig,
    val density: Density,
    val textMeasurer: TextMeasurer
) {

    /**
     * A list that stores all the [ImageEditItems] currently applied to the image.
     * This list is persistent and immutable, meaning that any modification creates a new list.
     * It is used to draw the items on the canvas and to manage undo/redo operations.
     */
    internal var imageEditItemList by mutableStateOf(persistentListOf<ImageEditItems>())
        private set

    /**
     * Represents the currently selected item for editing.
     * This item is actively being manipulated by the user, such as resizing, moving, or modifying
     * its properties.
     * It can be `null` if no item is currently selected for editing.
     * The value of this property is managed internally and can be observed for changes.
     */
    var currentImageEditItem by mutableStateOf<ImageEditItems?>(null)
        private set

    /**
     * Represents whether the toolbar menu is currently expanded or not.
     *
     * This property is a mutable state, meaning that changes to its value will trigger
     * recomposition in any Composable functions that observe it. It is primarily used to control
     * the visibility of the toolbar menu within the image editing interface.
     *
     * @see mutableStateOf
     */
    internal var isToolBarMenuExpanded by mutableStateOf(false)

    /**
     * Represents the current size of the canvas on which the image is being edited.
     * This size is used for layout calculations and ensuring that edit items
     * are positioned and scaled correctly within the visible canvas area.
     * It is updated when the layout of the editing UI changes.
     */
    internal var canvasSize by mutableStateOf(Size.Zero)

    /**
     * Represents the current corner of the edit item being interacted with.
     * This is used to determine how transformations (like resizing or moving)
     * should be applied to the `currentImageEditItem`.
     * It's nullable, meaning no specific corner is currently being interacted with.
     */
    internal var currentCorner by mutableStateOf<EditItemCorner?>(null)

    /**
     * Represents the current input configuration for the brush tool.
     * This state variable holds an instance of [ImageEditInput.BrushItem],
     * which defines properties like color, thickness, and style for brush strokes.
     * It is mutable and observed by Compose, so changes to it will trigger recomposition.
     * The initial value is a default [ImageEditInput.BrushItem].
     */
    internal var brushEditInput by mutableStateOf(ImageEditInput.BrushItem())

    /**
     * Represents the current input configuration for the erase tool.
     * This state variable holds an instance of [ImageEditInput.EraseItem],
     * which defines properties like thickness and style for erase strokes.
     * It is mutable and observed by Compose, so changes to it will trigger recomposition.
     * The initial value is a default [ImageEditInput.EraseItem].
     */
    internal var eraseEditInput by mutableStateOf(ImageEditInput.EraseItem())

    /**
     * Holds the current input configuration for the image editing tool.
     * This state variable is an instance of [ImageEditInput.ImageItem],
     * which defines properties such as the image bitmap, shape, position, and size
     * for the image item to be added or edited.
     * It is mutable and observed by Compose, meaning changes to it will trigger recomposition.
     * The initial value is a default [ImageEditInput.ImageItem].
     */
    internal var imageEditInput by mutableStateOf(ImageEditInput.ImageItem())

    /**
     * Represents the current input configuration for the shape tool.
     * This state variable holds an instance of [ImageEditInput.ShapeItem],
     * which defines properties like shape type, color, style (fill or stroke),
     * and dimensions for shapes to be drawn.
     * It is mutable and observed by Compose, so changes to it will trigger recomposition.
     * The initial value is a default [ImageEditInput.ShapeItem].
     */
    internal var shapeEditInput by mutableStateOf(ImageEditInput.ShapeItem())

    /**
     * Represents the current input configuration for the text tool.
     * This state variable holds an instance of [ImageEditInput.TextItem],
     * which defines properties like content, style, color, and size for text elements.
     * It is mutable and observed by Compose, so changes to it will trigger recomposition.
     * The initial value is a default [ImageEditInput.TextItem].
     */
    internal var textEditInput by mutableStateOf(ImageEditInput.TextItem())

    /**
     * Adds or updates an [ImageEditItems] in the `imageEditItemList`.
     *
     * If an item with the same `uuid` already exists in the list, it will be replaced
     * with the new `items`. Otherwise, the new `items` will be added to the end of the list.
     *
     * This function ensures that the `imageEditItemList` remains a persistent list,
     * meaning a new list instance is created upon modification.
     *
     * @param items The [ImageEditItems] to be added or updated in the list.
     */
    fun addImageEditItem(items: ImageEditItems) {

        imageEditItemList.find { editItems -> editItems.uuid == items.uuid }?.let { editItems ->

            imageEditItemList = imageEditItemList.remove(element = editItems).add(element = items)
        } ?: run {

            imageEditItemList = imageEditItemList.add(element = items)
        }
    }

    /**
     * Removes a specific [ImageEditItems] from the list of applied edits.
     *
     * This function updates the `imageEditItemList` by removing the provided `items`.
     * If the item is not found in the list, the list remains unchanged.
     * This is typically used when an edit needs to be undone or deleted.
     *
     * @param items The [ImageEditItems] to remove from the list.
     */
    fun removeImageEditItem(items: ImageEditItems) {

        imageEditItemList = imageEditItemList.remove(element = items)
    }

    /**
     * Undoes the last edit operation on the canvas.
     *
     * This function removes the most recently added [ImageEditItems] from the
     * [imageEditItemList]. If the list is empty, this function does nothing.
     */
    fun onUndoCanvas() {

        imageEditItemList.lastOrNull()?.let { items ->

            removeImageEditItem(items = items)
        }
    }

    /**
     * Clears all edits from the canvas.
     *
     * This function resets the current edit item to `null` and clears the `imageEditItemList`,
     * effectively removing all applied edits from the image.
     */
    fun onClearCanvas() {

        onCurrentImageEdit(items = null)
        imageEditItemList = persistentListOf()
    }

    /**
     * Sets the currently active item for editing.
     *
     * This function updates the `currentImageEditItem` property with the provided [items].
     * If [items] is `null`, it effectively deselects any currently active item.
     * This is typically called when a user selects an item on the canvas to modify it,
     * or when an editing operation is completed and the item should no longer be active.
     *
     * @param items The [ImageEditItems] to set as the current edit item, or `null` to clear the
     * selection.
     */
    fun onCurrentImageEdit(items: ImageEditItems?) {

        currentImageEditItem = items
    }

    /**
     * Creates and sets a new brush item as the current edit item.
     * This function initializes an [ImageEditItems.BrushItem] using the properties
     * defined in [brushEditInput]. A unique ID based on the current system time
     * is assigned to the new brush item.
     * The newly created brush item is then set as the `currentImageEditItem`
     * by calling [onCurrentImageEdit].
     */
    fun onBrushItem() {

        val items = ImageEditItems.BrushItem(
            color = brushEditInput.color,
            style = Stroke(
                width = brushEditInput.thickness,
                miter = brushEditInput.miter,
                cap = brushEditInput.strokeCap,
                join = brushEditInput.strokeJoin,
                pathEffect = PathEffect.dashPathEffect(
                    intervals = floatArrayOf(
                        brushEditInput.dashIntervalOff,
                        brushEditInput.dashIntervalOn
                    ),
                    phase = brushEditInput.dashPhase
                )
            ),
            smoothness = brushEditInput.smoothness,
            path = persistentListOf()
        ).apply {

            uuid = Clock.System.now().toEpochMilliseconds().toString()
        }

        onCurrentImageEdit(items = items)
    }

    /**
     * Creates and sets a new erase item as the current edit item.
     * This function initializes an [ImageEditItems.EraseItem] using the properties
     * defined in [eraseEditInput]. A unique ID based on the current system time
     * is assigned to the new erase item.
     * The newly created erase item is then set as the `currentImageEditItem`
     * by calling [onCurrentImageEdit].
     */
    fun onEraseItem() {

        val items = ImageEditItems.EraseItem(
            style = Stroke(
                width = eraseEditInput.thickness,
                cap = eraseEditInput.strokeCap,
                join = eraseEditInput.strokeJoin,
                pathEffect = PathEffect.dashPathEffect(
                    intervals = floatArrayOf(
                        eraseEditInput.dashIntervalOff,
                        eraseEditInput.dashIntervalOn
                    ),
                    phase = eraseEditInput.dashPhase
                )
            ),
            smoothness = eraseEditInput.smoothness,
            path = persistentListOf()
        ).apply {

            uuid = Clock.System.now().toEpochMilliseconds().toString()
        }

        onCurrentImageEdit(items = items)
    }

    /**
     * Creates and sets a new image item as the current edit item.
     * This function initializes an [ImageEditItems.ImageItem] using the properties
     * defined in [imageEditInput], including the bitmap, shape, position, and size.
     * A unique ID based on the current system time is assigned to the new image item.
     * The newly created image item is then set as the `currentImageEditItem`
     * by calling [onCurrentImageEdit].
     */
    fun onImageItem() {

        val items = ImageEditItems.ImageItem(
            bitmap = imageEditInput.bitmap,
            shape = imageEditInput.shape,
            position = imageEditInput.position,
            size = imageEditInput.size
        ).apply {

            uuid = Clock.System.now().toEpochMilliseconds().toString()
        }

        onCurrentImageEdit(items = items)
    }

    /**
     * Creates and sets a new shape item as the current edit item.
     *
     * This function initializes an [ImageEditItems.ShapeItem] using properties from
     * [shapeEditInput].
     * If the size or position in [shapeEditInput] is unspecified,
     * it calculates a default size (half of the canvas width, maintaining aspect ratio)
     * and centers the shape on the canvas.
     *
     * A unique ID based on the current system time is assigned to the new shape item.
     * The newly created shape item is then set as the `currentImageEditItem`
     * by calling [onCurrentImageEdit].
     */
    fun onShapeItem() {

        val sizeOfItem = shapeEditInput.size.takeIf { size ->

            size != Size.Unspecified
        } ?: canvasSize.fittedImageSize(
            Size(canvasSize.width / 2.0F, canvasSize.width / 2.0F)
        )

        val positionOfItem = shapeEditInput.position.takeIf { position ->

            position != Offset.Unspecified
        } ?: Offset(
            x = (canvasSize.width - sizeOfItem.width) / 2.0F,
            y = (canvasSize.height - sizeOfItem.height) / 2.0F
        )

        val items = ImageEditItems.ShapeItem(
            shape = shapeEditInput.shape,
            color = shapeEditInput.color,
            style = when (val style = shapeEditInput.style) {

                is Fill -> style

                is Stroke -> Stroke(
                    width = shapeEditInput.thickness,
                    miter = shapeEditInput.miter,
                    cap = shapeEditInput.strokeCap,
                    join = shapeEditInput.strokeJoin,
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(
                            shapeEditInput.dashIntervalOff,
                            shapeEditInput.dashIntervalOn
                        ),
                        phase = shapeEditInput.dashPhase
                    )
                )
            },
            position = positionOfItem,
            size = sizeOfItem
        ).apply {

            uuid = Clock.System.now().toEpochMilliseconds().toString()
        }

        onCurrentImageEdit(items = items)
    }

    /**
     * Creates and sets a new text item as the current edit item.
     * This function initializes an [ImageEditItems.TextItem] using the properties
     * defined in [textEditInput]. It calculates the appropriate size and position
     * for the text based on the content, style, and canvas dimensions.
     * A unique ID based on the current system time is assigned to the new text item.
     * The newly created text item is then set as the `currentImageEditItem`
     * by calling [onCurrentImageEdit].
     */
    fun onTextItem() {

        val maxWidth = textEditInput.size.takeIf { size ->

            size != Size.Unspecified
        }?.width?.toInt() ?: canvasSize.width.toInt()

        val maxHeight = textEditInput.size.takeIf { size ->

            size != Size.Unspecified
        }?.height?.toInt() ?: canvasSize.height.toInt()

        val sizeOfItem = textMeasurer.measure(
            text = textEditInput.content,
            style = textEditInput.style,
            overflow = TextOverflow.Clip,
            constraints = Constraints(maxWidth = maxWidth, maxHeight = maxHeight)
        ).size.toSize()

        val positionOfItem = Offset(
            x = (maxWidth - sizeOfItem.width) / 2.0F,
            y = (maxHeight - sizeOfItem.height) / 2.0F
        )

        val items = ImageEditItems.TextItem(
            content = textEditInput.content,
            style = textEditInput.style,
            position = positionOfItem,
            size = sizeOfItem
        ).apply {

            uuid = Clock.System.now().toEpochMilliseconds().toString()
        }

        onCurrentImageEdit(items = items)
    }

    /**
     * Refreshes the current edit item based on its type.
     *
     * This function is typically called when an edit item's properties
     * (e.g., color, size, text content) have been modified outside the direct
     * manipulation on the canvas (e.g., through a settings panel).
     *
     * It checks the type of the `currentImageEditItem` and calls the
     * corresponding `on<Type>Item()` function to re-initialize or update
     * the item with the latest input values.
     *
     * - If `currentImageEditItem` is a [ImageEditItems.BrushItem], it calls `onBrushItem()`.
     * - If `currentImageEditItem` is a [ImageEditItems.EraseItem], it calls `onEraseItem()`.
     * - If `currentImageEditItem` is a [ImageEditItems.ImageItem], it calls `onImageItem()`.
     * - If `currentImageEditItem` is a [ImageEditItems.ShapeItem], it calls `onShapeItem()`.
     * - If `currentImageEditItem` is a [ImageEditItems.TextItem], it calls `onTextItem()`.
     * - If `currentImageEditItem` is `null`, it does nothing.
     */
    fun onRefreshEditItem() {

        when (currentImageEditItem) {

            is ImageEditItems.BrushItem -> onBrushItem()
            is ImageEditItems.EraseItem -> onEraseItem()
            is ImageEditItems.ImageItem -> onImageItem()
            is ImageEditItems.ShapeItem -> onShapeItem()
            is ImageEditItems.TextItem -> onTextItem()
            null -> {}
        }
    }

    /**
     * Resets the current edit item being manipulated.
     *
     * This function is typically called when an editing operation is completed or cancelled.
     * It sets the `currentImageEditItem` to `null`, effectively deselecting any active item.
     * This allows the user to start a new editing operation or select a different item.
     */
    fun onResetEditItem() {

        onCurrentImageEdit(items = null)
    }

    /**
     * Generates an [ImageBitmap] that includes all the edits applied to the original image.
     *
     * This function draws the original [imageBitmap] onto a new [ImageBitmap]
     * of the same dimensions. Then, it iterates through all the [imageEditItemList]
     * and the [currentImageEditItem] (if any), drawing each edit item onto the canvas.
     * The drawing operations are scaled appropriately based on the [canvasSize]
     * to ensure edits appear correctly on the final bitmap.
     *
     * @return A new [ImageBitmap] containing the original image with all edits applied,
     * or `null` if the original [imageBitmap] is `null`.
     */
    suspend fun getEditedImageBitmap(): ImageBitmap? = withContext(Dispatchers.IO) {

        return@withContext imageBitmap?.let { bitmap ->

            val editedImageBitmap = ImageBitmap(bitmap.width, bitmap.height)
            val canvas = Canvas(image = editedImageBitmap)
            val drawScope = CanvasDrawScope()

            val scaleX = if (canvasSize.width != 0.0F) bitmap.width / canvasSize.width else 1.0F
            val scaleY = if (canvasSize.height != 0.0F) bitmap.height / canvasSize.height else 1.0F

            drawScope.draw(
                density = density,
                layoutDirection = LayoutDirection.Ltr,
                canvas = canvas,
                size = bitmap.toSize()
            ) {

                drawImage(
                    image = bitmap,
                    srcOffset = IntOffset.Zero,
                    srcSize = bitmap.toSize().toIntSize(),
                    dstOffset = IntOffset.Zero,
                    dstSize = size.toIntSize()
                )

                drawIntoCanvas { nativeCanvas ->

                    nativeCanvas.save()
                    nativeCanvas.scale(sx = scaleX, sy = scaleY)

                    imageEditItemList.forEach { items ->

                        drawImageEditItem(items = items, textMeasurer = textMeasurer)
                    }

                    currentImageEditItem?.let { items ->

                        drawImageEditItem(items = items, textMeasurer = textMeasurer)
                    }

                    nativeCanvas.restore()
                }
            }

            editedImageBitmap
        }
    }

    /**
     * Handles the start of an edit operation on an item.
     *
     * This function is invoked when the user initiates an edit on an existing item,
     * such as by clicking on one of its resize handles or starting to drag it.
     *
     * It performs the following actions:
     * 1. If there's a `currentImageEditItem`:
     *     a. Creates a new instance of the item by applying a new unique ID (UUID)
     *        based on the current system time. This is done to ensure that
     *        subsequent modifications during the drag operation are treated as changes
     *        to this new instance, allowing for undo/redo functionality if needed.
     *     b. Updates `currentImageEditItem` with this new instance.
     *     c. Determines which corner of the item was clicked based on the `position`
     *        and the configured handle size. This `currentCorner` will be used
     *        by `onEditItemChanges` to apply transformations (resize/move) correctly.
     *
     * @param position The [Offset] representing the coordinates where the edit interaction started
     *                 (e.g., the click position on a resize handle).
     */
    internal fun onEditItemStart(position: Offset) {

        currentImageEditItem?.let { items ->

            val newItems = items.apply {

                uuid = Clock.System.now().toEpochMilliseconds().toString()
            }

            onCurrentImageEdit(items = newItems)

            currentCorner = items.toRect()?.getEditItemCorner(
                clickPosition = position,
                threshold = maxOf(config.handleWidth, config.handleHeight).toPixel(density)
            )
        }
    }

    /**
     * Finalizes the current edit operation on an item.
     *
     * This function is called when the user finishes editing the `currentImageEditItem`
     * (e.g., by releasing a drag gesture). It performs the following actions:
     * 1. If `currentImageEditItem` is not null:
     *    - Adds the `currentImageEditItem` to the `imageEditItemList`.
     *    - Resets `currentImageEditItem` to null using `onResetEditItem()`.
     *    - Depending on the type of the `currentImageEditItem`, it calls the corresponding
     *      function to prepare for a new item of the same type (e.g., `onBrushItem()` for a
     *      brush stroke). This allows for continuous drawing or adding of similar items.
     *      - For `ImageItem`, `ShapeItem`, and `TextItem`, no specific action is taken to
     *        immediately create a new item, as these are typically placed once.
     * 2. Sets `currentCorner` to null, indicating that no corner is currently being interacted
     * with.
     */
    internal fun onEditItemEnd() {

        currentImageEditItem?.let { items ->

            addImageEditItem(items = items)
            onResetEditItem()

            when (items) {

                is ImageEditItems.BrushItem -> onBrushItem()
                is ImageEditItems.EraseItem -> onEraseItem()
                is ImageEditItems.ImageItem -> {}
                is ImageEditItems.ShapeItem -> {}
                is ImageEditItems.TextItem -> {}
            }
        }

        currentCorner = null
    }

    /**
     * Handles changes to the currently selected edit item based on user interaction.
     *
     * This function is called when the user is actively dragging or resizing an edit item.
     * It updates the properties of the `currentImageEditItem` based on the provided `position`
     * (for drawing tools like brush and erase) or `amount` (for transformations like moving
     * or resizing).
     *
     * For [ImageEditItems.BrushItem] and [ImageEditItems.EraseItem], it adds the new `position`
     * to the item's path.
     *
     * For [ImageEditItems.ImageItem], [ImageEditItems.ShapeItem], and [ImageEditItems.TextItem],
     * it calculates the new position and size of the item using `getEditItemPositionSize`
     * based on the `amount` of change. For [ImageEditItems.TextItem], it also remeasures
     * the text with the new size constraints.
     *
     * After updating the item, it calls `onCurrentImageEdit` to reflect the changes in the UI.
     * The `uuid` of the item is preserved to maintain its identity.
     *
     * @param position The current pointer position, used for brush and erase tools.
     * @param amount The change in offset (dx, dy) from the start of the drag,
     * used for image, shape, and text items.
     */
    internal fun onEditItemChanges(position: Offset, amount: Offset) {

        currentImageEditItem?.let { items ->

            when (items) {

                is ImageEditItems.BrushItem -> {

                    val newItems = items.copy(path = items.path.add(position)).apply {

                        uuid = items.uuid
                    }

                    onCurrentImageEdit(items = newItems)
                }

                is ImageEditItems.EraseItem -> {

                    val newItems = items.copy(path = items.path.add(position)).apply {

                        uuid = items.uuid
                    }

                    onCurrentImageEdit(items = newItems)
                }

                is ImageEditItems.ImageItem -> {

                    val (itemPosition, itemSize) = getEditItemPositionSize(
                        position = items.position,
                        size = items.size,
                        amount = amount
                    )

                    val newItems = items.copy(
                        position = itemPosition,
                        size = itemSize
                    ).apply {

                        uuid = items.uuid
                    }

                    onCurrentImageEdit(items = newItems)
                }

                is ImageEditItems.ShapeItem -> {

                    val (itemPosition, itemSize) = getEditItemPositionSize(
                        position = items.position,
                        size = items.size,
                        amount = amount
                    )

                    val newItems = items.copy(
                        position = itemPosition,
                        size = itemSize
                    ).apply {

                        uuid = items.uuid
                    }

                    onCurrentImageEdit(items = newItems)
                }

                is ImageEditItems.TextItem -> {

                    val (itemPosition, itemSize) = getEditItemPositionSize(
                        position = items.position,
                        size = items.size,
                        amount = amount
                    )

                    val sizeOfItem = textMeasurer.measure(
                        text = textEditInput.content,
                        style = textEditInput.style,
                        overflow = TextOverflow.Clip,
                        constraints = Constraints(
                            maxWidth = itemSize.width.toInt(),
                            maxHeight = itemSize.height.toInt(),
                        )
                    ).size.toSize()

                    val newItems = items.copy(
                        position = itemPosition,
                        size = sizeOfItem
                    ).apply {

                        uuid = items.uuid
                    }

                    onCurrentImageEdit(items = newItems)
                }
            }
        }
    }

    /**
     * Handles a click event on the canvas to select an existing edit item.
     *
     * This function iterates through the `imageEditItemList` to find if any
     * `ImageEditItems` was clicked at the given `position`.
     * If an item is found:
     *  - It sets the found item as the `currentImageEditItem`.
     *  - It removes the item from the `imageEditItemList`
     *  (to allow modification and re-addition later).
     *  - It returns `true` to indicate that a click on an existing item occurred.
     *
     * If no item is found at the click position, it returns `null`.
     *
     * @param position The [Offset] representing the coordinates of the click on the canvas.
     * @return `true` if an existing item was clicked and selected for editing, `null` otherwise.
     */
    internal fun onEditItemsClick(position: Offset): Boolean? {

        return imageEditItemList.find { items ->

            items.hasEditItemClicked(clickPosition = position)
        }?.let { items ->

            onCurrentImageEdit(items = items)
            imageEditItemList = imageEditItemList.remove(element = items)
            true
        }
    }

    /**
     * Calculates the new position and size of an editable item based on user interaction.
     *
     * This function determines how an item's position and size should change when a user drags
     * one of its corners or edges. It considers the current corner being dragged,
     * the initial position and size of the item, and the drag amount.
     *
     * The calculations ensure that the item stays within the canvas boundaries and maintains
     * a minimum size defined in the `config`. If a corner is being dragged, it also attempts
     * to maintain the item's aspect ratio.
     *
     * @param position The current top-left [Offset] of the item being edited.
     * @param size The current [Size] of the item being edited.
     * @param amount The [Offset] representing the amount of drag applied by the user.
     * @return A [Pair] containing the new top-left [Offset] (first) and new [Size] (second)
     * of the item after the drag operation.
     */
    internal fun getEditItemPositionSize(
        position: Offset,
        size: Size,
        amount: Offset
    ): Pair<Offset, Size> {

        val sizeLimit = config.minItemSize.toPixel(density = density)
        val minX = 0.0F
        val minY = 0.0F
        val maxX = canvasSize.width
        val maxY = canvasSize.height
        val initialBottomRight = position.toBottomRight(size = size)

        var calculatedTopLeft = position
        var calculatedSize = size

        val aspectRatio = size.takeIf { itemSize ->

            itemSize.width > 0.0F && itemSize.height > 0.0F && currentCorner.hasCornerEdge()
        }?.let { itemSize -> itemSize.width / itemSize.height } ?: 1.0F

        when (currentCorner) {

            EditItemCorner.TOP_LEFT, EditItemCorner.TOP_RIGHT, EditItemCorner.BOTTOM_LEFT,
            EditItemCorner.BOTTOM_RIGHT -> {

                val fixedCorner = when (currentCorner) {

                    EditItemCorner.TOP_LEFT -> initialBottomRight
                    EditItemCorner.TOP_RIGHT -> Offset(position.x, initialBottomRight.y)
                    EditItemCorner.BOTTOM_LEFT -> Offset(initialBottomRight.x, position.y)
                    EditItemCorner.BOTTOM_RIGHT -> position
                    else -> return Pair(position, size)
                }

                val draggedCorner = when (currentCorner) {

                    EditItemCorner.TOP_LEFT -> position
                    EditItemCorner.TOP_RIGHT -> Offset(initialBottomRight.x, position.y)
                    EditItemCorner.BOTTOM_LEFT -> Offset(position.x, initialBottomRight.y)
                    EditItemCorner.BOTTOM_RIGHT -> initialBottomRight
                    else -> return Pair(position, size)
                }

                currentCorner?.let { corner ->

                    calculateNewEditRect(
                        draggedCorner = draggedCorner,
                        fixedCorner = fixedCorner,
                        dragDelta = amount,
                        cornerType = corner,
                        aspectRatio = aspectRatio,
                        minSize = sizeLimit,
                        canvasWidth = maxX,
                        canvasHeight = maxY
                    )?.let { (rectTopLeft, rectBottomRight) ->

                        calculatedTopLeft = rectTopLeft

                        calculatedSize = Size(
                            width = (rectBottomRight.x - rectTopLeft.x).coerceAtLeast(sizeLimit),
                            height = (rectBottomRight.y - rectTopLeft.y).coerceAtLeast(sizeLimit)
                        )
                    } ?: run {

                        calculatedTopLeft = position
                        calculatedSize = size
                    }
                }
            }

            EditItemCorner.TOP_CENTRE -> {

                val potentialNewTopY = position.y + amount.y
                val newY = potentialNewTopY.coerceIn(minY, initialBottomRight.y - sizeLimit)

                calculatedTopLeft = position.copy(y = newY)

                calculatedSize = size.copy(
                    height = (initialBottomRight.y - newY).coerceAtLeast(sizeLimit)
                )
            }

            EditItemCorner.BOTTOM_CENTRE -> {

                val potentialNewBottomY = initialBottomRight.y + amount.y
                val newY = potentialNewBottomY.coerceIn(position.y + sizeLimit, maxY)

                calculatedSize = size.copy(height = (newY - position.y).coerceAtLeast(sizeLimit))
            }

            EditItemCorner.LEFT_CENTRE -> {

                val potentialNewLeftX = position.x + amount.x
                val newX = potentialNewLeftX.coerceIn(minX, initialBottomRight.x - sizeLimit)

                calculatedTopLeft = position.copy(x = newX)

                calculatedSize = size.copy(
                    width = (initialBottomRight.x - newX).coerceAtLeast(sizeLimit)
                )
            }

            EditItemCorner.RIGHT_CENTRE -> {

                val potentialNewRightX = initialBottomRight.x + amount.x
                val newX = potentialNewRightX.coerceIn(position.x + sizeLimit, maxX)

                calculatedSize = size.copy(width = (newX - position.x).coerceAtLeast(sizeLimit))
            }

            null -> {

                val targetWidth = calculatedSize.width.coerceAtLeast(0.0F)
                val targetHeight = calculatedSize.height.coerceAtLeast(0.0F)

                calculatedTopLeft = position.copy(
                    x = (position.x + amount.x).coerceIn(
                        minX, (maxX - targetWidth).coerceAtLeast(minX)
                    ),
                    y = (position.y + amount.y).coerceIn(
                        minY, (maxY - targetHeight).coerceAtLeast(minY)
                    )
                )
            }
        }

        var finalTopLeft = calculatedTopLeft

        var finalSize = Size(
            width = calculatedSize.width.coerceAtLeast(sizeLimit),
            height = calculatedSize.height.coerceAtLeast(sizeLimit)
        )

        finalTopLeft = finalTopLeft.copy(
            x = finalTopLeft.x.coerceIn(minX, (maxX - finalSize.width).coerceAtLeast(minX)),
            y = finalTopLeft.y.coerceIn(minY, (maxY - finalSize.height).coerceAtLeast(minY))
        )

        finalSize = finalSize.copy(
            width = finalSize.width.coerceAtMost(maxX - finalTopLeft.x),
            height = finalSize.height.coerceAtMost(maxY - finalTopLeft.y)
        ).let { newSize ->

            Size(newSize.width.coerceAtLeast(sizeLimit), newSize.height.coerceAtLeast(sizeLimit))
        }

        finalTopLeft = finalTopLeft.copy(
            x = finalTopLeft.x.coerceIn(minX, (maxX - finalSize.width).coerceAtLeast(minX)),
            y = finalTopLeft.y.coerceIn(minY, (maxY - finalSize.height).coerceAtLeast(minY))
        )

        currentCorner.takeIf { corner -> corner.hasCornerEdge() }?.let { corner ->

            var tempWidth = finalSize.width
            var tempHeight = finalSize.height

            (tempWidth / aspectRatio > tempHeight).takeIf { it }?.run {

                tempWidth = tempHeight * aspectRatio
            } ?: (tempHeight > tempWidth / aspectRatio).takeIf { it }?.run {

                tempHeight = tempWidth / aspectRatio
            }

            tempWidth = tempWidth.coerceAtLeast(sizeLimit)
            tempHeight = tempHeight.coerceAtLeast(sizeLimit)

            val adjustedTopLeftX = when (corner) {

                EditItemCorner.TOP_LEFT -> initialBottomRight.x - tempWidth
                EditItemCorner.TOP_RIGHT -> position.x
                EditItemCorner.BOTTOM_LEFT -> initialBottomRight.x - tempWidth
                EditItemCorner.BOTTOM_RIGHT -> position.x
                else -> finalTopLeft.x
            }

            val adjustedTopLeftY = when (corner) {

                EditItemCorner.TOP_LEFT -> initialBottomRight.y - tempHeight
                EditItemCorner.TOP_RIGHT -> initialBottomRight.y - tempHeight
                EditItemCorner.BOTTOM_LEFT -> position.y
                EditItemCorner.BOTTOM_RIGHT -> position.y
                else -> finalTopLeft.y
            }

            finalTopLeft = Offset(adjustedTopLeftX, adjustedTopLeftY)
            finalSize = Size(tempWidth, tempHeight)

            finalTopLeft = finalTopLeft.copy(
                x = finalTopLeft.x.coerceIn(minX, (maxX - finalSize.width).coerceAtLeast(minX)),
                y = finalTopLeft.y.coerceIn(minY, (maxY - finalSize.height).coerceAtLeast(minY))
            )

            finalSize = finalSize.copy(
                width = finalSize.width.coerceAtMost(
                    maxX - finalTopLeft.x
                ).coerceAtLeast(sizeLimit),
                height = finalSize.height.coerceAtMost(
                    maxY - finalTopLeft.y
                ).coerceAtLeast(sizeLimit)
            )
        }

        return Pair(first = finalTopLeft, second = finalSize)
    }

    /**
     * Calculates the new top-left and bottom-right coordinates of an editable item's bounding
     * rectangle
     * after a drag operation on one of its corners.
     *
     * This function considers the dragged corner, the fixed corner opposite to it, the drag delta,
     * the type of corner being dragged, the aspect ratio to maintain, the minimum allowed size for
     * the item, and the boundaries of the canvas.
     *
     * The calculation ensures that the item maintains its aspect ratio
     * (if specified for corner drags)
     * and does not go below the minimum size or outside the canvas boundaries.
     *
     * @param draggedCorner The initial [Offset] of the corner being dragged before the drag
     * operation.
     * @param fixedCorner The [Offset] of the corner opposite to the `draggedCorner`, which remains
     * stationary during the resize.
     * @param dragDelta The [Offset] representing the change in position of the `draggedCorner` due
     * to the drag.
     * @param cornerType The [EditItemCorner] that is being dragged. This determines how the width
     * and height are calculated and adjusted.
     * @param aspectRatio The desired aspect ratio (width / height) to maintain for the item.
     * This is primarily used when dragging corner handles.
     * @param minSize The minimum allowed size (width or height) for the item in pixels.
     * @param canvasWidth The width of the canvas in pixels, defining the right boundary.
     * @param canvasHeight The height of the canvas in pixels, defining the bottom boundary.
     * @return A [Pair] of [Offset] objects representing the new top-left and bottom-right
     * coordinates of the resized rectangle, or `null` if the operation is invalid
     * (e.g., resulting size is too small, or an unsupported `cornerType` is provided).
     */
    private fun calculateNewEditRect(
        draggedCorner: Offset,
        fixedCorner: Offset,
        dragDelta: Offset,
        cornerType: EditItemCorner,
        aspectRatio: Float,
        minSize: Float,
        canvasWidth: Float,
        canvasHeight: Float
    ): Pair<Offset, Offset>? {

        val newPosition = draggedCorner + dragDelta

        val proposedWidth = when (cornerType) {

            EditItemCorner.TOP_LEFT, EditItemCorner.BOTTOM_LEFT -> fixedCorner.x - newPosition.x
            EditItemCorner.TOP_RIGHT, EditItemCorner.BOTTOM_RIGHT -> newPosition.x - fixedCorner.x
            else -> return null
        }.coerceAtLeast(minSize)

        var adjustedWidth = proposedWidth
        var adjustedHeight = proposedWidth / aspectRatio

        (adjustedHeight < minSize).takeIf { it }?.run {

            adjustedHeight = minSize
            adjustedWidth = adjustedHeight * aspectRatio
        }

        adjustedWidth = adjustedWidth.coerceAtLeast(minSize)

        val (initialTopLeft, initialBottomRight) = when (cornerType) {

            EditItemCorner.TOP_LEFT -> Offset(
                fixedCorner.x - adjustedWidth,
                fixedCorner.y - adjustedHeight
            ) to fixedCorner

            EditItemCorner.TOP_RIGHT -> Offset(
                fixedCorner.x,
                fixedCorner.y - adjustedHeight
            ) to Offset(fixedCorner.x + adjustedWidth, fixedCorner.y)

            EditItemCorner.BOTTOM_LEFT -> Offset(
                fixedCorner.x - adjustedWidth,
                fixedCorner.y
            ) to Offset(fixedCorner.x, fixedCorner.y + adjustedHeight)

            EditItemCorner.BOTTOM_RIGHT -> fixedCorner to Offset(
                fixedCorner.x + adjustedWidth,
                fixedCorner.y + adjustedHeight
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

        (currentWidth / aspectRatio > currentHeight).takeIf { it }?.run {

            when (cornerType) {

                EditItemCorner.TOP_LEFT, EditItemCorner.BOTTOM_LEFT -> {

                    currentWidth = currentHeight * aspectRatio
                    finalTopLeft = finalTopLeft.copy(x = finalBottomRight.x - currentWidth)
                }

                EditItemCorner.TOP_RIGHT, EditItemCorner.BOTTOM_RIGHT -> {

                    currentWidth = currentHeight * aspectRatio
                    finalBottomRight = finalBottomRight.copy(x = finalTopLeft.x + currentWidth)
                }

                else -> {}
            }
        } ?: (currentHeight > currentWidth / aspectRatio).takeIf { it }?.run {

            when (cornerType) {

                EditItemCorner.TOP_LEFT, EditItemCorner.TOP_RIGHT -> {

                    currentHeight = currentWidth / aspectRatio
                    finalTopLeft = finalTopLeft.copy(y = finalBottomRight.y - currentHeight)
                }

                EditItemCorner.BOTTOM_LEFT, EditItemCorner.BOTTOM_RIGHT -> {

                    currentHeight = currentWidth / aspectRatio
                    finalBottomRight = finalBottomRight.copy(y = finalTopLeft.y + currentHeight)
                }

                else -> {}
            }
        }

        finalTopLeft = finalTopLeft.copy(
            x = finalTopLeft.x.coerceIn(0.0F, canvasWidth - minSize),
            y = finalTopLeft.y.coerceIn(0.0F, canvasHeight - minSize)
        )

        finalBottomRight = finalBottomRight.copy(
            x = (finalTopLeft.x + currentWidth.coerceAtLeast(minSize)).coerceIn(
                finalTopLeft.x + minSize,
                canvasWidth
            ),
            y = (finalTopLeft.y + currentHeight.coerceAtLeast(minSize)).coerceIn(
                finalTopLeft.y + minSize,
                canvasHeight
            )
        )

        val finalWidth = (finalBottomRight.x - finalTopLeft.x).coerceAtLeast(minSize)
        val finalHeight = (finalBottomRight.y - finalTopLeft.y).coerceAtLeast(minSize)

        (finalWidth < minSize || finalHeight < minSize).takeIf { it }?.run {

            return null
        }

        val resultBottomRight = Offset(
            x = (finalTopLeft.x + finalWidth).coerceIn(finalTopLeft.x + minSize, canvasWidth),
            y = (finalTopLeft.y + finalHeight).coerceIn(finalTopLeft.y + minSize, canvasHeight)
        )

        val resultTopLeft = finalTopLeft.copy(
            x = (resultBottomRight.x - finalWidth).coerceIn(0.0F, canvasWidth - minSize),
            y = (resultBottomRight.y - finalHeight).coerceIn(0.0F, canvasHeight - minSize)
        )

        ((resultBottomRight.x - resultTopLeft.x) < minSize ||
                (resultBottomRight.y - resultTopLeft.y) < minSize).takeIf { it }?.run {

            return null
        }

        return Pair(resultTopLeft, resultBottomRight)
    }

    internal fun clearState() {

        ImageInputCacheManager.evictAll()
        ImageEditCacheManager.evictAll()

        imageEditItemList = persistentListOf()
        currentImageEditItem = null
        isToolBarMenuExpanded = false
        canvasSize = Size.Zero
        currentCorner = null

        brushEditInput = ImageEditInput.BrushItem()
        eraseEditInput = ImageEditInput.EraseItem()
        imageEditInput = ImageEditInput.ImageItem()
        shapeEditInput = ImageEditInput.ShapeItem()
        textEditInput = ImageEditInput.TextItem()
    }

    companion object {

        private const val KEY_EDIT_ITEM_LIST = "IMAGE-EDIT-ITEM-LIST"
        private const val KEY_CURRENT_EDIT_ITEM = "IMAGE-EDIT-CURRENT-ITEM"
        private const val KEY_TOOLBAR_MENU_EXPANDED = "IMAGE-EDIT-TOOLBAR-MENU-EXPANDED"
        private const val KEY_CANVAS_SIZE = "IMAGE-EDIT-CANVAS-SIZE"
        private const val KEY_CURRENT_CORNER = "IMAGE-EDIT-CURRENT-CORNER"
        private const val KEY_BRUSH_INPUT = "IMAGE-EDIT-BRUSH-INPUT"
        private const val KEY_ERASE_INPUT = "IMAGE-EDIT-ERASE-INPUT"
        private const val KEY_IMAGE_INPUT = "IMAGE-EDIT-IMAGE-INPUT"
        private const val KEY_SHAPE_INPUT = "IMAGE-EDIT-SHAPE-INPUT"
        private const val KEY_TEXT_INPUT = "IMAGE-EDIT-TEXT-INPUT"

        fun StateSaver(
            imageBitmap: ImageBitmap?,
            config: ImageEditConfig,
            density: Density,
            textMeasurer: TextMeasurer
        ): Saver<ImageEditState, Any> = mapSaver(
            save = { state ->

                ImageInputCacheManager.add(KEY_BRUSH_INPUT, state.brushEditInput)
                ImageInputCacheManager.add(KEY_ERASE_INPUT, state.eraseEditInput)
                ImageInputCacheManager.add(KEY_IMAGE_INPUT, state.imageEditInput)
                ImageInputCacheManager.add(KEY_SHAPE_INPUT, state.shapeEditInput)
                ImageInputCacheManager.add(KEY_TEXT_INPUT, state.textEditInput)
                ImageEditListCacheManager.add(KEY_EDIT_ITEM_LIST, state.imageEditItemList)

                state.currentImageEditItem?.let { items ->

                    ImageEditCacheManager.add(KEY_CURRENT_EDIT_ITEM, items)
                } ?: run {

                    ImageEditCacheManager.remove(KEY_CURRENT_EDIT_ITEM)
                }

                mapOf(
                    KEY_TOOLBAR_MENU_EXPANDED to state.isToolBarMenuExpanded,
                    KEY_CANVAS_SIZE to state.canvasSize.toSizeData(),
                    KEY_CURRENT_CORNER to state.currentCorner
                )
            },
            restore = { elements ->

                @Suppress("UNCHECKED_CAST")
                ImageEditState(
                    imageBitmap = imageBitmap,
                    config = config,
                    density = density,
                    textMeasurer = textMeasurer
                ).apply {

                    imageEditItemList = ImageEditListCacheManager.get(
                        KEY_EDIT_ITEM_LIST
                    ) ?: persistentListOf()

                    currentImageEditItem = ImageEditCacheManager.get(KEY_CURRENT_EDIT_ITEM)

                    isToolBarMenuExpanded = elements.getOrElse(
                        KEY_TOOLBAR_MENU_EXPANDED
                    ) { false } as Boolean

                    canvasSize = (elements.getOrElse(
                        KEY_CANVAS_SIZE
                    ) { Size.Zero.toSizeData() } as SizeData).toSize()

                    currentCorner = elements.getOrElse(
                        KEY_CURRENT_CORNER
                    ) { null } as EditItemCorner?

                    brushEditInput = (ImageInputCacheManager.get(
                        KEY_BRUSH_INPUT
                    ) ?: ImageEditInput.BrushItem()) as ImageEditInput.BrushItem

                    eraseEditInput = (ImageInputCacheManager.get(
                        KEY_ERASE_INPUT
                    ) ?: ImageEditInput.EraseItem()) as ImageEditInput.EraseItem

                    imageEditInput = (ImageInputCacheManager.get(
                        KEY_IMAGE_INPUT
                    ) ?: ImageEditInput.ImageItem()) as ImageEditInput.ImageItem

                    shapeEditInput = (ImageInputCacheManager.get(
                        KEY_SHAPE_INPUT
                    ) ?: ImageEditInput.ShapeItem()) as ImageEditInput.ShapeItem

                    textEditInput = (ImageInputCacheManager.get(
                        KEY_TEXT_INPUT
                    ) ?: ImageEditInput.TextItem()) as ImageEditInput.TextItem
                }
            }
        )
    }
}