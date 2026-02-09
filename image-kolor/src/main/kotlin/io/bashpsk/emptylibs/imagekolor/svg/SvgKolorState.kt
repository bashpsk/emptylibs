package io.bashpsk.emptylibs.imagekolor.svg

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import io.bashpsk.emptylibs.formatter.format.toHexString
import io.bashpsk.emptylibs.imagekolor.utils.LOG_TAG
import io.bashpsk.emptylibs.serializationxml.Xml
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi

/**
 * Creates and remembers a [SvgKolorState] instance.
 *
 * @param source The SVG source string.
 * @return A remembered [SvgKolorState].
 */
@Composable
fun rememberSvgKolorState(source: String): SvgKolorState {

    return retain(source) { SvgKolorState(source = source) }
}

/**
 * State holder for SVG recoloring logic.
 *
 * @property source The original SVG source string.
 */
@Stable
class SvgKolorState(val source: String) {

    /**
     * Coroutine scope used for performing background tasks such as parsing the SVG source
     * and applying color transformations on a background dispatcher.
     */
    private val coroutineScope = CoroutineScope(context = SupervisorJob() + Dispatchers.Default)

    /**
     * Regex pattern to identify hex color codes in the SVG.
     */
    private val ColorHexRegex = Regex(pattern = "#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})\\b")

    /**
     * List of recolorable SVG elements extracted from the source.
     */
    var hexKolorDataList by mutableStateOf(persistentListOf<SvgKolorElement>())
        private set

    /**
     * The currently selected element for recoloring.
     */
    var selectedHex by mutableStateOf<SvgKolorElement?>(null)
        private set

    /**
     * The updated SVG source string with applied color changes.
     */
    var newSource by mutableStateOf(source)
        private set

    /**
     * The viewBox of the SVG.
     */
    var viewBox by mutableStateOf("0 0 24 24")
        private set

    init {

        coroutineScope.launch { hexKolorDataList = getKolorHexList(content = source) }
    }

    /**
     * Updates the color of a specific element.
     *
     * @param originalHex The original element to be updated.
     * @param newColor The new color to apply.
     */
    fun updateColor(originalHex: SvgKolorElement?, newColor: Color) {

        originalHex?.let { hex ->

            findIndex(element = hex)?.let { existIndex ->

                hexKolorDataList = hexKolorDataList.set(
                    index = existIndex,
                    element = hex.copy(newHex = newColor.toHexString(includeAlpha = false))
                )
            }
        }

        coroutineScope.launch { newSource = getColoredSvg() }
    }

    /**
     * Updates the selected hex element.
     *
     * @param newHex The element to select, or null to clear selection.
     */
    fun updateSelectedHex(newHex: SvgKolorElement?) {

        selectedHex = findIndex(element = newHex)?.let { existIndex ->

            hexKolorDataList.getOrNull(index = existIndex)
        }
    }

    /**
     * Generates the SVG string with current color replacements.
     *
     * @return The updated SVG source string.
     */
    suspend fun getColoredSvg(): String = withContext(context = Dispatchers.Default) {

        val replacements = hexKolorDataList.iterator()

        ColorHexRegex.replace(input = source) { result ->

            when (replacements.hasNext()) {

                true -> replacements.next().takeIf { kolor ->

                    kolor.oldHex.equals(other = result.value, ignoreCase = true)
                }?.newHex ?: result.value

                false -> result.value
            }
        }
    }

    /**
     * Extracts recolorable elements from the SVG content.
     *
     * @param content The SVG content string.
     * @return A list of extracted elements.
     */
    @OptIn(ExperimentalSerializationApi::class)
    private suspend fun getKolorHexList(
        content: String
    ): PersistentList<SvgKolorElement> = withContext(context = Dispatchers.Default) {

        try {

            val svgRoot = Xml.decodeFromString<SvgRoot>(content = content)

            viewBox = svgRoot.viewBox

            persistentListOf(
                svgRoot.paths,
                svgRoot.rects,
                svgRoot.circles,
                svgRoot.ellipses,
                svgRoot.lines,
                svgRoot.polylines,
                svgRoot.polygons,
                svgRoot.texts
            ).flatten().filter { kolor ->

                ColorHexRegex.matches(input = kolor.oldHex)
            }.map { kolor ->

                kolor.copy(newHex = kolor.oldHex)
            }.toPersistentList()
        } catch (exception: Exception) {

            currentCoroutineContext().ensureActive()
            Log.e(LOG_TAG, exception.message, exception)
            persistentListOf()
        }
    }

    /**
     * Finds the index of an element in [hexKolorDataList].
     *
     * @param element The element to find.
     * @return The index of the element, or null if not found.
     */
    private fun findIndex(element: SvgKolorElement?): Int? {

        if (element == null) return null

        return hexKolorDataList.indexOfFirst { kolor ->

            kolor.index == element.index && kolor.oldHex == element.oldHex
        }.takeIf { index -> index in hexKolorDataList.indices }
    }
}