package io.bashpsk.emptylibs.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import io.bashpsk.emptylibs.R
import io.bashpsk.emptylibs.composeutils.shape.PathShape
import io.bashpsk.emptylibs.composeutils.shape.toLabel
import io.bashpsk.emptylibs.imagekrop.crop.ImageKrop
import io.bashpsk.emptylibs.imagekrop.crop.ImageKropState
import io.bashpsk.emptylibs.imagekrop.crop.KropAspectRatio
import io.bashpsk.emptylibs.imagekrop.crop.KropConfig
import io.bashpsk.emptylibs.imagekrop.crop.KropShapeView
import io.bashpsk.emptylibs.imagekrop.crop.rememberImageKropState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ImageCropScreen() {

    val coroutineScope = rememberCoroutineScope()

    val imageBitmap = ImageBitmap.imageResource(R.drawable.wallpaper01)

    val handleColor = MaterialTheme.colorScheme.primary
    val targetColor = MaterialTheme.colorScheme.primaryContainer
    val borderColor = MaterialTheme.colorScheme.surface
    val overlayColor = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.5F)

    val kropConfig by remember(
        handleColor,
        targetColor,
        borderColor,
        overlayColor
    ) {
        derivedStateOf {
            KropConfig(
                minimumCropSize = 40.dp,
                handleColor = handleColor,
                targetColor = targetColor,
                borderColor = borderColor,
                overlayColor = overlayColor
            )
        }
    }

    val imageKropState = rememberImageKropState(imageBitmap = imageBitmap, config = kropConfig)
    val imagePreviewDialog = remember { MutableTransitionState(false) }
    val shapeCustomizeDialog = remember { MutableTransitionState(false) }

    var isImageEdit by rememberSaveable { mutableStateOf(false) }
    var modifiedImage by remember { mutableStateOf<ImageBitmap?>(imageBitmap) }
    var isImageModifying by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

            if (isImageEdit) ImageKropTopBar(
                undoButtonEnabled = imageKropState.undoButtonEnabled,
                onKropSave = {

                    coroutineScope.launch(Dispatchers.IO) {

                        isImageModifying = true
                        modifiedImage = imageKropState.onCroppedImageOrNull()
                        isImageModifying = false
                        isImageEdit = false
                    }
                },
                onUndoClick = imageKropState::removeLastImage,
                onPreviewClick = {

                    coroutineScope.launch(Dispatchers.IO) {

                        imagePreviewDialog.targetState = true
                        isImageModifying = true
                        modifiedImage = imageKropState.onCroppedPreviewImageOrNull()
                        isImageModifying = false
                    }
                },
                onNavigateBack = {

                    imageKropState.clearImageHistory()
                    isImageEdit = false
                }
            )
        },
        bottomBar = {

            if (isImageEdit) ImageKropBottomBar(
                state = imageKropState,
                shapeCustomizeDialog = shapeCustomizeDialog
            )
        }
    ) { innerPadding ->

        KropImagePreviewDialog(
            imagePreviewDialog = imagePreviewDialog,
            originalImage = imageKropState.originalImage,
            modifiedImage = modifiedImage,
            isImageModifying = isImageModifying
        )

        KropShapeCustomizationDialog(
            state = imageKropState,
            shapeCustomizeDialog = shapeCustomizeDialog
        )

        AnimatedVisibility(
            visible = isImageEdit,
            enter = slideInHorizontally() + fadeIn(),
            exit = slideOutHorizontally() + fadeOut()
        ) {

            ImageKrop(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                state = imageKropState
            )
        }

        AnimatedVisibility(
            visible = isImageEdit.not(),
            enter = slideInHorizontally() + fadeIn(),
            exit = slideOutHorizontally() + fadeOut()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                modifiedImage?.let { bitmap ->

                    Image(
                        modifier = Modifier.fillMaxWidth(),
                        bitmap = bitmap,
                        contentDescription = null
                    )
                } ?: Text("Image is Null!")

                Button(
                    shapes = ButtonDefaults.shapes(),
                    onClick = { isImageEdit = true }
                ) { Text("Edit Image") }
            }
        }
    }
}

@Composable
private fun ImageKropTopBar(
    undoButtonEnabled: Boolean,
    onKropSave: () -> Unit,
    onUndoClick: () -> Unit,
    onPreviewClick: () -> Unit,
    onNavigateBack: () -> Unit,
) {

    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        navigationIcon = {

            IconButton(
                shapes = IconButtonDefaults.shapes(),
                onClick = onNavigateBack
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
                shapes = IconButtonDefaults.shapes(),
                enabled = undoButtonEnabled,
                onClick = onUndoClick
            ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Image Crop Undo"
                )
            }

            IconButton(
                shapes = IconButtonDefaults.shapes(),
                onClick = onPreviewClick
            ) {

                Icon(
                    imageVector = Icons.Filled.Preview,
                    contentDescription = "Image Crop Preview"
                )
            }

            IconButton(
                shapes = IconButtonDefaults.shapes(),
                onClick = onKropSave
            ) {

                Icon(
                    imageVector = Icons.Filled.DoneAll,
                    contentDescription = "Image Crop Done"
                )
            }
        }
    )
}

