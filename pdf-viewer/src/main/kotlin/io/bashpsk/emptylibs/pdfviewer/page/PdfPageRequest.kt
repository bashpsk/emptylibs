package io.bashpsk.emptylibs.pdfviewer.page

import androidx.compose.runtime.Immutable

/**
 * Represents a request to render a specific page of a PDF document at a given quality level.
 *
 * @property page The index of the page to be rendered.
 * @property quality The scale factor or resolution multiplier applied to the rendered page.
 */
@Immutable
internal data class PdfPageRequest(val page: Int, val quality: Float)