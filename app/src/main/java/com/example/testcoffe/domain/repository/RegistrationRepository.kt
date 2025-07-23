package com.example.testcoffe.domain.repository

import com.example.testcoffe.domain.model.Registration
import com.example.testcoffe.utils.Resource
import kotlinx.coroutines.flow.Flow

interface RegistrationRepository {
    fun registration(email: String, password: String): Flow<Resource<Registration>>
}