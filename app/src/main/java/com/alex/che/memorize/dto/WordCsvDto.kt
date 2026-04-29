package com.alex.che.memorize.dto

import java.time.LocalDateTime

data class WordCsvDto (
    val word: String,
    val translation: String,
    val isDifficult: Boolean = false,
    val checkDate: LocalDateTime = LocalDateTime.of(1999, 1, 1, 1, 1),
)