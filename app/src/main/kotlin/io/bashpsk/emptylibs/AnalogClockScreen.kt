package io.bashpsk.emptylibs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.bashpsk.emptylibs.composewidgets.clock.analog.AnalogClock
import io.bashpsk.emptylibs.composewidgets.clock.analog.AnalogClockDefault
import io.bashpsk.emptylibs.composewidgets.clock.analog.AnalogClockShape

@Composable
fun AnalogClockScreen() {

    val clockShape = AnalogClockShape.Circle
//    val clockShape = AnalogClockShape.Triangle
//    val clockShape = AnalogClockShape.Polygon(sides = 5)
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
        majorDivisionWidth = 12.dp,
        majorDivisionThickness = 2.dp,
    )

    val colors = AnalogClockDefault.colors(
        majorTickColor = MaterialTheme.colorScheme.surfaceTint,
        minorTickColor = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.35F),
        hourHandColor = Color(99, 128, 255, 255),
        minuteHandColor = Color(255, 34, 119, 255),
        secondHandColor = Color(103, 255, 237, 255),
        borderColor = MaterialTheme.colorScheme.outlineVariant
    )

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
                    colors = colors
                )

//                Box(modifier = Modifier.fillMaxSize().border(width = 1.dp, Color.Red))

//                HorizontalDivider()

//                VerticalDivider()
            }
        }
    }
}