package com.alex.che.memorize

import android.app.Application
import androidx.room.Room
import com.alex.che.memorize.converter.WordConverter
import com.alex.che.memorize.domain.CsvService

import com.alex.che.memorize.repository.MemorizeDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {
    private val DATABASE_NAME = "memorize"
    val appModule = module {
        single {
            Room.databaseBuilder(androidApplication(), MemorizeDatabase::class.java, DATABASE_NAME)
                .allowMainThreadQueries().build()
        }
        single { CsvService() }
        single { WordConverter() }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
            fragmentFactory()
        }
    }
}