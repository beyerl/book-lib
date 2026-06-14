package com.lspace.booklib.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.lspace.booklib.data.local.entity.ReadingGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingGoalDao {

    @Query("SELECT * FROM reading_goals WHERE year = :year")
    fun observeForYear(year: Int): Flow<ReadingGoalEntity?>

    @Query("SELECT * FROM reading_goals WHERE year = :year")
    suspend fun getForYear(year: Int): ReadingGoalEntity?

    @Query("SELECT * FROM reading_goals ORDER BY year DESC")
    fun observeAll(): Flow<List<ReadingGoalEntity>>

    @Upsert
    suspend fun upsert(goal: ReadingGoalEntity)
}
