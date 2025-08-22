package io.bashpsk.emptylibs.imageedit.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentListOf
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

    var penThickness by mutableStateOf(4.dp)
        private set

    var currentImageEditItem by mutableStateOf<ImageEditItems?>(null)
        private set

    var isDrawingMode by mutableStateOf(true)
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

    fun onDrawingMode(mode: Boolean) {

        isDrawingMode = mode
    }

    fun addImageEditItem(item: ImageEditItems) {

        imageEditItemList.find { editItem -> editItem.id == item.id }?.let { editItem ->

            imageEditItemList = imageEditItemList.remove(element = editItem).add(element = item)
        } ?: run {

            imageEditItemList = imageEditItemList.add(element = item)
        }
    }

    fun removeImageEditItem(item: ImageEditItems) {

        imageEditItemList = imageEditItemList.remove(element = item)
    }

    fun onUndoCanvas() {

        imageEditItemList.lastOrNull()?.let { item ->

            imageEditItemList = imageEditItemList.remove(element = item)
        }
    }

    fun onClearCanvas() {

        imageEditItemList = persistentListOf()
    }

    fun onCurrentImageEdit(items: ImageEditItems?) {

        currentImageEditItem = items
    }

    internal fun onNewPathStart() {

        isDrawingMode.takeIf { canDraw -> canDraw }?.run {

            val path = ImageEditPath(
                id = Clock.System.now().toEpochMilliseconds().toString(),
                color = selectedPenColor,
                thickness = penThickness,
                strokeCap = selectedStrokeCap,
                strokeJoin = selectedStrokeJoin,
                path = persistentListOf()
            )

            val item = ImageEditItems.Path(path = path).apply {

                id = Clock.System.now().toEpochMilliseconds()
            }

            onCurrentImageEdit(items = item)
        }
    }

    internal fun onPathEnd() {

        isDrawingMode.takeIf { canDraw -> canDraw }?.run {

            currentImageEditItem?.let { editItems ->

                addImageEditItem(item = editItems)
                onCurrentImageEdit(items = null)
            } ?: return
        }
    }

    internal fun onPathDraw(position: Offset) {

        isDrawingMode.takeIf { canDraw -> canDraw }?.run {

            when (val item = currentImageEditItem) {

                is ImageEditItems.Path -> {

                    val path = item.path.copy(path = item.path.path.add(element = position))

                    val items = ImageEditItems.Path(path = path).apply {

                        id = Clock.System.now().toEpochMilliseconds()
                    }

                    onCurrentImageEdit(items = items)
                }

                else -> return
            }
        }
    }
}