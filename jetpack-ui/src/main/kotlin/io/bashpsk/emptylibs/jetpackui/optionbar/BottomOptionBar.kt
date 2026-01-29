package io.bashpsk.emptylibs.jetpackui.optionbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ContextualFlowRow
import androidx.compose.foundation.layout.ContextualFlowRowOverflow
import androidx.compose.foundation.layout.ContextualFlowRowOverflowScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.composeutils.offset.toDpOffsetData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * A composable function that displays a bottom option bar.
 *
 * This function uses a [ContextualFlowRow] to display the options. If there are more options than
 * can be displayed in the available space, an overflow menu is shown.
 * The overflow menu is a [DropdownMenu] that displays the remaining options.
 *
 * @param modifier The modifier to be applied to the bottom option bar.
 * @param optionList The list of options to be displayed.
 * @param moreOption The option to be displayed in the overflow menu item.
 * @param onOptionClick A callback that is invoked when an option is clicked.
 * @param maxLines The maximum number of lines to be used for the options.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BottomOptionBar(
    modifier: Modifier = Modifier,
    optionList: ImmutableList<OptionBarData> = persistentListOf(),
    moreOption: OptionBarData = OptionBarData.More,
    onOptionClick: (option: OptionBarData) -> Unit = {},
    maxLines: Int = 1,
) {

    var isMoreOptionMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var menuPosition by rememberSaveable { mutableStateOf(DpOffset.Zero.toDpOffsetData()) }
    var shownItemCount by rememberSaveable { mutableIntStateOf(0) }

    val remainingItems by remember(optionList, shownItemCount) {
        derivedStateOf { optionList.subList(shownItemCount, optionList.size) }
    }

    val expandIndicator = @Composable { scope: ContextualFlowRowOverflowScope ->

        shownItemCount = scope.shownItemCount
        OptionBarItem(
            optionData = moreOption,
            onClick = {

                onOptionClick(moreOption)
                isMoreOptionMenuExpanded = true
            }
        )
    }

    Box(
        modifier = Modifier
            .wrapContentSize()
            .background(color = BottomAppBarDefaults.containerColor),
        contentAlignment = Alignment.Center
    ) {

        ContextualFlowRow(
            modifier = modifier
                .align(alignment = Alignment.Center)
                .onPlaced { layoutCoordinates ->

                    menuPosition = DpOffset(
                        x = layoutCoordinates.size.width.dp,
                        y = 0.dp
                    ).toDpOffsetData()
                },
            maxLines = maxLines,
            itemCount = optionList.size,
            overflow = ContextualFlowRowOverflow.expandOrCollapseIndicator(
                minRowsToShowCollapse = 0,
                expandIndicator = expandIndicator,
                collapseIndicator = {}
            ),
            verticalArrangement = Arrangement.spacedBy(space = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) { itemIndex ->

            val option by remember(optionList, itemIndex) {
                derivedStateOf { optionList.getOrNull(index = itemIndex) }
            }

            option?.let { optionItem ->

                OptionBarItem(
                    optionData = optionItem,
                    onClick = {

                        onOptionClick(optionItem)
                        isMoreOptionMenuExpanded = false
                    }
                )
            }
        }

        DropdownMenu(
            expanded = isMoreOptionMenuExpanded,
            onDismissRequest = {

                isMoreOptionMenuExpanded = false
            },
            offset = menuPosition.toDpOffset()
        ) {

            HorizontalDivider()

            remainingItems.forEach { optionItem ->

                OptionMenuItem(
                    optionData = optionItem,
                    onClick = {

                        onOptionClick(optionItem)
                        isMoreOptionMenuExpanded = false
                    }
                )
            }

            HorizontalDivider()
        }
    }
}