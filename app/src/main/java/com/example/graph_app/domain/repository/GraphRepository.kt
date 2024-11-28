package com.example.graph_app.domain.repository

import com.example.graph_app.core.utils.Resource
import com.example.graph_app.domain.interactor.PointInteractor
import kotlinx.coroutines.flow.Flow

interface GraphRepository {
    fun getPoints(count: Int): Flow<Resource<List<PointInteractor>>>
}