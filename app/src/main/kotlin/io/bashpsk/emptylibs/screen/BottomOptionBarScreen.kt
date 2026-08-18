package io.bashpsk.emptylibs.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.bashpsk.emptylibs.jetpackui.optionbar.BottomOptionBar
import io.bashpsk.emptylibs.jetpackui.optionbar.OptionBarData
import io.bashpsk.emptylibs.component.jetpackui.FileOperation
import io.bashpsk.emptylibs.utils.setDebug

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
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {

                BottomOptionBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(insets = BottomAppBarDefaults.windowInsets),
                    optionList = optionList,
                    onOptionClick = onOperationClick
                )
            }
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
                    derivedStateOf { selectedPaths.any { path -> path == "Path 1" }.not() }
                }

                Button(
                    enabled = isSelected,
                    onClick = {
                        mainViewModel.addPathSelection("Path 1")
                    }
                ) {

                    Text("Path 1")
                }
            }

            item {

                val isSelected by remember(selectedPaths) {
                    derivedStateOf { selectedPaths.any { path -> path == "Path 2" }.not() }
                }

                Button(
                    enabled = isSelected,
                    onClick = {
                        mainViewModel.addPathSelection("Path 2")
                    }
                ) {

                    Text("Path 2")
                }
            }

            item {

                val isSelected by remember(selectedPaths) {
                    derivedStateOf { selectedPaths.any { path -> path == "Path 3" }.not() }
                }

                Button(
                    enabled = isSelected,
                    onClick = {
                        mainViewModel.addPathSelection("Path 3")
                    }
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