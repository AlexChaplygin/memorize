package com.alex.che.memorize.dto

import java.io.Serializable
import java.time.LocalDateTime

data class WordDto(
    val id: Int?,
    val word: String,
    val translation: String,
    val isDifficult: Boolean,
    var dictionaryId: Int?,
    val createdDate: LocalDateTime,
    val checkDate: LocalDateTime,
) : Serializable