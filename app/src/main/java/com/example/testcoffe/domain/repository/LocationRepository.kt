package com.example.testcoffe.domain.repository

import com.example.testcoffe.domain.model.Location
import com.example.testcoffe.utils.Resource
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getLocation(): Flow<Resource<List<Location>>>
}