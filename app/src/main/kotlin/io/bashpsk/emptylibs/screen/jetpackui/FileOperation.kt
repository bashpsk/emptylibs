package io.bashpsk.emptylibs.screen.jetpackui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MoveDown
import androidx.compose.material.icons.filled.RuleFolder
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Share
import io.bashpsk.jetpackui.optionbar.OptionBarData

object FileOperation {

    val MORE = OptionBarData(label = "More", icon = Icons.Filled.MoreVert)
    val DELETE = OptionBarData(label = "Delete", icon = Icons.Filled.Delete)
    val SHARE = OptionBarData(label = "Share", icon = Icons.Filled.Share)
    val INFO = OptionBarData(label = "Info", icon = Icons.Filled.Info)
    val COPY = OptionBarData(label = "Copy", icon = Icons.Filled.ContentCopy)
    val MOVE = OptionBarData(label = "Move", icon = Icons.Filled.MoveDown)
    val RENAME = OptionBarData(label = "Rename", icon = Icons.Filled.DriveFileRenameOutline)
    val SELECT_ALL = OptionBarData(label = "Select All", icon = Icons.Filled.SelectAll)
    val SELECT_NONE = OptionBarData(label = "Select None", icon = Icons.Filled.Deselect)
    val SELECT_INVERT = OptionBarData("Select Invert", Icons.AutoMirrored.Filled.ListAlt)
    val SELECT_FOLDERS = OptionBarData(label = "Select Folders", icon = Icons.Filled.RuleFolder)
    val SELECT_FILES = OptionBarData(label = "Select Files", icon = Icons.Filled.FilePresent)
}