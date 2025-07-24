package com.example.testcoffe.di

import com.example.testcoffe.ui.LocationViewModel
import com.example.testcoffe.ui.LoginViewModel
import com.example.testcoffe.ui.RegistrationViewModel
import com.example.testcoffe.ui.LocationIdViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { RegistrationViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { LocationViewModel(get()) }
    viewModel { LocationIdViewModel(get()) }
}