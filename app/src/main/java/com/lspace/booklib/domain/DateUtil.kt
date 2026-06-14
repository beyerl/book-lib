package com.lspace.booklib.domain

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtil {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun yearOf(epochMillis: Long): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = epochMillis
        return cal.get(Calendar.YEAR)
    }

    fun currentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)

    /** Formats as yyyy-MM-dd, or "" when null. */
    fun format(epochMillis: Long?): String =
        epochMillis?.let { dateFormat.format(Date(it)) }.orEmpty()

    /** Parses yyyy-MM-dd (and a few Goodreads variants) to epoch millis, or null. */
    fun parseOrNull(value: String?): Long? {
        val v = value?.trim().orEmpty()
        if (v.isEmpty()) return null
        for (pattern in arrayOf("yyyy-MM-dd", "yyyy/MM/dd", "MM/dd/yyyy")) {
            try {
                return SimpleDateFormat(pattern, Locale.US).parse(v)?.time
            } catch (_: Exception) {
                // try next pattern
            }
        }
        return null
    }
}
