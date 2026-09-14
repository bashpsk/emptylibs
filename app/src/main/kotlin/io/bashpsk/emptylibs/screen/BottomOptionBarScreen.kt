package io.bashpsk.emptylibs.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AppBarRow
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.bashpsk.emptylibs.component.jetpackui.FileOperation
import io.bashpsk.emptylibs.component.jetpackui.OptionBarData
import io.bashpsk.emptylibs.utils.setDebug

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomOptionBarScreen() {

    val mainViewModel = viewModel<BottomOptionBarViewModel>()

    val selectedPaths by mainViewModel.selectedPaths.collectAsStateWithLifecycle()
    val optionList by mainViewModel.optionList.collectAsStateWithLifecycle()
    val isPathSelect by mainViewModel.isPathSelect.collectAsStateWithLifecycle()

    val onOperationClick = remember<(OptionBarData) -> Unit> {
        { option ->

            option.label.setDebug()

            when (option) {

                FileOperation.Share -> {}
                FileOperation.SelectFiles -> {}
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {

            AnimatedVisibility(
                visible = isPathSelect,
                enter = slideInVertically { it / 2 } + fadeIn(),
                exit = slideOutVertically { it / 2 } + fadeOut()
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = BottomAppBarDefaults.containerColor),
                    contentAlignment = Alignment.Center
                ) {

                    AppBarRow(
                        modifier = Modifier.windowInsetsPadding(BottomAppBarDefaults.windowInsets),
                        overflowIndicator = { state ->

                            OptionBarItem(
                                optionData = FileOperation.More,
                                onClick = {

                                    if (state.isShowing) state.dismiss() else state.show()
                                    onOperationClick(FileOperation.More)
                                }
                            )
                        }
                    ) {

                        optionList.forEach { item ->

                            customItem(
                                appbarContent = {

                                    OptionBarItem(
                                        optionData = item,
                                        onClick = { onOperationClick(item) }
                                    )
                                },
                                menuContent = {

                                    OptionMenuItem(
                                        optionData = item,
                                        onClick = {

                                            it.dismiss()
                                            onOperationClick(item)
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }

            /*AnimatedVisibility(
                visible = isPathSelect,
                enter = slideInVertically { it / 2 } + fadeIn(),
                exit = slideOutVertically { it / 2 } + fadeOut()
            ) {

                BottomOptionBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(insets = BottomAppBarDefaults.windowInsets),
                    optionList = optionList,
                    onOptionClick = onOperationClick
                )
            }*/
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues),
            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {

                val isSelected by remember(selectedPaths) {
                    derivedStateOf { !selectedPaths.any { path -> path == "Path 1" } }
                }

                Button(
                    enabled = isSelected,
                    onClick = { mainViewModel.addPathSelection("Path 1") }
                ) {

                    Text("Path 1")
                }
            }

            item {

                val isSelected by remember(selectedPaths) {
                    derivedStateOf { !selectedPaths.any { path -> path == "Path 2" } }
                }

                Button(
                    enabled = isSelected,
                    onClick = { mainViewModel.addPathSelection("Path 2") }
                ) {

                    Text("Path 2")
                }
            }

            item {

                val isSelected by remember(selectedPaths) {
                    derivedStateOf { !selectedPaths.any { path -> path == "Path 3" } }
                }

                Button(
                    enabled = isSelected,
                    onClick = { mainViewModel.addPathSelection("Path 3") }
                ) {

                    Text("Path 3")
                }
            }

            item {

                val isSelected by remember(selectedPaths) {
                    derivedStateOf { selectedPaths.isNotEmpty() }
                }

                Button(
                    enabled = isSelected,
                    onClick = mainViewModel::clearPathSelection
                ) {

                    Text("Clear")
                }
            }
        }
    }
}

@Composable
private fun OptionBarItem(
    modifier: Modifier = Modifier,
    optionData: OptionBarData,
    onClick: () -> Unit = {}
) {

    val cardColors = CardDefaults.cardColors(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = Color.Transparent,
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.50F)
    )

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraSmall,
        enabled = optionData.enabled,
        colors = cardColors,
        onClick = onClick
    ) {

        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                modifier = Modifier.size(size = 20.dp),
                imageVector = optionData.icon,
                contentDescription = optionData.label
            )

            Spacer(modifier = Modifier.height(height = 8.dp))

            Text(
                text = optionData.label,
                textAlign = TextAlign.Center,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun OptionMenuItem(
    modifier: Modifier = Modifier,
    optionData: OptionBarData,
    onClick: () -> Unit = {}
) {

    DropdownMenuItem(
        modifier = modifier,
        enabled = optionData.enabled,
        text = {

            Text(
                text = optionData.label,
                textAlign = TextAlign.Start,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = {

            Icon(
                modifier = Modifier.size(size = 20.dp),
                imageVector = optionData.icon,
                contentDescription = optionData.label
            )
        },
        onClick = onClick
    )
}