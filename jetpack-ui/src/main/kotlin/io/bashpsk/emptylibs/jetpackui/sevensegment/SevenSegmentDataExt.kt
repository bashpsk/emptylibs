package io.bashpsk.emptylibs.jetpackui.sevensegment

import io.bashpsk.emptylibs.jetpackui.sevensegment.SevenSegmentData.Companion.Empty
import io.bashpsk.emptylibs.jetpackui.sevensegment.SevenSegmentData.Companion.NumberSegmentList
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * Finds the 7-segment data for a character, or returns null if the character is not in the model.
 *
 * @param model The map of characters to their corresponding 7-segment data.
 * @return The 7-segment data for the character, or null if the character is not in the model.
 */
fun Char?.findSegmentDataOrNull(
    model: ImmutableMap<Char, SevenSegmentData> = NumberSegmentList
): SevenSegmentData? {

    return model[this]
}

/**
 * Finds the 7-segment data for a character.
 *
 * @param model The map of characters to their corresponding 7-segment data.
 * @return The 7-segment data for the character, or [SevenSegmentData.Empty] if the character is not
 * in the model.
 */
fun Char?.findSegmentData(
    model: ImmutableMap<Char, SevenSegmentData> = NumberSegmentList
): SevenSegmentData {

    return this?.findSegmentDataOrNull(model = model) ?: Empty
}

/**
 * Finds the 7-segment data for a string.
 *
 * @param model The map of characters to their corresponding 7-segment data.
 * @return A list of 7-segment data for the string.
 */
fun String?.findSegmentData(
    model: ImmutableMap<Char, SevenSegmentData> = NumberSegmentList
): ImmutableList<SevenSegmentData> {

    return this?.map { char ->

        char.findSegmentData(model = model)
    }?.toImmutableList() ?: persistentListOf(Empty)
}

/**
 * Checks if the 7-segment data represents a dot or a colon.
 *
 * @return True if the data is a dot or a colon, false otherwise.
 */
fun SevenSegmentData.hasDotOrColon(): Boolean {

    return this == SevenSegmentData.Dot || this == SevenSegmentData.Colon
}