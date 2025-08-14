package io.bashpsk.emptylibs.jetpackui.textpicker

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import kotlin.math.abs

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun WheelTextPicker(
    modifier: Modifier = Modifier,
    items: List<String>,
    visibleCount: Int = 5,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    onSelected: (String) -> Unit
) {

    val density = LocalDensity.current
    val listState = rememberLazyListState()

    val itemHeight = with(density) {
        textStyle.lineHeight.takeIf { height ->

            height.isSpecified
        }?.toDp() ?: (textStyle.fontSize.toDp() * 1.6F)
    }

    val itemHeightPx = with(density) { itemHeight.toPx() }
    val centerIndex = visibleCount / 2

    val scrollPosition by remember {
        derivedStateOf { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
    }

    val nearestIndex by remember {
        derivedStateOf {

            val info = listState.layoutInfo
            val viewportCenter = (info.viewportStartOffset + info.viewportEndOffset) / 2

            info.visibleItemsInfo.minByOrNull { item ->

                abs((item.offset + item.size / 2) - viewportCenter)
            }?.index?.coerceIn(0.. items.lastIndex)
        }
    }

    var selectedIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(nearestIndex) {

        nearestIndex?.takeIf { index -> index != selectedIndex }?.let { index ->

            selectedIndex = index
            onSelected(items[index])
        }
    }

    BoxWithConstraints(
        modifier = modifier.height(height = itemHeight * visibleCount),
        contentAlignment = Alignment.Center
    ) {

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(vertical = itemHeight * centerIndex),
            flingBehavior = rememberSnapFlingBehavior(listState)
        ) {

            items(
                count = items.size,
                key = { index -> index }
            ) { index ->

                val (firstIndex, firstOffset) = scrollPosition
                val offsetPx = (index - firstIndex) * itemHeightPx - firstOffset
                val distanceFromCenter = offsetPx / itemHeightPx
                val distanceNormalized = abs(distanceFromCenter).coerceIn(0.0F.. 1.0F)

                val scale by animateFloatAsState(
                    targetValue = 1.2F - 0.2F * distanceNormalized,
                    animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
                    label = "Scale Animation"
                )

                val alpha by animateFloatAsState(
                    targetValue = 1.0F - 0.5F * distanceNormalized,
                    animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
                    label = "Alpha Animation"
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            alpha = alpha,
                            rotationX = distanceFromCenter * -25.0F
                        ),
                    text = items[index],
                    textAlign = TextAlign.Center,
                    style = textStyle
                )
            }
        }

        HighlightDivider(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = itemHeight)
        )
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun HighlightDivider(
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            thickness = 2.dp
        )

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            thickness = 2.dp
        )
    }
}