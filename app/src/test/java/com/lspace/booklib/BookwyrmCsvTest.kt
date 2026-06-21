package com.lspace.booklib

import com.lspace.booklib.data.importexport.BookwyrmCsv
import com.lspace.booklib.data.importexport.Csv
import com.lspace.booklib.data.importexport.CsvImport
import com.lspace.booklib.domain.DateUtil
import com.lspace.booklib.domain.model.Book
import com.lspace.booklib.domain.model.Shelf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BookwyrmCsvTest {

    // The real BookWyrm export header, verbatim.
    private val header = "title,author_text,remote_id,openlibrary_key,finna_key,libris_key," +
        "inventaire_id,librarything_key,goodreads_key,bnf_id,viaf,wikidata,asin,aasin,isfdb," +
        "isbn_10,isbn_13,oclc_number,pages,start_date,finish_date,stopped_date,rating," +
        "review_name,review_cw,review_content,review_published,shelf,shelf_name,shelf_date"

    @Test
    fun exportThenImportRoundTrips() {
        val finished = DateUtil.parseOrNull("2023-05-01")!!
        val started = DateUtil.parseOrNull("2023-04-01")!!
        val books = listOf(
            Book(
                id = 1, title = "Mort", author = "Terry Pratchett", isbn = "9780062225719",
                openLibraryKey = "OL123M", pageCount = 243, rating = 5,
                review = "Loved it", shelf = Shelf.FINISHED_READING,
                dateAdded = DateUtil.parseOrNull("2023-01-01")!!,
                dateStartedReading = started, dateFinishedReading = finished,
            ),
            Book(id = 2, title = "Dune", author = "Frank Herbert", shelf = Shelf.READING_LIST, dateAdded = 1L),
        )

        val csv = BookwyrmCsv.export(books)
        val imported = BookwyrmCsv.import(csv)

        assertEquals(2, imported.size)
        val mort = imported.first { it.title == "Mort" }
        assertEquals("Terry Pratchett", mort.author)
        assertEquals("OL123M", mort.openLibraryKey)
        assertEquals("9780062225719", mort.isbn)
        assertEquals(243, mort.pageCount)
        assertEquals(5, mort.rating)
        assertEquals("Loved it", mort.review)
        assertEquals(Shelf.FINISHED_READING, mort.shelf)
        assertEquals(started, mort.dateStartedReading)
        assertEquals(finished, mort.dateFinishedReading)

        assertEquals(Shelf.READING_LIST, imported.first { it.title == "Dune" }.shelf)
    }

    @Test
    fun shelvesMapAcrossAllStates() {
        fun row(vararg cells: String) = Csv.encodeRow(cells.toList())
        val csv = buildString {
            append(header).append('\n')
            append(blank("To Read book", shelf = "to-read")).append('\n')
            append(blank("Reading book", shelf = "reading")).append('\n')
            append(blank("Read book", shelf = "read", finish = "2024-01-02")).append('\n')
            append(blank("Stopped book", shelf = "read", stopped = "2024-02-03")).append('\n')
        }

        val imported = CsvImport.import(csv)
        assertEquals(4, imported.size)
        assertEquals(Shelf.READING_LIST, imported.first { it.title == "To Read book" }.shelf)
        assertEquals(Shelf.NOW_READING, imported.first { it.title == "Reading book" }.shelf)
        assertEquals(Shelf.FINISHED_READING, imported.first { it.title == "Read book" }.shelf)

        // A populated stopped_date marks the book as abandoned even when the shelf says "read".
        val stopped = imported.first { it.title == "Stopped book" }
        assertEquals(Shelf.STOPPED_READING, stopped.shelf)
        assertEquals(DateUtil.parseOrNull("2024-02-03"), stopped.dateFinishedReading)
    }

    @Test
    fun decimalRatingsRoundToIntegerScale() {
        val csv = buildString {
            append(header).append('\n')
            append(blank("Four", shelf = "read", rating = "4.00")).append('\n')
            append(blank("RoundUp", shelf = "read", rating = "4.50")).append('\n')
            append(blank("Unrated", shelf = "to-read", rating = "")).append('\n')
        }
        val imported = CsvImport.import(csv)
        assertEquals(4, imported.first { it.title == "Four" }.rating)
        assertEquals(5, imported.first { it.title == "RoundUp" }.rating)
        assertNull(imported.first { it.title == "Unrated" }.rating)
    }

    @Test
    fun importStripsLeadingByteOrderMark() {
        val csv = "﻿" + header + "\n" + blank("BomBook", shelf = "read") + "\n"
        val imported = CsvImport.import(csv)
        assertEquals(1, imported.size)
        assertEquals(Shelf.FINISHED_READING, imported[0].shelf)
    }

    @Test
    fun csvImportDetectsBookwyrmOverGoodreads() {
        val csv = header + "\n" + blank("Detected", shelf = "reading") + "\n"
        val imported = CsvImport.import(csv)
        assertEquals(1, imported.size)
        assertEquals(Shelf.NOW_READING, imported[0].shelf)
    }

    @Test
    fun exportUsesBookwyrmShelfIdentifiersAndDecimalRating() {
        val books = listOf(
            Book(id = 1, title = "T", author = "A", rating = 3, shelf = Shelf.NOW_READING, dateAdded = 1L),
        )
        val out = BookwyrmCsv.export(books)
        assertTrue(out.lineSequence().first().startsWith("title,author_text"))
        val dataLine = out.lineSequence().drop(1).first()
        assertTrue(dataLine.contains("reading"))
        assertTrue(dataLine.contains("3.00"))
    }

    // Builds a full-width BookWyrm row with only the fields we care about populated.
    private fun blank(
        title: String,
        shelf: String,
        rating: String = "",
        start: String = "",
        finish: String = "",
        stopped: String = "",
    ): String = Csv.encodeRow(
        listOf(
            title, "Author", "", "", "", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", start, finish, stopped, rating, "", "", "", "", shelf, shelf, "2024-01-01",
        ),
    )
}
