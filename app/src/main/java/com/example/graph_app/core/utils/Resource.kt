package com.example.graph_app.core.utils

sealed class Resource<out T> {
    data class Success<T>(val data: T): Resource<T>()
    data class Error(val message: String, val throwable: Throwable? = null): Resource<Nothing>()
    data object Loading: Resource<Nothing>()
    data object Idle: Resource<Nothing>()
}