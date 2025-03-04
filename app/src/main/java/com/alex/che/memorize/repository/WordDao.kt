package com.alex.che.memorize.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alex.che.memorize.entity.Word

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWord(vararg word: Word)

    @Update
    fun updateWord(vararg word: Word)

    @Delete
    fun deleteWord(vararg word: Word)

    @Query("SELECT * FROM word")
    fun loadAllWords(): List<Word>?

    @Query("SELECT * FROM word where dictionaryId = :dictId")
    fun loadWordsByDictId(dictId: Int): List<Word>?

    @Query("SELECT id FROM word where dictionaryId = :dictId and isDifficult = :isDifficult")
    fun loadIdsByDictId(dictId: Int, isDifficult: Boolean): List<Int>?

    @Query("SELECT * FROM word where id in (:ids)")
    fun loadWords(ids: List<Int>): List<Word>

    @Query("update word set isDifficult = :isChecked where id = :id")
    fun changeIsDifficult(id: Int?, isChecked: Boolean)
}