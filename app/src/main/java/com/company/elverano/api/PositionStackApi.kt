package com.company.elverano.api

import com.company.elverano.BuildConfig
import com.company.elverano.data.positionStack.PositionStack
import com.company.elverano.data.positionStack.PositionStackResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface PositionStackApi {

    companion object{
        const val BASE_URL ="http://api.positionstack.com/v1/"
        const val ClientID = BuildConfig.POSITION_STACK_ACCESS_KEY
    }

    @GET("forward?access_key=$ClientID&limit=1")
    fun getLocation(
        @Query("query") query: String
    ): Call<PositionStackResponse>
}