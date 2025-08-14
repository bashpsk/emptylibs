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
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun rememberCanvasSlateState(
    background: Color = Color.Black,
    initial: Color = Color.Green,
    colorList: ImmutableList<Color> = ColorList
): CanvasSlateState {

    return remember(background, initial, colorList) {
        CanvasSlateState(background = background, initial = initial, colorList = colorList)
    }
}

@OptIn(ExperimentalTime::class)
class CanvasSlateState(
    val background: Color,
    val initial: Color,
    val colorList: ImmutableList<Color>,
) {

    internal var canvasSize by mutableStateOf(Size.Zero)

    var selectedBackgroundColor by mutableStateOf(background)
        private set

    var selectedPenColor by mutableStateOf(initial)
        private set

    var penThickness by mutableStateOf(4.dp)
        private set

    var currentPath by mutableStateOf<PathData?>(null)
        private set

    var allPathList by mutableStateOf(persistentListOf<PathData>())

    fun updateBackgroundColor(color: Color) {

        selectedBackgroundColor = color
    }

    fun updatePenColor(color: Color) {

        selectedPenColor = color
    }

    fun updatePenThickness(thickness: Dp) {

        penThickness = thickness
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

    fun onCurrentPath(path: PathData?) {

        currentPath = path
    }

    internal fun onNewPathStart() {

        val path = PathData(
            id = Clock.System.now().toEpochMilliseconds().toString(),
            color = selectedPenColor,
            thickness = penThickness,
            path = persistentListOf()
        )

        onCurrentPath(path = path)
    }

    internal fun onPathEnd() {

        currentPath?.let { pathData ->

            allPathList = allPathList.add(element = pathData)
            onCurrentPath(path = null)
        } ?: return
    }

    internal fun onPathDraw(position: Offset) {

        currentPath?.let { pathData ->

            val path = pathData.copy(path = pathData.path.add(element = position))

            onCurrentPath(path = path)
        } ?: return
    }

    suspend fun getImageBitmap(density: Density): ImageBitmap? = withContext(Dispatchers.Default) {

        val imageBitmap = ImageBitmap(canvasSize.width.toInt(), canvasSize.height.toInt())
        val canvas = Canvas(image = imageBitmap)
        val drawScope = CanvasDrawScope()

        drawScope.draw(
            density = density,
            layoutDirection = LayoutDirection.Ltr,
            canvas = canvas,
            size = canvasSize
        ) {

            drawRect(color = selectedBackgroundColor)
            allPathList.forEach { pathData -> drawPathData(pathData = pathData) }
            currentPath?.let { pathData -> drawPathData(pathData = pathData) }
        }

        imageBitmap
    }
}