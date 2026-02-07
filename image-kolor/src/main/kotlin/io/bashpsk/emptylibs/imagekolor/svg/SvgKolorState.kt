package io.bashpsk.emptylibs.imagekolor.svg

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun rememberSvgKolorState(source: String = ""): SvgKolorState {

    val coroutineScope = rememberCoroutineScope()

    return retain(coroutineScope, source) {
        SvgKolorState(coroutineScope = coroutineScope, source = source)
    }
}

@Stable
class SvgKolorState(
    internal val coroutineScope: CoroutineScope,
    internal val source: String
) {

    private val ColorHexRegex = Regex(pattern = "#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})\\b")

    var hexKolorDataList by mutableStateOf(persistentListOf<SvgKolorData>())
        private set

    var selectedHex by mutableStateOf<SvgKolorData?>(null)
        private set

    init {

        coroutineScope.launch { hexKolorDataList = getKolorHexList(source) }
    }

    fun updateColor(originalHex: SvgKolorData?, newColor: Color) {

        originalHex?.let { hex ->

            findIndex(element = hex)?.let { existIndex ->

                hexKolorDataList = hexKolorDataList.set(
                    index = existIndex,
                    element = hex.copy(newHex = newColor.toSvgHexString())
                )
            }
        }
    }

    fun updateSelectedHex(newHex: SvgKolorData?) {

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

    private suspend fun getKolorHexList(
        content: String
    ): PersistentList<SvgKolorData> = withContext(context = Dispatchers.Default) {

        return@withContext ColorHexRegex.findAll(input = content).mapIndexed { index, result ->
            SvgKolorData(id = index, oldHex = result.value, newHex = result.value)
        }.toPersistentList()
    }

    private fun Color.toSvgHexString(): String {

        return "#%06X".format(this.toArgb() and 0x00FFFFFF)
    }

    private fun findIndex(element: SvgKolorData?): Int? {

        if (element == null) return null

        return hexKolorDataList.indexOfFirst { kolor ->

            kolor.id == element.id && kolor.oldHex == element.oldHex
        }.takeIf { index -> index in hexKolorDataList.indices }
    }
}