package io.bashpsk.emptylibs.pdftemplate.pdf

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.pdftemplate.sheet.SheetMargin
import io.bashpsk.emptylibs.pdftemplate.sheet.SheetSize

@Stable
sealed interface PdfTemplateType {

    data class ContentOnly(
        val sheet: SheetSize = SheetSize.A4,
        val margin: SheetMargin = SheetMargin.Default,
        val background: PdfTemplateBackground = PdfTemplateBackground.SolidColor(),
        val content: PdfTextInput,
        val numberStyle: TextStyle = TextStyle.Default
    ) : PdfTemplateType

    data class TitleAndContent(
        val sheet: SheetSize = SheetSize.A4,
        val margin: SheetMargin = SheetMargin.Default,
        val background: PdfTemplateBackground = PdfTemplateBackground.SolidColor(),
        val title: PdfTextInput,
        val content: PdfTextInput,
        val numberStyle: TextStyle = TextStyle.Default
    ) : PdfTemplateType

    data class TitleAndContentWithIndex(
        val sheet: SheetSize = SheetSize.A4,
        val margin: SheetMargin = SheetMargin.Default,
        val background: PdfTemplateBackground = PdfTemplateBackground.SolidColor(),
        val title: PdfTextInput,
        val contentList: Iterable<Pair<PdfTextInput, PdfTextInput>>,
        val contentItemSpace: Dp = 0.dp,
        val indexLabel: PdfTextInput = PdfTextInput(
            text = "Table of Contents",
            style = TextStyle.Default.copy(textDecoration = TextDecoration.Underline)
        ),
        val indexStyle: TextStyle = TextStyle.Default,
        val numberStyle: TextStyle = TextStyle.Default,
        val indexItemSpace: Dp = 0.dp
    ) : PdfTemplateType
}