package com.lspace.booklib.data.importexport

import com.lspace.booklib.domain.DateUtil
import com.lspace.booklib.domain.model.Book
import com.lspace.booklib.domain.model.Shelf

/** Renders the library as a Markdown document, grouped by shelf. */
object MarkdownExport {

    fun export(books: List<Book>): String {
        val sb = StringBuilder()
        sb.append("# L-Space Library\n\n")
        sb.append("_${books.size} books_\n\n")
        for (shelf in Shelf.entries) {
            val onShelf = books.filter { it.shelf == shelf }
            if (onShelf.isEmpty()) continue
            sb.append("## ${shelf.displayName} (${onShelf.size})\n\n")
            for (b in onShelf.sortedBy { it.title }) {
                val stars = b.rating?.let { " " + "★".repeat(it) + "☆".repeat(5 - it) }.orEmpty()
                sb.append("- **${b.title}**")
                if (b.author.isNotBlank()) sb.append(" — ${b.author}")
                b.yearPublished?.let { sb.append(" ($it)") }
                sb.append(stars)
                val read = readRange(b)
                if (read.isNotEmpty()) sb.append("  \n  _Read: ${read}_")
                sb.append('\n')
            }
            sb.append('\n')
        }
        return sb.toString()
    }

    private fun readRange(b: Book): String {
        val start = DateUtil.format(b.dateStartedReading)
        val finish = DateUtil.format(b.dateFinishedReading)
        return when {
            start.isNotEmpty() && finish.isNotEmpty() -> "$start – $finish"
            finish.isNotEmpty() -> finish
            start.isNotEmpty() -> "$start – "
            else -> ""
        }
    }
}
