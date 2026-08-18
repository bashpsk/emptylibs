package io.bashpsk.emptylibs.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.layouts.sticky.StickyRowLayout

@Composable
fun StickyRowLayoutScreen() {

    val largeContent = remember {
        "import androidx.compose.foundation.layout.Arrangement\n" +
                "import androidx.compose.foundation.layout.fillMaxSize\n" +
                "import androidx.compose.foundation.layout.fillMaxWidth\n" +
                "import androidx.compose.material3.Scaffold\n" +
                "import androidx.compose.runtime.Composable\n" +
                "import androidx.compose.ui.Alignment\n" +
                "import androidx.compose.ui.Modifier\n" +
                "import androidx.compose.ui.unit.dp\n" +
                "import io.bashpsk.emptylibs.layouts.sticky.StickyRowLayout".trimIndent()
    }

    val horizontalScrollState = rememberScrollState()

    val horizontalScrollOffset by remember {
        derivedStateOf { horizontalScrollState.value }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
                .horizontalScroll(horizontalScrollState),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp,
                alignment = Alignment.CenterVertically
            )
        ) {

            largeContent.lines().forEachIndexed { index, text ->

                StickyRowLayout(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalScroll = horizontalScrollOffset,
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 4.dp,
                        alignment = Alignment.Start
                    ),
                    verticalAlignment = Alignment.Top
                ) {

                    Text(
                        text = "${index + 1}",
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        modifier = Modifier.weight(1F),
                        text = text,
                        maxLines = 1
                    )
                }
            }
        }
    }
}