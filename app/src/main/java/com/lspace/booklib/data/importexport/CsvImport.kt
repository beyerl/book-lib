package com.lspace.booklib.data.importexport

import com.lspace.booklib.domain.model.Book

/**
 * Imports a library CSV, auto-detecting whether it is a BookWyrm or Goodreads
 * export from its header row. Falls back to the Goodreads parser for anything
 * that isn't recognisably BookWyrm.
 */
object CsvImport {

    fun import(text: String): List<Book> {
        val rows = Csv.parse(text.removePrefix(BOM))
        if (rows.isEmpty()) return emptyList()
        return if (BookwyrmCsv.matchesHeader(rows.first())) BookwyrmCsv.import(text)
        else GoodreadsCsv.import(text)
    }

    private const val BOM = "﻿"
}
