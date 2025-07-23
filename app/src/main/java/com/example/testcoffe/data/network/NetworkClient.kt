package com.example.testcoffe.data.network

import com.example.testcoffe.data.dto.LocationResponse
import com.example.testcoffe.data.dto.LoginRequest
import com.example.testcoffe.data.dto.LoginResponse
import com.example.testcoffe.data.dto.RegistrationRequest
import com.example.testcoffe.data.dto.RegistrationResponse
import com.example.testcoffe.domain.model.Location

interface NetworkClient {
    suspend fun doRequest(request: RegistrationRequest): retrofit2.Response<RegistrationResponse>
    suspend fun login(loginRequest: LoginRequest): retrofit2.Response<LoginResponse>
    suspend fun location(): retrofit2.Response<List<Location>>
}