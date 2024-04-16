package com.example.healthdata.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResponseModel(
    val stepCounts: Long,
    val stepDays: Int,
    val distanceCounts: Double,
    val distanceDays: Int,
    val sleepsMinute: Long,
    val sleepDays: Int,
    val caloriesBurnedCount: Double,
    val caloriesBurnedDays: Int,
    val activityMinute: Long,
    val activityDays: Int
) : Parcelable
