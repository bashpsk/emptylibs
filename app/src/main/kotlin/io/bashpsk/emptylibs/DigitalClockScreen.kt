package io.bashpsk.emptylibs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.RetainedEffect
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.bashpsk.emptylibs.composewidgets.clock.digital.DigitalClock
import io.bashpsk.emptylibs.composewidgets.clock.digital.DigitalClockDefault
import io.bashpsk.emptylibs.composewidgets.clock.digital.DigitalClockWithWeekDays
import io.bashpsk.emptylibs.jetpackui.sevensegment.SevenSegmentDefault
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun DigitalClockScreen() {

    val lifecycleOwner = LocalLifecycleOwner.current
    val timeCoroutineScope = rememberCoroutineScope()

    var currentTimeJob by retain { mutableStateOf<Job?>(null) }
//    var currentDateTime by retain { mutableStateOf(Clock.System.now().getSystemDateTime()) }

    var currentDateTime by rememberSaveable {
        mutableLongStateOf(Clock.System.now().toEpochMilliseconds())
    }

    val shape = MaterialTheme.shapes.extraSmall

    val colors = CardDefaults.elevatedCardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    RetainedEffect(lifecycleOwner) {

        currentTimeJob?.cancel()

        val lifecycleEventObserver = LifecycleEventObserver { _, event ->

            currentTimeJob = when (event) {

                Lifecycle.Event.ON_RESUME -> timeCoroutineScope.launch(Dispatchers.Default) {

                    while (isActive) {

                        delay(duration = 250.milliseconds)
//                        currentDateTime = Clock.System.now().getSystemDateTime()
                        currentDateTime = Clock.System.now().toEpochMilliseconds()
                    }
                }

                else -> {

                    currentTimeJob?.cancel()
                    null
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer = lifecycleEventObserver)

        onRetire {

            currentTimeJob?.cancel()
            lifecycleOwner.lifecycle.removeObserver(observer = lifecycleEventObserver)
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 16.dp,
                alignment = Alignment.CenterVertically
            )
        ) {

            ElevatedCard(
                shape = shape,
                colors = colors
            ) {

                DigitalClock(
                    modifier = Modifier.padding(12.dp),
                    dateTimeMillis = currentDateTime,
                    textStyles = DigitalClockDefault.textStyles()
                )
            }

            ElevatedCard(
                shape = shape,
                colors = colors
            ) {

                DigitalClock(
                    modifier = Modifier.padding(12.dp),
                    dateTimeMillis = currentDateTime,
                    properties = SevenSegmentDefault.properties(
                        width = 48.dp,
                        aspectRatio = 1.1F
                    )
                )
            }

            ElevatedCard(
                shape = shape,
                colors = colors
            ) {

                DigitalClockWithWeekDays(
                    modifier = Modifier.padding(12.dp),
                    localDateTime = currentDateTime,
                    textStyles = DigitalClockDefault.textStyles()
                )
            }
        }
    }
}