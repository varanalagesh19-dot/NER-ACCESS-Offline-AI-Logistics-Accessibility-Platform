package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route_history")
data class RouteHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originName: String,
    val destinationName: String,
    val routeTitle: String,
    val distanceKm: Double,
    val durationMinutes: Int,
    val accessibilityScore: Int,
    val difficulty: String,
    val preference: String,
    val pathSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)
