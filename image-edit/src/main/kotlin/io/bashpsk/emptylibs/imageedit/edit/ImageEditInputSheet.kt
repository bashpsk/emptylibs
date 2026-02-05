package io.bashpsk.emptylibs.imageedit.edit

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import io.bashpsk.emptylibs.composeutils.shape.BasicPathShapes
import io.bashpsk.emptylibs.composeutils.shape.PathShape
import io.bashpsk.emptylibs.composeutils.shape.toLabel
import io.bashpsk.emptylibs.formatter.format.toRoundedDecimal
import io.bashpsk.emptylibs.imageedit.utils.LOG_TAG
import io.bashpsk.emptylibs.imageutils.extension.fittedImageSize
import io.bashpsk.emptylibs.imageutils.extension.sameAs
import io.bashpsk.emptylibs.imageutils.extension.toSize
import io.bashpsk.emptylibs.kolorpicker.color.ColorPreview
import io.bashpsk.emptylibs.kolorpicker.color.KolorPickerDialog
import io.bashpsk.emptylibs.kolorpicker.color.rememberKolorPickerState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

/**
 * Composable function that displays a modal bottom sheet for editing image elements.
 * The content of the bottom sheet changes based on the currently selected image edit item.
 *
 * @param editToolInputSheetState The state of the modal bottom sheet, controlling its visibility.
 * @param state The current state of the image editor, containing information about the
 * selected item and its properties.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ImageEditInputSheet(
    editToolInputSheetState: SheetState,
    state: ImageEditState
) {

    val sheetCoroutineScope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = editToolInputSheetState.isVisible,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {

        ModalBottomSheet(
            modifier = Modifier.fillMaxSize(),
            sheetState = editToolInputSheetState,
            onDismissRequest = {

                sheetCoroutineScope.launch { editToolInputSheetState.hide() }
            },
            shape = MaterialTheme.shapes.small
        ) {

            when (state.currentImageEditItem) {

                is ImageEditItems.BrushItem -> InputEditBrush(state = state)
                is ImageEditItems.EraseItem -> InputEditErase(state = state)
                is ImageEditItems.ImageItem -> InputEditImage(state = state)
                is ImageEditItems.ShapeItem -> InputEditShape(state = state)
                is ImageEditItems.TextItem -> InputEditText(state = state)
                null -> {}
            }
        }
    }
}

/**
 * Composable function to display and edit brush properties.
 *
 * This function renders a [LazyColumn] containing various input controls
 * for modifying the brush settings such as color, smoothness, thickness, miter,
 * dash path, and stroke style.
 *
 * @param state The [ImageEditState] containing the current brush properties and callbacks to update
 * them.
 */
