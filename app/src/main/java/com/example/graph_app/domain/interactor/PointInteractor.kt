package com.example.graph_app.domain.interactor

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PointInteractor(
    val x: Double,
    val y: Double
): Parcelable
