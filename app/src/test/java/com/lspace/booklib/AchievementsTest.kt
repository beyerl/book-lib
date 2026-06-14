package com.lspace.booklib

import com.lspace.booklib.domain.model.Book
import com.lspace.booklib.domain.model.Shelf
import com.lspace.booklib.domain.model.booksFinishedInYear
import com.lspace.booklib.domain.model.yearSummaries
import org.junit.Assert.assertEquals
import org.junit.Test

class AchievementsTest {

    // Encode the year directly in the timestamp for a deterministic epoch->year stub.
    private val epochToYear: (Long) -> Int = { it.toInt() }

    private fun finished(year: Int) = Book(
        id = year.toLong(), title = "B$year", author = "A",
        shelf = Shelf.FINISHED_READING, dateFinishedReading = year.toLong(),
    )

    @Test
    fun yearSummariesGroupAndSortDescending() {
        val books = listOf(
            finished(2022), finished(2023), finished(2023), finished(2021),
            Book(id = 99, title = "Reading", author = "A", shelf = Shelf.NOW_READING),
        )
        val summaries = yearSummaries(books, epochToYear)
        assertEquals(listOf(2023, 2022, 2021), summaries.map { it.year })
        assertEquals(2, summaries.first { it.year == 2023 }.booksFinished)
    }

    @Test
    fun booksFinishedInYearCountsOnlyFinished() {
        val books = listOf(
            finished(2024), finished(2024),
            Book(id = 5, title = "X", author = "A", shelf = Shelf.READING_LIST, dateFinishedReading = 2024L),
        )
        assertEquals(2, booksFinishedInYear(books, 2024, epochToYear))
        assertEquals(0, booksFinishedInYear(books, 2025, epochToYear))
    }
}
