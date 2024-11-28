package com.example.graph_app.data.remote

import com.example.graph_app.data.remote.response.points.PointsResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GraphApi {


    @GET("/test/points")
    suspend fun getPoints(@Query("count") count: Int): Response<PointsResponseModel>


}