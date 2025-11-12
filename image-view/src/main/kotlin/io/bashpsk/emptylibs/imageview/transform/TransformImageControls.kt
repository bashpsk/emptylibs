package io.bashpsk.emptylibs.imageview.transform

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
internal fun TransformImageControls(
    modifier: Modifier = Modifier,
    onPreviousImage: () -> Unit,
    onNextImage: () -> Unit,
    onResetTransform: () -> Unit,
) {

    val cardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.75F),
        contentColor = MaterialTheme.colorScheme.onSurface
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(percent = 50),
        colors = cardColors
    ) {

        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

            CustomIconButton(
                icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                onClick = onPreviousImage
            )

            CustomIconButton(
                icon = Icons.Filled.Restore,
                onClick = onResetTransform
            )

            CustomIconButton(
                icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                onClick = onNextImage
            )
        }
    }
}

@Composable
private fun CustomIconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    onClick: () -> Unit
) {

    IconButton(
        modifier = modifier.size(size = 60.dp),
        onClick = onClick
    ) {

        Icon(
            modifier = Modifier.size(size = 40.dp),
            imageVector = icon,
            contentDescription = null
        )
    }
}