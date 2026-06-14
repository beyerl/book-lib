package com.lspace.booklib.data.importexport

import com.lspace.booklib.domain.DateUtil
import com.lspace.booklib.domain.model.Book
import com.lspace.booklib.domain.model.Shelf
import com.lspace.booklib.domain.model.Shelf.Companion.toGoodreads

/** Goodreads-compatible CSV export/import (a practical subset of Goodreads columns). */
object GoodreadsCsv {

    private val HEADER = listOf(
        "Title",
        "Author",
        "ISBN",
        "My Rating",
        "Date Read",
        "Date Added",
        "Bookshelves",
        "Exclusive Shelf",
    )

    fun export(books: List<Book>): String {
        val sb = StringBuilder()
        sb.append(Csv.encodeRow(HEADER)).append('\n')
        for (b in books) {
            sb.append(
                Csv.encodeRow(
                    listOf(
                        b.title,
                        b.author,
                        b.isbn.orEmpty(),
                        b.rating?.toString() ?: "0",
                        DateUtil.format(b.dateFinishedReading),
                        DateUtil.format(b.dateAdded.takeIf { it > 0 }),
                        b.shelf.displayName,
                        b.shelf.toGoodreads(),
                    ),
                ),
            ).append('\n')
        }
        return sb.toString()
    }

    /** Parses Goodreads CSV text into non-persisted books. Unknown columns are ignored. */
    fun import(text: String): List<Book> {
        val rows = Csv.parse(text)
        if (rows.isEmpty()) return emptyList()
        val header = rows.first().map { it.trim() }
        fun idx(name: String) = header.indexOfFirst { it.equals(name, ignoreCase = true) }
        val iTitle = idx("Title")
        val iAuthor = idx("Author")
        val iIsbn = idx("ISBN")
        val iRating = idx("My Rating")
        val iDateRead = idx("Date Read")
        val iDateAdded = idx("Date Added")
        val iExclusive = idx("Exclusive Shelf")

        fun List<String>.at(i: Int): String? =
            if (i in 0..lastIndex) this[i].trim().cleanIsbn().takeIf { it.isNotEmpty() } else null

        return rows.drop(1)
            .filter { it.any { cell -> cell.isNotBlank() } }
            .mapNotNull { cols ->
                val title = if (iTitle in 0..cols.lastIndex) cols[iTitle].trim() else ""
                if (title.isEmpty()) return@mapNotNull null
                val shelf = Shelf.fromGoodreads(cols.getOrNull(iExclusive))
                val rating = cols.getOrNull(iRating)?.trim()?.toIntOrNull()?.takeIf { it in 1..5 }
                val finished = DateUtil.parseOrNull(cols.getOrNull(iDateRead))
                val added = DateUtil.parseOrNull(cols.getOrNull(iDateAdded)) ?: 0L
                Book(
                    title = title,
                    author = if (iAuthor in 0..cols.lastIndex) cols[iAuthor].trim() else "",
                    isbn = cols.at(iIsbn),
                    rating = rating,
                    shelf = shelf,
                    dateAdded = added,
                    dateFinishedReading = if (shelf == Shelf.FINISHED_READING) finished else null,
                    dateStartedReading = null,
                )
            }
    }

    // Goodreads wraps ISBNs as ="9780..." — strip the spreadsheet escaping.
    private fun String.cleanIsbn(): String =
        removePrefix("=").trim('"').trim()
}
