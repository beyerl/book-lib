package com.lspace.booklib.ui.book

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.lspace.booklib.domain.DateUtil
import com.lspace.booklib.domain.model.Book
import com.lspace.booklib.domain.model.Shelf
import com.lspace.booklib.ui.components.BookCover
import com.lspace.booklib.ui.components.RatingBar

@Composable
fun BookReadView(
    book: Book,
    onRate: ((Int) -> Unit)? = null,
    onShelfChange: ((Shelf) -> Unit)? = null,
) {
    BookHeader(book)

    if (onShelfChange != null) {
        Text("Shelf", style = MaterialTheme.typography.titleMedium)
        ShelfSelector(selected = book.shelf, onSelect = onShelfChange)
    } else {
        LabeledValue("Shelf", book.shelf.displayName)
    }

    Text("Rating", style = MaterialTheme.typography.titleMedium)
    RatingBar(rating = book.rating, onRatingChange = onRate)

    val readRange = readRangeText(book)
    if (readRange != null) LabeledValue("Read", readRange)

    book.pageCount?.let { LabeledValue("Pages", it.toString()) }
    book.isbn?.let { LabeledValue("ISBN", it) }

    if (!book.description.isNullOrBlank()) {
        Text("Description", style = MaterialTheme.typography.titleMedium)
        Text(book.description, style = MaterialTheme.typography.bodyLarge)
    }
    if (!book.review.isNullOrBlank()) {
        Text("My notes", style = MaterialTheme.typography.titleMedium)
        Text(book.review, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun BookEditForm(
    book: Book,
    onSave: (Book) -> Unit,
    onShelfChange: (Shelf) -> Unit,
) {
    var title by remember(book.id) { mutableStateOf(book.title) }
    var author by remember(book.id) { mutableStateOf(book.author) }
    var year by remember(book.id) { mutableStateOf(book.yearPublished?.toString().orEmpty()) }
    var pages by remember(book.id) { mutableStateOf(book.pageCount?.toString().orEmpty()) }
    var isbn by remember(book.id) { mutableStateOf(book.isbn.orEmpty()) }
    var description by remember(book.id) { mutableStateOf(book.description.orEmpty()) }
    var review by remember(book.id) { mutableStateOf(book.review.orEmpty()) }

    fun pushSave() {
        onSave(
            book.copy(
                title = title.trim().ifEmpty { "Untitled" },
                author = author.trim(),
                yearPublished = year.trim().toIntOrNull(),
                pageCount = pages.trim().toIntOrNull(),
                isbn = isbn.trim().ifEmpty { null },
                description = description.trim().ifEmpty { null },
                review = review.trim().ifEmpty { null },
            ),
        )
    }

    Field("Title", title) { title = it; pushSave() }
    Field("Author", author) { author = it; pushSave() }
    Field("Year published", year, KeyboardType.Number) { year = it; pushSave() }
    Field("Page count", pages, KeyboardType.Number) { pages = it; pushSave() }
    Field("ISBN", isbn) { isbn = it; pushSave() }

    Text("Shelf", style = MaterialTheme.typography.titleMedium)
    ShelfSelector(selected = book.shelf, onSelect = onShelfChange)

    Text("Rating", style = MaterialTheme.typography.titleMedium)
    RatingBar(rating = book.rating, onRatingChange = { onSave(book.copy(rating = it.takeIf { r -> r in 1..5 })) })

    Field("Description", description, singleLine = false) { description = it; pushSave() }
    Field("My notes", review, singleLine = false) { review = it; pushSave() }
}

@Composable
private fun BookHeader(book: Book) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        BookCover(coverUrl = book.coverUrl, title = book.title, width = 96, height = 140)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(book.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            if (book.author.isNotBlank()) {
                Text(book.author, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            book.yearPublished?.let { Text("Published $it", style = MaterialTheme.typography.bodyMedium) }
        }
    }
}

@Composable
fun ShelfSelector(selected: Shelf, onSelect: (Shelf) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Shelf.entries.forEach { shelf ->
            FilterChip(
                selected = shelf == selected,
                onClick = { onSelect(shelf) },
                label = { Text(shelf.displayName) },
            )
        }
    }
}

@Composable
private fun LabeledValue(label: String, value: String) {
    Card {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, style = MaterialTheme.typography.titleMedium)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun Field(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    onChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
    )
}

private fun readRangeText(book: Book): String? {
    val start = DateUtil.format(book.dateStartedReading)
    val finish = DateUtil.format(book.dateFinishedReading)
    return when {
        start.isNotEmpty() && finish.isNotEmpty() -> "$start – $finish"
        finish.isNotEmpty() -> "finished $finish"
        start.isNotEmpty() -> "started $start"
        else -> null
    }
}
