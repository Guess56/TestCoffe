package com.example.testcoffe.domain.interactor

import com.example.testcoffe.data.interactor.LocationInteractor
import com.example.testcoffe.domain.model.Location
import com.example.testcoffe.domain.repository.LocationRepository
import com.example.testcoffe.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocationInteractorImpl(val repository: LocationRepository) : LocationInteractor {
    override fun getLocation(): Flow<Pair<List<Location>?, String?>> {
        return repository.getLocation().map { result ->
            when(result) {
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