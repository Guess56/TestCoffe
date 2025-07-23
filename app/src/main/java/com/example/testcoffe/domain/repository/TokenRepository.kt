package com.example.testcoffe.domain.repository

interface TokenRepository {
    suspend fun saveToken(token: String, lifetime: Long)
    suspend fun getToken(): String?
    suspend fun getTokenLifetime(): Long
    suspend fun clearToken()
}