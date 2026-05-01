package com.alex.che.memorize

import android.app.Application
import androidx.room.Room
import com.alex.che.memorize.di.appModule
import com.alex.che.memorize.di.viewModelModule
import com.alex.che.memorize.repository.MemorizeDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {
    private val DATABASE_NAME = "memorize"

    val databaseModule = module {
        single {
            Room.databaseBuilder(androidApplication(), MemorizeDatabase::class.java, DATABASE_NAME)
                .build()
        }
        single { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(databaseModule, appModule, viewModelModule)
            fragmentFactory()
        }
    }
}