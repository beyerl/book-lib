package com.lspace.booklib.ui.achievements

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lspace.booklib.ui.components.BookListItem
import com.lspace.booklib.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearBooksScreen(
    year: Int,
    viewModel: AchievementsViewModel,
    onOpenBook: (Long) -> Unit,
    onBack: () -> Unit,
) {
    val booksFlow = remember(year) { viewModel.booksForYear(year) }
    val books by booksFlow.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("The year $year in Books") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        if (books.isEmpty()) {
            EmptyState(title = "No books finished in $year", modifier = Modifier.padding(padding))
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding)) {
                items(books, key = { it.id }) { book ->
                    BookListItem(book = book, onClick = { onOpenBook(book.id) })
                }
            }
        }
    }
}
