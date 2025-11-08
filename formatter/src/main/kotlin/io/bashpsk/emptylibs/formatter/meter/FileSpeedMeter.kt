package io.bashpsk.emptylibs.formatter.meter

import io.bashpsk.emptylibs.formatter.extension.fileLength
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun fileSpeedMeter(
    source: File,
    destination: File,
    interval: Duration = 1000.milliseconds
): Flow<FileSpeedData?> {

    return flow {

        try {

            while (source.length() >= destination.length()) {

                val previous = destination.fileLength()

                currentCoroutineContext().ensureActive()
                delay(duration = interval)

                val newSpeedData = fileSpeedMeter(
                    total = source.fileLength(),
                    current = destination.fileLength(),
                    previous = previous
                )

                emit(value = newSpeedData)
            }
        } catch (exception: Exception) {

            currentCoroutineContext().ensureActive()
            emit(value = null)
        }
    }
}

fun fileSpeedMeter(total: Long, current: Long, previous: Long): FileSpeedData {

    val remaining = (total - current).coerceIn(range = 0L..total)
    val speed = (current - previous).coerceIn(range = 0L..total)

    return FileSpeedData(
        total = total,
        current = current,
        remaining = remaining,
        speed = speed,
        eta = if (speed > 0) remaining / speed else 0L
    )
}