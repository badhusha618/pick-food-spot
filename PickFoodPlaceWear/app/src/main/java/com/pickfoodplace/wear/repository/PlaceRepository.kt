package com.pickfoodplace.wear.repository

import com.pickfoodplace.wear.data.local.PlaceDao
import com.pickfoodplace.wear.data.local.PlaceEntity
import com.pickfoodplace.wear.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class PlaceRepository(
    private val api: ApiService,
    private val placeDao: PlaceDao
) {
    fun getPlaces(lat: Double?, lng: Double?, minPrice: Int?, maxPrice: Int?, query: String? = null): Flow<List<PlaceEntity>> = flow {
        val cached = withContext(Dispatchers.IO) { placeDao.getAll() }
        if (cached.isNotEmpty()) emit(cached)
        try {
            val remote = api.getPlaces(lat, lng, minPrice, maxPrice, query)
            val entities = remote.map { PlaceEntity(it.id, it.name, it.location, it.hourlyRate) }
            withContext(Dispatchers.IO) {
                placeDao.clear()
                placeDao.insertAll(entities)
            }
            emit(entities)
        } catch (e: Exception) {
            // emit cache if available
        }
    }
}