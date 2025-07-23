package com.example.testcoffe.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testcoffe.data.interactor.LoginInteractor
import com.example.testcoffe.domain.model.Login
import com.example.testcoffe.utils.LoginState
import com.example.testcoffe.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val loginInteractor: LoginInteractor): ViewModel() {
    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            loginInteractor
                .login(email, password)
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            processResult(resource.data)
                        }
                        is Resource.Error -> {
                            processError(resource.message.toString())
                        }
                    }
                }
        }
    }

    private fun processResult(data: Login?) {
        if (data == null) {
            _state.value = LoginState.Error("Получены пустые данные")
        } else {
            _state.value = LoginState.Content(data)
        }
    }

    private fun processError(message: String) {
        _state.value = LoginState.Error(message)
    }

}