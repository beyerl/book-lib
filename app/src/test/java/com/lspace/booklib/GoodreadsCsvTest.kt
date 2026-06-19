package com.lspace.booklib

import com.lspace.booklib.data.importexport.Csv
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
    fun importsRealGoodreadsExportAcrossShelvesAndRatings() {
        // Full Goodreads export header with the columns Goodreads actually emits.
        val header = "Book Id,Title,Author,Author l-f,Additional Authors,ISBN,ISBN13,My Rating," +
            "Average Rating,Publisher,Binding,Number of Pages,Year Published,Original Publication Year," +
            "Date Read,Date Added,Bookshelves,Bookshelves with positions,Exclusive Shelf,My Review," +
            "Spoiler,Private Notes,Read Count,Owned Copies"
        fun row(vararg cells: String) = Csv.encodeRow(cells.toList())
        val csv = buildString {
            append(header).append('\n')
            // read + rating + review
            append(row("1", "Mort", "Terry Pratchett", "", "", "=\"\"", "=\"9780062225719\"", "5",
                "4.2", "Harper", "Paperback", "243", "1987", "1987", "2023-05-01", "2023-01-01",
                "fantasy", "", "read", "Loved it", "false", "", "1", "1")).append('\n')
            // currently-reading
            append(row("2", "Dune", "Frank Herbert", "", "", "", "", "0",
                "4.3", "Ace", "Paperback", "688", "1965", "1965", "", "2024-02-02",
                "sci-fi", "", "currently-reading", "", "false", "", "0", "1")).append('\n')
            // to-read
            append(row("3", "Neuromancer", "William Gibson", "", "", "", "", "0",
                "4.0", "Ace", "Paperback", "271", "1984", "1984", "", "2024-03-03",
                "", "", "to-read", "", "false", "", "0", "0")).append('\n')
            // exclusive shelf is "read", but a custom "did-not-finish" bookshelf marks it abandoned
            append(row("4", "Ulysses", "James Joyce", "", "", "", "", "2",
                "3.7", "Vintage", "Paperback", "783", "1922", "1922", "", "2024-04-04",
                "did-not-finish, classics", "", "read", "", "false", "", "1", "1")).append('\n')
        }

        val imported = GoodreadsCsv.import(csv)
        assertEquals(4, imported.size)

        val mort = imported.first { it.title == "Mort" }
        assertEquals(Shelf.FINISHED_READING, mort.shelf)
        assertEquals(5, mort.rating)
        assertEquals("Loved it", mort.review)
        assertEquals("9780062225719", mort.isbn) // falls back to ISBN13 when ISBN is empty

        assertEquals(Shelf.NOW_READING, imported.first { it.title == "Dune" }.shelf)
        assertEquals(Shelf.READING_LIST, imported.first { it.title == "Neuromancer" }.shelf)

        val ulysses = imported.first { it.title == "Ulysses" }
        assertEquals(Shelf.STOPPED_READING, ulysses.shelf)
        assertEquals(2, ulysses.rating)
    }

    @Test
    fun importStripsLeadingByteOrderMark() {
        val csv = "\uFEFFTitle,Author,ISBN,My Rating,Date Read,Date Added,Bookshelves,Exclusive Shelf\n" +
            "Book,Auth,,3,,,,read\n"
        val imported = GoodreadsCsv.import(csv)
        assertEquals(1, imported.size)
        assertEquals(Shelf.FINISHED_READING, imported[0].shelf)
        assertEquals(3, imported[0].rating)
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
