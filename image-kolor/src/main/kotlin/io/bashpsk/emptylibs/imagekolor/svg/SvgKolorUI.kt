package io.bashpsk.emptylibs.imagekolor.svg

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import io.bashpsk.emptylibs.formatter.format.parseHexToColor

@Composable
internal fun SvgImageView(
    modifier: Modifier = Modifier,
    model: String
) {

    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(data = model.encodeToByteArray())
            .decoderFactory(factory = SvgDecoder.Factory())
            .build(),
        contentScale = ContentScale.Fit,
        contentDescription = "Svg"
    )
}

@Composable
internal fun SvgKolorMapView(
    modifier: Modifier = Modifier,
    hexItem: SvgKolorData,
    onClick: () -> Unit
) {

    val originalColor by remember(hexItem) {
        derivedStateOf { hexItem.oldHex.parseHexToColor() ?: Color.Unspecified }
    }

    val mappedColor by remember(hexItem) {
        derivedStateOf { hexItem.newHex.parseHexToColor() ?: Color.Unspecified }
    }

    ElevatedCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraSmall,
        onClick = onClick
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            ColorBoxView(
                modifier = Modifier.weight(weight = 1.0F),
                color = originalColor
            )

            Icon(
                modifier = Modifier.size(size = 18.dp),
                imageVector = Icons.Filled.DoubleArrow,
                contentDescription = "Mapped"
            )

            ColorBoxView(
                modifier = Modifier.weight(weight = 1.0F),
                color = mappedColor
            )
        }
    }
}

@Composable
private fun ColorBoxView(modifier: Modifier = Modifier, color: Color) {

    val shape = MaterialTheme.shapes.extraSmall

    Box(
        modifier = modifier
            .aspectRatio(ratio = 1.0F)
            .background(color = color, shape = shape)
            .border(width = 0.4.dp, color = MaterialTheme.colorScheme.outlineVariant, shape = shape)
    )
}