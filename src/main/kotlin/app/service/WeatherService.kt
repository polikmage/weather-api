package app.service

import app.client.OpenWeatherMapClient
import app.model.LocationDictionary
import app.model.LocationForecastResponse
import app.model.SummaryLocationTemperature
import app.model.TemperatureForecast
import app.model.WeatherSummaryResponse

class WeatherService {

    val openWeatherMapClient = OpenWeatherMapClient()

    suspend fun getSummary(unit: String, temperature: Int, locations: List<Int>): WeatherSummaryResponse {
        val listOfLocationTemps: List<SummaryLocationTemperature> = listOf(
            SummaryLocationTemperature(1234, true),
            SummaryLocationTemperature(5678, false)
        )

        val weatherSummaryResponse = WeatherSummaryResponse(listOfLocationTemps)
        return weatherSummaryResponse
        //return "Unit: $unit, Temperature: $temperature, Locations: $locations"
    }

    suspend fun getLocationTemperatures(location: LocationDictionary.LocationInfo): LocationForecastResponse {

        val owmForecast = openWeatherMapClient.callWeatherApi(location)
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

        return LocationForecastResponse(location, apiForecasts)
    }

}