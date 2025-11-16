package io.bashpsk.emptylibs

import android.os.Environment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.jetpackui.text.BasicTextEditor
import java.io.File

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BasicTextEditorScreen() {

    val textFile = remember {
        File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            "Test.txt"
        )
    }

    val sampleText = "pm uninstall --user 0 com.google.android.apps.googleassistant\n" +
            "pm uninstall --user 0 com.heytap.cloud\n" +
            "pm uninstall --user 0 com.android.chrome\n" +
            "pm uninstall --user 0 com.heytap.browser\n" +
            "pm uninstall --user 0 com.google.android.keep\n" +
            "pm uninstall --user 0 com.facebook.appmanager\n" +
            "pm uninstall --user 0 com.facebook.services\n" +
            "pm uninstall --user 0 com.finshell.fin\n" +
            "pm uninstall --user 0 com.google.android.googlequicksearchbox\n" +
            "pm uninstall --user 0 com.heytap.cloud\n" +
            "pm uninstall --user 0 com.opos.cs"

    val tooLongText = (0..3).joinToString(separator = "\n") { sampleText }

    var inputContent by remember { mutableStateOf(sampleText) }

    var inputContent2 by remember { mutableStateOf(TextFieldValue(sampleText)) }

    val textStyle = MaterialTheme.typography.bodyMedium

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {

            BasicTextEditor(
                modifier = Modifier.fillMaxWidth(),
                inputContent = inputContent,
                onContentChange = { newValue -> inputContent = newValue },
                textStyle = textStyle.copy(color = MaterialTheme.colorScheme.onBackground),
                numberColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70F),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                highlightColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                itemSpace = 8.dp,
                dividerContent = {

                    VerticalDivider(
                        modifier = Modifier.fillMaxHeight(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45F)
                    )
                }
            )

            /*BasicTextEditor(
                modifier = Modifier.fillMaxWidth(),
                inputContent = inputContent2,
                onContentChange = { newValue -> inputContent2 = newValue },
                textStyle = textStyle.copy(color = MaterialTheme.colorScheme.onBackground),
                numberColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70F),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.tertiary),
                highlightColor = MaterialTheme.colorScheme.onSecondary,
                itemSpace = 8.dp,
                dividerContent = {

                    VerticalDivider(
                        modifier = Modifier.fillMaxHeight(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45F)
                    )
                }
            )*/
        }
    }
}