@Composable
private fun InputEditBrush(state: ImageEditState) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        item {

            ColorSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                label = "Color:",
                color = state.brushEditInput.color,
                onColorChange = { newColor ->

                    state.apply {

                        brushEditInput = brushEditInput.copy(color = newColor)
                    }
                }
            )
        }

        item { HorizontalDivider() }

        item {

            SliderSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                label = "Smoothness",
                value = state.brushEditInput.smoothness.toFloat(),
                valueRange = 0.0F..10.0F,
                onValueChange = { newSmoothness ->

                    state.apply {

                        brushEditInput = brushEditInput.copy(smoothness = newSmoothness.toInt())
                    }
                }
            )
        }

        item {

            SliderSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                label = "Thickness",
                value = state.brushEditInput.thickness,
                valueRange = 0.3F..64.0F,
                onValueChange = { newThickness ->

                    state.apply {

                        brushEditInput = brushEditInput.copy(thickness = newThickness)
                    }
                }
            )
        }

        item {

            SliderSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                label = "Miter",
                value = state.brushEditInput.miter,
                valueRange = 0.3F..20.0F,
                onValueChange = { newMiter ->

                    state.apply {

                        brushEditInput = brushEditInput.copy(miter = newMiter)
                    }
                }
            )
        }

        item { HorizontalDivider() }

        item {

            DashPathSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                intervalOff = state.brushEditInput.dashIntervalOff,
                intervalOn = state.brushEditInput.dashIntervalOn,
                intervalPhase = state.brushEditInput.dashPhase,
                onIntervalOffChange = { newInterval ->

                    state.apply {

                        brushEditInput = brushEditInput.copy(dashIntervalOff = newInterval)
                    }
                },
                onIntervalOnChange = { newInterval ->

                    state.apply {

                        brushEditInput = brushEditInput.copy(dashIntervalOn = newInterval)
                    }
                },
                onIntervalPhaseChange = { newInterval ->

                    state.apply {

                        brushEditInput = brushEditInput.copy(dashPhase = newInterval)
                    }
                }
            )
        }

        item { HorizontalDivider() }

        item {

            StrokeSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                selectedStrokeCap = state.brushEditInput.strokeCap,
                selectedStrokeJoin = state.brushEditInput.strokeJoin,
                onStrokeCapChange = { newStrokeCap ->

                    state.apply {

                        brushEditInput = brushEditInput.copy(strokeCap = newStrokeCap)
                    }
                },
                onStrokeJoinChange = { newStrokeJoin ->

                    state.apply {

                        brushEditInput = brushEditInput.copy(strokeJoin = newStrokeJoin)
                    }
                }
            )
        }
    }
}

/**
 * Composable function to display and manage input controls for the erase tool.
 *
 * This function renders a [LazyColumn] containing various input controls that allow the user
 * to configure the properties of the erase tool, such as smoothness, thickness, miter,
 * dash path effect, and stroke cap/join.
 *
 * @param state The [ImageEditState] that holds the current state of the image editor, including the
 * erase tool's input parameters.
 */
@Composable
private fun InputEditErase(state: ImageEditState) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        item {

            SliderSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                label = "Smoothness",
                value = state.eraseEditInput.smoothness.toFloat(),
                valueRange = 0.0F..10.0F,
                onValueChange = { newSmoothness ->

                    state.apply {

                        eraseEditInput = eraseEditInput.copy(smoothness = newSmoothness.toInt())
                    }
                }
            )
        }

        item {

            SliderSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                label = "Thickness",
                value = state.eraseEditInput.thickness,
                valueRange = 0.1F..64.0F,
                onValueChange = { newThickness ->

                    state.apply {

                        eraseEditInput = eraseEditInput.copy(thickness = newThickness)
                    }
                }
            )
        }

        item {

            SliderSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                label = "Miter",
                value = state.eraseEditInput.miter,
                valueRange = 0.3F..20.0F,
                onValueChange = { newMiter ->

                    state.apply {

                        eraseEditInput = eraseEditInput.copy(miter = newMiter)
                    }
                }
            )
        }

        item { HorizontalDivider() }

        item {

            DashPathSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                intervalOff = state.eraseEditInput.dashIntervalOff,
                intervalOn = state.eraseEditInput.dashIntervalOn,
                intervalPhase = state.eraseEditInput.dashPhase,
                onIntervalOffChange = { newInterval ->

                    state.apply {

                        eraseEditInput = eraseEditInput.copy(dashIntervalOff = newInterval)
                    }
                },
                onIntervalOnChange = { newInterval ->

                    state.apply {

                        eraseEditInput = eraseEditInput.copy(dashIntervalOn = newInterval)
                    }
                },
                onIntervalPhaseChange = { newInterval ->

                    state.apply {

                        eraseEditInput = eraseEditInput.copy(dashPhase = newInterval)
                    }
                }
            )
        }

        item { HorizontalDivider() }

        item {

            StrokeSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                selectedStrokeCap = state.eraseEditInput.strokeCap,
                selectedStrokeJoin = state.eraseEditInput.strokeJoin,
                onStrokeCapChange = { newStrokeCap ->

                    state.apply {

                        eraseEditInput = eraseEditInput.copy(strokeCap = newStrokeCap)
                    }
                },
                onStrokeJoinChange = { newStrokeJoin ->

                    state.apply {

                        eraseEditInput = eraseEditInput.copy(strokeJoin = newStrokeJoin)
                    }
                }
            )
        }
    }
}

