package io.bashpsk.emptylibs.composewidgets.clock.digital

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.composewidgets.extension.hasAM
import io.bashpsk.emptylibs.composewidgets.extension.hasPM
import io.bashpsk.emptylibs.formatter.format.DateTimePattern
import io.bashpsk.emptylibs.formatter.format.dateTime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/**
 * A digital clock composable that displays the time and highlights the current day of the week.
 *
 * @param modifier The modifier to be applied to the composable.
 * @param textStyles The styles for the text elements in the clock.
 * @param localDateTime The local date and time to display, in milliseconds.
 * @param timeZone The time zone to use.
 * @param clockPattern The pattern for formatting the time.
 * @param disableTextAlpha The alpha value for disabled text.
 * @param clockIcon An optional icon to display next to the time.
 */
@Composable
fun DigitalClockWithWeekDays(
    modifier: Modifier = Modifier,
    localDateTime: Long,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    clockPattern: DateTimePattern = DateTimePattern.TIME_HH_MM_SS,
    textStyles: DigitalClockTextStyles = DigitalClockDefault.textStyles(),
    disableTextAlpha: Float = DigitalClockDefault.DISABLE_TEXT_ALPHA,
    clockIcon: @Composable (() -> Unit)? = {}
) {

    val currentDateTime by remember(localDateTime, timeZone) {
        derivedStateOf {
            Instant.fromEpochMilliseconds(localDateTime).toLocalDateTime(timeZone = timeZone)
        }
    }

    DigitalClockWithWeekDays(
        modifier = modifier,
        localDateTime = currentDateTime,
        clockPattern = clockPattern,
        textStyles = textStyles,
        disableTextAlpha = disableTextAlpha,
        clockIcon = clockIcon
    )
}

/**
 * A digital clock composable that displays the time and highlights the current day of the week.
 *
 * @param modifier The modifier to be applied to the composable.
 * @param textStyles The styles for the text elements in the clock.
 * @param localDateTime The local date and time to display.
 * @param clockPattern The pattern for formatting the time.
 * @param disableTextAlpha The alpha value for disabled text.
 * @param clockIcon An optional icon to display next to the time.
 */
@Composable
fun DigitalClockWithWeekDays(
    modifier: Modifier = Modifier,
    localDateTime: LocalDateTime,
    clockPattern: DateTimePattern = DateTimePattern.TIME_HH_MM_SS,
    textStyles: DigitalClockTextStyles = DigitalClockDefault.textStyles(),
    disableTextAlpha: Float = DigitalClockDefault.DISABLE_TEXT_ALPHA,
    clockIcon: @Composable (() -> Unit)? = {}
) {

    val weekDaysList by remember { derivedStateOf { DayOfWeekNames.ENGLISH_ABBREVIATED.names } }

    val currentTimeFormatted by remember(localDateTime, clockPattern) {
        derivedStateOf { localDateTime.dateTime(pattern = clockPattern).replace("1", " 1") }
    }

    val amIndicatorTextStyle by remember(localDateTime, textStyles, disableTextAlpha) {
        derivedStateOf {
            DigitalClockDefault.getTextStyle(
                textStyle = textStyles.indicator,
                enabled = localDateTime.hasAM(),
                alpha = disableTextAlpha
            )
        }
    }

    val pmIndicatorTextStyle by remember(localDateTime, textStyles, disableTextAlpha) {
        derivedStateOf {
            DigitalClockDefault.getTextStyle(
                textStyle = textStyles.indicator,
                enabled = localDateTime.hasPM(),
                alpha = disableTextAlpha
            )
        }
    }

    Column(
        modifier = modifier
            .clipToBounds()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            weekDaysList.forEach { day ->

                val isToday by remember(localDateTime, day) {
                    derivedStateOf {
                        localDateTime.dayOfWeek.name.startsWith(day, ignoreCase = true)
                    }
                }

                val dayTextStyle by remember(textStyles, isToday, disableTextAlpha) {
                    derivedStateOf {
                        DigitalClockDefault.getTextStyle(
                            textStyle = textStyles.days,
                            enabled = isToday,
                            alpha = disableTextAlpha
                        )
                    }
                }

                Text(
                    text = day.uppercase(),
                    textAlign = TextAlign.Center,
                    style = dayTextStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.Bottom
        ) {

            Column(
                modifier = Modifier.padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(space = 2.dp)
            ) {

                clockIcon?.invoke()
            }

            Text(
                text = currentTimeFormatted,
                textAlign = TextAlign.Center,
                style = textStyles.time,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Column(
                modifier = Modifier.padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(space = 2.dp)
            ) {

                Text(
                    text = "AM",
                    textAlign = TextAlign.Center,
                    style = amIndicatorTextStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "PM",
                    textAlign = TextAlign.Center,
                    style = pmIndicatorTextStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}