package com.alex.che.memorize.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alex.che.memorize.entity.Dictionary
import com.alex.che.memorize.entity.Word

@Database(entities = [Dictionary::class, Word::class], version = 1)
@TypeConverters(Converters::class)
abstract class MemorizeDatabase: RoomDatabase() {

    abstract val dictionaryDao: DictionaryDao
    abstract val wordDao: WordDao
}