/**
 * Composable function to display and edit image properties.
 *
 * This function provides UI elements for selecting a bitmap, adjusting its position and size,
 * and choosing a shape for the image. It uses [LazyColumn] to efficiently display the
 * different editing sections.
 *
 * @param state The [ImageEditState] that holds the current state of the image being edited.
 * This state is updated as the user interacts with the UI elements.
 */
@Composable
private fun InputEditImage(state: ImageEditState) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        item {

            BitmapSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                bitmap = state.imageEditInput.bitmap,
                onBitmapChange = { newBitmap ->

                    state.apply {

                        val sizeOfItem = imageEditInput.size.takeIf { size ->

                            size != Size.Unspecified
                        } ?: canvasSize.fittedImageSize(newBitmap.toSize())

                        val positionOfItem = imageEditInput.position.takeIf { position ->

                            position != Offset.Unspecified
                        } ?: Offset(
                            x = (canvasSize.width - sizeOfItem.width) / 2.0F,
                            y = (canvasSize.height - sizeOfItem.height) / 2.0F
                        )

                        imageEditInput = imageEditInput.copy(
                            bitmap = newBitmap,
                            position = positionOfItem,
                            size = sizeOfItem
                        )
                    }
                }
            )
        }

        item { HorizontalDivider() }

        item {

            PositionSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                position = state.imageEditInput.position,
                onPositionChange = { newPosition ->

                    state.apply {

                        imageEditInput = imageEditInput.copy(position = newPosition)
                    }
                }
            )
        }

        item {

            SizeSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                size = state.imageEditInput.size,
                onSizeChange = { newSize ->

                    state.apply {

                        imageEditInput = imageEditInput.copy(size = newSize)
                    }
                }
            )
        }

        item { HorizontalDivider() }

        item {

            ShapeSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                imageShape = state.imageEditInput.shape,
                imageShapeList = BasicPathShapes,
                onShapeChange = { newShape ->

                    state.apply {

                        imageEditInput = imageEditInput.copy(shape = newShape)
                    }
                }
            )
        }
    }
}

/**
 * Composable function to display and edit shape properties.
 *
 * This function provides a user interface for modifying various attributes of a shape,
 * such as its color, thickness, miter, dash pattern, stroke cap, stroke join, and the shape itself.
 * The changes are reflected in the [ImageEditState].
 *
 * @param state The current state of the image editor, containing the shape properties to be edited.
 */
