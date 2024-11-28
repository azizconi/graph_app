package com.example.graph_app.data.repository

import com.example.graph_app.core.utils.Resource
import com.example.graph_app.core.utils.mapData
import com.example.graph_app.core.utils.safeApiCall
import com.example.graph_app.data.remote.GraphApi
import com.example.graph_app.domain.interactor.PointInteractor
import com.example.graph_app.domain.repository.GraphRepository
import kotlinx.coroutines.flow.Flow

class GraphRepositoryImpl(private val api: GraphApi): GraphRepository {
    override fun getPoints(count: Int): Flow<Resource<List<PointInteractor>>> = safeApiCall {
        api.getPoints(count)
    }.mapData { it.toPointsInteractor() }
}