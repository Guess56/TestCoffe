package com.example.testcoffe.data.repository

import android.util.Log
import com.example.testcoffe.data.dto.LoginRequest
import com.example.testcoffe.data.dto.LoginResponse
import com.example.testcoffe.data.network.NetworkClient
import com.example.testcoffe.domain.model.Login
import com.example.testcoffe.domain.repository.LoginRepository
import com.example.testcoffe.domain.repository.TokenRepository
import com.example.testcoffe.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoginRepositoryImpl(
    private val networkClient: NetworkClient,
    private val tokenRepository: TokenRepository
) : LoginRepository {
    override fun login(email: String, password: String): Flow<Resource<Login>> = flow {
        try {
            if (email.isEmpty() || password.isEmpty()) {
                emit(Resource.Error("Данные не могут быть пустыми"))
                return@flow
            }
            val request = LoginRequest(email, password)
            val response = networkClient.login(request)

            if (response.isSuccessful) {
                val loginResponse = response.body()

                if (loginResponse != null) {
                    val login = loginResponse.toLogin()
                    tokenRepository.saveToken(login.token, login.tokenLifeTime.toLong())
                    emit(Resource.Success(login))
                } else {
                    emit(Resource.Error("Пустое тело ответа"))
                }

            } else {
                var errorMsg = "Ошибка: ${response.code()} - ${response.message()}"
                if (response.code() == 404) {
                    errorMsg = "Пользователя не существует"
                }

                emit(Resource.Error(errorMsg))
            }

        } catch (e: Exception) {
            emit(Resource.Error("Сетевая ошибка: ${e.message}"))
        }
    }
}

private fun LoginResponse.toLogin(): Login {
    return Login(
        token = this.token ?: throw IllegalArgumentException("token не может быть null"),
        tokenLifeTime = this.tokenLifeTime ?: throw IllegalArgumentException("tokenLifeTime не может быть null")
    )
}