@Composable
private fun InputEditShape(state: ImageEditState) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        item {

            ColorSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                label = "Color:",
                color = state.shapeEditInput.color,
                onColorChange = { newColor ->

                    state.apply {

                        shapeEditInput = shapeEditInput.copy(color = newColor)
                    }
                }
            )
        }

        /*item { HorizontalDivider() }

        item {

            ThicknessSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                thickness = state.shapeEditInput.thickness,
                onThicknessChange = { newThickness ->

                    state.apply {

                        shapeEditInput = shapeEditInput.copy(thickness = newThickness)
                    }
                }
            )
        }

        item {

            MiterSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                miter = state.shapeEditInput.miter,
                onMiterChange = { newMiter ->

                    state.apply {

                        shapeEditInput = shapeEditInput.copy(miter = newMiter)
                    }
                }
            )
        }

        item { HorizontalDivider() }

        item {

            DashPathSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                intervalOff = state.shapeEditInput.dashIntervalOff,
                intervalOn = state.shapeEditInput.dashIntervalOn,
                intervalPhase = state.shapeEditInput.dashPhase,
                onIntervalOffChange = { newInterval ->

                    state.apply {

                        shapeEditInput = shapeEditInput.copy(dashIntervalOff = newInterval)
                    }
                },
                onIntervalOnChange = { newInterval ->

                    state.apply {

                        shapeEditInput = shapeEditInput.copy(dashIntervalOn = newInterval)
                    }
                },
                onIntervalPhaseChange = { newInterval ->

                    state.apply {

                        shapeEditInput = shapeEditInput.copy(dashPhase = newInterval)
                    }
                }
            )
        }

        item { HorizontalDivider() }

        item {

            StrokeSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                selectedStrokeCap = state.shapeEditInput.strokeCap,
                selectedStrokeJoin = state.shapeEditInput.strokeJoin,
                onStrokeCapChange = { newStrokeCap ->

                    state.apply {

                        shapeEditInput = shapeEditInput.copy(strokeCap = newStrokeCap)
                    }
                },
                onStrokeJoinChange = { newStrokeJoin ->

                    state.apply {

                        shapeEditInput = shapeEditInput.copy(strokeJoin = newStrokeJoin)
                    }
                }
            )
        }*/

        item { HorizontalDivider() }

        item {

            ShapeSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                imageShape = state.shapeEditInput.shape,
                imageShapeList = BasicPathShapes,
                onShapeChange = { newShape ->

                    state.apply {

                        val sizeOfItem = shapeEditInput.size.takeIf { size ->

                            size != Size.Unspecified
                        } ?: canvasSize.fittedImageSize(
                            Size(canvasSize.width / 2.0F, canvasSize.width / 2.0F)
                        )

                        val positionOfItem = shapeEditInput.position.takeIf { position ->

                            position != Offset.Unspecified
                        } ?: Offset(
                            x = (canvasSize.width - sizeOfItem.width) / 2.0F,
                            y = (canvasSize.height - sizeOfItem.height) / 2.0F
                        )

                        shapeEditInput = shapeEditInput.copy(
                            shape = newShape,
                            position = positionOfItem,
                            size = sizeOfItem
                        )
                    }
                }
            )
        }
    }
}

/**
 * Composable function to display and edit text properties.
 * It uses a [LazyColumn] to display the text input field, color picker, font size slider, and a
 * preview of the text.
 *
 * @param state The current state of the image editor, containing the text input and style.
 */
@Composable
private fun InputEditText(state: ImageEditState) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        item {

            TextSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                text = state.textEditInput.content,
                style = state.textEditInput.style,
                onTextChange = { newContent ->

                    state.apply {

                        textEditInput = textEditInput.copy(content = newContent)
                    }
                },
                onStyleChange = { newStyle ->

                    state.apply {

                        textEditInput = textEditInput.copy(style = newStyle)
                    }
                }
            )
        }
    }
}

/**
 * A composable function that displays input fields for selecting the X and Y position.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param position The current position [Offset].
 * @param onPositionChange A callback function invoked when the position is changed.
 * It provides the new position as an [Offset].
 */
@Composable
private fun PositionSelectionView(
    modifier: Modifier = Modifier,
    position: Offset,
    onPositionChange: (newPosition: Offset) -> Unit
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Position:",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            NumberInputField(
                modifier = Modifier.weight(weight = 1.0F),
                label = "X",
                value = position.x.takeIf { number -> number > 0.0F }?.toString() ?: "",
                onValueChange = { newValue ->

                    onPositionChange(position.copy(x = newValue.toFloatOrNull() ?: 0.0F))
                }
            )

            NumberInputField(
                modifier = Modifier.weight(weight = 1.0F),
                label = "Y",
                value = position.y.takeIf { number -> number > 0.0F }?.toString() ?: "",
                onValueChange = { newValue ->

                    onPositionChange(position.copy(y = newValue.toFloatOrNull() ?: 0.0F))
                }
            )
        }
    }
}

/**
 * Composable function to display and edit the size (width and height) of an item.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param size The current size of the item.
 * @param onSizeChange A callback function invoked when the size is changed by the user.
 * It provides the new [Size] object.
 */
