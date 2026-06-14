package com.lspace.booklib.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lspace.booklib.domain.model.ReadingGoal

@Entity(tableName = "reading_goals")
data class ReadingGoalEntity(
    @PrimaryKey val year: Int,
    val targetBooks: Int,
) {
    fun toDomain() = ReadingGoal(year = year, targetBooks = targetBooks)

    companion object {
        fun fromDomain(goal: ReadingGoal) =
            ReadingGoalEntity(year = goal.year, targetBooks = goal.targetBooks)
    }
}
