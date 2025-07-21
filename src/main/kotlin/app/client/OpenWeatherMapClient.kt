package app.client

import app.model.OwmForecastResponse
import app.model.LocationDictionary
import app.model.WeatherUnitType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


class OpenWeatherMapClient (){


    suspend fun callWeatherApi(location: LocationDictionary.LocationInfo, unit: WeatherUnitType): OwmForecastResponse {
        val apiKey = "567013658fe040b6718b4009ca831df5" // Replace with your key
        //val lat = "50.0874654"
        //val lon = "14.4212535"
        val url = "https://api.openweathermap.org/data/2.5/forecast" +
                "?lat=${location.lat}&lon=${location.lon}&appid=$apiKey&units=${unit.owmValue}"

        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json{
                    ignoreUnknownKeys = true
                })
            }
        }

        val owmForecast: OwmForecastResponse = client.get(url).body()

        client.close()
        return owmForecast
    }
}