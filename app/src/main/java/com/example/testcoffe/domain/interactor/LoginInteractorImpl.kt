package com.example.testcoffe.domain.interactor

import com.example.testcoffe.data.interactor.LoginInteractor
import com.example.testcoffe.domain.model.Login
import com.example.testcoffe.domain.repository.LoginRepository
import com.example.testcoffe.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LoginInteractorImpl (private val repository: LoginRepository) : LoginInteractor {
    override fun login(email: String, password: String): Flow<Resource<Login>> =
        repository.login(email, password)
            .map { resource: Resource<Login> ->
                resource
            }

}