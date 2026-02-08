package io.bashpsk.emptylibs.imagekolor.svg

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder

/**
 * Internal composable to display an SVG from a string model.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param model The SVG string content.
 */
@Composable
internal fun SvgImageView(
    modifier: Modifier = Modifier,
    model: String
) {

    AsyncImage(
        modifier = modifier
            .aspectRatio(ratio = 1.0F)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = MaterialTheme.shapes.extraSmall
            )
            .padding(horizontal = 4.dp, vertical = 4.dp),
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(data = model.encodeToByteArray())
            .decoderFactory(factory = SvgDecoder.Factory())
            .build(),
        contentScale = ContentScale.Fit,
        contentDescription = "Svg"
    )
}

/**
 * Internal composable to display a single recolorable SVG element in a card.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param hexItem The SVG element data.
 * @param viewBox The viewBox of the SVG.
 * @param onClick Callback when the card is clicked.
 */
@Composable
internal fun SvgKolorMapView(
    modifier: Modifier = Modifier,
    hexItem: SvgKolorElement,
    viewBox: String,
    onClick: () -> Unit
) {

    val elementSource by remember(hexItem, viewBox) {
        derivedStateOf {
            """<svg viewBox="$viewBox" xmlns="http://www.w3.org/2000/svg">${
                hexItem.toSvgElement(hexItem.newHex)
            }</svg>"""
        }
    }

    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = MaterialTheme.shapes.extraSmall,
        onClick = onClick
    ) {

        SvgImageView(
            modifier = Modifier.fillMaxWidth(),
            model = elementSource
        )
    }
}