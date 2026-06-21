package com.lspace.booklib.data.importexport

import com.lspace.booklib.domain.DateUtil
import com.lspace.booklib.domain.model.Book
import com.lspace.booklib.domain.model.Shelf
import com.lspace.booklib.domain.model.Shelf.Companion.fromBookwyrm
import com.lspace.booklib.domain.model.Shelf.Companion.toBookwyrm
import com.lspace.booklib.domain.model.Shelf.Companion.toBookwyrmName
import kotlin.math.roundToInt

/**
 * BookWyrm-compatible CSV export/import. BookWyrm emits a wide column set; we
 * read/write the fields we model and leave the rest empty. Ratings are decimals
 * out of 5 (e.g. "4.00"), shelves are identifiers (to-read / reading / read /
 * stopped-reading), and dates are ISO yyyy-MM-dd.
 */
object BookwyrmCsv {

    // The full BookWyrm export header, in order, so files we write import cleanly back into BookWyrm.
    private val HEADER = listOf(
        "title", "author_text", "remote_id", "openlibrary_key", "finna_key", "libris_key",
        "inventaire_id", "librarything_key", "goodreads_key", "bnf_id", "viaf", "wikidata",
        "asin", "aasin", "isfdb", "isbn_10", "isbn_13", "oclc_number", "pages",
        "start_date", "finish_date", "stopped_date", "rating", "review_name", "review_cw",
        "review_content", "review_published", "shelf", "shelf_name", "shelf_date",
    )

    fun export(books: List<Book>): String {
        val sb = StringBuilder()
        sb.append(Csv.encodeRow(HEADER)).append('\n')
        for (b in books) {
            val isbn13 = b.isbn?.takeIf { it.length == 13 }
            val isbn10 = b.isbn?.takeIf { it.length == 10 }
            sb.append(
                Csv.encodeRow(
                    listOf(
                        b.title,                                   // title
                        b.author,                                  // author_text
                        "",                                        // remote_id
                        b.openLibraryKey.orEmpty(),                // openlibrary_key
                        "", "", "", "", "", "", "", "",            // finna…wikidata
                        "", "", "",                                // asin, aasin, isfdb
                        isbn10.orEmpty(),                          // isbn_10
                        isbn13.orEmpty(),                          // isbn_13
                        "",                                        // oclc_number
                        b.pageCount?.toString().orEmpty(),         // pages
                        DateUtil.format(b.dateStartedReading),     // start_date
                        if (b.shelf == Shelf.FINISHED_READING) DateUtil.format(b.dateFinishedReading) else "", // finish_date
                        if (b.shelf == Shelf.STOPPED_READING) DateUtil.format(b.dateFinishedReading) else "",  // stopped_date
                        b.rating?.let { String.format(java.util.Locale.US, "%.2f", it.toFloat()) }.orEmpty(),  // rating
                        "",                                        // review_name
                        "",                                        // review_cw
                        b.review.orEmpty(),                        // review_content
                        "",                                        // review_published
                        b.shelf.toBookwyrm(),                      // shelf
                        b.shelf.toBookwyrmName(),                  // shelf_name
                        DateUtil.format(b.dateAdded.takeIf { it > 0 }), // shelf_date
                    ),
                ),
            ).append('\n')
        }
        return sb.toString()
    }

    /** True when [header] (the first CSV row) looks like a BookWyrm export. */
    fun matchesHeader(header: List<String>): Boolean {
        val names = header.map { it.trim().lowercase() }
        return names.contains("author_text") || names.contains("remote_id")
    }

    /** Parses BookWyrm CSV text into non-persisted books. Unknown columns are ignored. */
    fun import(text: String): List<Book> {
        val rows = Csv.parse(text.removePrefix(BOM))
        if (rows.isEmpty()) return emptyList()
        val header = rows.first().map { it.trim() }
        fun idx(name: String) = header.indexOfFirst { it.equals(name, ignoreCase = true) }
        val iTitle = idx("title")
        val iAuthor = idx("author_text")
        val iOlKey = idx("openlibrary_key")
        val iIsbn13 = idx("isbn_13")
        val iIsbn10 = idx("isbn_10")
        val iPages = idx("pages")
        val iStart = idx("start_date")
        val iFinish = idx("finish_date")
        val iStopped = idx("stopped_date")
        val iRating = idx("rating")
        val iReview = idx("review_content")
        val iShelf = idx("shelf")
        val iShelfDate = idx("shelf_date")

        fun List<String>.cell(i: Int): String? =
            if (i in 0..lastIndex) this[i].trim().takeIf { it.isNotEmpty() } else null

        return rows.drop(1)
            .filter { it.any { cell -> cell.isNotBlank() } }
            .mapNotNull { cols ->
                val title = cols.cell(iTitle) ?: return@mapNotNull null
                val stoppedDate = DateUtil.parseOrNull(cols.getOrNull(iStopped))
                val shelf = if (stoppedDate != null) Shelf.STOPPED_READING
                else fromBookwyrm(cols.cell(iShelf))
                // BookWyrm ratings are decimals out of 5; round to our integer scale.
                val rating = cols.cell(iRating)?.toDoubleOrNull()?.roundToInt()?.takeIf { it in 1..5 }
                val isbn = cols.cell(iIsbn13) ?: cols.cell(iIsbn10)
                val started = DateUtil.parseOrNull(cols.getOrNull(iStart))
                val finished = DateUtil.parseOrNull(cols.getOrNull(iFinish))
                val added = DateUtil.parseOrNull(cols.getOrNull(iShelfDate)) ?: 0L
                Book(
                    title = title,
                    author = cols.cell(iAuthor).orEmpty(),
                    openLibraryKey = cols.cell(iOlKey),
                    isbn = isbn,
                    pageCount = cols.cell(iPages)?.toIntOrNull(),
                    rating = rating,
                    review = cols.cell(iReview),
                    shelf = shelf,
                    dateAdded = added,
                    dateStartedReading = started,
                    dateFinishedReading = when (shelf) {
                        Shelf.FINISHED_READING -> finished
                        Shelf.STOPPED_READING -> stoppedDate
                        else -> null
                    },
                )
            }
    }

    private const val BOM = "﻿"
}
