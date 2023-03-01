package com.fictivestudios.tafcha.Utils

import android.content.Context
import android.content.SharedPreferences
import com.fictivestudios.basinboatlighting.models.login.LoginData


import com.google.gson.Gson

object PreferenceData {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var foreverSharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    const val  PREFERENCE_NAME = "basinboatlighting"
    const val LOGIN_DATA = "loginData"


    fun storeKey(context: Context, key:  String, value: String) {
        sharedPreferences =
            context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun retrieveKey(context: Context, key: String): String? {
        sharedPreferences =
            context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }


    fun storeProfileData(context: Context, profileResponse: LoginData) {
        sharedPreferences =   context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        val prefsEditor: SharedPreferences.Editor = sharedPreferences.edit()
        prefsEditor.putString(LOGIN_DATA, Gson().toJson(profileResponse))
        prefsEditor.apply()
    }

    fun retrieveProfileData(context: Context): LoginData {
        sharedPreferences =
            context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        return if (sharedPreferences.getString(LOGIN_DATA, "").isNullOrEmpty()) {
            LoginData()
        } else {
            Gson().fromJson(
                sharedPreferences.getString(LOGIN_DATA, "")!!,
                LoginData::class.java
            )
        }
    }

//    fun storeProfileData(context: Context, profileResponse: LoginResponseData) {
//        sharedPreferences =
//            context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
//        val prefsEditor: SharedPreferences.Editor = sharedPreferences.edit()
//        prefsEditor.putString(LOGIN_DATA, Gson().toJson(profileResponse))
//        prefsEditor.apply()
//    }
//
//    fun retrieveProfileData(context: Context): LoginResponseData {
//        sharedPreferences =
//            context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
//        return if (sharedPreferences.getString(LOGIN_DATA, "").isNullOrEmpty()) {
//            LoginResponseData()
//        } else {
//            Gson().fromJson(
//                sharedPreferences.getString(LOGIN_DATA, "")!!,
//                LoginResponseData::class.java
//            )
//        }
//    }
////
//    fun storeupdateLoginData(context: Context, loginResponse: UpdateProfileItem?) {
//        sharedPreferences =
//            context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
//        val prefsEditor: SharedPreferences.Editor = sharedPreferences.edit()
//        prefsEditor.putString(LOGIN_DATA, Gson().toJson(loginResponse))
//        prefsEditor.apply()
//    }
//
//    fun retrieveupdatedataLoginData(context: Context): UpdateProfileItem {
//        sharedPreferences =
//            context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
//        return if (sharedPreferences.getString(LOGIN_DATA, "").isNullOrEmpty()) {
//            UpdateProfileItem()
//        } else {
//            Gson().fromJson(
//                sharedPreferences.getString(LOGIN_DATA, "")!!,
//                UpdateProfileItem::class.java
//            )
//        }
//    }

    fun clearPreference(context: Context) {
        sharedPreferences =
            context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun removeKey(context: Context, key: String) {
        sharedPreferences =
            context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }

    fun clearAllSharedPreferences() {
        editor.clear()
        editor.apply()
    }

}