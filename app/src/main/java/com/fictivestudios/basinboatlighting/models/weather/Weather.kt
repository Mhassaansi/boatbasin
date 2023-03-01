package com.fictivestudios.basinboatlighting.models.weather

data class Weather(
    var alerts: ArrayList<Alert>?,
    var current: Current?,
    var daily: ArrayList<Daily>?,
    var hourly: ArrayList<Hourly>?,
    var lat: Double?,
    var lon: Double?,
    var minutely: ArrayList<Minutely>?,
    var timezone: String?,
    var timezone_offset: Int?
)