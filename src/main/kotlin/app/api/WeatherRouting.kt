package org.mpo.app

import app.model.LocationDictionary
import app.service.WeatherService
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.path
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    install(ContentNegotiation) {json()}

    val weatherService = WeatherService()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/weather/summary"){
            val unit  = call.request.queryParameters["unit"] ?: "celsius"
            val temperature: Int = call.request.queryParameters["temperature"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest,"Missing or invalid 'temperature' parameter")
            val locations = call.request.queryParameters["locations"]?.split(",")?.mapNotNull { it.toIntOrNull() } ?: return@get call.respond(HttpStatusCode.BadRequest,"Missing or invalid 'locations' parameter")

            val response = weatherService.getSummary(unit,temperature,locations)
            call.respond(response)
        }
        get("/weather/locations/{locationId}"){
            val locationId: Int = call.parameters["locationId"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest,"Missing or invalid 'locationId' path parameter")

            val location = LocationDictionary.getLocation(locationId)?: return@get call.respond(HttpStatusCode.NotFound,"Coordinates for location: $locationId not found")

            val response = weatherService.getLocationTemperatures(location)
            call.respond(response)
        }
    }
}
