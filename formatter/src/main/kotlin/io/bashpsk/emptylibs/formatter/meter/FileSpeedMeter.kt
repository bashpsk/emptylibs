package io.bashpsk.emptylibs.formatter.meter

import android.util.Log
import io.bashpsk.emptylibs.formatter.extension.fileLength
import io.bashpsk.emptylibs.formatter.utils.LOG_TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Creates a [Flow] that measures the speed of a file operation and emits [FileSpeedData] at regular
 * intervals.
 *
 * This is useful for monitoring the progress of downloads, copies, or any operation where a file's
 * size changes over time.
 *
 * @param source The source file, used to determine the total size of the operation.
 * @param destination The destination file, which is being written to. Its size is monitored to
 * calculate progress and speed.
 * @param interval The time interval at which to emit new speed data.
 * @return A [Flow] that emits nullable [FileSpeedData] objects. It emits `null` if an error occurs
 * during monitoring.
 */
fun fileSpeedMeter(
    source: File,
    destination: File,
    interval: Duration = 1.seconds
): Flow<FileSpeedData?> {

    return flow {

        try {

            while (currentCoroutineContext().isActive) {

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
            Log.w(LOG_TAG, exception.message, exception)
            emit(value = null)
        }
    }.flowOn(context = Dispatchers.IO)
}

/**
 * Calculates [FileSpeedData] based on the total, current, and previous size of a file operation.
 *
 * @param total The total size of the file being transferred, in bytes.
 * @param current The current size of the file being transferred, in bytes.
 * @param previous The size of the file at the last measurement, in bytes.
 * @return A [FileSpeedData] object containing the calculated progress, speed, and ETA.
 */
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
