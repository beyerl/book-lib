package com.lspace.booklib.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lspace.booklib.data.repository.BookRepository
import com.lspace.booklib.domain.model.Book
import com.lspace.booklib.domain.model.Shelf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val results: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasSearched: Boolean = false,
) {
    val isEmptyResult: Boolean get() = hasSearched && !isLoading && error == null && results.isEmpty()
}

class SearchViewModel(
    private val bookRepository: BookRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _selected = MutableStateFlow<Book?>(null)
    val selected: StateFlow<Book?> = _selected.asStateFlow()

    fun onQueryChange(value: String) {
        _uiState.value = _uiState.value.copy(query = value)
    }

    fun search() {
        val query = _uiState.value.query.trim()
        if (query.isEmpty()) return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, hasSearched = true)
        viewModelScope.launch {
            try {
                val results = bookRepository.search(query)
                _uiState.value = _uiState.value.copy(results = results, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Search failed. Check your connection and try again.",
                )
            }
        }
    }

    /** Selects a result and lazily fetches its long description for the detail view. */
    fun select(book: Book) {
        _selected.value = book
        if (book.description == null && book.openLibraryKey != null) {
            viewModelScope.launch {
                val enriched = bookRepository.fetchDescription(book)
                if (_selected.value?.openLibraryKey == enriched.openLibraryKey) {
                    _selected.value = enriched
                }
            }
        }
    }

    fun addToShelf(book: Book, shelf: Shelf, onAdded: () -> Unit = {}) {
        viewModelScope.launch {
            bookRepository.addBook(book, shelf)
            onAdded()
        }
    }

    fun createBook(book: Book, shelf: Shelf, onCreated: () -> Unit = {}) {
        viewModelScope.launch {
            bookRepository.addBook(book, shelf)
            onCreated()
        }
    }
}
