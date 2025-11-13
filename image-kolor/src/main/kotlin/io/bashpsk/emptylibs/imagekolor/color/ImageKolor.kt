package io.bashpsk.emptylibs.imagekolor.color

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.imagekolor.color.ImageKolorInput.Companion.getIcon
import io.bashpsk.emptylibs.imagekolor.color.ImageKolorInput.Companion.getValue
import io.bashpsk.emptylibs.imagekolor.color.ImageKolorInput.Companion.range
import kotlin.math.roundToInt

/**
 * A Composable function that displays an image with applied color filters.
 *
 * This function takes an [ImageKolorState] which contains the image bitmap and
 * the color filter parameters. It renders the image using the Jetpack Compose `Image`
 * Composable. If the `imageBitmap` in the `state` is null, it displays a
 * "Image Load Failed!" message.
 *
 * The color filter is derived from the `state` and applied to the image.
 * The image is displayed with a fixed aspect ratio of 16:9 and fills the available width.
 *
 * @param modifier Optional [Modifier] to be applied to the root Composable of this component.
 * @param state The [ImageKolorState] that holds the image data and color filter settings.
 */
@Composable
fun KolorImageView(modifier: Modifier = Modifier, state: ImageKolorState) {

    val colorFilter by remember(state) { derivedStateOf { state.getColorFilter() } }

    state.imageBitmap?.let { bitmap ->

        Image(
            modifier = modifier,
            bitmap = bitmap,
            contentScale = ContentScale.Fit,
            colorFilter = colorFilter,
            contentDescription = "Image with Color Filter"
        )
    } ?: Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        BasicText(
            modifier = Modifier.fillMaxWidth(),
            text = "Image Load Failed!",
            style = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            autoSize = TextAutoSize.StepBased(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * A Composable function that displays a set of sliders for adjusting image color properties.
 *
 * This function arranges multiple [AdjustmentSlider] components in a vertical [Column].
 * Each slider controls a specific image adjustment like brightness, exposure, contrast, etc.
 * The state of these adjustments is managed by the [ImageKolorState] object.
 *
 * @param modifier Optional [Modifier] for this composable. Defaults to [Modifier].
 * @param state The [ImageKolorState] that holds the current values of the adjustments and their
 * enabled status.
 */
@Composable
fun KolorAdjustmentSliders(modifier: Modifier = Modifier, state: ImageKolorState) {

    Column(
        modifier = modifier.padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterVertically
        )
    ) {

        state.imageBitmap?.run {

            KolorInputRow(modifier = Modifier.fillMaxWidth(), state = state)

            AdjustmentSlider(
                modifier = Modifier.fillMaxWidth(),
                kolorInput = state.currentKolorInput,
                onValueChange = { state.updateValues(it) }
            )
        }
    }
}

/**
 * A Composable function that displays a slider for adjusting a value.
 *
 * This function creates a vertical layout containing a label and a slider.
 * The label displays the text from [ImageKolorInput.label] along with the current value
 * (obtained from [ImageKolorInput.getValue]) represented as a percentage within the
 * given [ImageKolorInput.range].
 * The slider allows the user to change the value.
 *
 * @param modifier The modifier to be applied to the Column that holds the label and slider.
 * @param kolorInput The [ImageKolorInput] that provides the label, current value, and value range
 * for the slider.
 * @param onValueChange A callback function that is invoked when the slider's value changes.
 * It receives the new Float value as an argument.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdjustmentSlider(
    modifier: Modifier = Modifier,
    kolorInput: ImageKolorInput,
    onValueChange: (Float) -> Unit
) {

    val kolorLabel by remember(kolorInput) { derivedStateOf { kolorInput.label } }
    val kolorValueRange by remember(kolorInput) { derivedStateOf { kolorInput.range } }
    val kolorValue by remember(kolorInput) { derivedStateOf { kolorInput.getValue() } }

    val valuePercentage by remember(kolorValue, kolorValueRange) {
        derivedStateOf {

            val range = kolorValueRange.endInclusive - kolorValueRange.start
            val adjustedValue = kolorValue - kolorValueRange.start
            val normalizedValue = adjustedValue / range

            ((normalizedValue * 200) - 100).roundToInt().coerceIn(range = -100..100)
        }
    }

    val sliderLabel by remember(kolorLabel, valuePercentage) {
        derivedStateOf {
            "$kolorLabel : ${
                if (valuePercentage > 0) "+${valuePercentage}" else "$valuePercentage"
            }%"
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(space = 4.dp)
    ) {

        Text(
            text = sliderLabel,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall
        )

        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = kolorValue,
            onValueChange = onValueChange,
            valueRange = kolorValueRange
        )
    }
}

/**
 * A Composable function that displays a horizontal row of selectable color input options.
 *
 * This function uses a [LazyRow] to efficiently display a list of [ImageKolorInput] items.
 * Each item is represented by a [KolorInputView]. When an item is selected,
 * the `currentKolorInput` in the provided [ImageKolorState] is updated.
 *
 * @param modifier Optional [Modifier] for this composable. Defaults to [Modifier].
 * @param state The [ImageKolorState] that holds the list of available color inputs
 * and the currently selected input.
 */
@Composable
private fun KolorInputRow(modifier: Modifier = Modifier, state: ImageKolorState) {

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        items(
            items = state.imageKolorInputList,
            key = { kolorInput -> kolorInput.label }
        ) { kolorInput ->

            val isSelected by remember(state.currentKolorInput, kolorInput) {
                derivedStateOf { state.currentKolorInput.label == kolorInput.label }
            }

            KolorInputView(
                modifier = Modifier.size(size = 64.dp),
                kolorInput = kolorInput,
                isSelected = isSelected,
                onKolorInput = { input ->

                    state.currentKolorInput = input
                }
            )
        }
    }
}

/**
 * A Composable function that displays a single color input option as a circular card.
 *
 * This function renders an [ElevatedCard] with a circular shape. Inside the card,
 * it displays an [Icon] representing the [kolorInput] type.
 * The card's background and content color change based on whether it is [isSelected].
 * When the card is clicked, the [onKolorInput] callback is invoked with the
 * corresponding [ImageKolorInput].
 *
 * @param modifier Optional [Modifier] to be applied to the [ElevatedCard].
 * @param kolorInput The [ImageKolorInput] to be displayed.
 * @param isSelected A boolean indicating whether this input option is currently selected.
 * @param onKolorInput A callback function that is invoked when this input option is selected.
 * It receives the selected [ImageKolorInput] as an argument.
 */
@Composable
private fun KolorInputView(
    modifier: Modifier = Modifier,
    kolorInput: ImageKolorInput,
    isSelected: Boolean,
    onKolorInput: (input: ImageKolorInput) -> Unit
) {

    val kolorInputIcon by remember(kolorInput, isSelected) {
        derivedStateOf { kolorInput.getIcon(isSelected = isSelected) }
    }

    val selectedCardColors = CardDefaults.elevatedCardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )

    val unSelectedCardColors = CardDefaults.elevatedCardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    val elevatedCardColors by remember(isSelected) {
        derivedStateOf { if (isSelected) selectedCardColors else unSelectedCardColors }
    }

    ElevatedCard(
        modifier = modifier.aspectRatio(ratio = 1F),
        shape = CircleShape,
        colors = elevatedCardColors,
        onClick = { onKolorInput(kolorInput) }
    ) {

        Icon(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            imageVector = kolorInputIcon,
            contentDescription = "Input Type"
        )
    }
}