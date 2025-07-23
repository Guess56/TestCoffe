package com.example.testcoffe.utils

import com.example.testcoffe.domain.model.CafeItems
import com.example.testcoffe.domain.model.Location

sealed interface LocationState {
    object Idle : LocationState
    data class Empty(val message: String) : LocationState
    data class Error(val message: String) : LocationState
    data class Content(val data: List<CafeItems>) : LocationState
}