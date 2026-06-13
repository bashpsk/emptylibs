package io.bashpsk.emptylibs.canvasslate.slate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.RetainedEffect
import androidx.compose.runtime.retain.retain
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
import io.bashpsk.emptylibs.composeutils.offset.hasNeared
import io.bashpsk.emptylibs.composeutils.offset.toOffsetData
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Clock

/**
 * Composable function to remember the state of the CanvasSlate.
 *
 * This function creates and remembers a [CanvasSlateState] instance, which holds the
 * current state of the drawing canvas, including background color, brush properties,
 * drawn paths, and drawing mode.
 *
 * The state is saved and restored across configuration changes using [retain].
 *
 * @param background The initial background color of the canvas. Defaults to [Color.DarkGray].
 * @param brush The initial brush color. Defaults to [Color.Green].
 * @return A [CanvasSlateState] instance that can be used to control and observe the canvas.
 */
@Composable
fun rememberCanvasSlateState(
    background: Color = CanvasSlateState.BackgroundColor,
    brush: Color = CanvasSlateState.BrushColor
): CanvasSlateState {

    val density = LocalDensity.current

    val state = retain(density) { CanvasSlateState(density = density) }

    RetainedEffect(background) {

        state.backgroundColor = background

        onRetire { }
    }

    RetainedEffect(brush) {

        state.brushColor = brush

        onRetire { }
    }

    return state
}

/**
 * State object that can be used to control and observe canvas slate changes.
 *
 * This state is responsible for holding the current state of the canvas, including the background
 * color, brush color, stroke cap, stroke join, brush thickness, current path, drawing mode,
 * and list of all paths. It also provides methods for updating these states and performing
 * actions such as clearing the canvas, undoing the last action, and getting an image bitmap
 * of the canvas.
 *
 * @param density The density of the display.
 */
@Stable
class CanvasSlateState(private val density: Density) {

    /**
     * The threshold in pixels used for determining if a touch event is near an existing path
     * when in editing mode. This value is calculated based on 12.dp converted to pixels
     * using the current screen density.
     */
    private val threshold = 12.dp.toPixel(density = density)

    /**
     * Represents the current size of the canvas.
     * This state variable is used to track and manage the dimensions of the drawing area.
     * It is initialized to `Size.Zero` and updated when the canvas layout changes.
     */
    internal var canvasSize by mutableStateOf(Size.Zero)

    /**
     * Represents the selected background color for the canvas.
     * This property holds the current background color chosen by the user.
     */
    var backgroundColor by mutableStateOf(BackgroundColor)

    /**
     * Represents the currently selected brush color for drawing on the canvas.
     *
     * This property holds the [Color] that will be used for new paths drawn on the canvas.
     */
    var brushColor by mutableStateOf(BrushColor)

    /**
     * Represents the currently selected stroke cap for drawing paths.
     * The stroke cap determines the shape drawn at the beginning and end of open sub-paths and
     * dashes.
     * It can be one of [StrokeCap.Butt], [StrokeCap.Round], or [StrokeCap.Square].
     * The default value is [StrokeCap.Round].
     */
    var strokeCap by mutableStateOf(StrokeCap.Round)

    /**
     * Represents the currently selected stroke join type for drawing paths.
     *
     * The stroke join determines the shape used to join two line segments where they meet.
     * It can be one of the values defined in the [StrokeJoin] enum
     * (e.g., [StrokeJoin.Round], [StrokeJoin.Miter], [StrokeJoin.Bevel]).
     */
    var strokeJoin by mutableStateOf(StrokeJoin.Round)

    /**
     * Represents the thickness of the brush used for drawing.
     * The default value is `4.0F`.
     */
    var brushThickness by mutableFloatStateOf(4.0F)

    /**
     * Represents the current path being drawn on the canvas.
     * This property holds the [CanvasSlatePath] object for the path currently being drawn.
     * It is updated as the user draws and is set to `null` when no path is being drawn.
     *
     * @see CanvasSlatePath
     */
    var currentPath by mutableStateOf<CanvasSlatePath?>(null)
        private set

    /**
     * A boolean state indicating whether the canvas is currently in drawing mode.
     * When `true`, user interactions will draw paths on the canvas.
     * When `false`, user interactions might be interpreted for other purposes, like selecting
     * existing paths.
     * This property is publicly readable but can only be modified internally within the
     * `CanvasSlateState`.
     */
    var isDrawingMode by mutableStateOf(true)
        private set

    /**
     * Stores all the paths drawn on the canvas.
     * This list is used to redraw the canvas when needed and to implement undo functionality.
     */
    var allPathList by mutableStateOf(persistentListOf<CanvasSlatePath>())
        internal set

