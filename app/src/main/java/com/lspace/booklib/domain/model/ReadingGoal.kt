package com.lspace.booklib.domain.model

/** A target number of books to finish in a given [year]. */
data class ReadingGoal(
    val year: Int,
    val targetBooks: Int,
)

/** Aggregated "The year &lt;yyyy&gt; in Books" summary. */
data class YearSummary(
    val year: Int,
    val booksFinished: Int,
)
