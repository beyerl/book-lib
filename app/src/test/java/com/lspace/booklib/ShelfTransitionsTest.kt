package com.lspace.booklib

import com.lspace.booklib.domain.model.Book
import com.lspace.booklib.domain.model.Shelf
import com.lspace.booklib.domain.model.applyShelfChange
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ShelfTransitionsTest {

    private fun book(shelf: Shelf = Shelf.READING_LIST) =
        Book(id = 1, title = "T", author = "A", shelf = shelf)

    @Test
    fun movingToNowReadingStampsStartDate() {
        val result = applyShelfChange(book(), Shelf.NOW_READING, now = 1000L)
        assertEquals(Shelf.NOW_READING, result.shelf)
        assertEquals(1000L, result.dateStartedReading)
        assertNull(result.dateFinishedReading)
    }

    @Test
    fun movingToFinishedStampsBothDates() {
        val result = applyShelfChange(book(), Shelf.FINISHED_READING, now = 2000L)
        assertEquals(2000L, result.dateStartedReading)
        assertEquals(2000L, result.dateFinishedReading)
    }

    @Test
    fun finishingPreservesExistingStartDate() {
        val reading = book(Shelf.NOW_READING).copy(dateStartedReading = 500L)
        val result = applyShelfChange(reading, Shelf.FINISHED_READING, now = 3000L)
        assertEquals(500L, result.dateStartedReading)
        assertEquals(3000L, result.dateFinishedReading)
    }

    @Test
    fun noOpWhenShelfUnchanged() {
        val reading = book(Shelf.NOW_READING).copy(dateStartedReading = 500L)
        val result = applyShelfChange(reading, Shelf.NOW_READING, now = 9999L)
        assertEquals(500L, result.dateStartedReading)
    }

    @Test
    fun movingToReadingListKeepsExistingDates() {
        val finished = book(Shelf.FINISHED_READING)
            .copy(dateStartedReading = 1L, dateFinishedReading = 2L)
        val result = applyShelfChange(finished, Shelf.READING_LIST, now = 3000L)
        assertEquals(1L, result.dateStartedReading)
        assertEquals(2L, result.dateFinishedReading)
    }
}
