package io.bashpsk.emptylibs.datastoreui.preference

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.datastoreui.resources.DatastoreUIDefaults

/**
 * Composable function for displaying a preference title.
 *
 * @param modifier The modifier to be applied to the component.
 * @param title A lambda function that returns the title string to be displayed.
 */
@Composable
internal inline fun PreferenceTitle(modifier: Modifier = Modifier, title: () -> String) {

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(space = 4.dp)
    ) {

        Text(
            text = title(),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Composable function to display the summary of a preference.
 *
 * @param modifier The modifier to be applied to the component.
 * @param summary A lambda function that returns the summary string to be displayed.
 * @param alpha The alpha value for the summary text. Defaults to
 * [DatastoreUIDefaults.SUMMARY_ALPHA].
 */
@Composable
internal inline fun PreferenceSummary(
    modifier: Modifier = Modifier,
    summary: () -> String = { "" },
    alpha: Float = DatastoreUIDefaults.SUMMARY_ALPHA
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(space = 4.dp)
    ) {

        Spacer(modifier = Modifier.height(height = 0.dp))

        Text(
            modifier = modifier.alpha(alpha = alpha),
            text = summary(),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

/**
 * Displays a summary text for a preference item.
 *
 * This composable is used to display a brief description or additional information
 * related to a preference.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param summary A lambda function that returns the summary string to be displayed. Defaults to an
 * empty string.
 * @param alpha The transparency of the summary text. Defaults to
 * [DatastoreUIDefaults.SUMMARY_ALPHA].
 * @param fontFamily The font family to be used for the summary text.
 */
@Composable
internal inline fun PreferenceSummary(
    modifier: Modifier = Modifier,
    summary: () -> String = { "" },
    alpha: Float = DatastoreUIDefaults.SUMMARY_ALPHA,
    fontFamily: FontFamily
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(space = 4.dp)
    ) {

        Spacer(modifier = Modifier.height(height = 0.dp))

        Text(
            modifier = modifier.alpha(alpha = alpha),
            text = summary(),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = fontFamily
        )
    }
}

/**
 * A Composable function that displays a "Done" button, typically used in preference dialogs.
 * This button, when clicked, executes the `onDoneClick` lambda.
 *
 * @param modifier The [Modifier] to be applied to the button's layout.
 * @param horiArrangement The horizontal arrangement of the button within its `Row`.
 * Defaults to [Arrangement.End].
 * @param verticalAlignment The vertical alignment of the button within its `Row`.
 * Defaults to [Alignment.CenterVertically].
 * @param onDoneClick A lambda function that will be invoked when the "Done" button is clicked.
 */
@Composable
internal fun PreferenceDialogButton(
    modifier: Modifier = Modifier,
    horiArrangement: Arrangement.Horizontal = Arrangement.End,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onDoneClick: () -> Unit
) {

    Row(
        modifier = modifier,
        horizontalArrangement = horiArrangement,
        verticalAlignment = verticalAlignment
    ) {

        Button(onClick = onDoneClick) {

            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = "Done"
            )

            Spacer(modifier = Modifier.width(width = 2.dp))

            Text(
                text = "Done",
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * A composable function that displays a row of buttons typically used in a preference dialog.
 * This overload includes both "Done" and "Reset" buttons.
 *
 * @param modifier The modifier to be applied to the row of buttons.
 * @param horiArrangement The horizontal arrangement of the buttons within the row.
 * Defaults to `Arrangement.SpaceAround`.
 * @param verticalAlignment The vertical alignment of the buttons within the row.
 * Defaults to `Alignment.CenterVertically`.
 * @param onDoneClick A lambda function to be executed when the "Done" button is clicked.
 * @param onResetClick A lambda function to be executed when the "Reset" button is clicked.
 */
@Composable
internal fun PreferenceDialogButton(
    modifier: Modifier = Modifier,
    horiArrangement: Arrangement.Horizontal = Arrangement.SpaceAround,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onDoneClick: () -> Unit,
    onResetClick: () -> Unit
) {

    Row(
        modifier = modifier,
        horizontalArrangement = horiArrangement,
        verticalAlignment = verticalAlignment
    ) {

        OutlinedButton (onClick = onResetClick) {

            Icon(
                imageVector = Icons.Filled.Restore,
                contentDescription = "Reset"
            )

            Spacer(modifier = Modifier.width(width = 2.dp))

            Text(
                text = "Reset",
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Button(onClick = onDoneClick) {

            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = "Done"
            )

            Spacer(modifier = Modifier.width(width = 2.dp))

            Text(
                text = "Done",
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}