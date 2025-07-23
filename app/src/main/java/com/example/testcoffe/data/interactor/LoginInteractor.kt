package com.example.testcoffe.data.interactor

import com.example.testcoffe.domain.model.Login
import com.example.testcoffe.utils.Resource
import kotlinx.coroutines.flow.Flow

interface LoginInteractor {
    fun login(email: String, password: String): Flow<Resource<Login>>
}