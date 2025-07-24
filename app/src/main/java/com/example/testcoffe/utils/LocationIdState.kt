package com.example.testcoffe.utils

import com.example.testcoffe.domain.model.LocationId

interface LocationIdState {
    object Idle : LocationIdState
    data class Empty(val message: String) : LocationIdState
    data class Error(val message: String) : LocationIdState
    data class Content(val data: List<LocationId>) : LocationIdState
}