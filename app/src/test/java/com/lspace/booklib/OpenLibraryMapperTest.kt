package com.lspace.booklib

import com.lspace.booklib.data.remote.dto.SearchDocDto
import com.lspace.booklib.data.remote.toBook
import com.lspace.booklib.data.remote.workIdOrNull
import com.lspace.booklib.domain.model.Shelf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OpenLibraryMapperTest {

    @Test
    fun mapsSearchDocToBook() {
        val doc = SearchDocDto(
            key = "/works/OL45883W",
            title = "Mort",
            authorNames = listOf("Terry Pratchett", "Someone Else"),
            firstPublishYear = 1987,
            coverId = 12345,
            isbns = listOf("9780062225719"),
            pageCountMedian = 243,
        )
        val book = doc.toBook()
        assertEquals("Mort", book.title)
        assertEquals("Terry Pratchett", book.author)
        assertEquals(1987, book.yearPublished)
        assertEquals(243, book.pageCount)
        assertEquals("9780062225719", book.isbn)
        assertEquals(Shelf.READING_LIST, book.shelf)
        assertEquals("https://covers.openlibrary.org/b/id/12345-M.jpg", book.coverUrl)
        assertEquals(0L, book.id)
    }

    @Test
    fun missingTitleFallsBackToUntitled() {
        val book = SearchDocDto(title = null).toBook()
        assertEquals("Untitled", book.title)
        assertEquals("", book.author)
        assertNull(book.coverUrl)
    }

    @Test
    fun extractsWorkId() {
        assertEquals("OL45883W", "/works/OL45883W".workIdOrNull())
        assertEquals("OL45883W", "OL45883W".workIdOrNull())
        assertNull("/authors/OL12A".workIdOrNull())
    }
}
