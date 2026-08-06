package io.bashpsk.emptylibs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.composeutils.shape.PathShape
import io.bashpsk.emptylibs.composeutils.shape.toLabel
import io.bashpsk.emptylibs.composeutils.shape.toPath

@Composable
fun PathShapeScreen() {

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {

            items(items = PathShape.BasicPathShapes) { pathShape ->

                Canvas(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .aspectRatio(ratio = 1F),
                    contentDescription = pathShape.toLabel()
                ) {

                    drawPath(path = pathShape.toPath(canvasSize = size), color = Color.Yellow)
                }
            }
        }
    }
}