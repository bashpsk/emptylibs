package io.bashpsk.emptylibs.formatter.extension

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.File

class FileExtTest {

    @Test
    fun fileLengthOrNull_existingFile_returnsLength() {

        val file = File.createTempFile("test", ".txt")
        file.writeText("Hello")

        assertEquals(5L, file.fileLengthOrNull())

        file.delete()
    }

    @Test
    fun fileLength_existingFile_returnsLength() {

        val file = File.createTempFile("test", ".txt")
        file.writeText("Hello")

        assertEquals(5L, file.fileLength())

        file.delete()
    }

    @Test
    fun fileLengthOrNull_nonExistingFile_returnsNull() {

        val file = File("does-not-exist-${System.nanoTime()}.txt")

        assertNull(file.fileLengthOrNull())
    }

    @Test
    fun fileLength_nonExistingFile_returnsZero() {

        val file = File("does-not-exist-${System.nanoTime()}.txt")

        assertEquals(0L, file.fileLength())
    }

    @Test
    fun fileLength_emptyFile_returnsZero() {

        val file = File.createTempFile("empty", ".txt")

        assertEquals(0L, file.fileLength())

        file.delete()
    }
}