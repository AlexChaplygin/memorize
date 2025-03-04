package com.alex.che.memorize.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "word")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Int?=null,
    val word: String,
    val translation: String,
    val isDifficult: Boolean,
    var dictionaryId: Int?,
    @ColumnInfo(name = "created_date")
    val createdDate: LocalDateTime,
)