package com.lspace.booklib.ui.importexport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lspace.booklib.data.importexport.GoodreadsCsv
import com.lspace.booklib.data.importexport.MarkdownExport
import com.lspace.booklib.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImportExportViewModel(
    private val repository: BookRepository,
) : ViewModel() {

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    suspend fun buildCsv(): String = GoodreadsCsv.export(repository.getAll())

    suspend fun buildMarkdown(): String = MarkdownExport.export(repository.getAll())

    fun importCsv(text: String) {
        viewModelScope.launch {
            try {
                val books = GoodreadsCsv.import(text)
                if (books.isEmpty()) {
                    _message.value = "No books found in that file."
                } else {
                    repository.upsertAll(books)
                    _message.value = "Imported ${books.size} books."
                }
            } catch (e: Exception) {
                _message.value = "Import failed: could not parse the file."
            }
        }
    }

    fun reportExport(kind: String, success: Boolean) {
        _message.value = if (success) "$kind exported." else "$kind export cancelled."
    }

    fun clearMessage() {
        _message.value = null
    }
}
