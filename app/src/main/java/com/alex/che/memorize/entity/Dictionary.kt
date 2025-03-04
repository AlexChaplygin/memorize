package com.alex.che.memorize.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "dictionary")
data class Dictionary(
    @PrimaryKey(autoGenerate = true)
    val id: Int?=null,
    val name: String,
    @ColumnInfo(name = "created_date")
    val createdDate: LocalDateTime,
)