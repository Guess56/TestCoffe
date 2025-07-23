package com.example.testcoffe.domain.interactor

import com.example.testcoffe.data.interactor.RegistrationInteractor
import com.example.testcoffe.domain.model.Registration
import com.example.testcoffe.domain.repository.RegistrationRepository
import com.example.testcoffe.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RegistrationInteractorImpl(private val repository: RegistrationRepository) : RegistrationInteractor {
    override fun registration(email: String, password: String): Flow<Resource<Registration>> =
        repository.registration(email, password)
            .map { resource: Resource<Registration> ->
                resource
            }
}