package app.service

import app.model.LocationForecastResponse
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.time.Instant

class WeatherCache(val ttl: Long = 600) {
    private val cache = ConcurrentHashMap<Int, Pair<LocationForecastResponse, Instant>>()

    fun setCache(locationForecastResponse: LocationForecastResponse) {
        cache[locationForecastResponse.locationInfo.locationId] = locationForecastResponse to Instant.now()
    }

    fun getCache(locationId: Int): LocationForecastResponse? {

        val cachedAt = cache[locationId]?.second ?: return null
        val isFresh = Duration.between(cachedAt, Instant.now()).seconds < ttl

        if (isFresh){
            return cache[locationId]?.first
        }
        return null
    }
}