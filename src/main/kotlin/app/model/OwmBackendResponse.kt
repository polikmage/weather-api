package app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable



    @Serializable
    data class OwmForecastResponse(
        val list: List<OwmWeatherListItem>
    )

    @Serializable
    data class OwmWeatherListItem(
        @SerialName("dt_txt")
        val dateTime: String,
        val main: OwmMainInfo
    )

    @Serializable
    data class OwmMainInfo(
        val temp: Double
    )
