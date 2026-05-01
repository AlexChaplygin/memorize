package com.alex.che.memorize.di

import com.alex.che.memorize.viewmodel.AddWordViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::AddWordViewModel)
}
