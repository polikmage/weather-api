package org.mpo.app

import app.model.ErrorResponse
import app.model.LocationDictionary
import app.model.WeatherUnitType
import app.service.WeatherService
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    install(ContentNegotiation) { json() }
    val apiKey = environment.config.property("weather.apiKey").getString()
    val apiUrl = environment.config.property("weather.apiUrl").getString()
    val weatherService = WeatherService(apiKey,apiUrl)

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/weather/summary") {
            val unit = call.request.queryParameters["unit"] ?: "celsius"
            val temperature: Int = call.request.queryParameters["temperature"]?.toIntOrNull()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        status = HttpStatusCode.BadRequest.value,
                        error = "BadRequest",
                        message = "Missing or invalid 'temperature' parameter"
                    )
                )
            val locations = call.request.queryParameters["locations"]?.split(",")?.mapNotNull { it.toIntOrNull() }
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        status = HttpStatusCode.BadRequest.value,
                        error = "BadRequest",
                        message = "Missing or invalid 'locations' parameter"
                    )
                )

            if (locations.isEmpty()) {
                return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        status = HttpStatusCode.BadRequest.value,
                        error = "BadRequest",
                        message = "No valid integer values in 'locations' parameter"
                    )
                )
            }


            val locationInfos = locations.map { locId ->
                LocationDictionary.getLocation(locId) ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse(
                        status = HttpStatusCode.NotFound.value,
                        error = "NotFound",
                        message = "Coordinates for location: $locId not found"
                    )
                )
            }

            val weatherUnit = WeatherUnitType.fromString(unit)

            val response = weatherService.getSummary(weatherUnit, temperature, locationInfos)
            call.respond(response)
        }
        get("/weather/locations/{locationId}") {
            val locationId: Int = call.parameters["locationId"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    status = HttpStatusCode.BadRequest.value,
                    error = "BadRequest",
                    message = "Missing or invalid 'locationId' path parameter"
                )
            )

            val location = LocationDictionary.getLocation(locationId) ?: return@get call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(
                    status = HttpStatusCode.BadRequest.value,
                    error = "BadRequest",
                    message = "Coordinates for location: $locationId not found"
                )
            )

            val response = weatherService.getLocationForecast(location, WeatherUnitType.CELSIUS)
            call.respond(response)
        }
    }
}
