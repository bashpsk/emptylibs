package io.bashpsk.emptylibs.screen.formatter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.formatter.format.DateTimePattern
import io.bashpsk.emptylibs.formatter.format.DurationPattern
import io.bashpsk.emptylibs.formatter.format.dateTime
import io.bashpsk.emptylibs.formatter.format.duration
import kotlin.time.Clock
import kotlin.time.Duration

@Composable
fun FormatView(
    modifier: Modifier = Modifier,
    pattern: DateTimePattern
) {

    val dateTimeInMillis by rememberSaveable(pattern) {
        mutableLongStateOf(Clock.System.now().toEpochMilliseconds())
    }

    val formattedDateTime by remember(pattern) {
        derivedStateOf { dateTimeInMillis.dateTime(pattern) }
    }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraSmall
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = pattern.name,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = formattedDateTime,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun FormatView(
    modifier: Modifier = Modifier,
    duration: Duration
) {

    val durationFormatted by remember(duration) {
        derivedStateOf { duration.duration(DurationPattern.TimeLabel()) }
    }

    val durationFormattedWithLabel by remember(duration) {
        derivedStateOf { duration.duration(DurationPattern.Separator(char = ":")) }
    }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraSmall
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = durationFormatted,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = durationFormattedWithLabel,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}