@Composable
internal fun ImageKropBottomBar(
    state: ImageKropState,
    shapeCustomizeDialog: MutableTransitionState<Boolean>
) {

    val imagePreviewCoroutineScope = rememberCoroutineScope()

    var isAspectRatioMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var isShapeMenuExpanded by rememberSaveable { mutableStateOf(false) }

    val aspectLockIcon by remember(state) {
        derivedStateOf { if (state.isAspectLocked) Icons.Filled.Lock else Icons.Filled.LockOpen }
    }

    BottomAppBar(modifier = Modifier.fillMaxWidth()) {

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.SpaceAround,
            itemVerticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                shapes = IconButtonDefaults.shapes(),
                onClick = { imagePreviewCoroutineScope.launch { state.onImageFlipHorizontal() } }
            ) {

                Icon(
                    imageVector = Icons.Filled.Flip,
                    contentDescription = "Flip Horizontal"
                )
            }

            IconButton(
                shapes = IconButtonDefaults.shapes(),
                onClick = { imagePreviewCoroutineScope.launch { state.onImageFlipVertical() } }
            ) {

                Icon(
                    modifier = Modifier.rotate(degrees = 90.0F),
                    imageVector = Icons.Filled.Flip,
                    contentDescription = "Flip Vertical"
                )
            }

            IconButton(
                shapes = IconButtonDefaults.shapes(),
                onClick = { isAspectRatioMenuExpanded = true }
            ) {

                Icon(
                    imageVector = Icons.Filled.AspectRatio,
                    contentDescription = "Aspect Ratio"
                )

                DropdownMenu(
                    expanded = isAspectRatioMenuExpanded,
                    onDismissRequest = { isAspectRatioMenuExpanded = false }
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
                        onClick = { state.updateAspectLocked(locked = !state.isAspectLocked) }
                    )

                    KropAspectRatio.Basic.forEach { aspectRatio ->

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
                                isAspectRatioMenuExpanded = false
                            }
                        )
                    }
                }
            }

            IconButton(
                shapes = IconButtonDefaults.shapes(),
                onClick = { isShapeMenuExpanded = true }
            ) {

                Icon(
                    imageVector = Icons.Filled.StarBorder,
                    contentDescription = "Shape"
                )

                DropdownMenu(
                    expanded = isShapeMenuExpanded,
                    onDismissRequest = { isShapeMenuExpanded = false }
                ) {

                    state.shapeList.forEach { shape ->

                        val isSelected by remember(state, shape) {
                            derivedStateOf { state.kropShape == shape }
                        }

                        DropdownMenuItem(
                            modifier = Modifier.size(size = 44.dp),
                            text = { KropShapeView(kropShape = shape, isSelected = isSelected) },
                            onClick = {

                                state.updateKropShape(shape)
                                isShapeMenuExpanded = false
                            }
                        )
                    }
                }
            }

            IconButton(
                shapes = IconButtonDefaults.shapes(),
                onClick = { shapeCustomizeDialog.targetState = true }
            ) {

                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Shape Customization"
                )
            }
        }
    }
}

@Composable
private fun KropImagePreviewDialog(
    imagePreviewDialog: MutableTransitionState<Boolean>,
    originalImage: ImageBitmap,
    modifiedImage: ImageBitmap?,
    isImageModifying: Boolean,
) {

    var isOriginalImage by rememberSaveable { mutableStateOf(false) }

    val selectedImage by remember(isOriginalImage, originalImage, modifiedImage) {
        derivedStateOf { if (isOriginalImage) originalImage else modifiedImage }
    }

    val titleCardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.50F),
        contentColor = MaterialTheme.colorScheme.onSurface
    )

    AnimatedVisibility(
        visibleState = imagePreviewDialog,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {

        AlertDialog(
            modifier = Modifier.fillMaxWidth(),
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            ),
            onDismissRequest = { imagePreviewDialog.targetState = false },
            shape = MaterialTheme.shapes.extraSmall,
            title = {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        modifier = Modifier.weight(weight = 1.0F),
                        text = "Image Preview",
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    IconButton(
                        shapes = IconButtonDefaults.shapes(),
                        onClick = { imagePreviewDialog.targetState = false }
                    ) {

                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Dialog Close"
                        )
                    }
                }
            },
            text = {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.CenterVertically
                    )
                ) {

                    if (isImageModifying) CircularWavyProgressIndicator()

                    selectedImage?.let { bitmap ->

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            ImageCompareButton(
                                isOriginalImage = isOriginalImage,
                                onShowImageBitmap = { isVisible -> isOriginalImage = isVisible }
                            )
                        }

                        Box(
                            modifier = Modifier.weight(weight = 1.0F),
                            contentAlignment = Alignment.TopCenter
                        ) {

                            Image(
                                modifier = Modifier.fillMaxWidth(),
                                bitmap = bitmap,
                                contentDescription = null
                            )

                            Card(
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp),
                                colors = titleCardColors,
                                shape = MaterialTheme.shapes.extraSmall
                            ) {

                                Text(
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .padding(vertical = 4.dp, horizontal = 8.dp),
                                    text = if (isOriginalImage) "Original" else "Modified",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    } ?: Text(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        text = "Image Not Found",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            },
            confirmButton = {

                Button(
                    shapes = ButtonDefaults.shapes(),
                    onClick = { imagePreviewDialog.targetState = false }
                ) {

                    Icon(
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Dialog Dismiss"
                    )

                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))

                    Text(text = "Dismiss")
                }
            }
        )
    }
}

