package io.bashpsk.emptylibs.datastoreui.font

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import io.bashpsk.emptylibs.datastoreui.extension.toReverseMap

/**
 * Remembers the font resource ID associated with a given font ID string.
 *
 * This composable function takes a font ID string and a map of font resource IDs to font ID
 * strings.
 * It returns a [State] object that holds the font resource ID (as an [Int]) corresponding to the
 * provided font ID string. If no matching font ID is found in the `entities` map, the state will
 * hold `null`.
 *
 * The function uses `remember` and `derivedStateOf` to efficiently update the result only when
 * the `id` or `entities` change. It also pre-computes a reversed map for faster lookups.
 *
 * @param id The string identifier of the font to look up.
 * @param entities A map where keys are font resource IDs (e.g., `R.font.my_font`) and
 *   values are their corresponding string identifiers.
 * @return A [State] holding the font resource ID (`Int?`) for the given `id`, or `null` if not
 * found.
 */
@Composable
fun rememberFontRes(id: String, entities: Map<Int, String>): State<Int?> {

    val reverseEntities by remember(entities) { derivedStateOf { entities.toReverseMap() } }

    return remember(reverseEntities, id) { derivedStateOf { reverseEntities[id] } }
}