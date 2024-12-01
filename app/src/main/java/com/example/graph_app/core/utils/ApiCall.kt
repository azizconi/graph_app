package com.example.graph_app.core.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import retrofit2.Response
import java.io.IOException

fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Flow<Resource<T>> = flow {
    val response = apiCall()
    if (response.isSuccessful) {
        val body = response.body()
        if (body != null) {
            emit(Resource.Success(body))
        } else {
            emit(Resource.Error("Пустое тело ответа"))
        }
    } else {
        val errorMsg = response.errorBody()?.string()
        emit(Resource.Error(errorMsg ?: "Ошибка"))
    }
}.onStart {
    emit(Resource.Loading)
}.catch { e ->
    Log.e("safeApiCall", "error = ${e.message}, localizedMessage = ${e.localizedMessage}", )
    val errorMessage = when (e) {
        is IOException -> "Проблемы с сетью"
        else -> "Неизвестная ошибка"
    }
    emit(Resource.Error(errorMessage, e))
}.flowOn(Dispatchers.IO)


inline fun <T, R> Flow<Resource<T>>.mapData(crossinline transform: suspend (T) -> R): Flow<Resource<R>> {
    return this.map { resource ->
        when (resource) {
            is Resource.Success -> {
                val transformedData = transform(resource.data)
                Resource.Success(transformedData)
            }
            is Resource.Error -> Resource.Error(resource.message, resource.throwable)
            is Resource.Loading -> Resource.Loading
            is Resource.Idle -> Resource.Idle
        }
    }
}