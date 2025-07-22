package app.service

import app.model.LocationForecastResponse
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.time.Instant

class WeatherCache(val ttl: Long = 3600) {

    val log = LoggerFactory.getLogger("WeatherCache")
    private val cache = ConcurrentHashMap<Int, Pair<LocationForecastResponse, Instant>>()

    fun setCache(locationForecastResponse: LocationForecastResponse) {
        cache[locationForecastResponse.locationInfo.locationId] = locationForecastResponse to Instant.now()
    }

    fun getCache(locationId: Int): LocationForecastResponse? {

        val cachedAt = cache[locationId]?.second ?: return null
        val isFresh = Duration.between(cachedAt, Instant.now()).seconds < ttl

        if (isFresh){
            log.info("Cache is fresh, cached at: $cachedAt, return cached value!")
            return cache[locationId]?.first
        }
        return null
    }
}