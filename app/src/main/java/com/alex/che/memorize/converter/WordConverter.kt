package com.alex.che.memorize.converter

import com.alex.che.memorize.dto.WordCsvDto
import com.alex.che.memorize.dto.WordDto
import com.alex.che.memorize.entity.Word
import org.koin.core.component.KoinComponent
import java.time.LocalDateTime

class WordConverter : KoinComponent {

    fun convert(source: WordCsvDto): Word =
        Word(
            id = null,
            word = source.word,
            translation = source.translation,
            isDifficult = false,
            dictionaryId = null,
            createdDate = LocalDateTime.now(),
            checkDate = LocalDateTime.now()
        )

    fun convert(source: Word): WordDto =
        WordDto(
            id = source.id,
            word = source.word,
            translation = source.translation,
            isDifficult = source.isDifficult,
            dictionaryId = source.dictionaryId,
            createdDate = source.createdDate,
            checkDate = source.checkDate
        )
}