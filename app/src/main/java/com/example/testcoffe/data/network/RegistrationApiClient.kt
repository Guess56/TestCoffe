package com.example.testcoffe.data.network

import com.example.testcoffe.data.dto.LocationIdResponse
import com.example.testcoffe.data.dto.LocationResponse
import com.example.testcoffe.data.dto.LoginRequest
import com.example.testcoffe.data.dto.LoginResponse
import com.example.testcoffe.data.dto.RegistrationRequest
import com.example.testcoffe.data.dto.RegistrationResponse
import com.example.testcoffe.domain.model.Location
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface RegistrationApiClient {
    @POST("/auth/register")
    suspend fun registration(@Body registrationRequest: RegistrationRequest): retrofit2.Response<RegistrationResponse>

    @POST("/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): retrofit2.Response<LoginResponse>

    @GET("/locations")
    suspend fun location(): retrofit2.Response<List<Location>>

    @GET("/location/{id}/menu")
    suspend fun locationId(
        @Path("id") id: Int
    ): retrofit2.Response<List<LocationIdResponse>>
}