package app.model

import kotlinx.serialization.Serializable

object LocationDictionary {

    @Serializable
    data class LocationInfo(val locationId: Int, val lat: Double, val lon: Double)

    private val locations = mapOf(
        2345 to LocationInfo(2345, 50.0755, 14.4378),   // Praha
        1456 to LocationInfo(1456, 40.7128, -74.0060),  // New York
        7653 to LocationInfo(7653, 27.7172, 85.3240)    // Kathmandu
    )

    fun getLocation(locationId: Int): LocationInfo? = locations[locationId]
}

enum class WeatherUnitType(val owmValue: String) {
    CELSIUS("metric"),
    FAHRENHEIT("imperial");

    companion object {
        fun fromString(param: String?): WeatherUnitType =
            when (param?.lowercase()) {
                "fahrenheit" -> FAHRENHEIT
                else -> CELSIUS
            }
    }
}