package com.lspace.booklib.data.repository

import com.lspace.booklib.data.local.ReadingGoalDao
import com.lspace.booklib.data.local.entity.ReadingGoalEntity
import com.lspace.booklib.domain.model.ReadingGoal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReadingGoalRepository(
    private val dao: ReadingGoalDao,
) {
    fun observeForYear(year: Int): Flow<ReadingGoal?> =
        dao.observeForYear(year).map { it?.toDomain() }

    fun observeAll(): Flow<List<ReadingGoal>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun getForYear(year: Int): ReadingGoal? = dao.getForYear(year)?.toDomain()

    suspend fun setGoal(year: Int, targetBooks: Int) {
        dao.upsert(ReadingGoalEntity(year = year, targetBooks = targetBooks))
    }
}
