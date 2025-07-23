package com.example.testcoffe.utils

import com.example.testcoffe.domain.model.Registration

sealed interface RegistrationState {
    object Idle : RegistrationState
    data class Empty(val message: String) : RegistrationState
    data class Error(val message: String) : RegistrationState
    data class Content(val data: Registration) : RegistrationState
}