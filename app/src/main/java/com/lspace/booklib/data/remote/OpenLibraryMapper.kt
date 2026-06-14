package com.lspace.booklib.data.remote

import com.lspace.booklib.data.remote.dto.SearchDocDto
import com.lspace.booklib.domain.model.Book
import com.lspace.booklib.domain.model.Shelf

/** Bare work id ("OL45883W") from an OpenLibrary key ("/works/OL45883W"). */
fun String.workIdOrNull(): String? =
    substringAfterLast("/works/", "").takeIf { it.isNotBlank() }
        ?: substringAfterLast('/').takeIf { it.startsWith("OL") && it.endsWith("W") }

/** Maps an OpenLibrary search result row to a non-persisted [Book]. */
fun SearchDocDto.toBook(): Book = Book(
    id = 0L,
    openLibraryKey = key,
    title = title?.takeIf { it.isNotBlank() } ?: "Untitled",
    author = authorNames.firstOrNull().orEmpty(),
    coverUrl = OpenLibraryApi.coverUrl(coverId),
    yearPublished = firstPublishYear,
    description = null,
    isbn = isbns.firstOrNull(),
    pageCount = pageCountMedian,
    shelf = Shelf.READING_LIST,
)
