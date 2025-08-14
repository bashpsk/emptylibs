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

    return remember(background, initial) {
        CanvasSlateState(background = background, initial = initial)
    }
}

@OptIn(ExperimentalTime::class)
class CanvasSlateState(
    val background: Color,
    val initial: Color
) {

    internal var canvasSize by mutableStateOf(Size.Zero)

    var selectedBackgroundColor by mutableStateOf(background)
        private set

    var selectedPenColor by mutableStateOf(initial)
        private set

    var penThickness by mutableStateOf(24.dp)
        private set

    var currentPath by mutableStateOf<PathData?>(null)
        private set

    var allPathList by mutableStateOf(persistentListOf<PathData>())

    internal var isToolBarMenuExpanded by mutableStateOf(false)

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
                allPathList.forEach { pathData -> drawPathData(pathData = pathData) }
                currentPath?.let { pathData -> drawPathData(pathData = pathData) }
            }

            imageBitmap
        }
    }
}