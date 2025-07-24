package com.example.testcoffe.domain.repository

import com.example.testcoffe.domain.model.LocationId
import com.example.testcoffe.utils.Resource
import kotlinx.coroutines.flow.Flow

interface LocationIdRepository {
    fun getMenu(id: Int): Flow<Resource<List<LocationId>>>
}