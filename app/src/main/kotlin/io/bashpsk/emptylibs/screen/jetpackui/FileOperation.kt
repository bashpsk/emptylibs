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
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import io.bashpsk.emptylibs.jetpackui.optionbar.OptionBarData

@Stable
sealed interface FileOperation : OptionBarData {

    data object More : FileOperation {

        override val label: String = "More"

        override val icon: ImageVector = Icons.Filled.MoreVert

        override val enabled: Boolean = true
    }

    data object Delete : FileOperation {

        override val label: String = "Delete"

        override val icon: ImageVector = Icons.Filled.Delete

        override val enabled: Boolean = true
    }

    data object Share : FileOperation {

        override val label: String = "Share"

        override val icon: ImageVector = Icons.Filled.Share

        override val enabled: Boolean = true
    }

    data object Info : FileOperation {

        override val label: String = "Info"

        override val icon: ImageVector = Icons.Filled.Info

        override val enabled: Boolean = true
    }

    data object Copy : FileOperation {

        override val label: String = "Copy"

        override val icon: ImageVector = Icons.Filled.ContentCopy

        override val enabled: Boolean = true
    }

    data object Move : FileOperation {

        override val label: String = "Move"

        override val icon: ImageVector = Icons.Filled.MoveDown

        override val enabled: Boolean = true
    }

    data object Rename : FileOperation {

        override val label: String = "Rename"

        override val icon: ImageVector = Icons.Filled.DriveFileRenameOutline

        override val enabled: Boolean = true
    }

    data object SelectAll : FileOperation {

        override val label: String = "Select All"

        override val icon: ImageVector = Icons.Filled.SelectAll

        override val enabled: Boolean = true
    }

    data object SelectNone : FileOperation {

        override val label: String = "Select None"

        override val icon: ImageVector = Icons.Filled.Deselect

        override val enabled: Boolean = true
    }

    data object SelectInvert : FileOperation {

        override val label: String = "Select Invert"

        override val icon: ImageVector = Icons.AutoMirrored.Filled.ListAlt

        override val enabled: Boolean = true
    }

    data object SelectFolders : FileOperation {

        override val label: String = "Select Folders"

        override val icon: ImageVector = Icons.Filled.RuleFolder

        override val enabled: Boolean = true
    }

    data object SelectFiles : FileOperation {

        override val label: String = "Select Files"

        override val icon: ImageVector = Icons.Filled.FilePresent

        override val enabled: Boolean = true
    }
}