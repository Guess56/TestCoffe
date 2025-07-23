package com.example.testcoffe.utils

import com.example.testcoffe.domain.model.CafeItems
import com.example.testcoffe.domain.model.Location
import com.example.testcoffe.domain.model.Point

object LocationMapper {
    fun Location.toCafeItems(userLocation: Point? = null): CafeItems {
        val distance = if (userLocation != null) {
            val meters = calculateDistance(userLocation, this.point)
            formatDistance(meters)
        } else {
            "Неизвестно"
        }

        return CafeItems(
            name = this.name,
            distance = distance
        )
    }
    fun calculateDistance(userLocation: Point, point2: Point): Double {
        val results = floatArrayOf(0f)
        android.location.Location.distanceBetween(
            userLocation.latitude,
            userLocation.longitude,
            point2.latitude,
            point2.longitude,
            results
        )
        return results[0].toDouble()
    }
    fun formatDistance(meters: Double): String {
        val formatted = when {
            meters < 1000 -> "${meters.toInt()} м"
            else -> "%.0f км".format(meters / 1000)
        }
        return "$formatted от вас"
    }
}