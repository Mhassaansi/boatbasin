package com.fictivestudios.basinboatlighting.models.login

data class LoginResponse(
    var bearer_token: String,
    var `data`: LoginData,
    var message: String,
    var status: Int
)