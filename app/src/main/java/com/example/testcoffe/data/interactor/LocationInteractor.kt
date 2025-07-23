package com.example.testcoffe.data.interactor

import com.example.testcoffe.domain.model.Location
import com.example.testcoffe.utils.Resource
import kotlinx.coroutines.flow.Flow

interface LocationInteractor {
    fun getLocation(): Flow<Pair<List<Location>?, String?>>
}