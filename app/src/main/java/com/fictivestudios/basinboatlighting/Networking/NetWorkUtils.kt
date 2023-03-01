package com.fictivestudios.getmefit.Networking

import android.webkit.MimeTypeMap
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


/**
 * Get JSON Request body
 * */
fun String.getJsonRequestBody() = this.toRequestBody("application/json".toMediaTypeOrNull())


/**
 * Get Form Data Body
 * */
fun String.getFormDataBody() = this.toRequestBody("multipart/form-data".toMediaTypeOrNull())


/**
 * Get Part Map
 * */
fun File.getPartMap(key: String): MultipartBody.Part {
    val reqFile = this.asRequestBody(this.absolutePath.getMimeType().toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(key, this.name, reqFile)
}


/**
 * Convert any data into json data
 * */
fun Any.convertGsonString(): String = Gson().toJson(this)


/**
 * Convert String into Data Class
 * */
inline fun <reified T> String.convertStringIntoClass(): T = Gson().fromJson(this, T::class.java)


/**
 * Get Mime Type ......
 * */
fun String.getMimeType(): String {
    return try {
        val mimeType = MimeTypeMap.getFileExtensionFromUrl(this)
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeType) ?: ""
    } catch (e: Exception) {
        ""
    }
}