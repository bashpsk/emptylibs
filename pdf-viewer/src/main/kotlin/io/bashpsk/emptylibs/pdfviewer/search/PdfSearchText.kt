package io.bashpsk.emptylibs.pdfviewer.search

import android.graphics.pdf.PdfRenderer
import android.os.Build
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toComposeRect
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * Searches for a specific text query within the [PdfRenderer.Page] and returns a list of bounding
 * boxes for each occurrence.
 *
 * This function utilizes the native `searchText` API available in Android 15 (Vanilla Ice Cream)
 * and above. On older Android versions, it returns an empty list as native PDF text searching is
 * not supported.
 *
 * @param query The text string to search for within the page.
 * @return An [ImmutableList] where each element is an [ImmutableList] of [Rect] objects
 * representing the visual boundaries of a match(a single match may span multiple rectangles/lines).
 */
internal fun PdfRenderer.Page.getSearchRectList(
    query: String
): ImmutableList<ImmutableList<Rect>> {

    if (query.isEmpty()) return persistentListOf()

    return when {

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM -> {

            searchText(query).map { bounds ->

                bounds.bounds.map { rectF -> rectF.toComposeRect() }.toImmutableList()
            }.toImmutableList()
        }

        else -> persistentListOf()
    }
}