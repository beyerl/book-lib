package com.lspace.booklib.data.importexport

/** Minimal RFC-4180-ish CSV reader/writer (no external dependency). */
object Csv {

    fun encodeField(value: String?): String {
        val v = value.orEmpty()
        val needsQuoting = v.any { it == ',' || it == '"' || it == '\n' || it == '\r' }
        return if (needsQuoting) "\"" + v.replace("\"", "\"\"") + "\"" else v
    }

    fun encodeRow(fields: List<String?>): String =
        fields.joinToString(",") { encodeField(it) }

    /** Parses the whole CSV text into rows of fields, honoring quotes and escaped quotes. */
    fun parse(text: String): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        var field = StringBuilder()
        var row = mutableListOf<String>()
        var inQuotes = false
        var i = 0
        val s = text
        while (i < s.length) {
            val c = s[i]
            when {
                inQuotes -> when {
                    c == '"' && i + 1 < s.length && s[i + 1] == '"' -> {
                        field.append('"'); i++
                    }
                    c == '"' -> inQuotes = false
                    else -> field.append(c)
                }
                c == '"' -> inQuotes = true
                c == ',' -> {
                    row.add(field.toString()); field = StringBuilder()
                }
                c == '\r' -> { /* ignore, handled with \n */ }
                c == '\n' -> {
                    row.add(field.toString()); field = StringBuilder()
                    rows.add(row); row = mutableListOf()
                }
                else -> field.append(c)
            }
            i++
        }
        // flush last field/row if any content remains
        if (field.isNotEmpty() || row.isNotEmpty()) {
            row.add(field.toString())
            rows.add(row)
        }
        return rows
    }
}
