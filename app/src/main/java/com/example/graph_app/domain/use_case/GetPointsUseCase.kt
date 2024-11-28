package com.example.graph_app.domain.use_case

import com.example.graph_app.core.utils.Resource
import com.example.graph_app.domain.interactor.PointInteractor
import com.example.graph_app.domain.repository.GraphRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPointsUseCase @Inject constructor(private val graphRepository: GraphRepository) {

    operator fun invoke(count: Int): Flow<Resource<List<PointInteractor>>> =
        graphRepository.getPoints(count)


}