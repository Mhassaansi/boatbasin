package com.fictivestudios.basinboatlighting.models.subscribe

data class Data(
    val avatar: String,
    val device_token: String,
    val device_type: String,
    val email: String,
    val emergency_number: String,
    val first_name: String,
    val id: Int,
    val is_allowed_flashy_bird: Int,
    val is_allowed_light_bird: Int,
    val is_allowed_location: Int,
    val is_allowed_loud_bird: Int,
    val is_allowed_nav_bird: Int,
    val is_allowed_push_notification: Int,
    val is_subscribed: Int,
    val is_verified: Int,
    val lang: String,
    val last_name: String,
    val lat: String,
    val profile_completed: Int
)