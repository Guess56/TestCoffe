package com.example.testcoffe.domain.repository

import com.example.testcoffe.domain.model.Login
import com.example.testcoffe.utils.Resource
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    fun login(email: String, password: String): Flow<Resource<Login>>
}