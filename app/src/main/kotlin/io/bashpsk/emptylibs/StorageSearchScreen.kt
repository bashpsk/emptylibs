package io.bashpsk.emptylibs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.storage.storage.DirectoryData
import io.bashpsk.emptylibs.storage.storage.DirectorySearchData
import io.bashpsk.emptylibs.storage.storage.FileData
import io.bashpsk.emptylibs.storage.storage.FileType
import io.bashpsk.emptylibs.storage.storage.FileType.Companion.extension
import io.bashpsk.emptylibs.storage.storage.StorageExt
import kotlinx.collections.immutable.ImmutableList

@Composable
fun StorageSearchScreen() {

    val context = LocalContext.current

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var includeFolders by rememberSaveable { mutableStateOf(true) }
    var isSearching by rememberSaveable { mutableStateOf(false) }
    var extensions by retain { mutableStateOf<ImmutableList<String>?>(null) }
    var searchResult by retain { mutableStateOf(DirectorySearchData()) }

    LaunchedEffect(searchQuery, includeFolders, extensions) {

        isSearching = true

        searchResult = StorageExt.getSearchDirectoryFileData(
            context = context,
            query = searchQuery,
            includeFolders = includeFolders,
            extensions = extensions
        )

        isSearching = false
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            )
        ) {

            item {

                if (isSearching) CircularProgressIndicator()
            }

            item {

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    itemVerticalAlignment = Alignment.CenterVertically
                ) {

                    Button(enabled = searchQuery.isNotEmpty(), onClick = { searchQuery = "" }) {

                        Text("Clear")
                    }

                    Button(enabled = searchQuery.isEmpty(), onClick = { searchQuery = "Vid" }) {

                        Text("Search --> Vid")
                    }

                    Button(
                        onClick = {

                            extensions = if (extensions == null) FileType.Video.extension else null
                        }
                    ) {

                        Text(if (extensions == null) "Ext --> Video" else "Ext --> Null")
                    }

                    Button(onClick = { includeFolders = !includeFolders }) {

                        Text(if (includeFolders) "Included Folders" else "Excluded Folders")
                    }
                }
            }

            item {

                Text("Folders: ${searchResult.folders.size} | Files: ${searchResult.files.size}")
            }

            items(searchResult.folders) { directoryData ->

                DirectoryItemView(directoryData)
            }

            items(searchResult.files) { fileData ->

                DirectoryItemView(fileData)
            }
        }
    }
}

@Composable
private fun DirectoryItemView(directoryData: DirectoryData) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraSmall
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = Icons.Filled.Folder,
                contentDescription = null
            )

            Text(
                text = directoryData.title,
                maxLines = 1,
                overflow = TextOverflow.MiddleEllipsis
            )
        }
    }
}

@Composable
private fun DirectoryItemView(fileData: FileData) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraSmall
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = Icons.AutoMirrored.Filled.InsertDriveFile,
                contentDescription = null
            )

            Text(
                text = fileData.title,
                maxLines = 1,
                overflow = TextOverflow.MiddleEllipsis
            )
        }
    }
}