@Composable
private fun ImageCompareButton(
    isOriginalImage: Boolean,
    onShowImageBitmap: (isVisible: Boolean) -> Unit
) {

    Button(
        shapes = ButtonDefaults.shapes(),
        onClick = { onShowImageBitmap(!isOriginalImage) }
    ) {

        Icon(
            modifier = Modifier.size(ButtonDefaults.iconSizeFor(ButtonDefaults.MinHeight)),
            imageVector = Icons.Filled.Compare,
            contentDescription = "Compare Image"
        )

        Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(ButtonDefaults.MinHeight)))

        Text(
            text = if (isOriginalImage) "Show Cropped Image" else "Show Original Image",
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun KropShapeCustomizationDialog(
    shapeCustomizeDialog: MutableTransitionState<Boolean>,
    state: ImageKropState
) {

    AnimatedVisibility(
        visibleState = shapeCustomizeDialog,
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
            onDismissRequest = { shapeCustomizeDialog.targetState = false },
            shape = MaterialTheme.shapes.extraSmall,
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
                        shapes = IconButtonDefaults.shapes(),
                        onClick = { shapeCustomizeDialog.targetState = false }
                    ) {

                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Dialog Close"
                        )
                    }
                }
            },
            text = { KropShapeCustomization(state = state) },
            confirmButton = {

                Button(
                    shapes = ButtonDefaults.shapes(),
                    onClick = { shapeCustomizeDialog.targetState = false }
                ) {

                    Icon(
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Dialog Done"
                    )

                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))

                    Text(text = "Done")
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

            is PathShape.None, is PathShape.Circle, is PathShape.Triangle -> Text(
                text = "Customization not available for this shape.",
                style = MaterialTheme.typography.bodyMedium
            )

            is PathShape.Polygon -> {

                val sliderState = rememberSliderState(
                    value = shape.sides.toFloat(),
                    trackRange = 4F..10F,
                    steps = 6
                )

                Text(text = "Side Count:", style = MaterialTheme.typography.bodyMedium)

                Slider(
                    modifier = Modifier.fillMaxWidth(),
                    state = sliderState,
                    onValueChange = { newValue -> sliderState.value = newValue},
                    onValueChangeFinished = {

                        val newShape = shape.copy(sides = sliderState.value.toInt().toShort())

                        state.apply {

                            updateShapeList(shape = newShape)
                            updateKropShape(shape = newShape)
                        }
                    }
                )
            }

            is PathShape.Rectangle -> {

                val sliderState = rememberSliderState(
                    value = shape.radius,
                    trackRange = 0.0F..0.75F,
                    steps = 15
                )

                Text(text = "Corner Radius:", style = MaterialTheme.typography.bodyMedium)

                Slider(
                    modifier = Modifier.fillMaxWidth(),
                    state = sliderState,
                    onValueChange = { newValue -> sliderState.value = newValue},
                    onValueChangeFinished = {

                        val newShape = shape.copy(radius = sliderState.value)

                        state.apply {

                            updateShapeList(shape = newShape)
                            updateKropShape(shape = newShape)
                        }
                    }
                )
            }

            is PathShape.CutCorner -> {

                val sliderState = rememberSliderState(
                    value = shape.radius,
                    trackRange = 0.0F..0.75F,
                    steps = 15
                )

                Text(text = "Corner Radius:", style = MaterialTheme.typography.bodyMedium)

                Slider(
                    modifier = Modifier.fillMaxWidth(),
                    state = sliderState,
                    onValueChange = { newValue -> sliderState.value = newValue},
                    onValueChangeFinished = {

                        val newShape = shape.copy(radius = sliderState.value)

                        state.apply {

                            updateShapeList(shape = newShape)
                            updateKropShape(shape = newShape)
                        }
                    }
                )
            }

            is PathShape.Star -> {

                val edgeCountSliderState = rememberSliderState(
                    value = shape.edges.toFloat(),
                    trackRange = 5F..30F,
                    steps = 24
                )

                val distanceSliderState = rememberSliderState(
                    value = shape.distance,
                    trackRange = 0.0F..5.0F,
                    steps = 6
                )

                Text(text = "Edge Count:", style = MaterialTheme.typography.bodyMedium)

                Slider(
                    modifier = Modifier.fillMaxWidth(),
                    state = edgeCountSliderState,
                    onValueChange = { newValue -> edgeCountSliderState.value = newValue},
                    onValueChangeFinished = {

                        val newShape = shape.copy(edges = edgeCountSliderState.value.toInt())

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
                    state = distanceSliderState,
                    onValueChange = { newValue -> distanceSliderState.value = newValue},
                    onValueChangeFinished = {

                        val newShape = shape.copy(distance = distanceSliderState.value)

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