@Composable
private fun SizeSelectionView(
    modifier: Modifier = Modifier,
    size: Size,
    onSizeChange: (newSize: Size) -> Unit
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Size:",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            NumberInputField(
                modifier = Modifier.weight(weight = 1.0F),
                label = "Width",
                value = size.width.takeIf { number -> number > 0.0F }?.toString() ?: "",
                onValueChange = { newValue ->

                    onSizeChange(size.copy(width = newValue.toFloatOrNull() ?: 0.0F))
                }
            )

            NumberInputField(
                modifier = Modifier.weight(weight = 1.0F),
                label = "Height",
                value = size.height.takeIf { number -> number > 0.0F }?.toString() ?: "",
                onValueChange = { newValue ->

                    onSizeChange(size.copy(height = newValue.toFloatOrNull() ?: 0.0F))
                }
            )
        }
    }
}

/**
 * A composable function that displays a color selection view.
 * It shows a label, a preview of the selected color, and opens a color picker dialog when clicked.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param label The text label to display above the color preview.
 * @param color The currently selected color.
 * @param onColorChange A callback function that is invoked when a new color is selected from the
 * color picker dialog.
 */
@Composable
private fun ColorSelectionView(
    modifier: Modifier = Modifier,
    label: String,
    color: Color,
    onColorChange: (color: Color) -> Unit
) {

    val colorPickerState = rememberKolorPickerState(initialColor = color)
    val colorPickerDialogState = remember { MutableTransitionState(false) }

    KolorPickerDialog(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding(),
        dialogVisibleState = colorPickerDialogState,
        state = colorPickerState,
        enableAlphaPanel = true,
        enableCopyButton = true,
        onSelectedColor = onColorChange
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = label,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        ColorPreview(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = MaterialTheme.shapes.extraSmall)
                .clickable(
                    role = Role.Button,
                    onClick = { colorPickerDialogState.targetState = true }
                ),
            color = color
        )
    }
}

/**
 * Composable function for selecting an image shape from a dropdown menu.
 *
 * @param modifier Modifier for styling the view.
 * @param imageShape The currently selected image shape.
 * @param imageShapeList A list of available image shapes.
 * @param onShapeChange Callback function invoked when a new shape is selected.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShapeSelectionView(
    modifier: Modifier = Modifier,
    imageShape: PathShape,
    imageShapeList: ImmutableList<PathShape>,
    onShapeChange: (shape: PathShape) -> Unit
) {

    var isShapeMenuExpanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Shape:",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        ExposedDropdownMenuBox(
            expanded = isShapeMenuExpanded,
            onExpandedChange = { isVisible ->

                isShapeMenuExpanded = isVisible
            }
        ) {

            OutlinedTextField(
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                value = imageShape.toLabel(),
                onValueChange = {},
                readOnly = true,
                label = {

                    Text(text = "Shape")
                },
                trailingIcon = {

                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isShapeMenuExpanded)
                }
            )

            ExposedDropdownMenu(
                expanded = isShapeMenuExpanded,
                onDismissRequest = {

                    isShapeMenuExpanded = false
                }
            ) {

                imageShapeList.forEach { shape ->

                    val isSelected by remember(shape, imageShape) {
                        derivedStateOf { shape == imageShape }
                    }

                    DropdownMenuItem(
                        text = {

                            Text(
                                text = shape.toLabel(),
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        leadingIcon = {

                            EditShapeView(shape = shape, isSelected = isSelected)
                        },
                        trailingIcon = {

                            AnimatedVisibility(
                                visible = isSelected,
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut()
                            ) {

                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Selected"
                                )
                            }
                        },
                        onClick = {

                            onShapeChange(shape)
                            isShapeMenuExpanded = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * Composable function to display a view for selecting text content, color, and font size.
 *
 * @param modifier Modifier for styling and layout.
 * @param text The current text content.
 * @param style The current text style.
 * @param onTextChange Callback function for when the text content changes.
 * @param onStyleChange Callback function for when the text style changes.
 */
