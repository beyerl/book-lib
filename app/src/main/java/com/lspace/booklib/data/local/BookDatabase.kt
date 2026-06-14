package com.lspace.booklib.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lspace.booklib.data.local.entity.BookEntity
import com.lspace.booklib.data.local.entity.ReadingGoalEntity

@Database(
    entities = [BookEntity::class, ReadingGoalEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class BookDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun readingGoalDao(): ReadingGoalDao

    companion object {
        const val NAME = "lspace.db"
    }
}
