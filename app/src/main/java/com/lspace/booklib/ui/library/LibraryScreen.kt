package com.lspace.booklib.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lspace.booklib.di.AppViewModelProvider
import com.lspace.booklib.domain.model.Shelf
import com.lspace.booklib.ui.components.BookListItem
import com.lspace.booklib.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onOpenBook: (Long) -> Unit,
    viewModel: LibraryViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var menuOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Library") },
                actions = {
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(Icons.Filled.Tune, contentDescription = "Page size")
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        Text(
                            "Books per page",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                        PAGE_SIZE_OPTIONS.forEach { size ->
                            DropdownMenuItem(
                                text = { Text("$size") },
                                onClick = { viewModel.setPageSize(size); menuOpen = false },
                            )
                        }
                    }
                },
            )
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Shelf.entries.forEach { shelf ->
                    FilterChip(
                        selected = shelf in state.selectedShelves,
                        onClick = { viewModel.toggleShelf(shelf) },
                        label = { Text(shelf.displayName) },
                    )
                }
            }

            if (state.allMatching.isEmpty()) {
                EmptyState(
                    title = "No books on these shelves",
                    subtitle = "Search for books and add them to a shelf to fill your library.",
                )
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(state.pageItems, key = { it.id }) { book ->
                        BookListItem(book = book, onClick = { onOpenBook(book.id) })
                    }
                }
                if (state.pageCount > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextButton(
                            onClick = { viewModel.previousPage() },
                            enabled = state.currentPage > 0,
                        ) { Text("Previous") }
                        AssistChip(
                            onClick = {},
                            label = { Text("Page ${state.currentPage + 1} / ${state.pageCount}") },
                        )
                        TextButton(
                            onClick = { viewModel.nextPage() },
                            enabled = state.currentPage < state.pageCount - 1,
                        ) { Text("Next") }
                    }
                }
            }
        }
    }
}
