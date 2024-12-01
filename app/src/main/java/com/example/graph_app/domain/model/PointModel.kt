package com.example.graph_app.domain.model

import com.example.graph_app.domain.interactor.PointInteractor

data class PointModel(
    val x: String,
    val y: String
) {
    companion object {
        fun toPointList(points: List<PointInteractor>): List<PointModel> {
            return points.map { toPointModel(it) }
        }

        private fun toPointModel(point: PointInteractor): PointModel {
            return PointModel(
                x = point.x.toString(),
                y = point.y.toString()
            )
        }
    }
}