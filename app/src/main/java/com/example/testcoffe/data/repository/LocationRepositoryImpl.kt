package com.example.testcoffe.data.repository

import com.example.testcoffe.data.dto.LocationResponse
import com.example.testcoffe.data.network.NetworkClient
import com.example.testcoffe.domain.model.Location
import com.example.testcoffe.domain.model.Point
import com.example.testcoffe.domain.repository.LocationRepository
import com.example.testcoffe.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocationRepositoryImpl(private val networkClient: NetworkClient) : LocationRepository {
    override fun getLocation(): Flow<Resource<List<Location>>> = flow {
        try {
            val response = networkClient.location()
            if (response.isSuccessful) {
                val locations = response.body()
                if (!locations.isNullOrEmpty()) {
                    emit(Resource.Success(locations))
                } else {
                    emit(Resource.Error("Список локаций пуст"))
                }
            } else {
                val errorMsg = when (response.code()) {
                    401 -> "Пользователь не авторизован"
                    else -> "Ошибка: ${response.code()} - ${response.message()}"
                }
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Сетевая ошибка: ${e.message ?: "Неизвестная ошибка"}"))
        }
    }
}

private fun LocationResponse.toLocation(): Location = Location(
    id = this.id.toInt(),
    name = this.name,
    point = Point(
        latitude = this.point.latitude.toDouble(),
        longitude = this.point.longitude.toDouble()
    )
)
