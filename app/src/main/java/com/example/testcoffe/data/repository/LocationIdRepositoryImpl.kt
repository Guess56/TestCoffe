package com.example.testcoffe.data.repository

import com.example.testcoffe.data.network.NetworkClient
import com.example.testcoffe.domain.model.LocationId
import com.example.testcoffe.domain.repository.LocationIdRepository
import com.example.testcoffe.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocationIdRepositoryImpl(private val networkClient: NetworkClient):LocationIdRepository {
    override fun getMenu(id: Int): Flow<Resource<List<LocationId>>> = flow {
        try {
            val response = networkClient.locationId(id)
            if (response.isSuccessful) {
                val locationIdResponse = response.body()
                if (!locationIdResponse.isNullOrEmpty()) {
                    val locationsId = locationIdResponse.map { item ->
                        LocationId(
                            id = item.id,
                            name = item.name,
                            imageUrl = item.imageUrl,
                            price = item.price)
                    }
                    emit(Resource.Success(locationsId))
                } else {
                    emit(Resource.Error("Список меню пуст"))
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