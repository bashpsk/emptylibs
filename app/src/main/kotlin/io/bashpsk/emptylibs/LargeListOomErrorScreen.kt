package io.bashpsk.emptylibs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.bashpsk.emptylibs.utils.setDebug
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun LargeListOomErrorScreen() {

    LaunchedEffect(Unit) {

//        getImmutableList().let { result ->
        getNormalList().let { result ->

            "${result.size}, ${result.lastOrNull()}".setDebug()
        }
    }
}

private const val count = 8_500_000L

// Directly map as ImmutableList; Does not got OOM error.
private suspend fun getImmutableList(): ImmutableList<Long> = withContext(Dispatchers.Default) {

    return@withContext persistentListOf<Long>().mutate { list ->

        (1L..count).forEach { index ->

            if (index == count) "FINISH".setDebug()
            list.add(index + 2)
        }
    }.also { "Make Immutable".setDebug() }
}

// Does not convertible to another type; Its got OOM error
private suspend fun getNormalList(): Array<Long> = withContext(Dispatchers.Default) {

    return@withContext (1L..count).map { index ->

        if (index == count) "FINISH".setDebug()
        index + 2
    }.toTypedArray().also { "Make Immutable".setDebug() }
}