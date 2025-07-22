package app.service

import app.client.OpenWeatherMapClient
import app.model.LocationDictionary
import app.model.LocationForecastResponse
import app.model.SummaryLocationTemperature
import app.model.TemperatureForecast
import app.model.WeatherSummaryResponse
import app.model.WeatherUnitType

class WeatherService {

    val openWeatherMapClient = OpenWeatherMapClient()
    val weatherCache = WeatherCache()

    suspend fun getSummary(unit: String, temperature: Int, locations: List<Int>): WeatherSummaryResponse {
        val listOfLocationTemps: List<SummaryLocationTemperature> = listOf(
            SummaryLocationTemperature(1234, true),
            SummaryLocationTemperature(5678, false)
        )

        val weatherSummaryResponse = WeatherSummaryResponse(listOfLocationTemps)
        return weatherSummaryResponse


        // for each location check cache or call getLocationTemperatures
        //locations.
    }

    suspend fun getLocationTemperatures(
        location: LocationDictionary.LocationInfo, unit: WeatherUnitType
    ): LocationForecastResponse {

        val cached = weatherCache.getCache(location.locationId)
        if (cached != null) {
            return cached
        }

        val owmForecast = openWeatherMapClient.callWeatherApi(location, unit)
        println("Datetime and temperatures:")
        owmForecast.list.forEach { item ->
            println("${item.dateTime}: ${item.main.temp}°C")
        }

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

}