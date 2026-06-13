package io.bashpsk.emptylibs

import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import io.bashpsk.emptylibs.formatter.format.findAspectRatio
import io.bashpsk.emptylibs.pdftemplate.pdf.PdfTemplateBackground
import io.bashpsk.emptylibs.pdftemplate.pdf.PdfTemplateType
import io.bashpsk.emptylibs.pdftemplate.pdf.PdfTextInput
import io.bashpsk.emptylibs.pdftemplate.pdf.rememberPdfTemplateState
import io.bashpsk.emptylibs.pdftemplate.sheet.SheetMargin
import io.bashpsk.emptylibs.pdftemplate.sheet.SheetSize
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfTemplateScreen() {

    val coroutineScope = rememberCoroutineScope()
    val pdfTemplateState = rememberPdfTemplateState()

    val sampleTemplate = ImageBitmap.imageResource(R.drawable.document_template_03)

    val lineText1 = "Jetpack Compose is a modern UI toolkit introduced by Google for building " +
            "native Android user interfaces. It simplifies and accelerates UI development by " +
            "leveraging the power of Kotlin and a declarative programming model."

    val lineText2 = "Jetpack Compose is fully declarative, allowing developers to describe their " +
            "UI components by calling predefined functions. This approach ensures that the UI " +
            "automatically updates when the underlying data changes. The toolkit is compatible " +
            "with existing Android views, making it easy to integrate into current projects. One " +
            "of the significant advantages of Jetpack Compose is its ability to increase " +
            "development speed. Developers no longer need to work with XML files for UI design; " +
            "instead, they can define the UI directly in Kotlin code. This reduces the amount of " +
            "boilerplate code and makes the codebase easier to maintain. Additionally, Jetpack " +
            "Compose is written entirely in Kotlin, which brings the benefits of concise and " +
            "idiomatic Kotlin programming."

    val lineCount = 9

    val largeText by remember(lineText1, lineText2) { derivedStateOf { "$lineText1\n$lineText2" } }
    var previewImageList by remember { mutableStateOf(persistentListOf<ImageBitmap>()) }

    val titleStyle = MaterialTheme.typography.headlineSmall.copy(
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold
    )
    val subTitleStyle = MaterialTheme.typography.titleSmall
    val indexLabelStyle = subTitleStyle.copy(textDecoration = TextDecoration.Underline)
    val contentStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp)
    val numberStyle = MaterialTheme.typography.labelSmall.copy(fontSize = 6.sp)

    val contentList by remember(lineText1, lineText2) {
        derivedStateOf {
            (0..lineCount).map { line ->

                when (line % 2 == 0) {

                    true -> PdfTextInput(
                        text = "A Modern UI Toolkit for Android",
                        style = subTitleStyle
                    ) to PdfTextInput(
                        text = lineText1,
                        style = contentStyle
                    )

                    false -> PdfTextInput(
                        text = "Key Features and Benefits",
                        style = subTitleStyle
                    ) to PdfTextInput(
                        text = lineText2,
                        style = contentStyle
                    )
                }
            }
        }
    }

    val templateTitleContent = PdfTemplateType.TitleAndContent(
        sheet = SheetSize.A4,
        margin = SheetMargin(left = 0.1F, top = 0.1F, right = 0.1F, bottom = 0.05F),
        background = PdfTemplateBackground.SolidColor(color = Color.Green),
        title = PdfTextInput(text = "Jetpack Compose", style = titleStyle),
        content = PdfTextInput(text = largeText, style = contentStyle),
        numberStyle = numberStyle
    )

    val templateTitleContentWithIndex = PdfTemplateType.TitleAndContentWithIndex(
        sheet = SheetSize.A2,
        margin = SheetMargin(left = 0.1F, top = 0.1F, right = 0.1F, bottom = 0.05F),
        background = PdfTemplateBackground.Image(bitmap = sampleTemplate),
        title = PdfTextInput(text = "Jetpack Compose", style = titleStyle),
        contentList = contentList,
        indexLabel = PdfTextInput(text = "Table of Contents", style = indexLabelStyle),
        indexStyle = contentStyle,
        numberStyle = numberStyle
    )

    LaunchedEffect(templateTitleContent) {

        pdfTemplateState.getPdfPreviewImage(
            template = templateTitleContent
        )?.let { imageBitmap ->

            previewImageList = previewImageList.adding(imageBitmap)
        }
    }

    LaunchedEffect(templateTitleContentWithIndex) {

        pdfTemplateState.getPdfPreviewImage(
            template = templateTitleContentWithIndex
        )?.let { imageBitmap ->

            previewImageList = previewImageList.adding(imageBitmap)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Button(
                        onClick = {

                            coroutineScope.launch {

                                pdfTemplateState.saveAsPdf(
                                    template = templateTitleContent,
                                    destination = File(
                                        Environment.getExternalStoragePublicDirectory(
                                            Environment.DIRECTORY_DOWNLOADS
                                        ),
                                        "Generated PDF.pdf"
                                    )
                                )
                            }
                        }
                    ) {

                        Text("Save as PDF")
                    }

                    Button(
                        onClick = {

                            coroutineScope.launch {

                                pdfTemplateState.saveAsPdf(
                                    template = templateTitleContentWithIndex,
                                    destination = File(
                                        Environment.getExternalStoragePublicDirectory(
                                            Environment.DIRECTORY_DOWNLOADS
                                        ),
                                        "Generated PDF(Index).pdf"
                                    )
                                )
                            }
                        }
                    ) {

                        Text("Save as PDF(Index)")
                    }
                }
            }

            items(previewImageList) { imageBitmap ->

                val aspectRatio by remember(imageBitmap) {
                    derivedStateOf { findAspectRatio(imageBitmap.width, imageBitmap.height) }
                }

                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(aspectRatio),
                    bitmap = imageBitmap,
                    contentScale = ContentScale.Fit,
                    contentDescription = null
                )
            }
        }
    }
}