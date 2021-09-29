package com.company.elverano.data.positionStack

import com.company.elverano.api.PositionStackApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PositionStackRepository @Inject constructor(
    private val api: PositionStackApi,
    private val database: PositionStackDatabase
) {
    private val dao: PositionStackDao = database.positionStackDao()

    fun getLocationFromAPI(query: String) = api.getLocation(query)

    suspend fun getLocationFromDatabase() = dao.getPositionStack()
    suspend fun insertPositionToDB(data: PositionStackResponse) {
        dao.insertPositionStack(data)
    }

   suspend fun deletePositionFromDB() {
        dao.deletePositionStack()
    }
}