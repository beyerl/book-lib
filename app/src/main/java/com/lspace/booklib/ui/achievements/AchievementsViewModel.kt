package com.lspace.booklib.ui.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lspace.booklib.data.repository.BookRepository
import com.lspace.booklib.data.repository.ReadingGoalRepository
import com.lspace.booklib.domain.DateUtil
import com.lspace.booklib.domain.model.Book
import com.lspace.booklib.domain.model.Shelf
import com.lspace.booklib.domain.model.YearSummary
import com.lspace.booklib.domain.model.booksFinishedInYear
import com.lspace.booklib.domain.model.yearSummaries
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AchievementsUiState(
    val year: Int = DateUtil.currentYear(),
    val goalTarget: Int? = null,
    val finishedThisYear: Int = 0,
    val summaries: List<YearSummary> = emptyList(),
) {
    val progress: Float
        get() = goalTarget?.takeIf { it > 0 }?.let { (finishedThisYear.toFloat() / it).coerceIn(0f, 1f) } ?: 0f
}

class AchievementsViewModel(
    private val bookRepository: BookRepository,
    private val goalRepository: ReadingGoalRepository,
) : ViewModel() {

    private val year = DateUtil.currentYear()

    val uiState: StateFlow<AchievementsUiState> = combine(
        bookRepository.observeAll(),
        goalRepository.observeForYear(year),
    ) { all, goal ->
        AchievementsUiState(
            year = year,
            goalTarget = goal?.targetBooks,
            finishedThisYear = booksFinishedInYear(all, year) { DateUtil.yearOf(it) },
            summaries = yearSummaries(all) { DateUtil.yearOf(it) },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AchievementsUiState())

    fun setGoal(target: Int) {
        viewModelScope.launch { goalRepository.setGoal(year, target) }
    }

    fun booksForYear(targetYear: Int): StateFlow<List<Book>> =
        bookRepository.observeAll().map { all ->
            all.filter {
                it.shelf == Shelf.FINISHED_READING &&
                    it.dateFinishedReading != null &&
                    DateUtil.yearOf(it.dateFinishedReading) == targetYear
            }.sortedByDescending { it.dateFinishedReading }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
