package com.lspace.booklib.data.local

import androidx.room.TypeConverter
import com.lspace.booklib.domain.model.Shelf

class Converters {
    @TypeConverter
    fun shelfToString(shelf: Shelf): String = shelf.name

    @TypeConverter
    fun stringToShelf(value: String): Shelf = Shelf.valueOf(value)
}
