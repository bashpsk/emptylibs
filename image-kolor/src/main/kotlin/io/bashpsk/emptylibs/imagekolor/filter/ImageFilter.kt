package io.bashpsk.emptylibs.imagekolor.filter

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.toImmutableList

/**
 * Composable function that displays a grid of image filters.
 *
 * This function uses a [LazyVerticalGrid] to efficiently display a list of available image filters.
 * Each filter is represented by a [KolorFilterView], which shows a preview of the filter applied
 * to a sample image. The user can select a filter by clicking on it.
 *
 * @param modifier Optional [Modifier] for this composable.
 * @param state The [ImageFilterState] that holds the current state of the image filter selection,
 * including the preview image and the currently selected filter. Defaults to a new instance created
 * by [rememberImageFilterState].
 */
@Composable
fun ImageFilter(
    modifier: Modifier = Modifier,
    state: ImageFilterState = rememberImageFilterState(),
    itemSize: Dp = 120.dp
) {

    val kolorFilterList = retain { ImageFilterType.entries.toImmutableList() }

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(minSize = itemSize),
        verticalArrangement = Arrangement.spacedBy(space = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 4.dp)
    ) {

        items(
            items = kolorFilterList,
            key = { kolorFilter -> kolorFilter.name }
        ) { kolorFilter ->

            val isSelected by remember(kolorFilter, state) {
                derivedStateOf { kolorFilter == state.selectedFilter }
            }

            KolorFilterView(
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
                kolorFilter = kolorFilter,
                imageModel = state.previewImage,
                isSelected = isSelected,
                onFilterClick = state::onSelectFilter
            )
        }
    }
}