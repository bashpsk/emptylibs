package io.bashpsk.emptylibs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.RetainedEffect
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.bashpsk.emptylibs.composewidgets.clock.analog.AnalogClock
import io.bashpsk.emptylibs.composewidgets.clock.analog.AnalogClockDefault
import io.bashpsk.emptylibs.composewidgets.clock.analog.AnalogClockShape
import io.bashpsk.emptylibs.formatter.format.DateTimePattern
import io.bashpsk.emptylibs.formatter.format.dateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AnalogClockScreen() {

    val lifecycleOwner = LocalLifecycleOwner.current
    val timeCoroutineScope = rememberCoroutineScope()

    var currentTimeJob by retain { mutableStateOf<Job?>(null) }
//    var currentDateTime by retain { mutableStateOf(Clock.System.now().getSystemDateTime()) }

    var currentDateTime by rememberSaveable {
        mutableLongStateOf(Clock.System.now().toEpochMilliseconds())
    }

    val currentDateTimeFormatted by remember(currentDateTime) {
        derivedStateOf { currentDateTime.dateTime(pattern = DateTimePattern.TIME_HH_MM_SS) }
    }

    val clockShape = AnalogClockShape.Circle
//    val clockShape = AnalogClockShape.Triangle
//    val clockShape = AnalogClockShape.Polygon(sides = 5)
//    val clockShape = AnalogClockShape.Polygon(sides = 6)
//    val clockShape = AnalogClockShape.Rectangle(radius = 0.1F)
//    val clockShape = AnalogClockShape.CutCorner(radius = 0.1F)
//    val clockShape = AnalogClockShape.Star(edges = 5, distance = 2.50F)

    val properties = AnalogClockDefault.properties(
        borderWidth = 8.dp,
        numberTextStyle = MaterialTheme.typography.titleMedium.copy(
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        minorDivisionWidth = 4.dp,
        minorDivisionThickness = 4.dp,
        majorDivisionWidth = 14.dp,
        majorDivisionThickness = 4.dp
    )

    val colors = AnalogClockDefault.colors(
        majorTickColor = MaterialTheme.colorScheme.surfaceTint,
        minorTickColor = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.35F),
        hourHandColor = Color(99, 128, 255, 255),
        minuteHandColor = Color(255, 34, 119, 255),
        secondHandColor = Color(103, 255, 237, 255),
        borderColor = MaterialTheme.colorScheme.outlineVariant
    )

    RetainedEffect(lifecycleOwner) {

        currentTimeJob?.cancel()

        val lifecycleEventObserver = LifecycleEventObserver { _, event ->

            currentTimeJob = when (event) {

                Lifecycle.Event.ON_RESUME -> timeCoroutineScope.launch(Dispatchers.Default) {

                    while (isActive) {

                        delay(duration = 250.milliseconds)
//                        currentDateTime = Clock.System.now().getSystemDateTime()
                        currentDateTime = Clock.System.now().toEpochMilliseconds()
                    }
                }

                else -> {

                    currentTimeJob?.cancel()
                    null
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer = lifecycleEventObserver)

        onRetire {

            currentTimeJob?.cancel()
            lifecycleOwner.lifecycle.removeObserver(observer = lifecycleEventObserver)
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            )
        ) {

            Text(
                text = currentDateTimeFormatted,
                style = MaterialTheme.typography.titleMedium
            )

            Box(
                modifier = Modifier
                    .weight(weight = 1.0F)
                    .aspectRatio(ratio = 1.0F)
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {

                AnalogClock(
                    modifier = Modifier.fillMaxSize(),
                    shape = clockShape,
                    properties = properties,
                    colors = colors,
                    localDateTime = currentDateTime
                )

//                Box(modifier = Modifier.fillMaxSize().border(width = 1.dp, Color.Red))

//                HorizontalDivider()

//                VerticalDivider()
            }
        }
    }
}