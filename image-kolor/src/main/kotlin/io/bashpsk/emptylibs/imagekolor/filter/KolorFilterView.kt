package io.bashpsk.emptylibs.imagekolor.filter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.imagekolor.R

/**
 * A Composable function that displays an image with a color filter applied.
 * It also shows the filter's label and allows interaction through clicks.
 *
 * @param modifier The modifier to be applied to the component.
 * @param kolorFilter A lambda function that returns the [ImageFilterType] to be applied.
 * @param imageModel A lambda function that returns the [ImageBitmap] to be displayed.
 * @param isSelected A lambda function that returns a boolean indicating if the filter is currently
 * selected. Defaults to false.
 * @param contentScale The [ContentScale] to be used for the image. Defaults to [ContentScale.Crop].
 * @param borderWidth The width of the border around the image. Defaults to 2.dp if selected, 0.2.dp
 * otherwise.
 * @param borderColor The color of the border around the image. Defaults to
 * [MaterialTheme.colorScheme.error].
 * @param shape The shape of the image and border. Defaults to [MaterialTheme.shapes.extraSmall].
 * @param onFilterClick A lambda function that is invoked when the filter view is clicked, passing
 * the selected [ImageFilterType].
 */
@Composable
fun KolorFilterView(
    modifier: Modifier = Modifier,
    kolorFilter: () -> ImageFilterType,
    imageModel: () -> ImageBitmap?,
    isSelected: () -> Boolean = { false },
    contentScale: ContentScale = ContentScale.Crop,
    borderWidth: Dp = if (isSelected()) 2.dp else 0.2.dp,
    borderColor: Color = MaterialTheme.colorScheme.error,
    shape: Shape = MaterialTheme.shapes.extraSmall,
    onFilterClick: (filter: ImageFilterType) -> Unit
) {

    val imageBitmap = ImageBitmap.imageResource(R.drawable.flower_01)

    val previewBitmap by remember(imageBitmap, imageModel()) {
        derivedStateOf { imageModel() ?: imageBitmap }
    }

    val borderModifierUnselected = Modifier.border(
        width = borderWidth,
        color = borderColor.copy(alpha = 0.40F),
        shape = shape
    )

    val borderModifierSelected = Modifier.border(
        width = borderWidth,
        color = borderColor,
        shape = shape
    )

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .then(if (isSelected()) borderModifierSelected else borderModifierUnselected)
            .clip(shape = shape),
        shape = shape,
        onClick = {

            onFilterClick(kolorFilter())
        }
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {

            Image(
                modifier = Modifier.fillMaxSize(),
                bitmap = previewBitmap,
                contentScale = contentScale,
                colorFilter = kolorFilter().colorFilter,
                contentDescription = kolorFilter().label
            )

            Text(
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.65F),
                        shape = MaterialTheme.shapes.extraSmall
                    )
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                text = kolorFilter().label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.inverseSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}