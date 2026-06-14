package com.lspace.booklib

import com.lspace.booklib.data.importexport.GoodreadsCsv
import com.lspace.booklib.domain.DateUtil
import com.lspace.booklib.domain.model.Book
import com.lspace.booklib.domain.model.Shelf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GoodreadsCsvTest {

    @Test
    fun exportThenImportRoundTrips() {
        val finishedDate = DateUtil.parseOrNull("2023-05-01")!!
        val books = listOf(
            Book(
                id = 1, title = "Mort", author = "Terry Pratchett", isbn = "9780062225719",
                rating = 5, shelf = Shelf.FINISHED_READING,
                dateAdded = DateUtil.parseOrNull("2023-01-01")!!,
                dateFinishedReading = finishedDate,
            ),
            Book(id = 2, title = "Dune", author = "Frank Herbert", shelf = Shelf.READING_LIST, dateAdded = 1L),
        )

        val csv = GoodreadsCsv.export(books)
        val imported = GoodreadsCsv.import(csv)

        assertEquals(2, imported.size)
        val mort = imported.first { it.title == "Mort" }
        assertEquals("Terry Pratchett", mort.author)
        assertEquals(5, mort.rating)
        assertEquals(Shelf.FINISHED_READING, mort.shelf)
        assertEquals(finishedDate, mort.dateFinishedReading)

        val dune = imported.first { it.title == "Dune" }
        assertEquals(Shelf.READING_LIST, dune.shelf)
    }

    @Test
    fun importHandlesQuotedFieldsWithCommas() {
        val csv = buildString {
            append("Title,Author,ISBN,My Rating,Date Read,Date Added,Bookshelves,Exclusive Shelf\n")
            append("\"Hitchhiker's Guide, The\",\"Adams, Douglas\",,4,,,,read\n")
        }
        val imported = GoodreadsCsv.import(csv)
        assertEquals(1, imported.size)
        assertEquals("Hitchhiker's Guide, The", imported[0].title)
        assertEquals("Adams, Douglas", imported[0].author)
        assertEquals(Shelf.FINISHED_READING, imported[0].shelf)
        assertEquals(4, imported[0].rating)
    }

    @Test
    fun goodreadsExclusiveShelvesMapCorrectly() {
        assertEquals(Shelf.READING_LIST, Shelf.fromGoodreads("to-read"))
        assertEquals(Shelf.NOW_READING, Shelf.fromGoodreads("currently-reading"))
        assertEquals(Shelf.FINISHED_READING, Shelf.fromGoodreads("read"))
        assertEquals(Shelf.STOPPED_READING, Shelf.fromGoodreads("did-not-finish"))
        assertEquals(Shelf.READING_LIST, Shelf.fromGoodreads(null))
    }

    @Test
    fun importStripsGoodreadsIsbnEscaping() {
        val csv = buildString {
            append("Title,Author,ISBN,My Rating,Date Read,Date Added,Bookshelves,Exclusive Shelf\n")
            append("Book,Auth,\"=\"\"9780000000001\"\"\",0,,,,to-read\n")
        }
        val imported = GoodreadsCsv.import(csv)
        assertEquals("9780000000001", imported[0].isbn)
    }

    @Test
    fun zeroRatingBecomesNull() {
        val csv = buildString {
            append("Title,Author,ISBN,My Rating,Date Read,Date Added,Bookshelves,Exclusive Shelf\n")
            append("Book,Auth,,0,,,,to-read\n")
        }
        val imported = GoodreadsCsv.import(csv)
        assertTrue(imported[0].rating == null)
    }
}
