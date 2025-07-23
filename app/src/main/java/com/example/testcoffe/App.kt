package com.example.testcoffe

import android.app.Application
import com.example.testcoffe.di.dataModule
import com.example.testcoffe.di.interactorModule
import com.example.testcoffe.di.repositoryModule
import com.example.testcoffe.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }
    }
}