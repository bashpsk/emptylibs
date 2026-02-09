package io.bashpsk.emptylibs.pdftemplate.pdf

import android.graphics.pdf.PdfDocument
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.toIntSize
import io.bashpsk.emptylibs.pdftemplate.input.SectionData
import io.bashpsk.emptylibs.pdftemplate.sheet.SheetSize
import io.bashpsk.emptylibs.pdftemplate.sheet.SheetSize.Companion.toRect
import io.bashpsk.emptylibs.pdftemplate.sheet.SheetSize.Companion.toSize
import io.bashpsk.emptylibs.pdftemplate.utils.LOG_TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

/**
 * Creates and remembers a [PdfTemplateState] instance across recompositions.
 *
 * This function retrieves the current [LocalDensity] and initializes a [TextMeasurer] to handle
 * text measurement and layout requirements within the state. The state is retained using [retain]
 * to ensure it survives configuration changes if needed by the underlying implementation.
 *
 * @return A remembered [PdfTemplateState] tied to the current composition.
 */
@Composable
fun rememberPdfTemplateState(): PdfTemplateState {

    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    return retain(density, textMeasurer) {
        PdfTemplateState(density = density, textMeasurer = textMeasurer)
    }
}

/**
 * A state object that manages the generation, pagination, and rendering of PDF documents.
 *
 * This class provides the logic for converting [PdfTemplateType] configurations into physical
 * PDF files or [ImageBitmap] previews. it handles complex layout tasks such as text measurement,
 * multi-page pagination, table of contents generation, and coordinate mapping between
 * Compose [DrawScope] and the Android [PdfDocument] canvas.
 *
 * @property density The [Density] used to resolve pixel-dependent measurements for the PDF canvas.
 * @property textMeasurer The [TextMeasurer] used to calculate text bounds and line breaking
 * for accurate pagination.
 */