@Composable
private fun TextSelectionView(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle,
    onTextChange: (text: String) -> Unit,
    onStyleChange: (style: TextStyle) -> Unit
) {

    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Text:",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            onValueChange = onTextChange,
            singleLine = true,
            label = {

                Text(text = "Text Input")
            },
            placeholder = {

                Text(text = "Text Input")
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {

                    focusManager.clearFocus()
                }
            )
        )

        HorizontalDivider()

        ColorSelectionView(
            modifier = Modifier.fillMaxWidth(),
            label = "Text Color",
            color = style.color,
            onColorChange = { newColor ->

                onStyleChange(style.copy(color = newColor))
            }
        )

        HorizontalDivider()

        SliderSelectionView(
            modifier = Modifier.fillMaxWidth(),
            label = "Font Size",
            value = style.fontSize.value,
            valueRange = 2.0F..64.0F,
            onValueChange = { newSize ->

                onStyleChange(style.copy(fontSize = newSize.sp))
            }
        )

        HorizontalDivider()

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Preview:",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            textAlign = TextAlign.Center,
            style = style,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * A composable function that displays a border selection view.
 *
 * This view allows the user to select the thickness and color of a border.
 *
 * @param modifier The modifier to be applied to the view.
 * @param border The current border thickness.
 * @param onBorderChange A callback function that is invoked when the border thickness changes.
 * @param borderColor The current border color.
 * @param onBorderColorChange A callback function that is invoked when the border color changes.
 */
@Composable
private fun BorderSelectionView(
    modifier: Modifier = Modifier,
    border: Float,
    onBorderChange: (border: Float) -> Unit,
    borderColor: Color,
    onBorderColorChange: (color: Color) -> Unit
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Border:",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        SliderSelectionView(
            modifier = Modifier.fillMaxWidth(),
            label = "Thickness",
            value = border,
            valueRange = 0.1F..64.0F,
            onValueChange = onBorderChange
        )

        ColorSelectionView(
            modifier = Modifier.fillMaxWidth(),
            label = "Border Color",
            color = borderColor,
            onColorChange = onBorderColorChange
        )
    }
}

/**
 * Composable function to display an image selection view.
 * It allows the user to pick an image from the device's storage and displays a preview of the
 * selected image.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param bitmap The current selected [ImageBitmap] to display.
 * @param onBitmapChange A callback function that is invoked when a new bitmap is selected or
 * changed.
 */
@Composable
private fun BitmapSelectionView(
    modifier: Modifier = Modifier,
    bitmap: ImageBitmap?,
    onBitmapChange: (bitmap: ImageBitmap) -> Unit
) {

    val context = LocalContext.current

    var selectedImageUri by rememberSaveable { mutableStateOf("") }

    val imagePickLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->

        selectedImageUri = uri.takeIf { uri -> uri != null }?.toString() ?: ""
    }

    val imageViewModifier = Modifier
        .size(size = 100.dp)
        .clip(shape = MaterialTheme.shapes.extraSmall)
        .clickable(role = Role.Image, onClick = { imagePickLauncher.launch("image/*") })

    LaunchedEffect(selectedImageUri) {

        selectedImageUri.takeIf { uri -> uri.isNotEmpty() }?.let { uri ->

            try {

                val imageRequest = ImageRequest.Builder(context)
                    .data(uri)
                    .allowHardware(false)
                    .build()

                context.imageLoader.execute(request = imageRequest).let { result ->

                    result.image?.toBitmap()?.asImageBitmap()?.takeIf { imageBitmap ->

                        bitmap?.let { imageBitmap.sameAs(it) } == false || bitmap == null
                    }?.let { imageBitmap -> onBitmapChange(imageBitmap) }
                }
            } catch (exception: Exception) {

                ensureActive()
                Log.w(LOG_TAG, exception.message, exception)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Image:",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        bitmap?.let { imageBitmap ->

            Image(
                modifier = Modifier.then(imageViewModifier),
                bitmap = imageBitmap,
                contentScale = ContentScale.Fit,
                contentDescription = "Selected Image"
            )
        } ?: run {

            Icon(
                modifier = Modifier.then(imageViewModifier),
                imageVector = Icons.Filled.AddPhotoAlternate,
                contentDescription = "Select Image"
            )
        }
    }
}

