package com.company.elverano.api

import com.company.elverano.BuildConfig
import com.company.elverano.data.positionStack.PositionStackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PositionStackApi {
    @GET("forward?access_key=$ClientID&limit=1")
    fun getLocation(
        @Query("query") query: String
    ): Call<PositionStackResponse>?

    companion object {
        const val BASE_URL = "http://api.positionstack.com/v1/"
        const val ClientID = BuildConfig.POSITION_STACK_ACCESS_KEY
    }
}