package com.example.testcoffe.data.repository

import android.util.Log
import com.example.testcoffe.data.dto.RegistrationRequest
import com.example.testcoffe.data.dto.RegistrationResponse
import com.example.testcoffe.data.network.NetworkClient
import com.example.testcoffe.domain.model.Registration
import com.example.testcoffe.domain.repository.RegistrationRepository
import com.example.testcoffe.domain.repository.TokenRepository
import com.example.testcoffe.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class RegistrationRepositoryImpl(
    private val networkClient: NetworkClient,
    private val tokenRepository: TokenRepository
): RegistrationRepository {
    override fun registration(email: String, password: String): Flow<Resource<Registration>> = flow {
        try {
            if (email.isEmpty() || password.isEmpty()) {
                emit(Resource.Error("Данные не могут быть пустыми"))
                return@flow
            }
            val request = RegistrationRequest(email, password)
            val response = networkClient.doRequest(request)

            if (response.isSuccessful) {
                val loginResponse = response.body()
                if (loginResponse != null) {
                    val registration = loginResponse.toRegistration()
                    tokenRepository.saveToken(registration.token, registration.tokenLifeTime.toLong())
                    emit(Resource.Success(registration))
                } else {
                    emit(Resource.Error("Пустое тело ответа"))
                }
            } else {
                var errorMsg = "Ошибка: ${response.code()} - ${response.message()}"
                emit(Resource.Error("Ошибка: ${response.code()} - ${response.message()}"))
                if (response.code() == 406) {
                    errorMsg = "Пользователь с таким именем уже существует"
                }
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Сетевая ошибка: ${e.message}"))
        }

    }

}
private fun RegistrationResponse.toRegistration(): Registration {
    return Registration(
        token = this.token
            ?: throw IllegalArgumentException("token не может быть null"),
        tokenLifeTime = this.tokenLifeTime
            ?: throw IllegalArgumentException("tokenLifeTime не может быть null")
    )
}