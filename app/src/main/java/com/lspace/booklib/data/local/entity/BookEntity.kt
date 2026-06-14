package com.lspace.booklib.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lspace.booklib.domain.model.Book
import com.lspace.booklib.domain.model.Shelf

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val openLibraryKey: String?,
    val title: String,
    val author: String,
    val coverUrl: String?,
    val yearPublished: Int?,
    val description: String?,
    val isbn: String?,
    val pageCount: Int?,
    val rating: Int?,
    val review: String?,
    val shelf: Shelf,
    val dateAdded: Long,
    val dateStartedReading: Long?,
    val dateFinishedReading: Long?,
) {
    fun toDomain() = Book(
        id = id,
        openLibraryKey = openLibraryKey,
        title = title,
        author = author,
        coverUrl = coverUrl,
        yearPublished = yearPublished,
        description = description,
        isbn = isbn,
        pageCount = pageCount,
        rating = rating,
        review = review,
        shelf = shelf,
        dateAdded = dateAdded,
        dateStartedReading = dateStartedReading,
        dateFinishedReading = dateFinishedReading,
    )

    companion object {
        fun fromDomain(book: Book) = BookEntity(
            id = book.id,
            openLibraryKey = book.openLibraryKey,
            title = book.title,
            author = book.author,
            coverUrl = book.coverUrl,
            yearPublished = book.yearPublished,
            description = book.description,
            isbn = book.isbn,
            pageCount = book.pageCount,
            rating = book.rating,
            review = book.review,
            shelf = book.shelf,
            dateAdded = book.dateAdded,
            dateStartedReading = book.dateStartedReading,
            dateFinishedReading = book.dateFinishedReading,
        )
    }
}
