package com.example.testcoffe.domain.interactor

import com.example.testcoffe.data.interactor.LocationIdInteractor
import com.example.testcoffe.domain.model.LocationId
import com.example.testcoffe.domain.repository.LocationIdRepository
import com.example.testcoffe.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocationIdInteractorImpl(val repository: LocationIdRepository): LocationIdInteractor {
    override fun getMenu(id: Int): Flow<Pair<List<LocationId>?, String?>> {
        return repository.getMenu(id).map { result ->
            when (result) {
                is Resource.Error -> {
                    Pair(null, result.message)
                }

                is Resource.Success -> {
                    Pair(result.data, null)
                }
            }
        }
    }
}