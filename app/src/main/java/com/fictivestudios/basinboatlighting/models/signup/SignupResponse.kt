package com.fictivestudios.basinboatlighting.models.signup

data class SignupResponse(
    var `data`: SignupData,
    var message: String,
    var status: Int
)