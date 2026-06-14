package com.lspace.booklib.di

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lspace.booklib.BookLibApplication
import com.lspace.booklib.ui.achievements.AchievementsViewModel
import com.lspace.booklib.ui.book.BookViewModel
import com.lspace.booklib.ui.home.HomeViewModel
import com.lspace.booklib.ui.importexport.ImportExportViewModel
import com.lspace.booklib.ui.library.LibraryViewModel
import com.lspace.booklib.ui.search.SearchViewModel

private fun CreationExtras.app(): BookLibApplication =
    this[APPLICATION_KEY] as BookLibApplication

/** Central factory wiring ViewModels to the [AppContainer]. */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { HomeViewModel(app().container.bookRepository, app().container.readingGoalRepository) }
        initializer { LibraryViewModel(app().container.bookRepository) }
        initializer { SearchViewModel(app().container.bookRepository) }
        initializer { BookViewModel(app().container.bookRepository) }
        initializer { AchievementsViewModel(app().container.bookRepository, app().container.readingGoalRepository) }
        initializer { ImportExportViewModel(app().container.bookRepository) }
    }
}
