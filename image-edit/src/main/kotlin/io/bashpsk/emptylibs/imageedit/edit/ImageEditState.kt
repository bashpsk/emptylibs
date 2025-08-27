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
import io.bashpsk.emptylibs.imageedit.edit.EditItemCorner.Companion.hasCornerEdge
import io.bashpsk.emptylibs.imageedit.extension.getEditItemCorner
import io.bashpsk.emptylibs.imageedit.extension.hasEditItemClicked
import io.bashpsk.emptylibs.imageedit.extension.toBottomRight
import io.bashpsk.emptylibs.imageedit.extension.toPixel
import io.bashpsk.emptylibs.imageedit.extension.toRect
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

            (tempWidth / aspectRatio > tempHeight + 0.001F).takeIf { it }?.run {

                tempWidth = tempHeight * aspectRatio
            } ?: (tempHeight > tempWidth / aspectRatio + 0.001F).takeIf { it }?.run {

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

        (currentWidth / aspectRatio > currentHeight + 0.001F).takeIf { it }?.run {

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
        } ?: (currentHeight > currentWidth / aspectRatio + 0.001F).takeIf { it }?.run {

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
}