    /**
     * Represents whether the toolbar menu is expanded or not.
     *
     * This property is used internally to control the visibility of the toolbar menu.
     * When `true`, the toolbar menu is displayed; when `false`, it is hidden.
     */
    internal var isToolBarMenuExpanded by mutableStateOf(false)

    /**
     * Holds the [CanvasSlatePath] that is currently being edited.
     * This is `null` if no path is being edited.
     * When a path is selected for editing, it is moved from [allPathList] to this property.
     * Changes to this path are reflected in [previewPathList].
     */
    internal var editCanvasSlatePath by mutableStateOf<CanvasSlatePath?>(null)

    /**
     * A list of [CanvasSlatePath] objects that are currently being previewed.
     * This list is used to store paths that are being edited or modified before they are applied to
     * the main [allPathList].
     * It allows for undoing changes and visualizing modifications before they become permanent.
     */
    internal var previewPathList by mutableStateOf(persistentListOf<CanvasSlatePath>())

    /**
     * Sets the drawing mode of the canvas.
     *
     * @param mode True to enable drawing mode, false to disable it.
     */
    fun onDrawingMode(mode: Boolean) {

        isDrawingMode = mode
    }

    /**
     * Clears the canvas by resetting the current path and the list of all paths.
     * This function effectively erases all drawings from the canvas.
     */
    fun onClearCanvas() {

        currentPath = null
        allPathList = persistentListOf()
    }

    /**
     * Undoes the last drawn path on the canvas.
     *
     * This function removes the most recently added path from the `allPathList`.
     * If the list is empty, no action is taken.
     */
    fun onUndoCanvas() {

        allPathList.lastOrNull()?.let { pathData ->

            allPathList = allPathList.removing(element = pathData)
        }
    }

    /**
     * Updates the current path being drawn on the canvas.
     *
     * @param path The [CanvasSlatePath] representing the current path, or null if no path is being
     * drawn.
     */
    fun onCurrentPath(path: CanvasSlatePath?) {

        currentPath = path
    }

    /**
     * Initializes a new path when drawing starts.
     *
     * This function is called when a new drawing stroke begins.
     * It only proceeds if [isDrawingMode] is true.
     * A new [CanvasSlatePath] is created with the current brush settings
     * (color, thickness, stroke cap, stroke join) and an empty list of points.
     * The [onCurrentPath] function is then called to set this new path as the active one.
     */
    internal fun onPathStart() {

        if (isDrawingMode) {

            val path = CanvasSlatePath(
                id = Clock.System.now().toEpochMilliseconds().toString(),
                color = brushColor.toArgb(),
                thickness = brushThickness,
                strokeCap = strokeCap.toString(),
                strokeJoin = strokeJoin.toString(),
                path = persistentListOf()
            )

            onCurrentPath(path = path)
        }
    }

    /**
     * Called when the drawing of a path is completed.
     *
     * If drawing mode is enabled and there is a current path,
     * it adds the current path to the list of all paths and resets the current path.
     */
    internal fun onPathEnd() {

        if (isDrawingMode) currentPath?.let { pathData ->

            allPathList = allPathList.adding(element = pathData)
            onCurrentPath(path = null)
        } ?: return
    }

    /**
     * Handles the drawing of a path on the canvas.
     *
     * This function is called when the user is drawing a path on the canvas. It checks if the
     * drawing mode is enabled.
     * If it is, it retrieves the current path being drawn. If a current path exists, it creates a
     * new path by adding
     * the current position to the existing path. The updated path is then set as the current path.
     *
     * @param position The current position of the drawing input (e.g., finger or stylus) on the
     * canvas.
     */
    internal fun onPathDraw(position: Offset) {

        if (isDrawingMode) currentPath?.let { pathData ->

            val path = pathData.copy(path = pathData.path.adding(position.toOffsetData()))

            onCurrentPath(path = path)
        } ?: return
    }

    /**
     * Handles the selection of a path for editing when not in drawing mode.
     *
     * This function is triggered when the user interacts with the canvas in a non-drawing mode
     * (e.g., an "edit" mode).
     * It iterates through all existing paths (`allPathList`) to find if the given `position`
     * (where the user interacted) is near any point on a path.
     *
     * The proximity check considers a `threshold` combined with the `thickness` of each path.
     *
     * If a path is found near the interaction `position`:
     *  - It calls `onUpdateEditPath` to set this path as the currently selected path for editing.
     *  - It copies the `allPathList` to `previewPathList`, which is likely used to display changes
     *  before they are finalized.
     *  - It returns `true` to indicate that a path was successfully selected for editing.
     *
     * If the application is in drawing mode (`isDrawingMode` is true) or if no path is found near
     * the `position`.
     *
     * @param position The [Offset] on the canvas where the interaction occurred.
     * @return `true` if a path was selected for editing, `null` otherwise.
     */
    internal fun onEditPathData(position: Offset): Boolean? {

        return if (isDrawingMode.not()) {

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
        } else null
    }

