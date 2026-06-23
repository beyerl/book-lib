package com.lspace.booklib.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lspace.booklib.data.repository.BookRepository
import com.lspace.booklib.domain.model.Book
import com.lspace.booklib.domain.model.Shelf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class LibraryUiState(
    val selectedShelves: Set<Shelf> = Shelf.entries.toSet(),
    val query: String = "",
    val pageSize: Int = 10,
    val page: Int = 0,
    val allMatching: List<Book> = emptyList(),
) {
    val pageCount: Int get() = if (allMatching.isEmpty()) 1 else ((allMatching.size - 1) / pageSize) + 1
    val currentPage: Int get() = page.coerceIn(0, pageCount - 1)
    val pageItems: List<Book>
        get() = allMatching.drop(currentPage * pageSize).take(pageSize)
}

val PAGE_SIZE_OPTIONS = listOf(5, 10, 20, 50)

class LibraryViewModel(
    bookRepository: BookRepository,
) : ViewModel() {

    private val selectedShelves = MutableStateFlow(Shelf.entries.toSet())
    private val query = MutableStateFlow("")
    private val pageSize = MutableStateFlow(10)
    private val page = MutableStateFlow(0)

    val uiState: StateFlow<LibraryUiState> = combine(
        bookRepository.observeAll(),
        selectedShelves,
        query,
        pageSize,
        page,
    ) { all, shelves, search, size, currentPage ->
        val trimmed = search.trim()
        val matching = all
            .filter { it.shelf in shelves }
            .filter { book ->
                trimmed.isBlank() ||
                    book.title.contains(trimmed, ignoreCase = true) ||
                    book.author.contains(trimmed, ignoreCase = true)
            }
        LibraryUiState(
            selectedShelves = shelves,
            query = search,
            pageSize = size,
            page = currentPage,
            allMatching = matching,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LibraryUiState())

    fun setQuery(value: String) {
        query.value = value
        page.value = 0
    }

    fun toggleShelf(shelf: Shelf) {
        val current = selectedShelves.value
        val next = if (shelf in current) current - shelf else current + shelf
        selectedShelves.value = next.ifEmpty { Shelf.entries.toSet() }
        page.value = 0
    }

    fun setPageSize(size: Int) {
        pageSize.value = size
        page.value = 0
    }

    fun nextPage() {
        page.value = page.value + 1
    }

    fun previousPage() {
        page.value = (page.value - 1).coerceAtLeast(0)
    }
}
