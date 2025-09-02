package io.bashpsk.emptylibs.canvasslate.slate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.canvasslate.extension.hasNeared
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun rememberCanvasSlateState(
    background: Color = Color.Black,
    initial: Color = Color.Green
): CanvasSlateState {

    val density = LocalDensity.current

    return rememberSaveable(
        background,
        initial,
        density,
        saver = CanvasSlateState.StateSaver(density = density)
    ) {
        CanvasSlateState(background = background, initial = initial, density = density)
    }
}

@OptIn(ExperimentalTime::class)
class CanvasSlateState(
    private val background: Color,
    private val initial: Color,
    private val density: Density
) {

    private val threshold = 12.dp.toPixel(density = density)

    internal var canvasSize by mutableStateOf(Size.Zero)

    var selectedBackgroundColor by mutableStateOf(background)
        private set

    var selectedBrushColor by mutableStateOf(initial)
        private set

    var selectedStrokeCap by mutableStateOf(StrokeCap.Round)
        private set

    var selectedStrokeJoin by mutableStateOf(StrokeJoin.Round)
        private set

    var brushThickness by mutableStateOf(4.dp)
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

    fun updateBrushColor(color: Color) {

        selectedBrushColor = color
    }

    fun updateStrokeCap(type: StrokeCap) {

        selectedStrokeCap = type
    }

    fun updateStrokeJoin(type: StrokeJoin) {

        selectedStrokeJoin = type
    }

    fun updateBrushThickness(thickness: Dp) {

        brushThickness = thickness
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

    internal fun onPathStart() {

        isDrawingMode.takeIf { canDraw -> canDraw }?.run {

            val path = CanvasSlatePath(
                id = Clock.System.now().toEpochMilliseconds().toString(),
                color = selectedBrushColor,
                thickness = brushThickness,
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

            CanvasDrawScope().draw(
                density = density,
                layoutDirection = LayoutDirection.Ltr,
                canvas = Canvas(image = imageBitmap),
                size = size
            ) {

                drawRect(color = selectedBackgroundColor)
                allPathList.forEach { pathData -> drawSlatePath(slatePath = pathData) }
                currentPath?.let { pathData -> drawSlatePath(slatePath = pathData) }
            }

            imageBitmap
        }
    }

    companion object {

        private object StateKeys {

            const val BACKGROUND = "CanvasSlateBackground"
            const val INITIAL = "CanvasSlateInitial"
            const val CANVAS_SIZE = "CanvasSlateCanvasSize"
            const val SELECTED_BACKGROUND_COLOR = "CanvasSlateSelectedBackgroundColor"
            const val SELECTED_BRUSH_COLOR = "CanvasSlateSelectedBrushColor"
            const val SELECTED_STROKE_CAP = "CanvasSlateSelectedStrokeCap"
            const val SELECTED_STROKE_JOIN = "CanvasSlateSelectedStrokeJoin"
            const val BRUSH_THICKNESS = "CanvasSlateBrushThickness"
            const val CURRENT_PATH = "CanvasSlateCurrentPath"
            const val IS_DRAWING_MODE = "CanvasSlateIsDrawingMode"
            const val ALL_PATH_LIST = "CanvasSlateAllPathList"
        }

        fun StateSaver(density: Density): Saver<CanvasSlateState, Map<String, Any?>> = Saver(
            save = { state ->

                mapOf(
                    StateKeys.BACKGROUND to state.background,
                    StateKeys.INITIAL to state.initial,
                    StateKeys.CANVAS_SIZE to state.canvasSize,
                    StateKeys.SELECTED_BACKGROUND_COLOR to state.selectedBackgroundColor,
                    StateKeys.SELECTED_BRUSH_COLOR to state.selectedBrushColor,
                    StateKeys.SELECTED_STROKE_CAP to state.selectedStrokeCap,
                    StateKeys.SELECTED_STROKE_JOIN to state.selectedStrokeJoin,
                    StateKeys.BRUSH_THICKNESS to state.brushThickness,
                    StateKeys.CURRENT_PATH to state.currentPath,
                    StateKeys.IS_DRAWING_MODE to state.isDrawingMode,
                    StateKeys.ALL_PATH_LIST to state.allPathList
                )
            },
            restore = { elements ->

                CanvasSlateState(
                    background = elements.getOrElse(StateKeys.BACKGROUND) { Color.Black } as Color,
                    initial = elements.getOrElse(StateKeys.INITIAL) { Color.Green } as Color,
                    density = density
                ).apply {

                    canvasSize = elements.getOrElse(StateKeys.CANVAS_SIZE) { Size.Zero } as Size

                    selectedBackgroundColor = elements.getOrElse(
                        StateKeys.SELECTED_BACKGROUND_COLOR
                    ) { background } as Color

                    selectedBrushColor = elements.getOrElse(
                        StateKeys.SELECTED_BRUSH_COLOR
                    ) { initial } as Color

                    selectedStrokeCap = elements.getOrElse(
                        StateKeys.SELECTED_STROKE_CAP
                    ) { StrokeCap.Round } as StrokeCap

                    selectedStrokeJoin = elements.getOrElse(
                        StateKeys.SELECTED_STROKE_JOIN
                    ) { StrokeJoin.Round } as StrokeJoin

                    brushThickness = elements.getOrElse(
                        StateKeys.BRUSH_THICKNESS
                    ) { 4.dp } as Dp

                    currentPath = elements.getOrElse(
                        StateKeys.CURRENT_PATH
                    ) { null } as CanvasSlatePath?

                    isDrawingMode = elements.getOrElse(
                        StateKeys.IS_DRAWING_MODE
                    ) { true } as Boolean

                    @Suppress("UNCHECKED_CAST")
                    allPathList = elements.getOrElse(
                        StateKeys.ALL_PATH_LIST
                    ) { persistentListOf<CanvasSlatePath>() } as PersistentList<CanvasSlatePath>
                }
            }
        )
    }
}