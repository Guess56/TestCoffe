package com.example.testcoffe.data.interactor

import com.example.testcoffe.domain.model.Registration
import com.example.testcoffe.utils.Resource
import kotlinx.coroutines.flow.Flow

interface RegistrationInteractor {
    fun registration(email: String, password: String): Flow<Resource<Registration>>
}