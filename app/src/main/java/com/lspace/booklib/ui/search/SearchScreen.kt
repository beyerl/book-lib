package com.lspace.booklib.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lspace.booklib.domain.model.Book
import com.lspace.booklib.domain.model.Shelf
import com.lspace.booklib.ui.components.BookListItem
import com.lspace.booklib.ui.components.EmptyState
import com.lspace.booklib.ui.components.ShelfPickerSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onOpenResult: (Book) -> Unit,
    onCreateFromScratch: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var pendingAdd by remember { mutableStateOf<Book?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Search") }) },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                label = { Text("Title, author, ISBN…") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { viewModel.search() }),
            )
            Button(
                onClick = { viewModel.search() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) { Text("Search OpenLibrary") }

            Box(Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    state.error != null -> EmptyStateWithCreate(
                        title = state.error!!,
                        subtitle = "You can also add a book from scratch.",
                        onCreateFromScratch = onCreateFromScratch,
                    )
                    state.isEmptyResult -> EmptyStateWithCreate(
                        title = "No results found",
                        subtitle = "Create this book from scratch instead.",
                        onCreateFromScratch = onCreateFromScratch,
                    )
                    state.results.isEmpty() -> EmptyStateWithCreate(
                        title = "Search the open book libraries",
                        subtitle = "Add results to any shelf, or create a book from scratch.",
                        onCreateFromScratch = onCreateFromScratch,
                    )
                    else -> LazyColumn(Modifier.fillMaxSize()) {
                        items(state.results) { book ->
                            BookListItem(
                                book = book,
                                onClick = { onOpenResult(book) },
                                onAdd = { pendingAdd = book },
                            )
                        }
                    }
                }
            }
        }
    }

    pendingAdd?.let { book ->
        ShelfPickerSheet(
            onDismiss = { pendingAdd = null },
            onSelect = { shelf ->
                viewModel.addToShelf(book, shelf)
                pendingAdd = null
            },
        )
    }
}

@Composable
private fun BoxScope.EmptyStateWithCreate(
    title: String,
    subtitle: String,
    onCreateFromScratch: () -> Unit,
) {
    Column(
        modifier = Modifier.align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        EmptyState(title = title, subtitle = subtitle)
        OutlinedButton(onClick = onCreateFromScratch, modifier = Modifier.padding(16.dp)) {
            Text("Create from scratch")
        }
    }
}
