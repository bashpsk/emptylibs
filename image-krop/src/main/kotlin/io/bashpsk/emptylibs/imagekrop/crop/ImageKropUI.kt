package io.bashpsk.emptylibs.imagekrop.crop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import io.bashpsk.emptylibs.imageutils.shape.ImageShape
import io.bashpsk.emptylibs.imageutils.shape.toLabel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

/**
 * Composable function for the top app bar in the image cropping UI.
 * It provides actions like navigating back, undoing changes, previewing the crop, and finalizing
 * the crop.
 *
 * @param modifier Modifier to be applied to the TopAppBar.
 * @param state The current state of the image cropping UI, containing information like the original
 * and modified images, crop parameters, etc.
 * @param imagePreviewSheetState State for the bottom sheet used to preview the image.
 * @param onNavigateBack Lambda function to handle navigation back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ImageKropTopBar(
    modifier: Modifier = Modifier,
    state: ImageKropState,
    imagePreviewSheetState: SheetState,
    onKropFinished: () -> Unit,
    onNavigateBack: () -> Unit
) {

    val imagePreviewCoroutineScope = rememberCoroutineScope()

    val isUndoImageBitmap by remember(state.imageList) {
        derivedStateOf { state.imageList.size > 1 }
    }

    TopAppBar(
        modifier = modifier,
        navigationIcon = {

            IconButton(
                onClick = {

                    state.clearImages()
                    onNavigateBack()
                }
            ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate Back"
                )
            }
        },
        title = {},
        actions = {

            IconButton(
                enabled = isUndoImageBitmap,
                onClick = {

                    state.imageList.lastOrNull()?.let { bitmap ->

                        state.removeLastImage()
                    }
                }
            ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Image Crop Undo"
                )
            }

            IconButton(
                onClick = {

                    imagePreviewCoroutineScope.launch {

                        when (val kropResult = state.getCroppedImageBitmap()) {

                            is KropResult.Init -> {}

                            is KropResult.Failed -> {}

                            is KropResult.Success -> {

                                state.updatePreviewImage(bitmap = kropResult.bitmap)
                                imagePreviewSheetState.expand()
                            }
                        }
                    }
                }
            ) {

                Icon(
                    imageVector = Icons.Filled.Preview,
                    contentDescription = "Image Crop Preview"
                )
            }

            IconButton(
                onClick = {

                    imagePreviewCoroutineScope.launch {

                        when (val kropResult = state.getCroppedImageBitmap()) {

                            is KropResult.Init -> {}

                            is KropResult.Failed -> {}

                            is KropResult.Success -> {

                                state.updateModifiedImage(bitmap = kropResult.bitmap)
                                state.clearImages()
                                onKropFinished()
                            }
                        }
                    }
                }
            ) {

                Icon(
                    imageVector = Icons.Filled.DoneAll,
                    contentDescription = "Image Crop Done"
                )
            }
        },
        windowInsets = WindowInsets(left = 0, top = 0, right = 0, bottom = 0)
    )
}

/**
 * Composable function for the bottom bar of the image cropping UI.
 * It provides controls for flipping the image, changing the aspect ratio, and selecting a crop
 * shape.
 *
 * @param modifier The modifier to be applied to the bottom bar.
 * @param state The current state of the image cropping UI, containing all necessary information
 * and callbacks for operations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ImageKropBottomBar(
    modifier: Modifier = Modifier,
    state: ImageKropState,
    aspectList: ImmutableList<KropAspectRatio>
) {

    val imagePreviewCoroutineScope = rememberCoroutineScope()

    val aspectLockIcon by remember(state) {
        derivedStateOf { if (state.isAspectLocked) Icons.Filled.Lock else Icons.Filled.LockOpen }
    }

    BottomAppBar(
        modifier = modifier,
        windowInsets = WindowInsets(left = 0, top = 0, right = 0, bottom = 0)
    ) {

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.SpaceAround,
            itemVerticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {

                    imagePreviewCoroutineScope.launch {

                        val kropResult = state.getCroppedImageBitmap(
                            imageRect = state.canvasSize.toRect(),
                            imageFlip = KropImageFlip.Horizontal
                        )

                        when (kropResult) {

                            is KropResult.Init -> {}

                            is KropResult.Failed -> {}

                            is KropResult.Success -> {

                                state.updateOriginalImage(bitmap = kropResult.bitmap)
                                state.addImage(bitmap = kropResult.bitmap)
                            }
                        }
                    }
                }
            ) {

                Icon(
                    imageVector = Icons.Filled.Flip,
                    contentDescription = "Flip Horizontal"
                )
            }

            IconButton(
                onClick = {

                    imagePreviewCoroutineScope.launch {

                        val kropResult = state.getCroppedImageBitmap(
                            imageRect = state.canvasSize.toRect(),
                            imageFlip = KropImageFlip.Vertical
                        )

                        when (kropResult) {

                            is KropResult.Init -> {}

                            is KropResult.Failed -> {}

                            is KropResult.Success -> {

                                state.updateOriginalImage(bitmap = kropResult.bitmap)
                                state.addImage(bitmap = kropResult.bitmap)
                            }
                        }
                    }
                }
            ) {

                Icon(
                    modifier = Modifier.rotate(degrees = 90.0F),
                    imageVector = Icons.Filled.Flip,
                    contentDescription = "Flip Vertical"
                )
            }

            IconButton(
                onClick = {

                    state.isAspectRatioMenuExpanded = true
                }
            ) {

                Icon(
                    imageVector = Icons.Filled.AspectRatio,
                    contentDescription = "Aspect Ratio"
                )

                DropdownMenu(
                    expanded = state.isAspectRatioMenuExpanded,
                    onDismissRequest = {

                        state.isAspectRatioMenuExpanded = false
                    }
                ) {

                    DropdownMenuItem(
                        modifier = Modifier.size(size = 64.dp),
                        text = {

                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {

                                Icon(
                                    imageVector = aspectLockIcon,
                                    contentDescription = "Aspect Locked"
                                )
                            }
                        },
                        onClick = {

                            state.updateAspectLocked(locked = state.isAspectLocked.not())
                        }
                    )

                    aspectList.forEach { aspectRatio ->

                        val isSelected by remember(state, aspectRatio) {
                            derivedStateOf { state.kropAspectRatio == aspectRatio }
                        }

                        DropdownMenuItem(
                            modifier = Modifier.size(size = 64.dp),
                            text = {

                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .alpha(if (isSelected) 0.50F else 1.0F),
                                    text = aspectRatio.label,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            onClick = {

                                state.updateAspectRatio(aspectRatio)
                                state.isAspectRatioMenuExpanded = false
                            }
                        )
                    }
                }
            }

            IconButton(
                onClick = {

                    state.isShapeMenuExpanded = true
                }
            ) {

                Icon(
                    imageVector = Icons.Filled.StarBorder,
                    contentDescription = "Shape"
                )

                DropdownMenu(
                    expanded = state.isShapeMenuExpanded,
                    onDismissRequest = {

                        state.isShapeMenuExpanded = false
                    }
                ) {

                    state.shapeList.forEach { shape ->

                        val isSelected by remember(state, shape) {
                            derivedStateOf { state.kropShape == shape }
                        }

                        DropdownMenuItem(
                            modifier = Modifier.size(size = 44.dp),
                            text = {

                                KropShapeView(kropShape = shape, isSelected = isSelected)
                            },
                            onClick = {

                                state.updateKropShape(shape)
                                state.isShapeMenuExpanded = false
                            }
                        )
                    }
                }
            }

            IconButton(
                onClick = {

                    state.isShapeCustomizeDialog = true
                }
            ) {

                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Shape Customization"
                )
            }
        }
    }
}

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
private fun KropShapeView(kropShape: ImageShape, isSelected: Boolean) {

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

@Composable
internal fun KropShapeCustomizationDialog(state: ImageKropState) {

    AnimatedVisibility(
        visible = state.isShapeCustomizeDialog,
        enter = fadeIn(),
        exit = fadeOut()
    ) {

        AlertDialog(
            modifier = Modifier.fillMaxWidth(fraction = 0.95F),
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            ),
            onDismissRequest = {

                state.isShapeCustomizeDialog = false
            },
            title = {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        modifier = Modifier.weight(weight = 1.0F),
                        text = state.kropShape.toLabel(),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    IconButton(
                        onClick = {

                            state.isShapeCustomizeDialog = false
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Dialog Close"
                        )
                    }
                }
            },
            text = {

                KropShapeCustomization(state = state)
            },
            confirmButton = {

                Button(
                    onClick = {

                        state.isShapeCustomizeDialog = false
                    }
                ) {

                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Dialog Done"
                    )

                    Spacer(modifier = Modifier.width(width = 4.dp))

                    Text(
                        text = "Done",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        )
    }
}

@Composable
private fun KropShapeCustomization(state: ImageKropState) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 4.dp)
    ) {

        when (val shape = state.kropShape) {

            is ImageShape.None, is ImageShape.Circle, is ImageShape.Triangle -> Text(
                text = "Customization not available for this shape.",
                style = MaterialTheme.typography.bodyMedium
            )

            is ImageShape.Polygon -> {

                Text(
                    text = "Side Count:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Slider(
                    modifier = Modifier.fillMaxWidth(),
                    value = shape.sides.toFloat(),
                    valueRange = 4F..10F,
                    steps = 6,
                    onValueChange = { newValue ->

                        val newShape = shape.copy(sides = newValue.toInt().toShort())

                        state.apply {

                            updateShapeList(shape = newShape)
                            updateKropShape(shape = newShape)
                        }
                    }
                )
            }

            is ImageShape.Rectangle -> {

                Text(
                    text = "Corner Radius:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Slider(
                    modifier = Modifier.fillMaxWidth(),
                    value = shape.radius,
                    valueRange = 0.0F..0.75F,
                    steps = 15,
                    onValueChange = { newValue ->

                        val newShape = shape.copy(radius = newValue)

                        state.apply {

                            updateShapeList(shape = newShape)
                            updateKropShape(shape = newShape)
                        }
                    }
                )
            }

            is ImageShape.CutCorner -> {

                Text(
                    text = "Corner Radius:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Slider(
                    modifier = Modifier.fillMaxWidth(),
                    value = shape.radius,
                    valueRange = 0.0F..0.75F,
                    steps = 15,
                    onValueChange = { newValue ->

                        val newShape = shape.copy(radius = newValue)

                        state.apply {

                            updateShapeList(shape = newShape)
                            updateKropShape(shape = newShape)
                        }
                    }
                )
            }

            is ImageShape.Star -> {

                Text(
                    text = "Edge Count:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Slider(
                    modifier = Modifier.fillMaxWidth(),
                    value = shape.edges.toFloat(),
                    valueRange = 3F..40F,
                    steps = 37,
                    onValueChange = { newValue ->

                        val newShape = shape.copy(edges = newValue.toInt().toShort())

                        state.apply {

                            updateShapeList(shape = newShape)
                            updateKropShape(shape = newShape)
                        }
                    }
                )

                Text(
                    text = "Distance between inner & outer points:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Slider(
                    modifier = Modifier.fillMaxWidth(),
                    value = shape.distance,
                    valueRange = 0.0F..5.0F,
                    steps = 6,
                    onValueChange = { newValue ->

                        val newShape = shape.copy(distance = newValue)

                        state.apply {

                            updateShapeList(shape = newShape)
                            updateKropShape(shape = newShape)
                        }
                    }
                )
            }
        }
    }
}