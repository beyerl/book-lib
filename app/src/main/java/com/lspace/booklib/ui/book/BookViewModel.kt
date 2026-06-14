package com.lspace.booklib.ui.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lspace.booklib.data.repository.BookRepository
import com.lspace.booklib.domain.model.Book
import com.lspace.booklib.domain.model.Shelf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookViewModel(
    private val repository: BookRepository,
) : ViewModel() {

    private val _book = MutableStateFlow<Book?>(null)
    val book: StateFlow<Book?> = _book.asStateFlow()

    private var loadedId: Long = -1L

    fun load(id: Long) {
        if (loadedId == id) return
        loadedId = id
        viewModelScope.launch {
            _book.value = repository.getById(id)
        }
    }

    fun save(updated: Book) {
        _book.value = updated
        viewModelScope.launch {
            repository.updateBook(updated)
        }
    }

    fun setRating(rating: Int) {
        val current = _book.value ?: return
        save(current.copy(rating = rating.takeIf { it in 1..5 }))
    }

    fun moveToShelf(shelf: Shelf) {
        val current = _book.value ?: return
        viewModelScope.launch {
            repository.moveToShelf(current, shelf)
            _book.value = repository.getById(current.id)
        }
    }

    fun delete(onDeleted: () -> Unit) {
        val current = _book.value ?: return
        viewModelScope.launch {
            repository.deleteBook(current)
            onDeleted()
        }
    }
}
