package app.service

import app.client.OpenWeatherMapClient
import app.model.LocationDictionary
import app.model.LocationForecastResponse
import app.model.SummaryLocationTemperature
import app.model.TemperatureForecast
import app.model.WeatherSummaryResponse
import app.model.WeatherUnitType
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WeatherService(
    private val apiKey: String,
    private val apiUrl: String
) {
    val log = LoggerFactory.getLogger("WeatherService")
    val openWeatherMapClient = OpenWeatherMapClient(apiKey, apiUrl)
    val weatherCache = WeatherCache()

    suspend fun getSummary(
        unit: WeatherUnitType,
        temperature: Int,
        locationInfos: List<LocationDictionary.LocationInfo>
    ): WeatherSummaryResponse {
        val results = coroutineScope {
            locationInfos.map { locInfo ->
                async {
                    val forecast = getLocationForecast(locInfo, unit)
                    SummaryLocationTemperature(
                        locInfo.locationId,
                        willBeWarmer = willBeAboveThresholdTomorrow(forecast, temperature)
                    )
                }

            }.awaitAll()
        }

        return WeatherSummaryResponse(results)
    }

    suspend fun getLocationForecast(
        location: LocationDictionary.LocationInfo, unit: WeatherUnitType
    ): LocationForecastResponse {

        val cached = weatherCache.getCache(location.locationId)
        if (cached != null) {
            return cached
        }

        val owmForecast = openWeatherMapClient.callWeatherApi(location, unit)

        val apiForecasts = owmForecast.list.map {
            TemperatureForecast(
                dateTime = it.dateTime,
                temperature = it.main.temp
            )
        }

        val locationForecastResponse = LocationForecastResponse(location, apiForecasts)

        weatherCache.setCache(locationForecastResponse)

        return locationForecastResponse
    }

    fun willBeAboveThresholdTomorrow(
        response: LocationForecastResponse,
        threshold: Int,
        today: LocalDate = LocalDate.now() // for testability
    ): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val tomorrow = today.plusDays(1)
        return response.temperatureForecast.any { forecast ->
            val forecastDate = LocalDateTime.parse(forecast.dateTime, formatter).toLocalDate()
            forecastDate == tomorrow && forecast.temperature > threshold
        }
    }

}