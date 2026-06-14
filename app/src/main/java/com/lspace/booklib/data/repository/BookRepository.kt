package com.lspace.booklib.data.repository

import com.lspace.booklib.data.local.BookDao
import com.lspace.booklib.data.local.entity.BookEntity
import com.lspace.booklib.data.remote.OpenLibraryApi
import com.lspace.booklib.data.remote.toBook
import com.lspace.booklib.data.remote.workIdOrNull
import com.lspace.booklib.domain.model.Book
import com.lspace.booklib.domain.model.Shelf
import com.lspace.booklib.domain.model.applyShelfChange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookRepository(
    private val bookDao: BookDao,
    private val api: OpenLibraryApi,
    private val now: () -> Long = System::currentTimeMillis,
) {
    fun observeAll(): Flow<List<Book>> =
        bookDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeByShelf(shelf: Shelf): Flow<List<Book>> =
        bookDao.observeByShelf(shelf).map { list -> list.map { it.toDomain() } }

    fun observeById(id: Long): Flow<Book?> =
        bookDao.observeById(id).map { it?.toDomain() }

    suspend fun getById(id: Long): Book? = bookDao.getById(id)?.toDomain()

    suspend fun getAll(): List<Book> = bookDao.getAll().map { it.toDomain() }

    /** Inserts a book, stamping [Book.dateAdded] and applying shelf-date rules. Returns the new id. */
    suspend fun addBook(book: Book, shelf: Shelf = book.shelf): Long {
        val stamped = applyShelfChange(book.copy(dateAdded = now()), shelf, now())
        return bookDao.insert(BookEntity.fromDomain(stamped.copy(id = 0L)))
    }

    suspend fun updateBook(book: Book) {
        bookDao.update(BookEntity.fromDomain(book))
    }

    /** Moves a book to [shelf], updating read-date stamps. */
    suspend fun moveToShelf(book: Book, shelf: Shelf) {
        val updated = applyShelfChange(book, shelf, now())
        bookDao.update(BookEntity.fromDomain(updated))
    }

    suspend fun deleteBook(book: Book) {
        bookDao.delete(BookEntity.fromDomain(book))
    }

    suspend fun upsertAll(books: List<Book>) {
        bookDao.upsertAll(books.map { BookEntity.fromDomain(it) })
    }

    // --- OpenLibrary search ---

    suspend fun search(query: String): List<Book> {
        if (query.isBlank()) return emptyList()
        return api.search(query).docs.map { it.toBook() }
    }

    /** Fetches the work description for a search result that has an OpenLibrary key. */
    suspend fun fetchDescription(book: Book): Book {
        val workId = book.openLibraryKey?.workIdOrNull() ?: return book
        return try {
            val work = api.getWork(workId)
            book.copy(description = work.descriptionText() ?: book.description)
        } catch (_: Exception) {
            book
        }
    }
}