@Stable
class PdfTemplateState(
    private val density: Density,
    private val textMeasurer: TextMeasurer
) {

    /**
     * Returns a [Flow] that emits an [ImageBitmap] representing a preview of the PDF.
     *
     * @param template The [PdfTemplateType] to generate a preview for.
     */
    fun getPdfPreviewImageFlow(template: PdfTemplateType): Flow<ImageBitmap?> {

        return flow {

            emit(value = getPdfPreviewImage(template = template))
        }.flowOn(context = Dispatchers.IO)
    }

    /**
     * Generates a preview image for the given [PdfTemplateType].
     *
     * @param template The template configuration.
     * @return An [ImageBitmap] of a representative page, or null if rendering fails.
     */
    suspend fun getPdfPreviewImage(
        template: PdfTemplateType
    ): ImageBitmap? = withContext(context = Dispatchers.IO) {

        return@withContext try {

            val sheetSize = template.sheet
            val sheetMargin = template.margin
            val background = template.background
            val numberStyle = template.numberStyle

            val sheetBounds = sheetSize.toRect(margin = sheetMargin)
            val numberHeight = getPageNumberHeight(style = numberStyle)

            val imageBitmap = ImageBitmap(width = sheetSize.width, height = sheetSize.height)

            CanvasDrawScope().draw(
                density = density,
                layoutDirection = LayoutDirection.Ltr,
                canvas = Canvas(image = imageBitmap),
                size = sheetSize.toSize()
            ) {

                drawBackground(background = background)

                val (previewPageNumber, sectionsToDraw) = when (template) {

                    is PdfTemplateType.ContentOnly -> 1 to listOf(
                        createContentSection(
                            content = template.content,
                            bounds = sheetBounds,
                            startPage = 1
                        )
                    )

                    is PdfTemplateType.TitleAndContent -> 2 to listOf(
                        createContentSection(
                            content = template.content,
                            bounds = sheetBounds,
                            startPage = 2
                        )
                    )

                    is PdfTemplateType.TitleAndContentWithIndex -> {

                        val initialSections = template.contentList.map { (subTitle, subContent) ->

                            createContentSection(
                                title = subTitle,
                                content = subContent,
                                bounds = sheetBounds
                            )
                        }

                        val tocTitleLayout = measureText(
                            input = template.indexLabel,
                            maxWidth = sheetBounds.width.toInt()
                        )

                        val tocPageNumberWidth = textMeasurer.measure(
                            text = "999",
                            style = template.indexStyle
                        ).size.width

                        val tocTitleMaxWidth = (sheetBounds.width - tocPageNumberWidth).toInt()

                        val tocEntries = initialSections.map { section ->

                            textMeasurer.measure(
                                text = section.title.text,
                                style = template.indexStyle,
                                constraints = Constraints(maxWidth = tocTitleMaxWidth)
                            )
                        }

                        val tocPageCount = calculateTocPageCount(
                            tocEntries = tocEntries,
                            titleHeight = tocTitleLayout.size.height,
                            bounds = sheetBounds,
                            numberHeight = numberHeight
                        )

                        (1 + tocPageCount + 1) to initialSections
                    }
                }

                var currentY = sheetBounds.top

                for (section in sectionsToDraw) {

                    currentCoroutineContext().ensureActive()

                    if (section.title.text.isNotEmpty()) {

                        if (currentY + section.titleLayout.size.height > sheetBounds
                                .bottom - numberHeight
                        ) break

                        drawPdfText(
                            layout = section.titleLayout,
                            topLeft = Offset(x = sheetBounds.left, y = currentY),
                            width = sheetBounds.width,
                            alignment = section.title.alignment
                        )

                        currentY += section.titleLayout.size.height
                    }

                    val (lastLine, newY) = drawPartialText(
                        layout = section.contentLayout,
                        lineStart = 0,
                        top = currentY,
                        rect = sheetBounds,
                        numberHeight = numberHeight
                    )

                    currentY = newY
                    if (lastLine < section.contentLayout.lineCount) break
                }

                drawPageNumber(pageNumber = previewPageNumber, style = numberStyle)
            }

            imageBitmap
        } catch (exception: Exception) {

            currentCoroutineContext().ensureActive()
            Log.e(LOG_TAG, exception.message, exception)
            null
        }
    }

    /**
     * Saves the provided [PdfTemplateType] as a PDF file to the [destination].
     *
     * This function handles the full pagination and rendering logic for different
     * template types, including title pages and tables of contents.
     *
     * @param template The template configuration containing content, styles, and layout settings.
     * @param destination The file path where the PDF will be written.
     * @throws IOException If there is an error writing to the file system.
     * @throws IllegalArgumentException If the template configuration is invalid.
     * @throws NullPointerException If required template components or the destination is null.
     */
    @Throws(IOException::class, IllegalArgumentException::class, NullPointerException::class)
    suspend fun saveAsPdf(
        template: PdfTemplateType,
        destination: String?
    ) = withContext(Dispatchers.IO) {

        return@withContext saveAsPdf(
            template = template,
            destination = destination?.let { path -> File(path) }
        )
    }

    /**
     * Saves the provided [PdfTemplateType] as a PDF file to the [destination].
     *
     * This function handles the full pagination and rendering logic for different
     * template types, including title pages and tables of contents.
     *
     * @param template The template configuration containing content, styles, and layout settings.
     * @param destination The file where the PDF will be written.
     * @throws IOException If there is an error writing to the file system.
     * @throws IllegalArgumentException If the template configuration is invalid.
     * @throws NullPointerException If required template components or the destination is null.
     */
    @Throws(IOException::class, IllegalArgumentException::class, NullPointerException::class)
    suspend fun saveAsPdf(
        template: PdfTemplateType,
        destination: File?
    ) = withContext(Dispatchers.IO) {

        if (destination == null) throw NullPointerException("Destination cannot be null")

        val pdfDocument = PdfDocument()

        CanvasDrawScope().apply {

            when (template) {

                is PdfTemplateType.ContentOnly -> setContentOnly(
                    pdfDocument = pdfDocument,
                    template = template
                )

                is PdfTemplateType.TitleAndContent -> setTitleContent(
                    pdfDocument = pdfDocument,
                    template = template
                )

                is PdfTemplateType.TitleAndContentWithIndex -> setTitleContentWithIndex(
                    pdfDocument = pdfDocument,
                    template = template
                )
            }
        }

        try {

            destination.outputStream().use { outputStream ->

                pdfDocument.writeTo(outputStream)
            }
        } finally {

            pdfDocument.close()
        }
    }

    /**
     * Generates and adds pages to the [PdfDocument] for a template containing only content.
     *
     * This method calculates the layout bounds based on the sheet size and margins,
     * creates a content section, and draws it across one or more pages as needed.
     *
     * @param pdfDocument The [PdfDocument] where the pages will be added.
     * @param template The [PdfTemplateType.ContentOnly] configuration containing the content and
     * styles.
     */
    private fun CanvasDrawScope.setContentOnly(
        pdfDocument: PdfDocument,
        template: PdfTemplateType.ContentOnly
    ) {

        val sheetBounds = template.sheet.toRect(margin = template.margin)
        val numberHeight = getPageNumberHeight(style = template.numberStyle)

        val section = createContentSection(
            content = template.content,
            bounds = sheetBounds,
            startPage = 1
        )

        drawPaginatedSections(
            pdfDocument = pdfDocument,
            sheet = template.sheet,
            rect = sheetBounds,
            sections = listOf(section),
            numberStyle = template.numberStyle,
            background = template.background,
            pageNumberStart = 1,
            numberHeight = numberHeight
        )
    }

    /**
     * Renders a PDF with a title page followed by paginated content.
     *
     * This function first creates a dedicated page for the title, centering it vertically.
     * Then, it creates a content section and uses [drawPaginatedSections] to render it
     * across one or more subsequent pages, starting from page 2.
     *
     * @param pdfDocument The [PdfDocument] to draw into.
     * @param template The [PdfTemplateType.TitleAndContent] configuration containing the title and
     * content.
     */
    private fun CanvasDrawScope.setTitleContent(
        pdfDocument: PdfDocument,
        template: PdfTemplateType.TitleAndContent
    ) {

        val sheetBounds = template.sheet.toRect(margin = template.margin)
        val titleLayout = measureText(input = template.title, maxWidth = sheetBounds.width.toInt())
        val numberHeight = getPageNumberHeight(style = template.numberStyle)

        drawPage(
            pdfDocument = pdfDocument,
            sheet = template.sheet,
            pageNumber = 1,
            numberStyle = template.numberStyle,
            showPageNumber = false,
            background = template.background
        ) {

            drawPdfText(
                layout = titleLayout,
                topLeft = Offset(
                    x = sheetBounds.left,
                    y = (size.height - titleLayout.size.height) / 2
                ),
                width = sheetBounds.width,
                alignment = template.title.alignment
            )
        }

        val section = createContentSection(
            content = template.content,
            bounds = sheetBounds,
            startPage = 2
        )

        drawPaginatedSections(
            pdfDocument = pdfDocument,
            sheet = template.sheet,
            rect = sheetBounds,
            sections = listOf(section),
            numberStyle = template.numberStyle,
            background = template.background,
            pageNumberStart = 2,
            numberHeight = numberHeight
        )
    }

    /**
     * Generates and draws a multi-page PDF document that includes a title page, a table of contents
     * (index), and multiple content sections.
     *
     * This method performs complex layout calculations to:
     * 1. Create a centered title page.
     * 2. Calculate the number of pages required for the Table of Contents based on the number of
     *    sections.
     * 3. Simulate pagination for all content sections to determine their starting page numbers for
     *    the index.
     * 4. Draw the Table of Contents with clickable page references.
     * 5. Draw the actual content sections across multiple pages, handling text wrapping and
     *    overflows.
     *
     * @param pdfDocument The [PdfDocument] instance where pages will be added.
     * @param template The [PdfTemplateType.TitleAndContentWithIndex] configuration containing the
     * data.
     */
    private fun CanvasDrawScope.setTitleContentWithIndex(
        pdfDocument: PdfDocument,
        template: PdfTemplateType.TitleAndContentWithIndex
    ) {

        val sheetBounds = template.sheet.toRect(margin = template.margin)
        val titleLayout = measureText(input = template.title, maxWidth = sheetBounds.width.toInt())
        val numberHeight = getPageNumberHeight(style = template.numberStyle)

        drawPage(
            pdfDocument = pdfDocument,
            sheet = template.sheet,
            pageNumber = 1,
            numberStyle = template.numberStyle,
            showPageNumber = false,
            background = template.background
        ) {

            drawPdfText(
                layout = titleLayout,
                topLeft = Offset(
                    x = sheetBounds.left,
                    y = (size.height - titleLayout.size.height) / 2
                ),
                width = sheetBounds.width,
                alignment = template.title.alignment
            )
        }

        val tocTitleLayout = measureText(
            input = template.indexLabel,
            maxWidth = sheetBounds.width.toInt()
        )
        val tocPageNumberWidth = textMeasurer.measure(
            text = "999",
            style = template.indexStyle
        ).size.width
        val tocTitleMaxWidth = (sheetBounds.width - tocPageNumberWidth).toInt()

        val initialSections = template.contentList.map { (title, content) ->
            createContentSection(title = title, content = content, bounds = sheetBounds)
        }

        val tocEntries = initialSections.map { section ->

            textMeasurer.measure(
                text = section.title.text,
                style = template.indexStyle,
                constraints = Constraints(maxWidth = tocTitleMaxWidth)
            )
        }

        val tocPageCount = calculateTocPageCount(
            tocEntries = tocEntries,
            titleHeight = tocTitleLayout.size.height,
            bounds = sheetBounds,
            numberHeight = numberHeight
        )

        val pageNumberStart = 1 + tocPageCount + 1

        val sections = initialSections.fold(
            mutableListOf<SectionData>() to (pageNumberStart to sheetBounds.top)
        ) { (list, pageInfo), section ->

            val (page, positionY) = pageInfo
            val sectionHeight = section.titleLayout.size.height
            val fitsOnPage = positionY + sectionHeight <= sheetBounds.bottom - numberHeight
            val startPage = if (fitsOnPage) page else page + 1
            val startY = if (fitsOnPage) positionY else sheetBounds.top

            val (endPage, endY) = simulatePagination(
                rect = sheetBounds,
                layout = section.contentLayout,
                startPage = startPage,
                startY = startY + sectionHeight,
                numberHeight = numberHeight
            )

            list.add(element = section.copy(startingPage = startPage, positionY = startY))
            list to (endPage to endY)
        }.first

        (2 until pageNumberStart).forEach { tocPageNumber ->

            drawPage(
                pdfDocument = pdfDocument,
                sheet = template.sheet,
                pageNumber = tocPageNumber,
                numberStyle = template.numberStyle,
                showPageNumber = true,
                background = template.background
            ) {

                var positionY = sheetBounds.top

                when (tocPageNumber) {

                    2 -> {

                        drawText(
                            textLayoutResult = tocTitleLayout,
                            topLeft = Offset(
                                x = sheetBounds.left + (sheetBounds.width - tocTitleLayout.size
                                    .width) / 2,
                                y = positionY
                            )
                        )

                        positionY += tocTitleLayout.size.height
                    }
                }

                sections.forEachIndexed { index, section ->

                    tocEntries.getOrNull(index = index)?.let { entry ->

                        val entryPage = calculateTocEntryPage(
                            entries = tocEntries,
                            index = index,
                            initialOffset = when (tocPageNumber) {

                                2 -> tocTitleLayout.size.height.toFloat()
                                else -> 0F
                            },
                            bounds = sheetBounds,
                            numberHeight = numberHeight
                        ) + 1

                        when (entryPage) {

                            tocPageNumber -> {

                                val numberLayout = textMeasurer.measure(
                                    text = "${section.startingPage}",
                                    style = template.indexStyle
                                )

                                drawText(
                                    textLayoutResult = entry,
                                    topLeft = Offset(x = sheetBounds.left, y = positionY)
                                )

                                drawText(
                                    textLayoutResult = numberLayout,
                                    topLeft = Offset(
                                        x = sheetBounds.right - numberLayout.size.width,
                                        y = positionY
                                    )
                                )

                                positionY += entry.size.height
                            }
                        }
                    }
                }
            }
        }

        drawPaginatedSections(
            pdfDocument = pdfDocument,
            sheet = template.sheet,
            rect = sheetBounds,
            sections = sections,
            numberStyle = template.numberStyle,
            background = template.background,
            pageNumberStart = pageNumberStart,
            numberHeight = numberHeight
        )
    }

    /**
     * Draws multiple content sections across one or more PDF pages, handling automatic pagination
     * when content exceeds the available vertical space.
     *
     * This function iterates through the provided [sections], drawing titles and content text.
     * It manages line-breaking across page boundaries, ensures headers are correctly placed,
     * and applies backgrounds and page numbers to every generated page.
     *
     * @param pdfDocument The [PdfDocument] instance where new pages will be added.
     * @param sheet The dimensions of the physical PDF page.
     * @param rect The drawable area within the page (considering margins).
     * @param sections The list of [SectionData] containing measured text layouts to be drawn.
     * @param numberStyle The [TextStyle] used for rendering page numbers.
     * @param background The [PdfTemplateBackground] (color or image) to apply to each page.
     * @param pageNumberStart The initial page number to start counting from.
     * @param numberHeight The calculated height of the page number text, used to prevent overlap.
     */
    private fun CanvasDrawScope.drawPaginatedSections(
        pdfDocument: PdfDocument,
        sheet: SheetSize,
        rect: Rect,
        sections: List<SectionData>,
        numberStyle: TextStyle,
        background: PdfTemplateBackground,
        pageNumberStart: Int,
        numberHeight: Float
    ) {

        var sectionIndex = 0
        var lineIndex = 0
        var isTitleDrawn = false
        var pageNumber = pageNumberStart

        while (sectionIndex < sections.size) {

            drawPage(
                pdfDocument = pdfDocument,
                sheet = sheet,
                pageNumber = pageNumber,
                numberStyle = numberStyle,
                showPageNumber = true,
                background = background
            ) {

                var positionY = rect.top

                while (sectionIndex < sections.size) {

                    val section = sections.getOrNull(index = sectionIndex) ?: break

                    when {

                        isTitleDrawn.not() -> {

                            if (pageNumber == section.startingPage && lineIndex == 0) {
                                positionY = section.positionY
                            }

                            section.title.text.takeIf { text -> text.isNotEmpty() }?.run {

                                val height = section.titleLayout.size.height

                                if (positionY + height > rect.bottom - numberHeight) return@drawPage

                                drawPdfText(
                                    layout = section.titleLayout,
                                    topLeft = Offset(x = rect.left, y = positionY),
                                    width = rect.width,
                                    alignment = section.title.alignment
                                )

                                positionY += height
                            }

                            isTitleDrawn = true
                        }
                    }

                    val (nextUnderline, newY) = drawPartialText(
                        layout = section.contentLayout,
                        lineStart = lineIndex,
                        top = positionY,
                        rect = rect,
                        numberHeight = numberHeight
                    )

                    lineIndex = nextUnderline
                    positionY = newY

                    when {

                        lineIndex >= section.contentLayout.lineCount -> {

                            sectionIndex++
                            lineIndex = 0
                            isTitleDrawn = false
                        }

                        else -> break
                    }
                }
            }

            pageNumber++
        }
    }

    /**
     * Draws a portion of the [layout] that fits within the remaining vertical space of the current
     * page.
     *
     * This function calculates how many lines of text can be rendered starting from [lineStart]
     * without exceeding the boundary defined by the page margin and the page number height.
     *
     * @param layout The [TextLayoutResult] containing the full text and styling to be drawn.
     * @param lineStart The index of the first line to start drawing from in this call.
     * @param top The vertical coordinate (Y) where the text drawing should begin.
     * @param rect The boundaries of the printable area (excluding margins).
     * @param numberHeight The height reserved at the bottom for the page number.
     * @return A [Pair] containing the index of the next line to be drawn (the end index of this
     * segment) and the new vertical position (Y) after drawing the text.
     */
    private fun DrawScope.drawPartialText(
        layout: TextLayoutResult,
        lineStart: Int,
        top: Float,
        rect: Rect,
        numberHeight: Float
    ): Pair<Int, Float> {

        val position = layout.getLineTop(lineIndex = lineStart)
        val availableHeight = (rect.bottom - numberHeight) - top

        val endLineIndex = (lineStart until layout.lineCount).firstOrNull { index ->
            layout.getLineBottom(lineIndex = index) - position > availableHeight
        } ?: layout.lineCount

        return when {

            endLineIndex > lineStart -> {

                val textHeight = layout.getLineBottom(lineIndex = endLineIndex - 1) - position

                clipRect(
                    left = rect.left,
                    top = top,
                    right = rect.right,
                    bottom = top + textHeight
                ) {

                    drawText(
                        textLayoutResult = layout,
                        topLeft = Offset(x = rect.left, y = top - position)
                    )
                }

                endLineIndex to (top + textHeight)
            }

            else -> lineStart to top
        }
    }

    /**
     * Simulates the pagination of a given text layout to determine where it ends.
     * This function calculates the final page number and the vertical position (Y-coordinate)
     * on that page after laying out the entire text, considering page breaks.
     *
     * @param rect The bounding box for the content on the page, excluding margins and page number
     * area.
     * @param layout The [TextLayoutResult] of the content to be paginated.
     * @param startPage The page number where the content starts.
     * @param startY The initial vertical position (Y-coordinate) on the starting page.
     * @param numberHeight The height reserved for the page number at the bottom of each page.
     * @return A [Pair] containing the end page number (first) and the final Y-coordinate (second)
     * on that page.
     */
    private fun simulatePagination(
        rect: Rect,
        layout: TextLayoutResult,
        startPage: Int,
        startY: Float,
        numberHeight: Float
    ): Pair<Int, Float> {

        var currentPage = startPage
        var currentY = startY
        var linesProcessed = 0

        while (linesProcessed < layout.lineCount) {

            val position = layout.getLineTop(lineIndex = linesProcessed)
            val availableHeight = (rect.bottom - numberHeight) - currentY

            val endLineIndex = (linesProcessed until layout.lineCount).firstOrNull { index ->
                layout.getLineBottom(lineIndex = index) - position > availableHeight
            } ?: layout.lineCount

            when (endLineIndex) {

                linesProcessed -> {

                    currentPage++
                    currentY = rect.top
                }

                else -> {

                    linesProcessed = endLineIndex

                    currentY = when {

                        linesProcessed < layout.lineCount -> {

                            currentPage++
                            rect.top
                        }

                        else -> currentY + (layout.getLineBottom(endLineIndex - 1) - position)
                    }
                }
            }
        }

        return currentPage to currentY
    }

    /**
     * Starts a new page in the [PdfDocument], executes a drawing block, and finishes the page.
     *
     * This helper function manages the lifecycle of a [PdfDocument.Page], providing a [DrawScope]
     * for rendering content, background, and optional page numbers.
     *
     * @param pdfDocument The [PdfDocument] instance being written to.
     * @param sheet The size configuration for the page.
     * @param pageNumber The current page index to be recorded in the PDF metadata.
     * @param numberStyle The [TextStyle] used for rendering the page number.
     * @param showPageNumber Whether to draw the page number at the bottom of the page.
     * @param background The [PdfTemplateBackground] to apply (solid color or image).
     * @param drawBlock The content drawing operations to perform on the page.
     */
    private fun CanvasDrawScope.drawPage(
        pdfDocument: PdfDocument,
        sheet: SheetSize,
        pageNumber: Int,
        numberStyle: TextStyle,
        showPageNumber: Boolean,
        background: PdfTemplateBackground,
        drawBlock: DrawScope.() -> Unit
    ) {

        val pdfPage = pdfDocument.startPage(
            PdfDocument.PageInfo.Builder(sheet.width, sheet.height, pageNumber).create()
        )

        draw(
            density = this@PdfTemplateState.density,
            layoutDirection = LayoutDirection.Ltr,
            canvas = Canvas(pdfPage.canvas),
            size = sheet.toSize()
        ) {

            drawBackground(background = background)

            drawBlock()

            if (showPageNumber) drawPageNumber(pageNumber = pageNumber, style = numberStyle)
        }

        pdfDocument.finishPage(pdfPage)
    }

    /**
     * Draws measured text onto the canvas with specific horizontal alignment.
     *
     * This helper function calculates the horizontal offset required to align the text within a
     * specified width (e.g., Start, Center, or End) before rendering it.
     *
     * @param layout The [TextLayoutResult] containing the measured text and style.
     * @param topLeft The base [Offset] representing the top-left corner of the drawing area.
     * @param width The total available width used to calculate alignment.
     * @param alignment The [Alignment] strategy to apply horizontally.
     */
    private fun DrawScope.drawPdfText(
        layout: TextLayoutResult,
        topLeft: Offset,
        width: Float,
        alignment: Alignment
    ) {

        val positionX = when (alignment) {

            Alignment.TopStart, Alignment.CenterStart, Alignment.BottomStart -> 0F

            Alignment.TopCenter, Alignment.Center, Alignment.BottomCenter -> {
                (width - layout.size.width) / 2F
            }

            Alignment.TopEnd, Alignment.CenterEnd, Alignment.BottomEnd -> width - layout.size.width
            else -> 0F
        }

        drawText(textLayoutResult = layout, topLeft = topLeft.copy(x = topLeft.x + positionX))
    }

    /**
     * Draws the background for the current page based on the provided [PdfTemplateBackground] type.
     *
     * Supports drawing a solid color or a bitmap image scaled to fit the current canvas size.
     *
     * @param background The background configuration (SolidColor or Image).
     */
    private fun DrawScope.drawBackground(background: PdfTemplateBackground) {

        when (background) {

            is PdfTemplateBackground.SolidColor -> drawRect(color = background.color)

            is PdfTemplateBackground.Image -> background.bitmap?.let { bitmap ->

                drawImage(image = bitmap, dstSize = size.toIntSize())
            }
        }
    }

    /**
     * Draws the page number at the bottom center of the current page.
     *
     * @param pageNumber The integer value of the page number to display.
     * @param style The [TextStyle] to apply to the page number text.
     */
    private fun DrawScope.drawPageNumber(pageNumber: Int, style: TextStyle) {

        textMeasurer.measure(text = "$pageNumber", style = style).let { layout ->

            drawText(
                textLayoutResult = layout,
                topLeft = Offset(
                    x = (size.width - layout.size.width) / 2,
                    y = size.height - layout.size.height
                )
            )
        }
    }

    /**
     * Calculates the total number of pages required to render the Table of Contents (TOC).
     *
     * This function iterates through measured TOC entries and determines how many pages
     * are needed based on the available vertical space within the sheet boundaries,
     * accounting for the TOC title height and the footer page number height.
     *
     * @param tocEntries A list of [TextLayoutResult] representing the measured title of each
     * section.
     * @param titleHeight The pixel height of the "Table of Contents" header label.
     * @param bounds The printable area of the PDF page.
     * @param numberHeight The height of the page number rendered at the bottom.
     * @return The total page count required for the TOC (minimum 1).
     */
    private fun calculateTocPageCount(
        tocEntries: List<TextLayoutResult>,
        titleHeight: Int,
        bounds: Rect,
        numberHeight: Float
    ): Int {

        return tocEntries.fold(
            1 to bounds.top + titleHeight
        ) { (count, currentY), entry ->

            val height = entry.size.height

            when {

                currentY + height > bounds.bottom - numberHeight -> {
                    (count + 1) to (bounds.top + height)
                }

                else -> count to (currentY + height)
            }
        }.first
    }

    /**
     * Calculates which page a specific table of contents (TOC) entry will appear on.
     *
     * This function simulates the layout of TOC entries to determine the page number
     * for the entry at the given [index]. It considers the available space within the
     * [bounds], accounting for an [initialOffset] (like a TOC title), and the space
     * reserved for the page number ([numberHeight]).
     *
     * @param entries A list of [TextLayoutResult] for all TOC entries.
     * @param index The zero-based index of the specific TOC entry to calculate the page for.
     * @param initialOffset The vertical offset on the first page, typically for the TOC title.
     * @param bounds The [Rect] defining the drawable area for the content.
     * @param numberHeight The height reserved at the bottom of the page for the page number.
     * @return The calculated page number (1-based) where the specified entry will be drawn.
     */
    private fun calculateTocEntryPage(
        entries: List<TextLayoutResult>,
        index: Int,
        initialOffset: Float,
        bounds: Rect,
        numberHeight: Float
    ): Int {

        return entries.take(index + 1).fold(
            1 to bounds.top + initialOffset
        ) { (count, currentY), entry ->

            val height = entry.size.height

            when {

                currentY + height > bounds.bottom - numberHeight -> {
                    (count + 1) to (bounds.top + height)
                }

                else -> count to (currentY + height)
            }
        }.first
    }

    /**
     * Creates a [SectionData] object by measuring the title and content text.
     *
     * @param title The input configuration for the section's title.
     * @param content The input configuration for the section's body text.
     * @param bounds The rectangular area available for the content, used to determine maximum
     * width.
     * @return A [SectionData] instance containing the measured layout results.
     */
    private fun createContentSection(
        title: PdfTextInput,
        content: PdfTextInput,
        bounds: Rect
    ): SectionData {

        return SectionData(
            title = title,
            content = content,
            titleLayout = measureText(input = title, maxWidth = bounds.width.toInt()),
            contentLayout = measureText(input = content, maxWidth = bounds.width.toInt()),
            startingPage = 0,
            positionY = 0F
        )
    }

    /**
     * Creates a [SectionData] object by measuring the title and content text.
     *
     * @param content The input configuration for the section's body text.
     * @param bounds The rectangular area available for the content, used to determine maximum
     * width.
     * @param startPage The starting page number for the section.
     * @return A [SectionData] instance containing the measured layout results.
     */
    private fun createContentSection(
        content: PdfTextInput,
        bounds: Rect,
        startPage: Int
    ): SectionData {

        return SectionData(
            title = PdfTextInput(text = ""),
            content = content,
            titleLayout = textMeasurer.measure(
                text = "",
                style = TextStyle.Default,
                constraints = Constraints(maxWidth = 0)
            ),
            contentLayout = measureText(input = content, maxWidth = bounds.width.toInt()),
            startingPage = startPage,
            positionY = bounds.top
        )
    }

    /**
     * Measures the provided [PdfTextInput] and returns a [TextLayoutResult].
     *
     * This helper function uses the internal [textMeasurer] to calculate the layout
     * constraints, including the text content, style, and maximum allowed width.
     *
     * @param input The text and style information to be measured.
     * @param maxWidth The maximum horizontal space available for the text.
     * @return A [TextLayoutResult] containing the dimensions and layout info.
     */
    private fun measureText(input: PdfTextInput, maxWidth: Int): TextLayoutResult {

        return textMeasurer.measure(
            text = input.text,
            style = input.style,
            constraints = Constraints(maxWidth = maxWidth)
        )
    }

    /**
     * Calculates the vertical height required to render a page number using the
     * provided [TextStyle].
     *
     * This is used to determine the bottom padding/buffer needed on each page to ensure
     * that content does not overlap with the page number.
     *
     * @param style The [TextStyle] used for the page number.
     * @return The height of the measured text in pixels.
     */
    private fun getPageNumberHeight(style: TextStyle): Float {

        return textMeasurer.measure(text = "1", style = style).size.height.toFloat()
    }
}