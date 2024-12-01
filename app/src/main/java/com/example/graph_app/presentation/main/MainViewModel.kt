package com.example.graph_app.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graph_app.core.utils.Resource
import com.example.graph_app.domain.interactor.PointInteractor
import com.example.graph_app.domain.use_case.GetPointsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getPointsUseCase: GetPointsUseCase
): ViewModel() {

    private val _points = MutableStateFlow<Resource<List<PointInteractor>>>(Resource.Idle)
    val pointsResult = _points.asStateFlow()


    fun getPoints(count: Int) {
        viewModelScope.launch {
            getPointsUseCase(count).collect {
                _points.value = it
            }
        }
    }

    fun resetPointsResult() {
        _points.value = Resource.Idle
    }

}