    /**
     * Updates the path currently being edited.
     *
     * This function is used internally to set or clear the path that is
     * targeted for editing operations.
     *
     * @param path The [CanvasSlatePath] to be set as the edit path, or null to clear it.
     */
    internal fun onUpdateEditPath(path: CanvasSlatePath?) {

        editCanvasSlatePath = path
    }

    /**
     * Adds or updates a path in the preview list.
     * If a path with the same ID already exists in the `previewPathList`, it is replaced with the
     * new path. Otherwise, the new path is added to the list.
     *
     * @param path The [CanvasSlatePath] to be added or updated in the preview list.
     */
    internal fun addPathInPreview(path: CanvasSlatePath) {

        previewPathList.find { pathData -> pathData.id == path.id }?.let { pathData ->

            previewPathList = previewPathList.removing(element = pathData).adding(element = path)
        }
    }

    /**
     * Undoes the last modification made to the preview path list.
     *
     * This function retrieves the last path data from the `previewPathList`.
     * It then finds the corresponding path in the `allPathList` using the ID.
     * If a match is found, it updates the `editCanvasSlatePath` with the original path
     * and restores the `previewPathList` by removing the last modification and adding
     * the original path back.
     */
    internal fun onUndoPreview() {

        previewPathList.lastOrNull()?.let { pathData ->

            allPathList.find { path -> pathData.id == path.id }?.let { path ->

                onUpdateEditPath(path = path)
                previewPathList = previewPathList.removing(element = pathData).adding(path)
            }
        }
    }

    /**
     * Applies the changes made in the preview path list to the main path list.
     * This function is typically called when the user confirms the edits made to a path.
     */
    internal fun onApplyPreview() {

        allPathList = previewPathList
    }

    /**
     * Deletes the currently selected path for editing.
     *
     * This function checks if there's a path currently selected for editing(`editCanvasSlatePath`).
     * If a path is selected:
     * 1. It removes that path from the `previewPathList`.
     * 2. It updates `allPathList` with the modified `previewPathList`.
     * 3. It clears the selected edit path by calling `onUpdateEditPath` with `null`.
     */
    internal fun onDeleteEditPath() {

        editCanvasSlatePath?.let { pathData ->

            allPathList = previewPathList.removingAll { path -> pathData.id == path.id }
            onUpdateEditPath(path = null)
        }
    }

    /**
     * Retrieves an [ImageBitmap] representation of the current canvas content.
     *
     * It creates an [ImageBitmap] with the dimensions of the canvas and then draws the
     * background color, all existing paths, and the current path (if any) onto it.
     *
     * @return An [ImageBitmap] containing the rendered canvas content, or `null` if the canvas size
     * is zero (i.e., not yet measured or invalid).
     */
    suspend fun getImageBitmap(): ImageBitmap? = withContext(Dispatchers.IO) {

        return@withContext if (canvasSize != Size.Zero) {

            val imageBitmap = ImageBitmap(canvasSize.width.toInt(), canvasSize.height.toInt())

            CanvasDrawScope().draw(
                density = density,
                layoutDirection = LayoutDirection.Ltr,
                canvas = Canvas(image = imageBitmap),
                size = canvasSize
            ) {

                drawRect(color = backgroundColor)
                allPathList.forEach { pathData -> drawSlatePath(slatePath = pathData) }
                currentPath?.let { pathData -> drawSlatePath(slatePath = pathData) }
            }

            imageBitmap
        } else null
    }

    internal fun clearState() {

        canvasSize = Size.Zero
        backgroundColor = BackgroundColor
        brushColor = BrushColor
        strokeCap = StrokeCap.Round
        strokeJoin = StrokeJoin.Round
        brushThickness = 4.0F
        currentPath = null
        isDrawingMode = true
        allPathList = persistentListOf()

        isToolBarMenuExpanded = false
        editCanvasSlatePath = null
        previewPathList = persistentListOf()
    }

    companion object {

        /**
         * Default background color of the canvas.
         */
        val BackgroundColor = Color.DarkGray

        /**
         * Default brush color.
         */
        val BrushColor = Color.Green
    }
}