package com.company.elverano.data.positionStack

import com.company.elverano.api.PositionStackApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PositionStackRepository @Inject constructor(private val positionStackApi: PositionStackApi) {
    fun getLocation(query: String) =  positionStackApi.getLocation(query= query)
}