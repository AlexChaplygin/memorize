package com.alex.che.memorize.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alex.che.memorize.entity.Word
import java.time.LocalDateTime

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(vararg word: Word)

    @Update
    suspend fun updateWord(vararg word: Word)

    @Delete
    suspend fun deleteWord(vararg word: Word)

    @Query("SELECT * FROM word")
    suspend fun loadAllWords(): List<Word>?

    @Query("SELECT * FROM word where dictionaryId = :dictId")
    suspend fun loadWordsByDictId(dictId: Int): List<Word>?

    @Query("SELECT * FROM word where dictionaryId = :dictId order by checkDate asc")
    suspend fun loadByDictId(dictId: Int): List<Word>

    @Query("SELECT * FROM word where dictionaryId = :dictId and isDifficult = true order by checkDate asc")
    suspend fun loadDifficultWordsByDictId(dictId: Int): List<Word>?

    @Query("SELECT * FROM word where id in (:ids)")
    suspend fun loadWords(ids: List<Int>): List<Word>

    @Query("update word set isDifficult = :isChecked where id = :id")
    suspend fun changeIsDifficult(id: Int?, isChecked: Boolean)

    @Query("update word set checkDate = :date where id = :id")
    suspend fun changeWordCheckDateById(id: Int?, date: LocalDateTime)
}