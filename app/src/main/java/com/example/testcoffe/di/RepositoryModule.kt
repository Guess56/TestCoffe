package com.example.testcoffe.di

import com.example.testcoffe.data.repository.LocationIdRepositoryImpl
import com.example.testcoffe.data.repository.LocationRepositoryImpl
import com.example.testcoffe.data.repository.LoginRepositoryImpl
import com.example.testcoffe.data.repository.RegistrationRepositoryImpl
import com.example.testcoffe.data.repository.TokenRepositoryImpl
import com.example.testcoffe.domain.repository.LocationIdRepository
import com.example.testcoffe.domain.repository.LocationRepository
import com.example.testcoffe.domain.repository.LoginRepository
import com.example.testcoffe.domain.repository.RegistrationRepository
import com.example.testcoffe.domain.repository.TokenRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<RegistrationRepository> {
        RegistrationRepositoryImpl(get(), get())
    }
    single<TokenRepository> {
        TokenRepositoryImpl(androidContext())
    }
    single<LoginRepository> {
        LoginRepositoryImpl(get(), get())
    }
    single<LocationRepository> {
        LocationRepositoryImpl(get())
    }
    single<LocationIdRepository> {
        LocationIdRepositoryImpl(get())
    }
}