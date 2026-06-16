package com.daniel.chickenfood

import android.app.Application
import com.daniel.chickenfood.di.appModule
import com.daniel.chickenfood.helper.AppConfigs
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ChickenFoodApp : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Inicializar AppConfigs con persistencia de sesión
        AppConfigs.init(applicationContext)
        
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@ChickenFoodApp)
            modules(appModule)
        }
    }
}
