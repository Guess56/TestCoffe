package com.example.testcoffe.di

import com.example.testcoffe.data.interactor.LocationInteractor
import com.example.testcoffe.data.interactor.LoginInteractor
import com.example.testcoffe.data.interactor.RegistrationInteractor
import com.example.testcoffe.domain.interactor.LocationInteractorImpl
import com.example.testcoffe.domain.interactor.LoginInteractorImpl
import com.example.testcoffe.domain.interactor.RegistrationInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    single<RegistrationInteractor> {
        RegistrationInteractorImpl(get())
    }
    single<LoginInteractor> {
        LoginInteractorImpl(get())
    }
    single<LocationInteractor> {
        LocationInteractorImpl(get())
    }
}