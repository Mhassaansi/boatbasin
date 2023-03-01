package com.fictivestudios.basinboatlighting.models.weather

data class Current(
    var clouds: Int?,
    var dew_point: Double?,
    var dt: Int?,
    var feels_like: Double?,
    var humidity: Int?,
    var pressure: Int?,
    var sunrise: Int?,
    var sunset: Int?,
    var temp: Double?,
    var uvi: Float?,
    var visibility: Int?,
    var weather: List<WeatherX>?,
    var wind_deg: Int?,
    var wind_gust: Double?,
    var wind_speed: Double?
)