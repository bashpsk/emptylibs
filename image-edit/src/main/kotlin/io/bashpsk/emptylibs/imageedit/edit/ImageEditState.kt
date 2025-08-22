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

    fun onImageItem() {

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
    }

    internal fun onPathStart() {

        currentImageEditItem?.takeIf { items -> items is ImageEditItems.PathItem }?.run {

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
    }

    internal fun onPathEnd() {

        currentImageEditItem?.takeIf { items -> items is ImageEditItems.PathItem }?.run {

            currentImageEditItem?.let { editItems ->

                addImageEditItem(items = editItems)
            } ?: return
        }
    }

    internal fun onPathDraw(position: Offset) {

        currentImageEditItem?.takeIf { items -> items is ImageEditItems.PathItem }?.run {

            when (val editItems = currentImageEditItem) {

                is ImageEditItems.PathItem -> {

                    val items = editItems.copy(path = editItems.path.add(position)).apply {

                        uuid = editItems.uuid
                    }

                    onCurrentImageEdit(items = items)
                }

                else -> return
            }
        }
    }
}