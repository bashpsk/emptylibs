package io.bashpsk.emptylibs.imagekolor.svg

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.formatter.format.parseHexToColor
import io.bashpsk.emptylibs.jetpackui.layout.TwoPaneAdaptiveLayout
import io.bashpsk.emptylibs.kolorpicker.color.KolorPickerDialog
import io.bashpsk.emptylibs.kolorpicker.color.rememberKolorPickerState

/**
 * A composable component that facilitates the visualization and recoloring of SVG elements.
 *
 * It provides a two-pane adaptive layout:
 * - The first pane displays a side-by-side comparison of the original SVG source and the
 *   dynamically recolored version.
 * - The second pane displays a grid of color maps derived from the SVG, allowing users to select
 *   specific color regions and update them using a color picker dialog.
 *
 * @param modifier The [Modifier] to be applied to the root layout of this component.
 * @param state The [SvgKolorState] instance that manages the SVG data, color mappings, and update
 * logic.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun SvgKolor(
    modifier: Modifier = Modifier,
    state: SvgKolorState,
) {

    val activity = LocalActivity.current
    val windowSizeClass = activity?.let { calculateWindowSizeClass(activity = it) }
    val kolorPickerState = rememberKolorPickerState()

    val colorHexList by remember(state.hexKolorDataList) {
        derivedStateOf { state.hexKolorDataList }
    }

    val windowWidthSize by remember(windowSizeClass) {
        derivedStateOf { windowSizeClass?.widthSizeClass ?: WindowWidthSizeClass.Compact }
    }

    val aspectRatio by remember(windowWidthSize) {
        derivedStateOf { if (windowWidthSize == WindowWidthSizeClass.Compact) 2.0F else 0.50F }
    }

    KolorPickerDialog(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding(),
        state = kolorPickerState,
        onSelectedColor = { newColor ->

            state.apply {

                updateColor(originalHex = selectedHex, newColor = newColor)
                updateSelectedHex(newHex = null)
            }
        },
        enableAlphaPanel = false,
        enableCopyButton = true,
        enablePasteButton = true
    )

    TwoPaneAdaptiveLayout(
        modifier = modifier,
        aspectRatio = aspectRatio,
        firstPane = {

            when (windowWidthSize) {

                WindowWidthSizeClass.Compact -> Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    SvgImageView(
                        modifier = Modifier.weight(weight = 1F),
                        model = state.source
                    )

                    SvgImageView(
                        modifier = Modifier.weight(weight = 1F),
                        model = state.newSource
                    )
                }

                else -> Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(space = 4.dp)
                ) {

                    SvgImageView(
                        modifier = Modifier.weight(weight = 1F),
                        model = state.source
                    )

                    SvgImageView(
                        modifier = Modifier.weight(weight = 1F),
                        model = state.newSource
                    )
                }
            }
        },
        secondPane = {

            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Adaptive(minSize = 120.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                verticalArrangement = Arrangement.spacedBy(space = 4.dp)
            ) {

                items(
                    items = colorHexList,
                    key = { hexItem -> "${hexItem.index}-${hexItem.oldHex}" }
                ) { hexItem ->

                    SvgKolorMapView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem(
                                fadeInSpec = tween(durationMillis = 250),
                                fadeOutSpec = tween(durationMillis = 100),
                                placementSpec = spring(
                                    stiffness = Spring.StiffnessLow,
                                    dampingRatio = Spring.DampingRatioMediumBouncy
                                )
                            ),
                        hexItem = hexItem,
                        viewBox = state.viewBox,
                        onClick = {

                            state.updateSelectedHex(newHex = hexItem)
                            kolorPickerState.apply {

                                updateColor(color = hexItem.newHex.parseHexToColor() ?: Color.Black)
                                dialogVisible.targetState = true
                            }
                        }
                    )
                }
            }
        }
    )
}