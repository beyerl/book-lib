package com.lspace.booklib.domain.model

/** The reading shelves a book can live on. */
enum class Shelf(val displayName: String) {
    READING_LIST("Reading List"),
    NOW_READING("Now Reading"),
    FINISHED_READING("Finished Reading"),
    STOPPED_READING("Stopped Reading");

    companion object {
        // Shelf names (custom Goodreads bookshelves) that signal an abandoned read.
        private val DNF_MARKERS = listOf("did-not-finish", "did_not_finish", "dnf", "abandoned", "unfinished", "stopped-reading", "gave-up")

        /** Maps a Goodreads "Exclusive Shelf" value to our shelves. */
        fun fromGoodreads(value: String?): Shelf = when (value?.trim()?.lowercase()) {
            "currently-reading" -> NOW_READING
            "read" -> FINISHED_READING
            "did-not-finish", "dnf", "abandoned" -> STOPPED_READING
            else -> READING_LIST // "to-read" and unknown
        }

        /**
         * Maps a Goodreads row to a shelf using both its "Exclusive Shelf" and the
         * "Bookshelves" column. Goodreads only has three exclusive shelves
         * (to-read / currently-reading / read), so an abandoned read is expressed as a
         * custom bookshelf (e.g. "did-not-finish"); that takes precedence here.
         */
        fun fromGoodreads(exclusiveShelf: String?, bookshelves: String?): Shelf {
            val shelves = bookshelves.orEmpty().lowercase()
            return if (DNF_MARKERS.any { shelves.contains(it) }) STOPPED_READING
            else fromGoodreads(exclusiveShelf)
        }

        /** The Goodreads "Exclusive Shelf" value for this shelf. */
        fun Shelf.toGoodreads(): String = when (this) {
            READING_LIST -> "to-read"
            NOW_READING -> "currently-reading"
            FINISHED_READING -> "read"
            STOPPED_READING -> "did-not-finish"
        }
    }
}
