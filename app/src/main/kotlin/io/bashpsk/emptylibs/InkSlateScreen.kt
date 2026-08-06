package io.bashpsk.emptylibs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.ink.authoring.compose.InProgressStrokes
import androidx.ink.brush.Brush
import androidx.ink.brush.ExperimentalInkCustomBrushApi
import androidx.ink.brush.StockBrushes
import androidx.ink.brush.compose.createWithComposeColor
import androidx.ink.rendering.android.canvas.CanvasStrokeRenderer
import androidx.ink.strokes.Stroke

@OptIn(ExperimentalInkCustomBrushApi::class)
@Composable
fun InkSlateScreen() {

    val defaultBrush = remember {
        Brush.createWithComposeColor(
            family = StockBrushes.pressurePen(),
            color = Color.Yellow,
            size = 5F,
            epsilon = 0.1F
        )
    }

    val renderer = remember { CanvasStrokeRenderer.create() }
    val finishedStrokes = remember { mutableStateListOf<Stroke>() }

    Scaffold(modifier = Modifier.fillMaxSize()) {paddingValues ->

        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {

                finishedStrokes.forEach { stroke ->
                    renderer.draw(
                        canvas = drawContext.canvas.nativeCanvas,
                        stroke = stroke,
                        strokeToScreenTransform = android.graphics.Matrix()
                    )
                }
            }

            InProgressStrokes(
                defaultBrush = defaultBrush,
                onStrokesFinished = { strokes ->
                    finishedStrokes += strokes
                }
            )
        }
    }
}