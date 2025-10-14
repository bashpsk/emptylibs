package io.bashpsk.emptylibs.imagewallpaper.wallpaper

import android.app.WallpaperManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A composable function that displays a dialog to select the wallpaper type.
 *
 * This dialog allows the user to choose whether to set a given image as the home screen wallpaper,
 * the lock screen wallpaper, or both. It handles the wallpaper setting process in a coroutine
 * and displays a loading indicator while the operation is in progress. The dialog can be
 * dismissed via a close button, the back press, or by clicking the cancel button.
 *
 * The dialog's visibility is controlled by an external `MutableTransitionState`, allowing for
 * animated appearance and disappearance.
 *
 * @param dialogVisibleState A [MutableTransitionState] to control the visibility of the dialog.
 * @param imageBitmap The [ImageBitmap] to be set as the wallpaper.
 * @param containerColor The background color of the dialog container.
 * Defaults to `AlertDialogDefaults.containerColor`.
 */
@Composable
internal fun WallpaperTypeDialog(
    dialogVisibleState: MutableTransitionState<Boolean>,
    imageBitmap: ImageBitmap,
    containerColor: Color = AlertDialogDefaults.containerColor
) {

    val context = LocalContext.current
    val wallpaperManager = remember { WallpaperManager.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()

    var isWallpaperLoading by rememberSaveable { mutableStateOf(false) }

    AnimatedVisibility(
        visibleState = dialogVisibleState,
        enter = slideInVertically(),
        exit = slideOutHorizontally()
    ) {

        AlertDialog(
            modifier = Modifier.fillMaxWidth(fraction = 0.95F),
            onDismissRequest = {

                dialogVisibleState.targetState = false
            },
            properties = DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = true,
                usePlatformDefaultWidth = false
            ),
            shape = MaterialTheme.shapes.small,
            containerColor = containerColor,
            title = {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        modifier = Modifier.weight(weight = 1.0F),
                        text = "Set Wallpaper for",
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    IconButton(
                        onClick = {

                            dialogVisibleState.targetState = false
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

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(space = 4.dp)
                    ) {

                        Button(
                            modifier = Modifier.fillMaxWidth(fraction = 0.85F),
                            enabled = isWallpaperLoading.not(),
                            onClick = {

                                coroutineScope.launch(context = Dispatchers.IO) {

                                    wallpaperManager.setImageWallpaper(
                                        imageBitmap,
                                        WallpaperType.Home
                                    ).let { result ->

                                        isWallpaperLoading = false
                                        dialogVisibleState.targetState = false
                                    }
                                }
                            }
                        ) {

                            Text(
                                text = WallpaperType.Home.label,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            modifier = Modifier.fillMaxWidth(fraction = 0.85F),
                            enabled = isWallpaperLoading.not(),
                            onClick = {

                                coroutineScope.launch(context = Dispatchers.IO) {

                                    wallpaperManager.setImageWallpaper(
                                        imageBitmap,
                                        WallpaperType.Lock
                                    ).let { result ->

                                        isWallpaperLoading = false
                                        dialogVisibleState.targetState = false
                                    }
                                }
                            }
                        ) {

                            Text(
                                text = WallpaperType.Lock.label,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        FilledTonalButton(
                            modifier = Modifier.fillMaxWidth(fraction = 0.85F),
                            enabled = isWallpaperLoading.not(),
                            onClick = {

                                coroutineScope.launch(context = Dispatchers.IO) {

                                    wallpaperManager.setImageWallpaper(
                                        imageBitmap,
                                        WallpaperType.HomeAndLock
                                    ).let { result ->

                                        isWallpaperLoading = false
                                        dialogVisibleState.targetState = false
                                    }
                                }
                            }
                        ) {

                            Text(
                                text = WallpaperType.HomeAndLock.label,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        HorizontalDivider(modifier = Modifier.fillMaxWidth(fraction = 0.85F))

                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(fraction = 0.85F),
                            onClick = {

                                dialogVisibleState.targetState = false
                            }
                        ) {

                            Text(
                                text = "Cancel",
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = isWallpaperLoading,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {

                        CircularProgressIndicator()
                    }
                }
            },
            confirmButton = {}
        )
    }
}