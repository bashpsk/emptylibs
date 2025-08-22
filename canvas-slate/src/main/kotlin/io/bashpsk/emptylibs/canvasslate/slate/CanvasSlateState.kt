package io.bashpsk.emptylibs.canvasslate.slate

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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.canvasslate.extension.hasNeared
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun rememberCanvasSlateState(
    background: Color = Color.Black,
    initial: Color = Color.Green,
    threshold: Dp = 12.dp
): CanvasSlateState {

    return remember(background, initial, threshold) {
        CanvasSlateState(background = background, initial = initial, threshold = threshold.value)
    }
}

@OptIn(ExperimentalTime::class)
class CanvasSlateState(
    private val background: Color,
    private val initial: Color,
    private val threshold: Float
) {

    internal var canvasSize by mutableStateOf(Size.Zero)

    var selectedBackgroundColor by mutableStateOf(background)
        private set

    var selectedPenColor by mutableStateOf(initial)
        private set

    var selectedStrokeCap by mutableStateOf(StrokeCap.Round)
        private set

    var selectedStrokeJoin by mutableStateOf(StrokeJoin.Round)
        private set

    var penThickness by mutableStateOf(4.dp)
        private set

    var currentPath by mutableStateOf<CanvasSlatePath?>(null)
        private set

    var isDrawingMode by mutableStateOf(true)
        private set

    var allPathList by mutableStateOf(persistentListOf<CanvasSlatePath>())

    internal var isToolBarMenuExpanded by mutableStateOf(false)

    internal var editCanvasSlatePath by mutableStateOf<CanvasSlatePath?>(null)

    internal var previewPathList by mutableStateOf(persistentListOf<CanvasSlatePath>())

    fun updateBackgroundColor(color: Color) {

        selectedBackgroundColor = color
    }

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

    fun onClearCanvas() {

        currentPath = null
        allPathList = persistentListOf()
    }

    fun onUndoCanvas() {

        allPathList.lastOrNull()?.let { pathData ->

            allPathList = allPathList.remove(element = pathData)
        }
    }

    fun onCurrentPath(path: CanvasSlatePath?) {

        currentPath = path
    }

    internal fun onNewPathStart() {

        isDrawingMode.takeIf { canDraw -> canDraw }?.run {

            val path = CanvasSlatePath(
                id = Clock.System.now().toEpochMilliseconds().toString(),
                color = selectedPenColor,
                thickness = penThickness,
                strokeCap = selectedStrokeCap,
                strokeJoin = selectedStrokeJoin,
                path = persistentListOf()
            )

            onCurrentPath(path = path)
        }
    }

    internal fun onPathEnd() {

        isDrawingMode.takeIf { canDraw -> canDraw }?.run {

            currentPath?.let { pathData ->

                allPathList = allPathList.add(element = pathData)
                onCurrentPath(path = null)
            } ?: return
        }
    }

    internal fun onPathDraw(position: Offset) {

        isDrawingMode.takeIf { canDraw -> canDraw }?.run {

            currentPath?.let { pathData ->

                val path = pathData.copy(path = pathData.path.add(element = position))

                onCurrentPath(path = path)
            } ?: return
        }
    }

    internal fun onEditPathData(position: Offset): Boolean? {

        return isDrawingMode.takeIf { canDraw -> canDraw.not() }?.run {

            allPathList.find { pathData ->

                val pointThreshold = threshold + pathData.thickness.value

                pathData.path.find { point ->

                    point.hasNeared(point = position, threshold = pointThreshold)
                } != null
            }?.let { pathData ->

                onUpdateEditPath(path = pathData)
                previewPathList = allPathList
                true
            }
        }
    }

    internal fun onUpdateEditPath(path: CanvasSlatePath?) {

        editCanvasSlatePath = path
    }

    internal fun addPathInPreview(path: CanvasSlatePath) {

        previewPathList.find { pathData -> pathData.id == path.id }?.let { pathData ->

            previewPathList = previewPathList.remove(element = pathData).add(element = path)
        }
    }

    internal fun onUndoPreview() {

        previewPathList.lastOrNull()?.let { pathData ->

            allPathList.find { path -> pathData.id == path.id }?.let { path ->

                onUpdateEditPath(path = path)
                previewPathList = previewPathList.remove(element = pathData).add(element = path)
            }
        }
    }

    internal fun onApplyPreview() {

        allPathList = previewPathList
    }

    internal fun onDeleteEditPath() {

        editCanvasSlatePath?.let { pathData ->

            allPathList = previewPathList.removeAll { path -> pathData.id == path.id }
            onUpdateEditPath(path = null)
        }
    }

    suspend fun getImageBitmap(density: Density): ImageBitmap? = withContext(Dispatchers.Default) {

        return@withContext canvasSize.takeIf { size -> size != Size.Zero }?.let { size ->

            val imageBitmap = ImageBitmap(size.width.toInt(), size.height.toInt())
            val canvas = Canvas(image = imageBitmap)
            val drawScope = CanvasDrawScope()

            drawScope.draw(
                density = density,
                layoutDirection = LayoutDirection.Ltr,
                canvas = canvas,
                size = size
            ) {

                drawRect(color = selectedBackgroundColor)
                allPathList.forEach { pathData -> drawSlatePath(slatePath = pathData) }
                currentPath?.let { pathData -> drawSlatePath(slatePath = pathData) }
            }

            imageBitmap
        }
    }
}