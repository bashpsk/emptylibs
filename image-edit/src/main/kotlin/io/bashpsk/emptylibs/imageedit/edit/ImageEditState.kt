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
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.imageutils.extension.fittedImageSize
import io.bashpsk.emptylibs.imageutils.extension.toSize
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun rememberImageEditState(imageBitmap: ImageBitmap?): ImageEditState {

    return remember(imageBitmap) {
        ImageEditState(imageBitmap = imageBitmap)
    }
}

@OptIn(ExperimentalTime::class)
class ImageEditState(val imageBitmap: ImageBitmap?) {

    internal var imageEditItemList by mutableStateOf(persistentListOf<ImageEditItems>())
        private set

    var selectedPenColor by mutableStateOf(Color.White)
        private set

    var selectedStrokeCap by mutableStateOf(StrokeCap.Round)
        private set

    var selectedStrokeJoin by mutableStateOf(StrokeJoin.Round)
        private set

    var penThickness by mutableStateOf(24.dp)
        private set

    var selectedBitmap by mutableStateOf<ImageBitmap?>(null)
        private set

    var currentImageEditItem by mutableStateOf<ImageEditItems?>(null)
        private set

    internal var isToolBarMenuExpanded by mutableStateOf(false)

    internal var canvasSize by mutableStateOf(Size.Zero)

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
            id = Clock.System.now().toEpochMilliseconds().toString(),
            thickness = penThickness,
            strokeCap = selectedStrokeCap,
            strokeJoin = selectedStrokeJoin,
            path = persistentListOf()
        ).apply {

            uuid = Clock.System.now().toEpochMilliseconds().toString()
        }

        onCurrentImageEdit(items = items)
    }

    fun onImageItem() {

        val sizeOfItem = canvasSize / 2.5F

        val positionOfItem = Offset(
            x = (canvasSize.width - sizeOfItem.width) / 2.0F,
            y = (canvasSize.height - sizeOfItem.height) / 2.0F
        )

        val items = ImageEditItems.ImageItem(
            bitmap = selectedBitmap ?: return,
            position = positionOfItem,
            size = canvasSize.fittedImageSize(imageSize = selectedBitmap.toSize())
        ).apply {

            uuid = Clock.System.now().toEpochMilliseconds().toString()
        }

        onCurrentImageEdit(items = items)
    }

    fun onPathItem() {

        val items = ImageEditItems.PathItem(
            id = Clock.System.now().toEpochMilliseconds().toString(),
            color = selectedPenColor,
            thickness = penThickness,
            strokeCap = selectedStrokeCap,
            strokeJoin = selectedStrokeJoin,
            path = persistentListOf()
        ).apply {

            uuid = Clock.System.now().toEpochMilliseconds().toString()
        }

        onCurrentImageEdit(items = items)
    }

    fun onShapeItem() {

    }

    fun onTextItem() {

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

                        drawImageEditItem(items = items)
                    }

                    currentImageEditItem?.let { items ->

                        drawImageEditItem(items = items)
                    }

                    nativeCanvas.restore()
                }
            }

            editedImageBitmap
        }
    }

    internal fun onEditItemStart() {

        currentImageEditItem?.let { items ->

            when (items) {

                is ImageEditItems.EraseItem -> {

                    val items = ImageEditItems.EraseItem(
                        id = Clock.System.now().toEpochMilliseconds().toString(),
                        thickness = penThickness,
                        strokeCap = selectedStrokeCap,
                        strokeJoin = selectedStrokeJoin,
                        path = persistentListOf()
                    ).apply {

                        uuid = Clock.System.now().toEpochMilliseconds().toString()
                    }

                    onCurrentImageEdit(items = items)
                }

                is ImageEditItems.ImageItem -> {

                    val newItems = ImageEditItems.ImageItem(
                        bitmap = items.bitmap,
                        position = items.position,
                        size = items.size
                    ).apply {

                        uuid = Clock.System.now().toEpochMilliseconds().toString()
                    }

                    onCurrentImageEdit(items = newItems)
                }

                is ImageEditItems.PathItem -> {

                    val items = ImageEditItems.PathItem(
                        id = Clock.System.now().toEpochMilliseconds().toString(),
                        color = selectedPenColor,
                        thickness = penThickness,
                        strokeCap = selectedStrokeCap,
                        strokeJoin = selectedStrokeJoin,
                        path = persistentListOf()
                    ).apply {

                        uuid = Clock.System.now().toEpochMilliseconds().toString()
                    }

                    onCurrentImageEdit(items = items)
                }

                is ImageEditItems.ShapeItem -> {

                }

                is ImageEditItems.TextItem -> {

                }
            }
        }
    }

    internal fun onEditItemEnd() {

        currentImageEditItem?.let { items ->

            when (items) {

                is ImageEditItems.EraseItem -> {

                    addImageEditItem(items = items)
                    onResetEditItem()
                    onEraseItem()
                }

                is ImageEditItems.ImageItem -> {

                    addImageEditItem(items = items)
                    onResetEditItem()
                }

                is ImageEditItems.PathItem -> {

                    addImageEditItem(items = items)
                    onResetEditItem()
                    onPathItem()
                }

                is ImageEditItems.ShapeItem -> {

                }

                is ImageEditItems.TextItem -> {

                }
            }
        }
    }

    internal fun onEditItemChanges(position: Offset, size: Size?) {

        currentImageEditItem?.let { items ->

            when (items) {

                is ImageEditItems.EraseItem -> {

                    val newItems = items.copy(path = items.path.add(position)).apply {

                        uuid = items.uuid
                    }

                    onCurrentImageEdit(items = newItems)
                }

                is ImageEditItems.ImageItem -> {

                    val imageSize = size ?: items.size

                    val newItems = items.copy(position = position, size = imageSize).apply {

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

                }

                is ImageEditItems.TextItem -> {

                }
            }
        }
    }
}