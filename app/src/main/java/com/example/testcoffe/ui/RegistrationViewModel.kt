package com.example.testcoffe.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testcoffe.data.interactor.RegistrationInteractor
import com.example.testcoffe.domain.model.Registration
import com.example.testcoffe.utils.RegistrationState
import com.example.testcoffe.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(private val registrationInteractor: RegistrationInteractor) : ViewModel() {
    private val _state = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val state: StateFlow<RegistrationState> = _state.asStateFlow()

    fun registration(email: String, password: String) {
        viewModelScope.launch {
            registrationInteractor
                .registration(email, password)
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

    private fun processResult(data: Registration?) {
        if (data == null) {
            _state.value = RegistrationState.Error("Получены пустые данные")
        } else {
            _state.value = RegistrationState.Content(data)
        }
    }

    private fun processError(message: String) {
        _state.value = RegistrationState.Error(message)
    }
}