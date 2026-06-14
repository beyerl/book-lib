package com.lspace.booklib.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

object Routes {
    const val HOME = "home"
    const val SEARCH_GRAPH = "search_graph"
    const val SEARCH = "search"
    const val SEARCH_DETAIL = "search_detail"
    const val NEW_BOOK = "new_book"
    const val LIBRARY = "library"
    const val ACHIEVEMENTS_GRAPH = "achievements_graph"
    const val ACHIEVEMENTS = "achievements"
    const val YEAR_BOOKS = "year_books"
    const val IMPORT_EXPORT = "import_export"

    const val BOOK = "book"
    const val ARG_BOOK_ID = "bookId"
    fun book(id: Long) = "$BOOK/$id"

    const val ARG_YEAR = "year"
    fun yearBooks(year: Int) = "$YEAR_BOOKS/$year"
}

/** Items shown in the bottom navigation bar. */
enum class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    HOME(Routes.HOME, "Home", Icons.Filled.Home),
    LIBRARY(Routes.LIBRARY, "Library", Icons.AutoMirrored.Filled.MenuBook),
    SEARCH(Routes.SEARCH_GRAPH, "Search", Icons.Filled.Search),
    ACHIEVEMENTS(Routes.ACHIEVEMENTS_GRAPH, "Goals", Icons.Filled.EmojiEvents),
}
