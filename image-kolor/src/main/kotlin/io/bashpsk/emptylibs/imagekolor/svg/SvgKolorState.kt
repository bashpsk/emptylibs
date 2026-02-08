package io.bashpsk.emptylibs.imagekolor.svg

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import io.bashpsk.emptylibs.imagekolor.utils.LOG_TAG
import io.bashpsk.emptylibs.serializationxml.Xml
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi

@Composable
fun rememberSvgKolorState(source: String): SvgKolorState {

    val coroutineScope = rememberCoroutineScope()

    return retain(coroutineScope, source) {
        SvgKolorState(coroutineScope = coroutineScope, source = source)
    }
}

@Stable
class SvgKolorState(
    val coroutineScope: CoroutineScope,
    val source: String
) {

    private val ColorHexRegex = Regex(pattern = "#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})\\b")

    var hexKolorDataList by mutableStateOf(persistentListOf<SvgKolorElement>())
        private set

    var selectedHex by mutableStateOf<SvgKolorElement?>(null)
        private set

    var newSource by mutableStateOf(source)
        private set

    var viewBox by mutableStateOf("0 0 24 24")
        private set

    init {

        coroutineScope.launch { hexKolorDataList = getKolorHexList(content = source) }
    }

    fun updateColor(originalHex: SvgKolorElement?, newColor: Color) {

        originalHex?.let { hex ->

            findIndex(element = hex)?.let { existIndex ->

                hexKolorDataList = hexKolorDataList.set(
                    index = existIndex,
                    element = hex.copy(newHex = newColor.toSvgHexString())
                )
            }
        }

        coroutineScope.launch { newSource = getColoredSvg() }
    }

    fun updateSelectedHex(newHex: SvgKolorElement?) {

        selectedHex = findIndex(element = newHex)?.let { existIndex ->

            hexKolorDataList.getOrNull(index = existIndex)
        }
    }

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

    private fun findIndex(element: SvgKolorElement?): Int? {

        if (element == null) return null

        return hexKolorDataList.indexOfFirst { kolor ->

            kolor.index == element.index && kolor.oldHex == element.oldHex
        }.takeIf { index -> index in hexKolorDataList.indices }
    }
}