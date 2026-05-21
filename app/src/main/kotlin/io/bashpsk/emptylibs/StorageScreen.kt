package io.bashpsk.emptylibs

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.bashpsk.emptylibs.formatter.format.toFileSize
import io.bashpsk.emptylibs.storage.storage.FileData
import io.bashpsk.emptylibs.storage.storage.FileType
import io.bashpsk.emptylibs.storage.storage.FileType.Companion.extension
import io.bashpsk.emptylibs.storage.storage.FileVisibleType
import io.bashpsk.emptylibs.storage.storage.StorageExt
import io.bashpsk.emptylibs.storage.storage.StorageVolumeData
import io.bashpsk.emptylibs.storage.storage.StorageVolumeType
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun StorageScreen() {

    val context = LocalContext.current
    val fileCoroutineScope = rememberCoroutineScope()

    val storageVolumeList by StorageExt.getStorageVolumeFlow(
        context = context
    ).collectAsStateWithLifecycle(initialValue = persistentListOf())

    var selectedVolumeData by remember { mutableStateOf<StorageVolumeData?>(null) }

    val dcimDirectory = remember {
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
    }

    val imageFileList by StorageExt.getFileListByExtensionsFlow(
        context = context,
        dcimDirectory?.path ?: "",
        extensions = FileType.Image.extension
    ).collectAsStateWithLifecycle(initialValue = persistentListOf())

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            items(
                items = storageVolumeList,
                key = { volumeData -> volumeData.uuid }
            ) { volumeData ->

                val isSelected by remember {
                    derivedStateOf { selectedVolumeData?.path == volumeData.path }
                }

                StorageVolumeSmall(
                    volumeData = { volumeData },
                    isSelected = { isSelected },
                    onStorageVolume = { volume ->

                        selectedVolumeData = volume
                        Log.d("StorageExt", volume.toString())

                        fileCoroutineScope.launch(Dispatchers.IO) {

                            StorageExt.getDirectoryFileFlow(
                                context = context,
                                path = volume.path
                            ).collectLatest { files ->

                                Log.d("StorageExt", files.toString())
                            }
                        }
                    }
                )
            }

            item {

                FlowRow {

                    Button(
                        onClick = {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                                Intent(
                                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                                ).apply {

                                    data = ("package:" + context.packageName).toUri()
                                }.let(context::startActivity)
                            }
                        }
                    ) {

                        Text("MANAGE PERMISSION")
                    }

                    Button(
                        onClick = {

                            selectedVolumeData?.let { volume ->

                                fileCoroutineScope.launch(Dispatchers.IO) {

                                    StorageExt.makeFolderOrFile(
                                        parentPath = volume.path,
                                        name = "Folder One",
                                        isFolder = true,
                                        visibleType = FileVisibleType.Public
                                    ).let { result ->

                                        Log.d("StorageExt", result.toString())
                                    }
                                }
                            }
                        }
                    ) {

                        Text("Make Folder as Public")
                    }

                    Button(
                        onClick = {

                            selectedVolumeData?.let { volume ->

                                fileCoroutineScope.launch(Dispatchers.IO) {

                                    StorageExt.makeFolderOrFile(
                                        parentPath = volume.path,
                                        name = "Folder Two",
                                        isFolder = true,
                                        visibleType = FileVisibleType.Hidden
                                    ).let { result ->

                                        Log.d("StorageExt", result.toString())
                                    }
                                }
                            }
                        }
                    ) {

                        Text("Make Folder as Hidden")
                    }

                    Button(
                        onClick = {

                            selectedVolumeData?.let { volume ->

                                fileCoroutineScope.launch(Dispatchers.IO) {

                                    StorageExt.makeFolderOrFile(
                                        parentPath = volume.path,
                                        name = "File One.txt",
                                        isFolder = false,
                                        visibleType = FileVisibleType.Public
                                    ).let { result ->

                                        Log.d("StorageExt", result.toString())
                                    }
                                }
                            }
                        }
                    ) {

                        Text("Make File as Public")
                    }

                    Button(
                        onClick = {

                            selectedVolumeData?.let { volume ->

                                fileCoroutineScope.launch(Dispatchers.IO) {

                                    StorageExt.makeFolderOrFile(
                                        parentPath = volume.path,
                                        name = "File Two.txt",
                                        isFolder = false,
                                        visibleType = FileVisibleType.Hidden
                                    ).let { result ->

                                        Log.d("StorageExt", result.toString())
                                    }
                                }
                            }
                        }
                    ) {

                        Text("Make File as Hidden")
                    }
                }
            }

            items(
                items = imageFileList,
                key = { fileData -> fileData.path }
            ) { fileData ->

                FileView(fileData = fileData)
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
private fun StorageVolumeSmall(
    volumeData: () -> StorageVolumeData,
    isSelected: () -> Boolean,
    onStorageVolume: (volume: StorageVolumeData) -> Unit = {}
) {

    val context = LocalContext.current

    val volumePercentage by remember {
        derivedStateOf { volumeData().usedSize.toFloat() / volumeData().totalSize }
    }

    val defaultContainerColor = MaterialTheme.colorScheme.primary

    val containerColors = mutableStateListOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer
    )

    val containerColor = remember { containerColors.randomOrNull() ?: defaultContainerColor }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = {

            onStorageVolume(volumeData())
        },
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
            contentColor = contentColorFor(backgroundColor = containerColor)
        ),
        shape = MaterialTheme.shapes.small
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(space = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    modifier = Modifier.size(64.dp),
                    imageVector = when (volumeData().volumeType) {

                        StorageVolumeType.INTERNAL -> Icons.Filled.Storage
                        StorageVolumeType.SD_CARD -> Icons.Filled.SdCard
                        StorageVolumeType.OTG -> Icons.Filled.Usb
                        StorageVolumeType.UNKNOWN -> Icons.Filled.QuestionMark
                    },
                    contentDescription = "Volume Type"
                )

                Text(
                    text = volumeData().title,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                StorageSpaceRow(
                    title = { "Used" },
                    space = { volumeData().usedSize.toFileSize(context = context) }
                )

                StorageSpaceRow(
                    title = { "Free" },
                    space = { volumeData().availableSize.toFileSize(context = context) }
                )

                StorageSpaceRow(
                    title = { "Total" },
                    space = { volumeData().totalSize.toFileSize(context = context) }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(space = 4.dp)
                ) {

                    Text(
                        modifier = Modifier.alpha(alpha = 0.75f),
                        text = "${(volumePercentage * 100).roundToInt()}%",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(weight = 1.0f),
                        progress = { volumePercentage },
                        color = contentColorFor(backgroundColor = containerColor),
                        trackColor = contentColorFor(containerColor).copy(alpha = 0.35f),
                        strokeCap = StrokeCap.Round
                    )
                }
            }

            when {

                isSelected() -> Icon(
                    modifier = Modifier.align(Alignment.TopEnd),
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Selected"
                )
            }
        }
    }
}

@Composable
private fun StorageSpaceRow(title: () -> String, space: () -> String) {

    Row(
        modifier = Modifier.fillMaxWidth(fraction = 0.90f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            modifier = Modifier
                .weight(weight = 1.0f)
                .alpha(alpha = 0.75f),
            text = title(),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            modifier = Modifier
                .weight(weight = 1.0f)
                .alpha(alpha = 0.75f),
            text = space(),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FileView(fileData: FileData) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraSmall
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Text(
                text = fileData.title,
                maxLines = 1,
                overflow = TextOverflow.MiddleEllipsis
            )

            Text(text = fileData.path)
        }
    }
}