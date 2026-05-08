package com.daniel.chickenfood

import android.app.Application
import com.daniel.chickenfood.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ChickenFoodApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@ChickenFoodApp)
            modules(appModule)
        }
    }
}
