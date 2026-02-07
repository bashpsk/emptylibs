package io.bashpsk.emptylibs.imagekolor.svg

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.jetpackui.layout.TwoPaneAdaptiveLayout
import io.bashpsk.emptylibs.kolorpicker.color.KolorPickerDialog
import io.bashpsk.emptylibs.kolorpicker.color.rememberKolorPickerState

@Composable
fun SvgKolor(
    modifier: Modifier = Modifier,
    state: SvgKolorState,
) {

    val kolorPickerState = rememberKolorPickerState()

    val colorHexList by remember(state.hexKolorDataList) {
        derivedStateOf { state.hexKolorDataList }
    }

    KolorPickerDialog(
        modifier = Modifier.fillMaxSize(),
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
        firstPane = {

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                SvgImageView(modifier = Modifier.weight(weight = 1F), model = state.source)

                SvgImageView(modifier = Modifier.weight(weight = 1F), model = state.newSource)
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
                    key = { hexItem -> "${hexItem.id}-${hexItem.oldHex}" }
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
                        onClick = {

                            state.updateSelectedHex(newHex = hexItem)
                            kolorPickerState.dialogVisibleState.targetState = true
                        }
                    )
                }
            }
        }
    )
}