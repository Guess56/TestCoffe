package com.example.testcoffe.data.network

import com.example.testcoffe.data.dto.LocationIdResponse
import com.example.testcoffe.data.dto.LocationResponse
import com.example.testcoffe.data.dto.LoginRequest
import com.example.testcoffe.data.dto.LoginResponse
import com.example.testcoffe.data.dto.RegistrationRequest
import com.example.testcoffe.data.dto.RegistrationResponse
import com.example.testcoffe.domain.model.Location
import retrofit2.Response

class RetrofitClient(private val apiClient: RegistrationApiClient) : NetworkClient {
    override suspend fun doRequest(request: RegistrationRequest): Response<RegistrationResponse> {
        return apiClient.registration(request)
    }

    override suspend fun login(loginRequest: LoginRequest): Response<LoginResponse> {
        return apiClient.login(loginRequest)
    }

    override suspend fun location(): Response<List<Location>> {
        return apiClient.location()
    }

    override suspend fun locationId(id: Int): Response<List<LocationIdResponse>> {
        return apiClient.locationId(id)
    }
}