/**
 * A Composable function that displays a slider for selecting a float value within a given range.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param label The label to be displayed above the slider.
 * @param value The current value of the slider.
 * @param valueRange The range of values that the slider can take.
 * @param steps The number of discrete steps that the slider can take.
 * Defaults to 0, which means continuous.
 * @param onValueChange A callback that is invoked when the value of the slider changes.
 */
@Composable
private fun SliderSelectionView(
    modifier: Modifier = Modifier,
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    onValueChange: (value: Float) -> Unit
) {

    val selectedValue by remember(value) {
        derivedStateOf { "$label - ${value.toRoundedDecimal(fraction = 1)}" }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = selectedValue,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            valueRange = valueRange,
            steps = steps,
            onValueChange = onValueChange
        )
    }
}

/**
 * Composable function for selecting stroke cap and stroke join.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param selectedStrokeCap The currently selected stroke cap.
 * @param selectedStrokeJoin The currently selected stroke join.
 * @param onStrokeCapChange Callback function invoked when the stroke cap changes.
 * @param onStrokeJoinChange Callback function invoked when the stroke join changes.
 */
@Composable
private fun StrokeSelectionView(
    modifier: Modifier = Modifier,
    selectedStrokeCap: StrokeCap,
    selectedStrokeJoin: StrokeJoin,
    onStrokeCapChange: (stroke: StrokeCap) -> Unit,
    onStrokeJoinChange: (stroke: StrokeJoin) -> Unit
) {

    val strokeCapList = persistentListOf(StrokeCap.Round, StrokeCap.Butt, StrokeCap.Square)
    val strokeJoinList = persistentListOf(StrokeJoin.Round, StrokeJoin.Bevel, StrokeJoin.Miter)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Stroke Cap:",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        strokeCapList.forEach { strokeCap ->

            val isSelected by remember(selectedStrokeCap, strokeCap) {
                derivedStateOf { selectedStrokeCap == strokeCap }
            }

            StrokeView(
                stroke = strokeCap,
                isSelected = isSelected,
                onClick = onStrokeCapChange
            )
        }

        HorizontalDivider()

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Stroke Join:",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        strokeJoinList.forEach { strokeJoin ->

            val isSelected by remember(selectedStrokeJoin, strokeJoin) {
                derivedStateOf { selectedStrokeJoin == strokeJoin }
            }

            StrokeView(
                stroke = strokeJoin,
                isSelected = isSelected,
                onClick = onStrokeJoinChange
            )
        }
    }
}

/**
 * A composable view for selecting dash path effect properties like interval on, interval off, and
 * phase.
 * It provides sliders for users to adjust these values.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param intervalOff The current "off" interval value of the dash effect.
 * @param intervalOn The current "on" interval value of the dash effect.
 * @param intervalPhase The current phase value of the dash effect.
 * @param onIntervalOffChange A callback function invoked when the "off" interval value changes.
 * @param onIntervalOnChange A callback function invoked when the "on" interval value changes.
 * @param onIntervalPhaseChange A callback function invoked when the phase value changes.
 */
