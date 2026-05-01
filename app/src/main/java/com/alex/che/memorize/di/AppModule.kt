package com.alex.che.memorize.di

import com.alex.che.memorize.converter.WordConverter
import com.alex.che.memorize.domain.CsvService
import org.koin.dsl.module

val appModule = module {
    single { WordConverter() }
    single { CsvService(memorizeDatabase = get(), wordConverter = get()) }
}
