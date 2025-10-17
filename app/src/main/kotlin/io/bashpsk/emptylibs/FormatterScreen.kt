package io.bashpsk.emptylibs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.formatter.format.DateTimePattern
import io.bashpsk.emptylibs.screen.formatter.FormatView
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
fun FormatterScreen() {

    val sampleDurationList = remember {
        persistentListOf(
            0.milliseconds,
            133300.milliseconds,
            1.seconds,
            1.hours,
            99.hours,
            1.days,
            33333333330.milliseconds
        )
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            items(
                items = sampleDurationList
            ) { duration ->

                FormatView(duration = duration)
            }

            item {

                HorizontalDivider()
            }

            items(
                items = DateTimePattern.entries.toImmutableList()
            ) { pattern ->

                FormatView(pattern = pattern)
            }

            item {

                HorizontalDivider()
            }
        }
    }
}