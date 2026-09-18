package io.bashpsk.emptylibs.imagekrop.crop

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.composeutils.shape.PathShape

/**
 * A composable function that displays a KropShape as a preview.
 *
 * This function uses a Canvas to draw the shape. The color of the shape changes based on whether it
 * is selected or not.
 *
 * @param kropShape The KropShape to be displayed.
 * @param isSelected A boolean indicating whether the shape is currently selected.
 */
@Composable
fun KropShapeView(kropShape: PathShape, isSelected: Boolean) {

    val unSelectedIconColor = MaterialTheme.colorScheme.onSurface
    val selectedIconColor = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.5F)

    val shapeColor by remember(isSelected, selectedIconColor, unSelectedIconColor) {
        derivedStateOf { if (isSelected) selectedIconColor else unSelectedIconColor }
    }

    Canvas(
        modifier = Modifier.size(size = 20.dp),
        contentDescription = "Krop Shape"
    ) {

        drawKropShapePreview(kropShape = kropShape, shapeColor = shapeColor)
    }
}