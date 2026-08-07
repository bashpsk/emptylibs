package io.bashpsk.emptylibs.formatter.extension

import kotlinx.datetime.LocalDateTime
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DateTimeExtTest {

    @Test
    fun hasAM_returnsTrue_forMorningHours() {

        val midnight = LocalDateTime(2023, 10, 27, 0, 0)
        val morning = LocalDateTime(2023, 10, 27, 8, 30)
        val justBeforeNoon = LocalDateTime(2023, 10, 27, 11, 59)

        assertTrue("00:00 should be AM", midnight.hasAM())
        assertTrue("08:30 should be AM", morning.hasAM())
        assertTrue("11:59 should be AM", justBeforeNoon.hasAM())
    }

    @Test
    fun hasAM_returnsFalse_forAfternoonAndEveningHours() {

        val noon = LocalDateTime(2023, 10, 27, 12, 0)
        val afternoon = LocalDateTime(2023, 10, 27, 15, 45)
        val justBeforeMidnight = LocalDateTime(2023, 10, 27, 23, 59)

        assertFalse("12:00 should not be AM", noon.hasAM())
        assertFalse("15:45 should not be AM", afternoon.hasAM())
        assertFalse("23:59 should not be AM", justBeforeMidnight.hasAM())
    }

    @Test
    fun hasPM_returnsTrue_forAfternoonAndEveningHours() {

        val noon = LocalDateTime(2023, 10, 27, 12, 0)
        val afternoon = LocalDateTime(2023, 10, 27, 15, 45)
        val justBeforeMidnight = LocalDateTime(2023, 10, 27, 23, 59)

        assertTrue("12:00 should be PM", noon.hasPM())
        assertTrue("15:45 should be PM", afternoon.hasPM())
        assertTrue("23:59 should be PM", justBeforeMidnight.hasPM())
    }

    @Test
    fun hasPM_returnsFalse_forMorningHours() {

        val midnight = LocalDateTime(2023, 10, 27, 0, 0)
        val morning = LocalDateTime(2023, 10, 27, 8, 30)
        val justBeforeNoon = LocalDateTime(2023, 10, 27, 11, 59)

        assertFalse("00:00 should not be PM", midnight.hasPM())
        assertFalse("08:30 should not be PM", morning.hasPM())
        assertFalse("11:59 should not be PM", justBeforeNoon.hasPM())
    }
}