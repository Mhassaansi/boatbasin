package com.fictivestudios.basinboatlighting.models.subscribe

import com.fictivestudios.basinboatlighting.models.login.LoginData

data class SubscribeReponse(
    val bearer_token: String,
    val `data`: LoginData,
    val message: String,
    val status: Int
)