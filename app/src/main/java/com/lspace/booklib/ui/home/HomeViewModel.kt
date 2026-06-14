package com.lspace.booklib.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lspace.booklib.data.repository.BookRepository
import com.lspace.booklib.data.repository.ReadingGoalRepository
import com.lspace.booklib.domain.DateUtil
import com.lspace.booklib.domain.model.Book
import com.lspace.booklib.domain.model.Shelf
import com.lspace.booklib.domain.model.booksFinishedInYear
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val year: Int = DateUtil.currentYear(),
    val nowReading: List<Book> = emptyList(),
    val goalTarget: Int? = null,
    val finishedThisYear: Int = 0,
) {
    val progress: Float
        get() = goalTarget?.takeIf { it > 0 }?.let { (finishedThisYear.toFloat() / it).coerceIn(0f, 1f) } ?: 0f
}

class HomeViewModel(
    bookRepository: BookRepository,
    goalRepository: ReadingGoalRepository,
) : ViewModel() {

    private val year = DateUtil.currentYear()

    val uiState: StateFlow<HomeUiState> = combine(
        bookRepository.observeByShelf(Shelf.NOW_READING),
        bookRepository.observeAll(),
        goalRepository.observeForYear(year),
    ) { nowReading, all, goal ->
        HomeUiState(
            year = year,
            nowReading = nowReading,
            goalTarget = goal?.targetBooks,
            finishedThisYear = booksFinishedInYear(all, year) { DateUtil.yearOf(it) },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
}
