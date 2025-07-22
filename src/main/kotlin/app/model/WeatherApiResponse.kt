package app.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherSummaryResponse(
    val locations: List<SummaryLocationTemperature>
)

@Serializable
data class SummaryLocationTemperature(
    val locationId: Int,
    val willBeWarmer: Boolean
)

@Serializable
data class LocationForecastResponse(
    val locationInfo: LocationDictionary.LocationInfo,
    val temperatureForecast: List<TemperatureForecast>
)

@Serializable
data class TemperatureForecast(
    val dateTime: String,
    val temperature: Double
)

@Serializable
data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String
)

