package com.example.testcoffe.di

import com.example.testcoffe.data.network.NetworkClient
import com.example.testcoffe.data.network.RegistrationApiClient
import com.example.testcoffe.data.network.RetrofitClient
import com.example.testcoffe.data.repository.TokenRepositoryImpl
import com.example.testcoffe.domain.interactor.AuthInterceptor
import com.example.testcoffe.domain.repository.TokenRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val dataModule = module {
    single<RegistrationApiClient> {
        Retrofit.Builder()
            .baseUrl("http://212.41.30.90:35005/")
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RegistrationApiClient::class.java)
    }
    single { AuthInterceptor(get()) }
    single<NetworkClient> {
        RetrofitClient(get())
    }
    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    single<TokenRepository> { TokenRepositoryImpl(get()) }
}