package io.bashpsk.emptylibs.canvasslate.slate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.canvasslate.extension.hasNeared
import io.bashpsk.emptylibs.composeutils.offset.toOffsetData
import io.bashpsk.emptylibs.composeutils.size.SizeData
import io.bashpsk.emptylibs.composeutils.size.toSizeData
import io.bashpsk.emptylibs.composeutils.stroke.toStrokeCap
import io.bashpsk.emptylibs.composeutils.stroke.toStrokeJoin
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
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
        saver = CanvasSlateState.StateSaver(
            background = background,
            initial = initial,
            density = density
        )
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

    var brushThickness by mutableStateOf(4.0F)
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

    fun updateBrushThickness(thickness: Float) {

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
                color = selectedBrushColor.toArgb(),
                thickness = brushThickness,
                strokeCap = selectedStrokeCap.toString(),
                strokeJoin = selectedStrokeJoin.toString(),
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

                val path = pathData.copy(path = pathData.path.add(position.toOffsetData()))

                onCurrentPath(path = path)
            } ?: return
        }
    }

    internal fun onEditPathData(position: Offset): Boolean? {

        return isDrawingMode.takeIf { canDraw -> canDraw.not() }?.run {

            allPathList.find { pathData ->

                val pointThreshold = threshold + pathData.thickness

                pathData.path.find { point ->

                    point.toOffset().hasNeared(point = position, threshold = pointThreshold)
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

        private const val KEY_CANVAS_SIZE = "CANVAS-SLATE-CANVAS-SIZE"
        private const val KEY_BACKGROUND_COLOR = "CANVAS-SLATE-BACKGROUND-COLOR"
        private const val KEY_BRUSH_COLOR = "CANVAS-SLATE-BRUSH-COLOR"
        private const val KEY_STROKE_CAP = "CANVAS-SLATE-STROKE-CAP"
        private const val KEY_STROKE_JOIN = "CANVAS-SLATE-STROKE-JOIN"
        private const val KEY_BRUSH_THICKNESS = "CANVAS-SLATE-BRUSH-THICKNESS"
        private const val KEY_CURRENT_PATH = "CANVAS-SLATE-CURRENT-PATH"
        private const val KEY_DRAWING_MODE = "CANVAS-SLATE-DRAWING-MODE"
        private const val KEY_ALL_PATH_LIST = "CANVAS-SLATE-ALL-PATH-LIST"

        fun StateSaver(
            background: Color,
            initial: Color,
            density: Density
        ): Saver<CanvasSlateState, Any> = mapSaver(
            save = { state ->

                mapOf(
                    KEY_CANVAS_SIZE to state.canvasSize.toSizeData(),
                    KEY_BACKGROUND_COLOR to state.selectedBackgroundColor.toArgb(),
                    KEY_BRUSH_COLOR to state.selectedBrushColor.toArgb(),
                    KEY_STROKE_CAP to state.selectedStrokeCap.toString(),
                    KEY_STROKE_JOIN to state.selectedStrokeJoin.toString(),
                    KEY_BRUSH_THICKNESS to state.brushThickness,
                    KEY_CURRENT_PATH to state.currentPath,
                    KEY_DRAWING_MODE to state.isDrawingMode,
                    KEY_ALL_PATH_LIST to state.allPathList.toTypedArray()
                )
            },
            restore = { elements ->

                CanvasSlateState(
                    background = background,
                    initial = initial,
                    density = density
                ).apply {

                    canvasSize = (elements.getOrElse(
                        KEY_CANVAS_SIZE
                    ) { Size.Zero.toSizeData() } as SizeData).toSize()

                    selectedBackgroundColor = Color(elements.getOrElse(
                        KEY_BACKGROUND_COLOR
                    ) { background.toArgb() } as Int)

                    selectedBrushColor = Color(elements.getOrElse(
                        KEY_BRUSH_COLOR
                    ) { initial.toArgb() } as Int)

                    selectedStrokeCap = (elements.getOrElse(
                        KEY_STROKE_CAP
                    ) { StrokeCap.Round.toString() } as String).toStrokeCap()

                    selectedStrokeJoin = (elements.getOrElse(
                        KEY_STROKE_JOIN
                    ) { StrokeJoin.Round.toString() } as String).toStrokeJoin()

                    brushThickness = elements.getOrElse(KEY_BRUSH_THICKNESS) { 4.0F } as Float
                    currentPath = elements.getOrElse(KEY_CURRENT_PATH) { null } as CanvasSlatePath?
                    isDrawingMode = elements.getOrElse(KEY_DRAWING_MODE) { true } as Boolean

                    @Suppress("UNCHECKED_CAST")
                    allPathList = (elements.getOrElse(KEY_ALL_PATH_LIST) {

                        arrayOf<CanvasSlatePath>()
                    } as Array<CanvasSlatePath>).toPersistentList()
                }
            }
        )
    }
}