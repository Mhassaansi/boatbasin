package com.fictivestudios.basinboatlighting.models.login

data class LoginData(
    val avatar: String="",
    val device_token: String="",
    val device_type: String="",
    val email: String="",
    val emergency_number: String="",
    val first_name: String="",
    val id: Int=0,
    val is_allowed_flashy_bird: Int=0,
    val is_allowed_light_bird: Int=0,
    val is_allowed_location: Int=0,
    val is_allowed_loud_bird: Int=0,
    val is_allowed_nav_bird: Int=0,
    val is_allowed_push_notification: Int=0,
    val is_subscribed: Int=0,
    val is_verified: Int=0,
    val lang: String="",
    val last_name: String="",
    val lat: String="",
    val profile_completed: Int=0
)