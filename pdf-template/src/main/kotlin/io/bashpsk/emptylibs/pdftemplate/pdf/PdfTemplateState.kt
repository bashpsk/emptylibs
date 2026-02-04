package io.bashpsk.emptylibs.pdftemplate.pdf

import android.graphics.pdf.PdfDocument
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toIntSize
import io.bashpsk.emptylibs.pdftemplate.input.SectionData
import io.bashpsk.emptylibs.pdftemplate.sheet.SheetSize
import io.bashpsk.emptylibs.pdftemplate.sheet.SheetSize.Companion.toRect
import io.bashpsk.emptylibs.pdftemplate.sheet.SheetSize.Companion.toSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

@Composable
fun rememberPdfTemplateState(): PdfTemplateState {

    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    return retain(density, textMeasurer) {
        PdfTemplateState(density = density, textMeasurer = textMeasurer)
    }
}

@Stable
class PdfTemplateState(
    private val density: Density,
    private val textMeasurer: TextMeasurer
) {

    @Throws(IOException::class, IllegalArgumentException::class, NullPointerException::class)
    suspend fun saveAsPdf(
        templateType: PdfTemplateType,
        destination: File?
    ) = withContext(context = Dispatchers.IO) {

        val pdfDocument = PdfDocument()

        CanvasDrawScope().apply {

            when (templateType) {

                is PdfTemplateType.ContentOnly -> setContentOnly(
                    pdfDocument = pdfDocument,
                    templateType = templateType
                )

                is PdfTemplateType.TitleAndContent -> setTitleContent(
                    pdfDocument = pdfDocument,
                    templateType = templateType
                )

                is PdfTemplateType.TitleAndContentWithIndex -> setTitleContentWithIndex(
                    pdfDocument = pdfDocument,
                    templateType = templateType
                )
            }
        }

        try {

            destination?.outputStream()?.use { outputStream ->

                pdfDocument.writeTo(outputStream)
            }
        } finally {

            pdfDocument.close()
        }
    }

    private fun CanvasDrawScope.setContentOnly(
        pdfDocument: PdfDocument,
        templateType: PdfTemplateType.ContentOnly
    ) {

        val sheetBounds = templateType.sheet.toRect(margin = templateType.margin)

        val section = SectionData(
            title = PdfTextInput(text = ""),
            content = templateType.content,
            titleLayout = textMeasurer.measure(
                text = "",
                style = TextStyle.Default,
                constraints = Constraints(maxWidth = 0)
            ),
            contentLayout = textMeasurer.measure(
                text = templateType.content.text,
                style = templateType.content.style,
                constraints = Constraints(maxWidth = sheetBounds.width.toInt())
            ),
            startingPage = 1,
            positionY = sheetBounds.top
        )

        drawPaginatedSections(
            pdfDocument = pdfDocument,
            sheet = templateType.sheet,
            rect = sheetBounds,
            sections = listOf(section),
            numberStyle = templateType.numberStyle,
            background = templateType.background,
            pageNumberStart = 1,
            numberHeight = getPageNumberHeight(numberStyle = templateType.numberStyle),
            spacing = 0.dp
        )
    }

    private fun CanvasDrawScope.setTitleContent(
        pdfDocument: PdfDocument,
        templateType: PdfTemplateType.TitleAndContent
    ) {

        val sheetBounds = templateType.sheet.toRect(margin = templateType.margin)

        val titleLayout = textMeasurer.measure(
            text = templateType.title.text,
            style = templateType.title.style,
            constraints = Constraints(maxWidth = sheetBounds.width.toInt())
        )

        val numberHeight = getPageNumberHeight(numberStyle = templateType.numberStyle)

        drawPage(
            pdfDocument = pdfDocument,
            sheet = templateType.sheet,
            pageNumber = 1,
            numberStyle = templateType.numberStyle,
            showPageNumber = false,
            background = templateType.background
        ) {

            drawPdfText(
                layout = titleLayout,
                topLeft = Offset(
                    x = sheetBounds.left,
                    y = (size.height - titleLayout.size.height) / 2
                ),
                width = sheetBounds.width,
                alignment = templateType.title.alignment
            )
        }

        val section = SectionData(
            title = PdfTextInput(text = ""),
            content = templateType.content,
            titleLayout = textMeasurer.measure(
                text = "",
                style = TextStyle.Default,
                constraints = Constraints(maxWidth = 0)
            ),
            contentLayout = textMeasurer.measure(
                text = templateType.content.text,
                style = templateType.content.style,
                constraints = Constraints(maxWidth = sheetBounds.width.toInt())
            ),
            startingPage = 2,
            positionY = sheetBounds.top
        )

        drawPaginatedSections(
            pdfDocument = pdfDocument,
            sheet = templateType.sheet,
            rect = sheetBounds,
            sections = listOf(section),
            numberStyle = templateType.numberStyle,
            background = templateType.background,
            pageNumberStart = 2,
            numberHeight = numberHeight,
            spacing = 0.dp
        )
    }

    private fun CanvasDrawScope.setTitleContentWithIndex(
        pdfDocument: PdfDocument,
        templateType: PdfTemplateType.TitleAndContentWithIndex
    ) {

        val sheetBounds = templateType.sheet.toRect(margin = templateType.margin)

        val titleLayout = textMeasurer.measure(
            text = templateType.title.text,
            style = templateType.title.style,
            constraints = Constraints(maxWidth = sheetBounds.width.toInt())
        )

        val numberHeight = getPageNumberHeight(numberStyle = templateType.numberStyle)

        drawPage(
            pdfDocument = pdfDocument,
            sheet = templateType.sheet,
            pageNumber = 1,
            numberStyle = templateType.numberStyle,
            showPageNumber = false,
            background = templateType.background
        ) {

            drawPdfText(
                layout = titleLayout,
                topLeft = Offset(
                    x = sheetBounds.left,
                    y = (size.height - titleLayout.size.height) / 2
                ),
                width = sheetBounds.width,
                alignment = templateType.title.alignment
            )
        }

        val tocTitle = textMeasurer.measure(
            text = templateType.indexLabel.text,
            style = templateType.indexLabel.style,
            constraints = Constraints(maxWidth = sheetBounds.width.toInt())
        )

        val tocEntrySpacing = templateType.indexItemSpace.toPx()
        val tocPageNumberWidth = textMeasurer.measure(
            text = "999",
            style = templateType.indexStyle
        ).size.width
        val tocTitleMaxWidth = (sheetBounds.width - tocPageNumberWidth).toInt()

        val initialSections = templateType.contentList.map { (subTitle, subContent) ->

            SectionData(
                title = subTitle,
                content = subContent,
                titleLayout = textMeasurer.measure(
                    text = subTitle.text,
                    style = subTitle.style,
                    constraints = Constraints(maxWidth = sheetBounds.width.toInt())
                ),
                contentLayout = textMeasurer.measure(
                    text = subContent.text,
                    style = subContent.style,
                    constraints = Constraints(maxWidth = sheetBounds.width.toInt())
                ),
                startingPage = 0,
                positionY = 0F
            )
        }

        val tocEntries = initialSections.map { section ->

            textMeasurer.measure(
                text = section.title.text,
                style = templateType.indexStyle,
                constraints = Constraints(maxWidth = tocTitleMaxWidth)
            )
        }

        val tocPageCount = tocEntries.fold(
            initial = 1 to sheetBounds.top + tocTitle.size.height
        ) { (count, positionY), entry ->

            val height = entry.size.height + tocEntrySpacing

            if (positionY + height > sheetBounds.bottom - numberHeight) {
                (count + 1) to (sheetBounds.top + height)
            } else count to (positionY + height)
        }.first

        val sections = initialSections.fold(
            initial = mutableListOf<SectionData>() to (1 + tocPageCount + 1 to sheetBounds.top)
        ) { (list, pageInfo), section ->

            val (page, positionY) = pageInfo
            val fitsOnPage = positionY + section.titleLayout.size.height <= sheetBounds
                .bottom - numberHeight
            val startPage = if (fitsOnPage) page else page + 1
            val startY = if (fitsOnPage) positionY else sheetBounds.top
            val contentStartY = startY + section.titleLayout.size.height

            val (endPage, endY) = simulatePagination(
                rect = sheetBounds,
                layout = section.contentLayout,
                startPage = startPage,
                startY = contentStartY,
                numberHeight = numberHeight,
                spacing = templateType.contentItemSpace
            )

            list.add(element = section.copy(startingPage = startPage, positionY = startY))
            list to (endPage to endY)
        }.first

        (2 until 2 + tocPageCount).forEach { tocPageNumber ->

            drawPage(
                pdfDocument = pdfDocument,
                sheet = templateType.sheet,
                pageNumber = tocPageNumber,
                numberStyle = templateType.numberStyle,
                showPageNumber = true,
                background = templateType.background
            ) {

                var positionY = sheetBounds.top

                if (tocPageNumber == 2) {

                    drawText(
                        textLayoutResult = tocTitle,
                        topLeft = Offset(
                            x = sheetBounds.left + (sheetBounds.width - tocTitle.size.width) / 2,
                            y = positionY
                        )
                    )

                    positionY += tocTitle.size.height
                }

                sections.forEachIndexed { index, section ->

                    tocEntries.getOrNull(index = index)?.let { entry ->

                        val entryHeight = entry.size.height + tocEntrySpacing

                        val entryPage = tocEntries.take(n = index + 1).fold(
                            initial = 1 to sheetBounds.top + (if (tocPageNumber == 2) {
                                tocTitle.size.height.toFloat()
                            } else 0F)
                        ) { (count, currentY), entryLayout ->

                            val height = entryLayout.size.height + tocEntrySpacing
                            if (currentY + height > sheetBounds.bottom - numberHeight) {
                                (count + 1) to (sheetBounds.top + height)
                            } else count to (currentY + height)
                        }.first + 1

                        if (entryPage == tocPageNumber) {

                            val pageNumberLayout = textMeasurer.measure(
                                text = "${section.startingPage}",
                                style = templateType.indexStyle
                            )

                            drawText(
                                textLayoutResult = entry,
                                topLeft = Offset(x = sheetBounds.left, y = positionY)
                            )

                            drawText(
                                textLayoutResult = pageNumberLayout,
                                topLeft = Offset(
                                    x = sheetBounds.right - pageNumberLayout.size.width,
                                    y = positionY
                                )
                            )

                            positionY += entryHeight
                        }
                    }
                }
            }
        }

        drawPaginatedSections(
            pdfDocument = pdfDocument,
            sheet = templateType.sheet,
            rect = sheetBounds,
            sections = sections,
            numberStyle = templateType.numberStyle,
            background = templateType.background,
            pageNumberStart = 1 + tocPageCount + 1,
            numberHeight = numberHeight,
            spacing = templateType.contentItemSpace
        )
    }

    private fun getPageNumberHeight(numberStyle: TextStyle): Float {

        return textMeasurer.measure(text = "1", style = numberStyle).size.height.toFloat()
    }

    private fun CanvasDrawScope.drawPaginatedSections(
        pdfDocument: PdfDocument,
        sheet: SheetSize,
        rect: Rect,
        sections: List<SectionData>,
        numberStyle: TextStyle,
        background: PdfTemplateBackground,
        pageNumberStart: Int,
        numberHeight: Float,
        spacing: Dp
    ) {

        var sectionIdx = 0
        var lineIdx = 0
        var titleDrawn = false
        var pageNumber = pageNumberStart

        while (sectionIdx < sections.size) {

            drawPage(
                pdfDocument = pdfDocument,
                sheet = sheet,
                pageNumber = pageNumber,
                numberStyle = numberStyle,
                showPageNumber = true,
                background = background
            ) {

                var positionY = rect.top

                while (sectionIdx < sections.size) {

                    val section = sections.getOrNull(index = sectionIdx) ?: break

                    if (titleDrawn.not()) {

                        if (pageNumber == section.startingPage && lineIdx == 0) {
                            positionY = section.positionY
                        }

                        section.title.text.takeIf { it.isNotEmpty() }?.run {

                            if (positionY + section.titleLayout.size.height
                                > rect.bottom - numberHeight
                            ) return@drawPage

                            drawPdfText(
                                layout = section.titleLayout,
                                topLeft = Offset(x = rect.left, y = positionY),
                                width = rect.width,
                                alignment = section.title.alignment
                            )

                            positionY += section.titleLayout.size.height
                        }

                        titleDrawn = true
                    }

                    val result = drawPartialText(
                        layout = section.contentLayout,
                        lineStart = lineIdx,
                        top = positionY,
                        rect = rect,
                        numberHeight = numberHeight
                    )

                    positionY = result.second + spacing.toPx()
                    lineIdx = result.first

                    if (lineIdx >= section.contentLayout.lineCount) {

                        sectionIdx++
                        lineIdx = 0
                        titleDrawn = false
                    } else break
                }
            }

            pageNumber++
        }
    }

    private fun DrawScope.drawPartialText(
        layout: TextLayoutResult,
        lineStart: Int,
        top: Float,
        rect: Rect,
        numberHeight: Float
    ): Pair<Int, Float> {

        val position = layout.getLineTop(lineIndex = lineStart)
        val availableHeight = (rect.bottom - numberHeight) - top

        val j = (lineStart until layout.lineCount).firstOrNull { lineIndex ->
            layout.getLineBottom(lineIndex = lineIndex) - position > availableHeight
        } ?: layout.lineCount

        return if (j > lineStart) {

            clipRect(
                left = rect.left,
                top = top,
                right = rect.right,
                bottom = top + (layout.getLineBottom(lineIndex = j - 1) - position)
            ) {

                drawText(
                    textLayoutResult = layout,
                    topLeft = Offset(x = rect.left, y = top - position)
                )
            }

            j to (top + (layout.getLineBottom(lineIndex = j - 1) - position))
        } else lineStart to top
    }

    private fun simulatePagination(
        rect: Rect,
        layout: TextLayoutResult,
        startPage: Int,
        startY: Float,
        numberHeight: Float,
        spacing: Dp
    ): Pair<Int, Float> {

        val itemSpace = with(density) { spacing.toPx() }
        var currentPage = startPage
        var currentY = startY
        var linesProcessed = 0

        while (linesProcessed < layout.lineCount) {

            val position = layout.getLineTop(lineIndex = linesProcessed)
            val availableHeight = (rect.bottom - numberHeight) - currentY

            val j = (linesProcessed until layout.lineCount).firstOrNull { lineIndex ->
                layout.getLineBottom(lineIndex = lineIndex) - position > availableHeight
            } ?: layout.lineCount

            if (j == linesProcessed) {

                currentPage++
                currentY = rect.top
            } else {

                linesProcessed = j

                if (linesProcessed < layout.lineCount) {

                    currentPage++
                    currentY = rect.top
                } else currentY += (layout.getLineBottom(lineIndex = j - 1) - position) + itemSpace
            }
        }

        return currentPage to currentY
    }

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

            when (background) {

                is PdfTemplateBackground.SolidColor -> drawRect(color = background.color)

                is PdfTemplateBackground.Image -> background.bitmap?.let { bitmap ->

                    drawImage(image = bitmap, dstSize = size.toIntSize())
                }
            }

            drawBlock()

            if (showPageNumber) {

                textMeasurer.measure(text = "$pageNumber", style = numberStyle).let { layout ->

                    drawText(
                        textLayoutResult = layout,
                        topLeft = Offset(
                            x = (size.width - layout.size.width) / 2,
                            y = size.height - layout.size.height
                        )
                    )
                }
            }
        }

        pdfDocument.finishPage(pdfPage)
    }

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
}