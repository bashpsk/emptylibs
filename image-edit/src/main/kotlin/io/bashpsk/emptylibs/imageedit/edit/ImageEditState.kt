package io.bashpsk.emptylibs.imageedit.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import io.bashpsk.emptylibs.imageedit.extension.getEditItemCorner
import io.bashpsk.emptylibs.imageedit.extension.hasEditItemClicked
import io.bashpsk.emptylibs.imageedit.extension.toBottomLeft
import io.bashpsk.emptylibs.imageedit.extension.toBottomRight
import io.bashpsk.emptylibs.imageedit.extension.toPixel
import io.bashpsk.emptylibs.imageedit.extension.toRect
import io.bashpsk.emptylibs.imageedit.extension.toTopRight
import io.bashpsk.emptylibs.imageutils.extension.fittedImageSize
import io.bashpsk.emptylibs.imageutils.extension.toSize
import io.bashpsk.emptylibs.imageutils.shape.ImageShape
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun rememberImageEditState(
    imageBitmap: ImageBitmap?,
    config: ImageEditConfig = ImageEditConfig.surfaceBased()
): ImageEditState {

    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    return remember(imageBitmap, config, density, textMeasurer) {
        ImageEditState(
            imageBitmap = imageBitmap,
            config = config,
            density = density,
            textMeasurer = textMeasurer
        )
    }
}

