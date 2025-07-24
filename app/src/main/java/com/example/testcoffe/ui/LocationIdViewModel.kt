package com.example.testcoffe.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testcoffe.data.interactor.LocationIdInteractor
import com.example.testcoffe.domain.model.LocationId
import com.example.testcoffe.utils.LocationIdState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationIdViewModel(
    private val locationIdInteractor: LocationIdInteractor
): ViewModel() {
    private val _state = MutableStateFlow<LocationIdState>(LocationIdState.Idle)
    val state: StateFlow<LocationIdState> = _state.asStateFlow()

    private val _quantities = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val quantities: StateFlow<Map<Long, Int>> = _quantities

    fun getMenu(id: Int) {
        viewModelScope.launch {
            locationIdInteractor
                .getMenu(id)
                .collect { (menu, errorMessage) ->
                    Log.e("getLocation", "locations: $menu, error: $errorMessage")
                    if (!errorMessage.isNullOrEmpty()) {
                        processError(errorMessage)
                    } else {
                        processResult(menu)
                    }
                }
        }
    }
    fun setQuantity(id: Long, quantity: Int) {
        Log.d("LocationIdViewModel", "setQuantity: id=$id quantity=$quantity")
        _quantities.value = _quantities.value.toMutableMap().also { map ->
            if (quantity > 0) {
                map[id] = quantity
            } else {
                map.remove(id)
            }
        }
    }

    private fun processResult(data: List<LocationId>?) {
        if (data.isNullOrEmpty()) {
            _state.value = LocationIdState.Empty("Список локаций пуст")
        } else {
            _state.value = LocationIdState.Content(data)
        }
    }

    private fun processError(message: String) {
        _state.value = LocationIdState.Error(message)
    }
}
