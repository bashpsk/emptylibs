package io.bashpsk.emptylibs.formatter.meter

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Measures the speed of a file operation and update [FileSpeedData] at regular
 * intervals.
 *
 * This is useful for monitoring the progress of downloads, copies, or any operation where a file's
 * size changes over time.
 *
 * @param source The source file, used to determine the total size of the operation.
 * @param destination The destination file, which is being written to. Its size is monitored to
 * calculate progress and speed.
 * @param interval The time interval at which to emit new speed data.
 * @param onSpeedChange Update an operation [FileSpeedData].
 */
@Throws(IOException::class, FileNotFoundException::class, SecurityException::class)
suspend inline fun fileSpeedMeter(
    source: File,
    destination: File,
    interval: Duration = 1.seconds,
    crossinline onSpeedChange: suspend (FileSpeedData) -> Unit
) = withContext(context = Dispatchers.IO) {

    while (currentCoroutineContext().isActive) {

        val previous = destination.length()

        delay(duration = interval)
        currentCoroutineContext().ensureActive()

        val newSpeedData = fileSpeedMeter(
            total = source.length(),
            current = destination.length(),
            previous = previous
        )

        onSpeedChange(newSpeedData)
    }
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