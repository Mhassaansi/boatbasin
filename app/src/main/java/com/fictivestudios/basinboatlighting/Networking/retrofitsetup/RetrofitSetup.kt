package com.fictivestudios.tafcha.networkSetup.retrofitsetup

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.ecommercemvvmpractice2.utilities.extensions.showToast
import com.fictivestudios.basinboatlighting.activities.HomeActivity
import com.fictivestudios.basinboatlighting.activities.RegistrationActivity
import com.fictivestudios.basinboatlighting.databinding.ProgressloaderBinding
import com.fictivestudios.basinboatlighting.utils.validationMessage
import com.fictivestudios.getmefit.Networking.ApiService
import com.fictivestudios.getmefit.Networking.BASE_URL
import com.fictivestudios.tafcha.Utils.PreferenceData
import com.fictivestudios.tafcha.Utils.PreferenceUtils
import com.fictivestudios.tafcha.networkSetup.callhandler.CallHandler
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitSetup {
    // lateinit var  networkMonitor: NetworkConnectionInterceptor
    private lateinit var dialog: AlertDialog
    fun <T> callApi(
        context: Context,
        progress: Boolean,
        isSpeech:Boolean,
        token: String,
        callHandler: CallHandler<T>
    ) {


        val okHttpClient: OkHttpClient = if (token.isEmpty()) {
            OkHttpClient.Builder()
                .readTimeout(150, TimeUnit.MINUTES)
                .connectTimeout(150, TimeUnit.MINUTES)
                .protocols(listOf(Protocol.HTTP_1_1))
                .addInterceptor(

                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                )
                .build()


        }
        else {

            OkHttpClient.Builder()
                .readTimeout(250, TimeUnit.MINUTES)
                .connectTimeout(250, TimeUnit.MINUTES)
                .protocols(listOf(Protocol.HTTP_1_1))
                .addInterceptor {
                        chain ->
                 HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                    val original = chain.request()
                    val request = original.newBuilder()
                        .header("accept","application/json")
                        .header("Authorization", "Bearer " + token)
                        .addHeader("Connection","close")
                        .method(original.method, original.body)
                        .build()
                        chain.proceed(request)

                }
                .build()

        }


        val gson = GsonBuilder()
            .setLenient()
            .create()

        var apiInterface :ApiService?=null


          val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()

        val retrofitweather = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        if(isSpeech){

            apiInterface = retrofitweather.create(ApiService::class.java)
        }
        else{
            apiInterface = retrofit.create(ApiService::class.java)
        }













        /**
         * Coroutine Exception Handler
         * */
        val coRoutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()

            CoroutineScope(Dispatchers.Main).launch {
                throwable.message.let { callHandler.error(it ?: "") }
            }
        }


        /**
         * Call Api
         * */
        CoroutineScope(Dispatchers.IO + coRoutineExceptionHandler + Job()).launch {
            flow {
                withContext(Dispatchers.Main) {
                    context.loader()
                }
                emit(callHandler.sendRequest(apiInterFace = apiInterface) as Response<*>)
            }.flowOn(Dispatchers.IO).collect { response ->

                withContext(Dispatchers.Main) {
                    hideLoader()


                    if (response.isSuccessful) {

                        callHandler.success(response as T)
                    } else {
                        response.errorBody()?.string()?.let {
                            val jsonObject = JSONObject(it)
//                            if (jsonObject.has("response_speak")) {
//                                callHandler.error(jsonObject.getString("response_speak"))
//                            }
                           // else
                                if (jsonObject.has("message")) {
                                context.validationMessage(jsonObject.getString("message"))
                                callHandler.error(jsonObject.getString("message"))
                                    if(jsonObject.getString("message")=="Unauthenticated."){
                                        PreferenceData.clearPreference(context)
                                        PreferenceUtils.saveBoolean("isLogin",false)
                                        PreferenceUtils.saveString("token",null)
                                        PreferenceUtils.saveBoolean("btONOFF", false)
                                        PreferenceUtils.saveBoolean("lb",false)
                                        PreferenceUtils.saveBoolean("fb",false)
                                        PreferenceUtils.saveBoolean("nb",false)
                                        context.startActivity(Intent(context, RegistrationActivity::class.java))
                                        HomeActivity().finish().javaClass::class.java
                                        Toast.makeText(context,"Logout Successfully...",Toast.LENGTH_LONG).show()


                                    }
                            }

                        }
                    }
                }
            }
        }

    }



    private fun Context.loader() = try {
        val alertBuilder = AlertDialog.Builder(this)
        val layoutView = ProgressloaderBinding.inflate(LayoutInflater.from(this))
        alertBuilder.setCancelable(true)
        alertBuilder.setView(layoutView.root)
        dialog = alertBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    } catch (e: Exception) {
        e.printStackTrace()
    }


    fun hideLoader() {
        if (::dialog.isInitialized && dialog.isShowing) {
            dialog.dismiss()
        }
    }

}