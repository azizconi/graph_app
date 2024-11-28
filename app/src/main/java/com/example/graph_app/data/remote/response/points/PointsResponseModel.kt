package com.example.graph_app.data.remote.response.points

import com.example.graph_app.domain.interactor.PointInteractor

data class PointsResponseModel(
    val points: List<Point>
) {
    fun toPointsInteractor(): List<PointInteractor> = points.map {
        PointInteractor(it.x, it.y)
    }
}