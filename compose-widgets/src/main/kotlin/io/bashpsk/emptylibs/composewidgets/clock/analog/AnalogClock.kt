package io.bashpsk.emptylibs.composewidgets.clock.analog

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.layout.onVisibilityChanged
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.bashpsk.emptylibs.composewidgets.extension.getSystemDateTime
import io.bashpsk.emptylibs.imageutils.shape.getMinimumRadius
import io.bashpsk.emptylibs.imageutils.shape.toPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration

/**
 * A composable function that displays a fully functional analog clock.
 *
 * This clock updates its hands every second to show the current time. The clock is lifecycle-aware
 * and will only update when the component is visible and the app is in the foreground.
 *
 * @param modifier The modifier to be applied to the clock.
 * @param shape The [ClockShape] of the clock's border. Defines the outer boundary of the clock.
 * @param properties The [AnalogClockProperties] to customize the visual appearance of the clock's
 * components.
 * @param colors The [AnalogClockColors] to define the colors for various parts of the clock.
 * @param interval The [Duration] between each clock update.
 */
@Composable
fun AnalogClock(
    modifier: Modifier = Modifier,
    shape: ClockShape = AnalogClockDefault.shape,
    properties: AnalogClockProperties = AnalogClockDefault.properties(),
    colors: AnalogClockColors = AnalogClockDefault.colors(),
    interval: Duration = AnalogClockDefault.interval
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val textMeasurer = rememberTextMeasurer()
    val timeCoroutineScope = rememberCoroutineScope()

    var isClockVisible by rememberSaveable { mutableStateOf(false) }
    var currentTimeJob by retain { mutableStateOf<Job?>(null) }
    var currentDateTime by retain { mutableStateOf(Clock.System.now().getSystemDateTime()) }

    DisposableEffect(lifecycleOwner, isClockVisible) {

        currentTimeJob?.cancel()

        val lifecycleEventObserver = LifecycleEventObserver { _, event ->

            currentTimeJob = when (event) {

                Lifecycle.Event.ON_RESUME if (isClockVisible) -> {

                    timeCoroutineScope.launch(context = Dispatchers.Default) {

                        while (isActive) {

                            delay(duration = interval)
                            currentDateTime = Clock.System.now().getSystemDateTime()
                        }
                    }
                }

                else -> {

                    currentTimeJob?.cancel()
                    null
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer = lifecycleEventObserver)

        onDispose {

            currentTimeJob?.cancel()
            lifecycleOwner.lifecycle.removeObserver(observer = lifecycleEventObserver)
        }
    }

    Canvas(
        modifier = modifier
            .clipToBounds()
            .onVisibilityChanged { isVisible ->

                isClockVisible = isVisible
            },
        contentDescription = "Analog Clock"
    ) {

        val borderWidth = properties.borderWidth.toPx()
        val halfBorderWidth = borderWidth / 2
        val canvasRadius = size.minDimension / 2
        val borderRadius = canvasRadius - halfBorderWidth
        val clockRadius = borderRadius - halfBorderWidth
        val handRadius = shape.getMinimumRadius(boundingRadius = clockRadius)

        val borderPath = shape.toPath(
            canvasSize = Size(width = borderRadius * 2, height = borderRadius * 2)
        ).apply {

            val matrix = Matrix().apply {

                translate(x = halfBorderWidth, y = halfBorderWidth)
            }

            transform(matrix = matrix)
        }

        val clockPath = shape.toPath(
            canvasSize = Size(width = clockRadius * 2, height = clockRadius * 2)
        ).apply {

            val matrix = Matrix().apply {

                translate(x = borderWidth, y = borderWidth)
            }

            transform(matrix = matrix)
        }

        drawClockFace(
            properties = properties,
            colors = colors,
            textMeasurer = textMeasurer,
            clockRadius = clockRadius,
            borderWidth = borderWidth,
            borderPath = borderPath,
            clockPath = clockPath
        )

        drawClockHands(
            properties = properties,
            colors = colors,
            localDateTime = currentDateTime,
            handRadius = handRadius
        )
    }
}