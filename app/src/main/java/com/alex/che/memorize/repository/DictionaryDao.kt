package com.alex.che.memorize.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alex.che.memorize.entity.Dictionary

@Dao
interface DictionaryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDictionary(vararg dictionary: Dictionary)

    @Update
    fun updateDictionary(vararg dictionary: Dictionary)

    @Delete
    fun deleteDictionary(vararg dictionary: Dictionary)

    @Query("DELETE from dictionary where id = :id")
    fun deleteDictionary(id:Int)

    @Query("SELECT * FROM dictionary")
    fun loadAllDictionaries(): List<Dictionary>?

    @Query("select * from dictionary where id = :id")
    fun findDictionaryById(id:Int): Dictionary
}