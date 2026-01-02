package io.bashpsk.emptylibs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.jetpackui.sevensegment.SevenSegmentData
import io.bashpsk.emptylibs.jetpackui.sevensegment.SevenSegmentDefault
import io.bashpsk.emptylibs.jetpackui.sevensegment.SevenSegmentDisplay

@Composable
fun SevenSegmentDisplayScreen() {

    val itemSize = 64.dp
    val itemSpace = 4.dp

    val colors = SevenSegmentDefault.colors()
    val properties = SevenSegmentDefault.properties()

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 12.dp,
                alignment = Alignment.CenterVertically
            )
        ) {

            SevenSegmentDisplay(
                modifier = Modifier.width(width = itemSize / 2),
                data = SevenSegmentData.Nine,
                colors = colors,
                properties = properties
            )

            SevenSegmentDisplay(
                modifier = Modifier.width(width = itemSize / 2),
                data = ' ',
                colors = colors,
                properties = properties
            )

            SevenSegmentDisplay(
                modifier = Modifier.width(width = itemSize / 2),
                data = 'K',
                colors = colors,
                properties = properties
            )

            SevenSegmentDisplay(
                modifier = Modifier.fillMaxWidth(),
                data = "0123456789 ",
                colors = colors,
                properties = properties,
                itemSize = itemSize,
                itemSpace = itemSpace
            )
        }
    }
}