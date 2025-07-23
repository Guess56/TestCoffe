package com.example.testcoffe

import android.app.Application
import android.util.Log
import com.example.testcoffe.di.dataModule
import com.example.testcoffe.di.interactorModule
import com.example.testcoffe.di.repositoryModule
import com.example.testcoffe.di.viewModelModule
import com.yandex.mapkit.MapKitFactory
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        val apiKey = getString(R.string.mapkit_api_key)
        MapKitFactory.setApiKey(apiKey)
        MapKitFactory.initialize(this)
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }
    }
}