@OptIn(ExperimentalTime::class)
class ImageEditState(
    val imageBitmap: ImageBitmap?,
    val config: ImageEditConfig,
    val density: Density,
    val textMeasurer: TextMeasurer
) {

    internal var imageEditItemList by mutableStateOf(persistentListOf<ImageEditItems>())
        private set

    var selectedPenColor by mutableStateOf(Color.Red)
        private set

    var selectedStrokeCap by mutableStateOf(StrokeCap.Round)
        private set

    var selectedStrokeJoin by mutableStateOf(StrokeJoin.Round)
        private set

    var penThickness by mutableStateOf(24.dp)
        private set

    var selectedBitmap by mutableStateOf<ImageBitmap?>(null)
        private set

    var enteredText by mutableStateOf("Empty Libs")
        private set

    var textStyle by mutableStateOf(TextStyle.Default)
        private set

    var selectedShape by mutableStateOf<ImageShape>(ImageShape.None)
        private set

    var currentImageEditItem by mutableStateOf<ImageEditItems?>(null)
        private set

    internal var isToolBarMenuExpanded by mutableStateOf(false)

    internal var canvasSize by mutableStateOf(Size.Zero)

    internal var currentCorner by mutableStateOf<EditItemCorner?>(null)

    fun updatePenColor(color: Color) {

        selectedPenColor = color
    }

    fun updateStrokeCap(type: StrokeCap) {

        selectedStrokeCap = type
    }

    fun updateStrokeJoin(type: StrokeJoin) {

        selectedStrokeJoin = type
    }

    fun updatePenThickness(thickness: Dp) {

        penThickness = thickness
    }

    fun updateBitmap(bitmap: ImageBitmap?) {

        selectedBitmap = bitmap
    }

    fun updateText(text: String) {

        enteredText = text
    }

    fun updateTextStyle(style: TextStyle) {

        textStyle = style
    }

    fun updateShape(shape: ImageShape) {

        selectedShape = shape
    }

    fun addImageEditItem(items: ImageEditItems) {

        imageEditItemList.find { editItems -> editItems.uuid == items.uuid }?.let { editItems ->

            imageEditItemList = imageEditItemList.remove(element = editItems).add(element = items)
        } ?: run {

            imageEditItemList = imageEditItemList.add(element = items)
        }
    }

    fun removeImageEditItem(items: ImageEditItems) {

        imageEditItemList = imageEditItemList.remove(element = items)
    }

    fun onUndoCanvas() {

        imageEditItemList.lastOrNull()?.let { items ->

            removeImageEditItem(items = items)
        }
    }

    fun onClearCanvas() {

        imageEditItemList = persistentListOf()
    }

    fun onCurrentImageEdit(items: ImageEditItems?) {

        currentImageEditItem = items
    }

    fun onEraseItem() {

        val items = ImageEditItems.EraseItem(
            style = Stroke(
                width = penThickness.value,
                cap = selectedStrokeCap,
                join = selectedStrokeJoin
            ),
            path = persistentListOf()
        ).apply {

            uuid = Clock.System.now().toEpochMilliseconds().toString()
        }

        onCurrentImageEdit(items = items)
    }

    fun onImageItem() {

        val sizeOfItem = canvasSize.fittedImageSize(imageSize = selectedBitmap.toSize())

        val positionOfItem = Offset(
            x = (canvasSize.width - sizeOfItem.width) / 2.0F,
            y = (canvasSize.height - sizeOfItem.height) / 2.0F
        )

        val items = ImageEditItems.ImageItem(
            bitmap = selectedBitmap ?: return,
            position = positionOfItem,
            size = sizeOfItem
        ).apply {

            uuid = Clock.System.now().toEpochMilliseconds().toString()
        }

        onCurrentImageEdit(items = items)
    }

    fun onPathItem() {

        val items = ImageEditItems.PathItem(
            color = selectedPenColor,
            style = Stroke(
                width = penThickness.value,
                cap = selectedStrokeCap,
                join = selectedStrokeJoin
            ),
            path = persistentListOf()
        ).apply {

            uuid = Clock.System.now().toEpochMilliseconds().toString()
        }

        onCurrentImageEdit(items = items)
    }

    fun onShapeItem() {

        val sizeOfItem = canvasSize / 2.5F

        val positionOfItem = Offset(
            x = (canvasSize.width - sizeOfItem.width) / 2.0F,
            y = (canvasSize.height - sizeOfItem.height) / 2.0F
        )

        val items = ImageEditItems.ShapeItem(
            shape = selectedShape,
            color = selectedPenColor,
            style = Fill,
            position = positionOfItem,
            size = sizeOfItem
        ).apply {

            uuid = Clock.System.now().toEpochMilliseconds().toString()
        }

        onCurrentImageEdit(items = items)
    }

    fun onTextItem() {

        val sizeOfItem = textMeasurer.measure(
            text = enteredText,
            style = textStyle,
            overflow = TextOverflow.Clip,
            constraints = Constraints(
                maxWidth = canvasSize.width.toInt(),
                maxHeight = canvasSize.height.toInt()
            )
        ).size.toSize()

        val positionOfItem = Offset(
            x = (canvasSize.width - sizeOfItem.width) / 2.0F,
            y = (canvasSize.height - sizeOfItem.height) / 2.0F
        )

        val items = ImageEditItems.TextItem(
            content = enteredText,
            style = textStyle,
            position = positionOfItem,
            size = sizeOfItem
        ).apply {

            uuid = Clock.System.now().toEpochMilliseconds().toString()
        }

        onCurrentImageEdit(items = items)
    }

    fun onResetEditItem() {

        onCurrentImageEdit(items = null)
        updateBitmap(bitmap = null)
    }

    suspend fun getEditedImageBitmap(
        density: Density
    ): ImageBitmap? = withContext(Dispatchers.Default) {

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
                    srcSize = IntSize(bitmap.width, bitmap.height),
                    dstOffset = IntOffset.Zero,
                    dstSize = IntSize(size.width.toInt(), size.height.toInt())
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

    internal fun onEditItemEnd() {

        currentImageEditItem?.let { items ->

            addImageEditItem(items = items)
            onResetEditItem()

            when (items) {

                is ImageEditItems.EraseItem -> onEraseItem()
                is ImageEditItems.ImageItem -> {}
                is ImageEditItems.PathItem -> onPathItem()
                is ImageEditItems.ShapeItem -> {}
                is ImageEditItems.TextItem -> {}
            }
        }

        currentCorner = null
    }

    internal fun onEditItemChanges(position: Offset, amount: Offset) {

        currentImageEditItem?.let { items ->

            when (items) {

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

                is ImageEditItems.PathItem -> {

                    val newItems = items.copy(path = items.path.add(position)).apply {

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
                        text = enteredText,
                        style = textStyle,
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

    internal fun onEditItemsClick(position: Offset): Boolean? {

        return imageEditItemList.find { items ->

            items.hasEditItemClicked(clickPosition = position)
        }?.let { items ->

            onCurrentImageEdit(items = items)
            imageEditItemList = imageEditItemList.remove(element = items)
            true
        }
    }

    internal fun ImageEditState.getEditItemPositionSize(
        position: Offset,
        size: Size,
        amount: Offset,
    ): Pair<Offset, Size> {

        val sizeLimit = config.minItemSize.toPixel(density = density)
        val minX = 0f
        val minY = 0f
        val maxX = canvasSize.width
        val maxY = canvasSize.height

        var finalTopLeft = position
        var finalSize = size

        val topLeft = position
        val topRight = position.toTopRight(size = size)
        val bottomLeft = position.toBottomLeft(size = size)
        val bottomRight = position.toBottomRight(size = size)

        when (currentCorner) {

            EditItemCorner.TOP_LEFT -> {

                ((bottomRight.x - (topLeft.x + amount.x)) >= sizeLimit &&
                        (bottomRight.y - (topLeft.y + amount.y)) >= sizeLimit)
                    .takeIf { isAdjustable -> isAdjustable }?.run {

                        val newX = (topLeft.x + amount.x).coerceIn(
                            minX..bottomRight.x - sizeLimit
                        )

                        val newY = (topLeft.y + amount.y).coerceIn(
                            minY..bottomRight.y - sizeLimit
                        )

                        finalTopLeft = Offset(newX, newY)

                        finalSize = Size(
                            width = (bottomRight.x - newX).coerceAtLeast(sizeLimit),
                            height = (bottomRight.y - newY).coerceAtLeast(sizeLimit)
                        )
                    }
            }

            EditItemCorner.TOP_RIGHT -> {

                ((topRight.x + amount.x - bottomLeft.x) >= sizeLimit &&
                        (bottomRight.y - (topRight.y + amount.y)) >= sizeLimit)
                    .takeIf { isAdjustable -> isAdjustable }?.run {

                        val newX = (topRight.x + amount.x).coerceIn(
                            bottomLeft.x + sizeLimit..maxX
                        )

                        val newY = (topRight.y + amount.y).coerceIn(
                            minY..bottomLeft.y - sizeLimit
                        )

                        finalTopLeft = Offset(bottomLeft.x, newY)

                        finalSize = Size(
                            width = (newX - bottomLeft.x).coerceAtLeast(sizeLimit),
                            height = (bottomLeft.y - newY).coerceAtLeast(sizeLimit)
                        )
                    }
            }

            EditItemCorner.BOTTOM_LEFT -> {

                ((topRight.x - (bottomLeft.x + amount.x)) >= sizeLimit &&
                        ((bottomLeft.y + amount.y) - topRight.y) >= sizeLimit)
                    .takeIf { isAdjustable -> isAdjustable }?.run {

                        val newX = (bottomLeft.x + amount.x).coerceIn(
                            minX..topRight.x - sizeLimit
                        )

                        val newY = (bottomLeft.y + amount.y).coerceIn(
                            topRight.y + sizeLimit..maxY
                        )

                        finalTopLeft = Offset(newX, topRight.y)

                        finalSize = Size(
                            width = (topRight.x - newX).coerceAtLeast(sizeLimit),
                            height = (newY - topRight.y).coerceAtLeast(sizeLimit)
                        )
                    }
            }

            EditItemCorner.BOTTOM_RIGHT -> {

                (((bottomRight.x + amount.x) - topLeft.x) >= sizeLimit &&
                        ((bottomRight.y + amount.y) - topLeft.y) >= sizeLimit)
                    .takeIf { isAdjustable -> isAdjustable }?.run {

                        val newX = (bottomRight.x + amount.x).coerceIn(
                            topLeft.x + sizeLimit..maxX
                        )

                        val newY = (bottomRight.y + amount.y).coerceIn(
                            topLeft.y + sizeLimit..maxY
                        )

                        finalTopLeft = topLeft

                        finalSize = Size(
                            width = (newX - topLeft.x).coerceAtLeast(sizeLimit),
                            height = (newY - topLeft.y).coerceAtLeast(sizeLimit)
                        )
                    }
            }

            EditItemCorner.LEFT_CENTRE -> {

                ((topRight.x - (topLeft.x + amount.x)) >= sizeLimit).takeIf { isAdjustable ->

                    isAdjustable
                }?.run {

                    val newX = (topLeft.x + amount.x).coerceIn(
                        minX..topRight.x - sizeLimit
                    )

                    finalTopLeft = Offset(newX, topLeft.y)

                    finalSize = Size(
                        width = (topRight.x - newX).coerceAtLeast(sizeLimit),
                        height = size.height
                    )
                }
            }

            EditItemCorner.TOP_CENTRE -> {

                ((bottomLeft.y - (topLeft.y + amount.y)) >= sizeLimit).takeIf { isAdjustable ->

                    isAdjustable
                }?.run {

                    val newY = (topLeft.y + amount.y).coerceIn(
                        minY..bottomLeft.y - sizeLimit
                    )

                    finalTopLeft = Offset(topLeft.x, newY)

                    finalSize = Size(
                        width = size.width,
                        height = (bottomLeft.y - newY).coerceAtLeast(sizeLimit)
                    )
                }
            }

            EditItemCorner.RIGHT_CENTRE -> {

                (((topRight.x + amount.x) - topLeft.x) >= sizeLimit).takeIf { isAdjustable ->

                    isAdjustable
                }?.run {

                    val newX = (topRight.x + amount.x).coerceIn(
                        topLeft.x + sizeLimit..maxX
                    )

                    finalTopLeft = topLeft

                    finalSize = Size(
                        width = (newX - topLeft.x).coerceAtLeast(sizeLimit),
                        height = size.height
                    )
                }
            }

            EditItemCorner.BOTTOM_CENTRE -> {

                (((bottomLeft.y + amount.y) - topLeft.y) >= sizeLimit).takeIf { isAdjustable ->

                    isAdjustable
                }?.run {

                    val newY = (bottomLeft.y + amount.y).coerceIn(
                        topLeft.y + sizeLimit..maxY
                    )

                    finalTopLeft = topLeft

                    finalSize = Size(
                        width = size.width,
                        height = (newY - topLeft.y).coerceAtLeast(sizeLimit)
                    )
                }
            }

            null -> {

                finalTopLeft = Offset(
                    x = (position.x + amount.x).coerceIn(minX..maxX - size.width),
                    y = (position.y + amount.y).coerceIn(minY..maxY - size.height)
                )

                finalSize = size
            }
        }

        var currentWidth = finalSize.width.coerceAtLeast(sizeLimit)
        var currentHeight = finalSize.height.coerceAtLeast(sizeLimit)

        var currentTopLeftX = finalTopLeft.x.coerceIn(
            minX..(maxX - currentWidth).coerceAtLeast(minX)
        )
        var currentTopLeftY = finalTopLeft.y.coerceIn(
            minY..(maxY - currentHeight).coerceAtLeast(minY)
        )

        currentWidth = currentWidth.coerceAtMost((maxX - currentTopLeftX).coerceAtLeast(0f))
            .coerceAtLeast(sizeLimit)
        currentHeight = currentHeight.coerceAtMost((maxY - currentTopLeftY).coerceAtLeast(0f))
            .coerceAtLeast(sizeLimit)

        currentTopLeftX = currentTopLeftX.coerceIn(
            minX..(maxX - currentWidth).coerceAtLeast(minX)
        )
        currentTopLeftY = currentTopLeftY.coerceIn(
            minY..(maxY - currentHeight).coerceAtLeast(minY)
        )

        return Pair(Offset(currentTopLeftX, currentTopLeftY), Size(currentWidth, currentHeight))
    }
}