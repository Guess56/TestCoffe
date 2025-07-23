package com.example.testcoffe.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testcoffe.data.interactor.LocationInteractor
import com.example.testcoffe.domain.model.Location
import com.example.testcoffe.domain.model.Point
import com.example.testcoffe.utils.LocationMapper.toCafeItems
import com.example.testcoffe.utils.LocationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationViewModel(
    private val locationInteractor: LocationInteractor
): ViewModel() {
    private val _state = MutableStateFlow<LocationState>(LocationState.Idle)
    val state: StateFlow<LocationState> = _state.asStateFlow()

    fun getLocation(pointUser: Point) {
        viewModelScope.launch {
            locationInteractor
                .getLocation()
                .collect { (locations, errorMessage) ->
                    Log.e("getLocation", "locations: $locations, error: $errorMessage")
                    if (!errorMessage.isNullOrEmpty()) {
                        processError(errorMessage)
                    } else {
                        processResult(locations, pointUser)
                    }
                }
        }
    }

    private fun processResult(data: List<Location>?,pointUser: Point) {
        if (data.isNullOrEmpty()) {
            _state.value = LocationState.Empty("Список локаций пуст")
        } else {
            val cafeItemsList = data.map { location -> location.toCafeItems(pointUser) }
            _state.value = LocationState.Content(cafeItemsList)
        }
    }

    private fun processError(message: String) {
        _state.value = LocationState.Error(message)
    }
}