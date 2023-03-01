package com.fictivestudios.tafcha.networkSetup.callhandler

import com.fictivestudios.getmefit.Networking.ApiService


interface CallHandler<T>{

suspend fun sendRequest (apiInterFace: ApiService):T

fun success (response:T)

fun error (message:String)


}