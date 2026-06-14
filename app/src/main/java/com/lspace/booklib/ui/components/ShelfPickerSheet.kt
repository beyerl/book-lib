package com.lspace.booklib.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lspace.booklib.domain.model.Shelf

/** Bottom sheet that lets the user choose a shelf to add/move a book to. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfPickerSheet(
    title: String = "Add to shelf",
    onDismiss: () -> Unit,
    onSelect: (Shelf) -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            Shelf.entries.forEach { shelf ->
                ListItem(
                    headlineContent = { Text(shelf.displayName) },
                    leadingContent = {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(shelf) },
                )
            }
        }
    }
}
