package io.bashpsk.emptylibs.animations.diagonal

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.bashpsk.emptylibs.animations.shimmer.ShimmerEffectProperties

/**
 * Properties for configuring a diagonal label.
 *
 * @property containerColor The background color of the diagonal ribbon.
 * @property labelColor The color of the text label.
 * @property labelStyle The text style for the label.
 * @property padding The padding inside the ribbon around the text.
 * @property shimmerProperties Optional properties for adding a shimmer effect to the ribbon.
 */
@Immutable
data class DiagonalLabelProperties(
    val containerColor: Color = Color.Red,
    val labelColor: Color = Color.White,
    val labelStyle: TextStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
    val padding: PaddingValues = PaddingValues(all = 4.dp),
    val shimmerProperties: ShimmerEffectProperties? = null
)