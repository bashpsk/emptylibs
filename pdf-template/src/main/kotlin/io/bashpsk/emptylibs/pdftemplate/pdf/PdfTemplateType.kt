package io.bashpsk.emptylibs.pdftemplate.pdf

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import io.bashpsk.emptylibs.pdftemplate.sheet.SheetMargin
import io.bashpsk.emptylibs.pdftemplate.sheet.SheetSize

/**
 * Defines the structure and layout of a PDF document.
 */
@Stable
sealed interface PdfTemplateType {

    /** The physical dimensions of the PDF pages. */
    val sheet: SheetSize

    /** The margins to apply to the content on each page. */
    val margin: SheetMargin

    /** The background to draw on each page. */
    val background: PdfTemplateBackground

    /** The style to use for page numbers. */
    val numberStyle: TextStyle

    /**
     * A simple template containing only content, starting from the first page.
     *
     * @property content The main text content to be paginated.
     */
    data class ContentOnly(
        override val sheet: SheetSize = SheetSize.A4,
        override val margin: SheetMargin = SheetMargin.Default,
        override val background: PdfTemplateBackground = PdfTemplateBackground.SolidColor(),
        override val numberStyle: TextStyle = TextStyle.Default,
        val content: PdfTextInput
    ) : PdfTemplateType

    /**
     * A template with a centered title page followed by paginated content.
     *
     * @property title The text for the title page.
     * @property content The main text content.
     */
    data class TitleAndContent(
        override val sheet: SheetSize = SheetSize.A4,
        override val margin: SheetMargin = SheetMargin.Default,
        override val background: PdfTemplateBackground = PdfTemplateBackground.SolidColor(),
        override val numberStyle: TextStyle = TextStyle.Default,
        val title: PdfTextInput,
        val content: PdfTextInput
    ) : PdfTemplateType

    /**
     * A complex template with a title page, a generated table of contents (index),
     * and multiple sections of content.
     *
     * @property title The main title for the document.
     * @property contentList A list of sections, each having a title and content.
     * @property indexLabel The text to display at the top of the Table of Contents.
     * @property indexStyle The text style for entries in the Table of Contents.
     */
    data class TitleAndContentWithIndex(
        override val sheet: SheetSize = SheetSize.A4,
        override val margin: SheetMargin = SheetMargin.Default,
        override val background: PdfTemplateBackground = PdfTemplateBackground.SolidColor(),
        override val numberStyle: TextStyle = TextStyle.Default,
        val title: PdfTextInput,
        val contentList: Iterable<Pair<PdfTextInput, PdfTextInput>>,
        val indexLabel: PdfTextInput = PdfTextInput(
            text = "Table of Contents",
            style = TextStyle.Default.copy(textDecoration = TextDecoration.Underline)
        ),
        val indexStyle: TextStyle = TextStyle.Default,
    ) : PdfTemplateType
}