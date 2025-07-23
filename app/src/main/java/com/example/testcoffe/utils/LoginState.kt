package com.example.testcoffe.utils

import com.example.testcoffe.domain.model.Login

sealed interface LoginState {
    object Idle : LoginState
    data class Empty(val message: String) : LoginState
    data class Error(val message: String) : LoginState
    data class Content(val data: Login) : LoginState
}