@Composable
private fun DashPathSelectionView(
    modifier: Modifier = Modifier,
    intervalOff: Float,
    intervalOn: Float,
    intervalPhase: Float,
    onIntervalOffChange: (newInterval: Float) -> Unit,
    onIntervalOnChange: (newInterval: Float) -> Unit,
    onIntervalPhaseChange: (newInterval: Float) -> Unit
) {

    val intervalOffLabel by remember(intervalOff) {
        derivedStateOf { "Interval OFF Size - ${intervalOff.toRoundedDecimal(fraction = 1)} Px" }
    }

    val intervalOnLabel by remember(intervalOn) {
        derivedStateOf { "Interval ON Size - ${intervalOn.toRoundedDecimal(fraction = 1)} Px" }
    }

    val intervalPhaseLabel by remember(intervalPhase) {
        derivedStateOf {
            "Interval Phase Size - ${intervalPhase.toRoundedDecimal(fraction = 1)} Px"
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Dash Effect:",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = intervalOffLabel,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyMedium,
        )

        Slider(
            value = intervalOff,
            valueRange = 0.0F..100.0F,
            onValueChange = onIntervalOffChange
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = intervalOnLabel,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyMedium,
        )

        Slider(
            value = intervalOn,
            valueRange = 0.0F..100.0F,
            onValueChange = onIntervalOnChange
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = intervalPhaseLabel,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyMedium,
        )

        Slider(
            value = intervalPhase,
            valueRange = 0.0F..100.0F,
            onValueChange = onIntervalPhaseChange
        )
    }
}

/**
 * Composable function to display a single stroke cap option.
 *
 * @param modifier Modifier for styling the layout.
 * @param stroke The [StrokeCap] to display.
 * @param isSelected Boolean indicating if this stroke cap is currently selected.
 * @param onClick Callback invoked when this stroke cap is clicked.
 */
@Composable
private fun StrokeView(
    modifier: Modifier = Modifier,
    stroke: StrokeCap,
    isSelected: Boolean,
    onClick: (stroke: StrokeCap) -> Unit
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.extraSmall)
            .selectable(
                selected = isSelected,
                role = Role.RadioButton,
                onClick = {

                    onClick(stroke)
                }
            )
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        RadioButton(selected = isSelected, onClick = null)

        Text(
            modifier = Modifier.weight(weight = 1.0F),
            text = stroke.toString(),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Composable function to display a selectable stroke join option.
 *
 * @param modifier Modifier for styling the layout.
 * @param stroke The [StrokeJoin] to display.
 * @param isSelected Boolean indicating if this stroke join is currently selected.
 * @param onClick Lambda function to be invoked when this stroke join option is clicked.
 */
@Composable
private fun StrokeView(
    modifier: Modifier = Modifier,
    stroke: StrokeJoin,
    isSelected: Boolean,
    onClick: (stroke: StrokeJoin) -> Unit
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.extraSmall)
            .selectable(
                selected = isSelected,
                role = Role.RadioButton,
                onClick = {

                    onClick(stroke)
                }
            )
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        RadioButton(selected = isSelected, onClick = null)

        Text(
            modifier = Modifier.weight(weight = 1.0F),
            text = stroke.toString(),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * A Composable function that displays an outlined text field for number input.
 * It uses [OutlinedTextField] and sets the keyboard type to [KeyboardType.Number].
 *
 * @param modifier The modifier to be applied to the text field.
 * @param label The label to be displayed for the text field.
 * @param value The current value of the text field.
 * @param onValueChange A callback that is invoked when the value of the text field changes.
 */
@Composable
private fun NumberInputField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (newValue: String) -> Unit
) {

    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = {

            Text(text = label)
        },
        placeholder = {

            Text(text = label)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        keyboardActions = KeyboardActions(
            onDone = {

                focusManager.clearFocus()
            }
        )
    )
}

/**
 * Composable function to display a preview of an editable shape.
 *
 * @param shape The [PathShape] to be displayed.
 * @param isSelected A boolean indicating whether the shape is currently selected.
 * The color of the shape will change based on this state.
 */
@Composable
private fun EditShapeView(shape: PathShape, isSelected: Boolean) {

    val unSelectedIconColor = MaterialTheme.colorScheme.onSurface
    val selectedIconColor = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.5F)

    val shapeColor by remember(isSelected, selectedIconColor, unSelectedIconColor) {
        derivedStateOf { if (isSelected) selectedIconColor else unSelectedIconColor }
    }

    Canvas(
        modifier = Modifier.size(size = 24.dp),
        contentDescription = "Shape Item"
    ) {

        drawEditShapePreview(shape = shape, color = shapeColor)
    }
}