package com.lspace.booklib.domain.model

/** The reading shelves a book can live on. */
enum class Shelf(val displayName: String) {
    READING_LIST("Reading List"),
    NOW_READING("Now Reading"),
    FINISHED_READING("Finished Reading"),
    STOPPED_READING("Stopped Reading");

    companion object {
        /** Maps a Goodreads "Exclusive Shelf" value to our shelves. */
        fun fromGoodreads(value: String?): Shelf = when (value?.trim()?.lowercase()) {
            "currently-reading" -> NOW_READING
            "read" -> FINISHED_READING
            "did-not-finish", "dnf", "abandoned" -> STOPPED_READING
            else -> READING_LIST // "to-read" and unknown
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
