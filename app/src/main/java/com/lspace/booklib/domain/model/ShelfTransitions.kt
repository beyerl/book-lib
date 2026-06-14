package com.lspace.booklib.domain.model

/**
 * Applies a shelf change to [book], stamping read dates per the spec:
 *  - moving to NOW_READING stamps the start date (if not already set),
 *  - moving to FINISHED_READING stamps the finish date (and back-fills a
 *    start date if the book was never on the Now-Reading shelf).
 * Dates already set are preserved so the "read from–till" range is stable.
 */
fun applyShelfChange(book: Book, newShelf: Shelf, now: Long): Book {
    if (book.shelf == newShelf) return book
    var started = book.dateStartedReading
    var finished = book.dateFinishedReading
    when (newShelf) {
        Shelf.NOW_READING -> if (started == null) started = now
        Shelf.FINISHED_READING -> {
            if (started == null) started = now
            if (finished == null) finished = now
        }
        Shelf.READING_LIST, Shelf.STOPPED_READING -> {
            // leave existing dates untouched
        }
    }
    return book.copy(
        shelf = newShelf,
        dateStartedReading = started,
        dateFinishedReading = finished,
    )
}

/**
 * Groups finished books into "year in books" summaries, newest year first.
 * [epochToYear] converts a finish timestamp to a calendar year (injected so
 * this stays pure and unit-testable without Android).
 */
fun yearSummaries(books: List<Book>, epochToYear: (Long) -> Int): List<YearSummary> =
    books.asSequence()
        .filter { it.shelf == Shelf.FINISHED_READING && it.dateFinishedReading != null }
        .groupingBy { epochToYear(it.dateFinishedReading!!) }
        .eachCount()
        .map { (year, count) -> YearSummary(year = year, booksFinished = count) }
        .sortedByDescending { it.year }

/** Count of books finished in [year]. */
fun booksFinishedInYear(books: List<Book>, year: Int, epochToYear: (Long) -> Int): Int =
    books.count {
        it.shelf == Shelf.FINISHED_READING &&
            it.dateFinishedReading != null &&
            epochToYear(it.dateFinishedReading) == year
    }
