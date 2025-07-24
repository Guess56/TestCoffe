package com.example.testcoffe.data.interactor

import com.example.testcoffe.domain.model.LocationId
import kotlinx.coroutines.flow.Flow

interface LocationIdInteractor {
    fun getMenu(id: Int): Flow<Pair<List<LocationId>?, String?>>
}