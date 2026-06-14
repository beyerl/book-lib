package com.lspace.booklib.domain.model

/**
 * A book in the library or a search result. [id] == 0L denotes a book not yet
 * persisted (e.g. a search result or a freshly-created draft).
 */
data class Book(
    val id: Long = 0L,
    val openLibraryKey: String? = null,
    val title: String,
    val author: String,
    val coverUrl: String? = null,
    val yearPublished: Int? = null,
    val description: String? = null,
    val isbn: String? = null,
    val pageCount: Int? = null,
    val rating: Int? = null,
    val review: String? = null,
    val shelf: Shelf = Shelf.READING_LIST,
    val dateAdded: Long = 0L,
    val dateStartedReading: Long? = null,
    val dateFinishedReading: Long? = null,
) {
    val isPersisted: Boolean get() = id != 0L

    /** First ~120 chars of the description, for list previews. */
    val descriptionIncipit: String?
        get() = description?.replace('\n', ' ')?.trim()?.let {
            if (it.length <= 120) it else it.take(120).trimEnd() + "…"
        }
}
