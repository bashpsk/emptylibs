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
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import io.bashpsk.emptylibs.formatter.format.EmptyFormat
import io.bashpsk.emptylibs.imageedit.utils.LOG_TAG
import io.bashpsk.emptylibs.imageutils.extension.fittedImageSize
import io.bashpsk.emptylibs.imageutils.extension.sameAs
import io.bashpsk.emptylibs.imageutils.extension.toSize
import io.bashpsk.emptylibs.imageutils.shape.BasicImageShapes
import io.bashpsk.emptylibs.imageutils.shape.ImageShape
import io.bashpsk.emptylibs.imageutils.shape.toLabel
import io.bashpsk.emptylibs.kolorpicker.color.ColorPickerDialog
import io.bashpsk.emptylibs.kolorpicker.color.ColorPreview
import io.bashpsk.emptylibs.kolorpicker.color.rememberColorPickerState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

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

            SmoothnessSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                smoothness = state.brushEditInput.smoothness,
                onSmoothnessChange = { newSmoothness ->

                    state.apply {

                        brushEditInput = brushEditInput.copy(smoothness = newSmoothness)
                    }
                }
            )
        }

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
                thickness = state.brushEditInput.thickness,
                onThicknessChange = { newThickness ->

                    state.apply {

                        brushEditInput = brushEditInput.copy(thickness = newThickness)
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
                miter = state.brushEditInput.miter,
                onMiterChange = { newMiter ->

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

@Composable
private fun InputEditErase(state: ImageEditState) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        item {

            SmoothnessSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                smoothness = state.eraseEditInput.smoothness,
                onSmoothnessChange = { newSmoothness ->

                    state.apply {

                        eraseEditInput = eraseEditInput.copy(smoothness = newSmoothness)
                    }
                }
            )
        }

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
                thickness = state.eraseEditInput.thickness,
                onThicknessChange = { newThickness ->

                    state.apply {

                        eraseEditInput = eraseEditInput.copy(thickness = newThickness)
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
                miter = state.eraseEditInput.miter,
                onMiterChange = { newMiter ->

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
                            size = sizeOfItem,
                            position = positionOfItem
                        )
                    }
                }
            )
        }

        item {

            HorizontalDivider()
        }

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

        item {

            HorizontalDivider()
        }

        item {

            BorderSelectionView(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 100),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                border = state.imageEditInput.border,
                onBorderChange = { newBorder ->

                    state.apply {

                        imageEditInput = imageEditInput.copy(border = newBorder)
                    }
                },
                borderColor = state.imageEditInput.borderColor,
                onBorderColorChange = { newColor ->

                    state.apply {

                        imageEditInput = imageEditInput.copy(borderColor = newColor)
                    }
                }
            )
        }

        item {

            HorizontalDivider()
        }

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
                imageShapeList = BasicImageShapes,
                onShapeChange = { newShape ->

                    state.apply {

                        imageEditInput = imageEditInput.copy(shape = newShape)
                    }
                }
            )
        }
    }
}

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

        item { HorizontalDivider() }

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
                imageShape = state.shapeEditInput.shape,
                imageShapeList = BasicImageShapes,
                onShapeChange = { newShape ->

                    state.apply {

                        shapeEditInput = shapeEditInput.copy(shape = newShape)
                    }
                }
            )
        }
    }
}

@Composable
private fun InputEditText(state: ImageEditState) {

}

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

@Composable
private fun ColorSelectionView(
    modifier: Modifier = Modifier,
    label: String,
    color: Color,
    onColorChange: (color: Color) -> Unit
) {

    val colorPickerState = rememberColorPickerState(initialColor = color, enableAlphaPanel = true)
    val colorPickerDialogState = remember { MutableTransitionState(false) }

    ColorPickerDialog(
        dialogVisibleState = colorPickerDialogState,
        state = colorPickerState,
        enableCopyButtons = true,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShapeSelectionView(
    modifier: Modifier = Modifier,
    imageShape: ImageShape,
    imageShapeList: ImmutableList<ImageShape>,
    onShapeChange: (shape: ImageShape) -> Unit
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
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
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

        ThicknessSelectionView(
            modifier = Modifier.fillMaxWidth(),
            thickness = border,
            onThicknessChange = onBorderChange
        )

        ColorSelectionView(
            modifier = Modifier.fillMaxWidth(),
            label = "Border Color",
            color = borderColor,
            onColorChange = onBorderColorChange
        )
    }
}

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

@Composable
private fun SmoothnessSelectionView(
    modifier: Modifier = Modifier,
    smoothness: Int,
    onSmoothnessChange: (smoothness: Int) -> Unit
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Smoothness - $smoothness",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Slider(
            value = smoothness.toFloat(),
            valueRange = 0.0F..10.0F,
            steps = 9,
            onValueChange = { newValue ->

                onSmoothnessChange(newValue.toInt())
            }
        )
    }
}

@Composable
private fun ThicknessSelectionView(
    modifier: Modifier = Modifier,
    thickness: Float,
    onThicknessChange: (thickness: Float) -> Unit
) {

    val selectedThickness by remember(thickness) {
        derivedStateOf {
            "Thickness - ${EmptyFormat.toRoundedDecimal(decimal = thickness, fraction = 1)} Px"
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = selectedThickness,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Slider(
            value = thickness,
            valueRange = 0.1F..64.0F,
            onValueChange = onThicknessChange
        )
    }
}

@Composable
private fun MiterSelectionView(
    modifier: Modifier = Modifier,
    miter: Float,
    onMiterChange: (miter: Float) -> Unit
) {

    val selectedMiter by remember(miter) {
        derivedStateOf { "Miter - ${EmptyFormat.toRoundedDecimal(decimal = miter, fraction = 1)}" }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = selectedMiter,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Slider(
            value = miter,
            valueRange = 0.3F..20.0F,
            onValueChange = onMiterChange
        )
    }
}

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
        derivedStateOf {
            "Interval OFF Size - ${
                EmptyFormat.toRoundedDecimal(decimal = intervalOff, fraction = 1)
            } Px"
        }
    }

    val intervalOnLabel by remember(intervalOn) {
        derivedStateOf {
            "Interval ON Size - ${
                EmptyFormat.toRoundedDecimal(decimal = intervalOn, fraction = 1)
            } Px"
        }
    }

    val intervalPhaseLabel by remember(intervalPhase) {
        derivedStateOf {
            "Interval Phase Size - ${
                EmptyFormat.toRoundedDecimal(decimal = intervalPhase, fraction = 1)
            } Px"
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

@Composable
private fun EditShapeView(shape: ImageShape, isSelected: Boolean) {

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