package com.fictivestudios.basinboatlighting.ui.home.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommercemvvmpractice2.utilities.extensions.showToast
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.adapter.WeatherAdapter
import com.fictivestudios.basinboatlighting.base.BaseFragment
import com.fictivestudios.basinboatlighting.databinding.WwatherBinding
import com.fictivestudios.basinboatlighting.models.weather.Daily
import com.fictivestudios.basinboatlighting.models.weather.Weather
import com.fictivestudios.basinboatlighting.utils.Titlebar
import com.fictivestudios.basinboatlighting.utils.checkGPSEnable
import com.fictivestudios.basinboatlighting.utils.checkPermissionslocation
import com.fictivestudios.getmefit.Networking.ApiService
import com.fictivestudios.getmefit.uitilites.Constants
import com.fictivestudios.tafcha.Utils.PreferenceUtils
import com.fictivestudios.tafcha.networkSetup.callhandler.CallHandler
import com.fictivestudios.tafcha.networkSetup.retrofitsetup.RetrofitSetup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class WeatherScreen : BaseFragment(), WeatherAdapter.ItemClickListener {


    private lateinit var wbinding: WwatherBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var manager : LocationManager? =null
    var latitude: Float? = null
    var longitude: Float? = null

    var daliy = ArrayList<Daily>()

    override fun setTitlebar(titlebar: Titlebar) {

    }


    @SuppressLint("NewApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        wbinding = DataBindingUtil.inflate(inflater, R.layout.wwather, container, false)
        getActivityContext?.hideButtonHome()
        getActivityContext?.hideBttomBar()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivityContext!!)
        manager = getActivityContext?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (checkPermissionslocation(getActivityContext!!)) {
            if (!manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getActivityContext?.let { it1 -> checkGPSEnable(it1) }
            }
            else{
                currentLocation()

            }



        } else {
            requestPermissions()
            currentLocation()


        }



        wbinding.backwether.setOnClickListener {
            getActivityContext?.onBackPressed()
        }


        return wbinding.root


    }


    fun showWatherAdapter() {

        val homeDailyExerciesAdapter = WeatherAdapter(getActivityContext!!, daliy, this)
        wbinding.btdevList.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL, false
            )
            wbinding.btdevList.adapter = homeDailyExerciesAdapter
            wbinding.btdevList.adapter!!.notifyDataSetChanged()
        }

    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun weather() {

        lifecycleScope.launch(Dispatchers.IO) {
            if (Constants.isNetworkConnected(getActivityContext!!, true)) {
                try {

                    RetrofitSetup().callApi(getActivityContext!!,
                        true,
                        true,
                        PreferenceUtils.getString("token"),
                        object : CallHandler<Response<Weather>> {
                            override suspend fun sendRequest(apiInterFace: ApiService): Response<Weather> {
                                return apiInterFace.weather(
                                    // loginData.lat.toFloat(),
                                    //loginData.lang.toFloat(),
                                    latitude!!.toFloat(),
                                    longitude!!.toFloat(),
                                    "2ac92682800e42aba666346e3a92af0d",
                                    "imperial"
                                )
                            }

                            override fun success(response: Response<Weather>) {

                                daliy = response.body()?.daily!!
                                wbinding.cTemp.text = response.body()?.current?.temp?.toInt()
                                    ?.let { it.toString() } + " \u2109"
                                showWatherAdapter()

                            }

                            override fun error(message: String) {
                                RetrofitSetup().hideLoader()
                                Toast.makeText(
                                    getActivityContext!!, "Error",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }
                    )


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

    override fun onItemClick(
        dt: Long,
        susnet: Long,
        sunrise: Long,
        moonrise: Long,
        moonset: Long,
        humidity: Int,
        wind_speed: Float,
        windDegree : String,
        min: Float,
        max: Float,
        dsp: String
    ) {


        val action = WeatherScreenDirections.actionWeatherScreenToWeatherDescription(
            dt.toString(),
            susnet.toString(),
            sunrise.toString(),
            moonrise.toString(),
            moonset.toString(),
            humidity.toString(),
            wind_speed.toString(),
            windDegree,
            min.toInt(),
            max.toInt(),
            dsp,
            wbinding.cTemp.text.toString()

        )
        findNavController().navigate(action)


    }


    private fun currentLocation() {

        if (ActivityCompat.checkSelfPermission(
                getActivityContext!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getActivityContext!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latitude = location.latitude.toFloat()
                    longitude = location.longitude.toFloat()
                  //  showToast(latitude.toString() + " " + longitude.toString(), getActivityContext)
                    weather()

                } else {

                  //  showToast("Location is empty", getActivityContext)
                }


            }

    }


    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            getActivityContext!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            getActivityContext!!,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


            } else {
                //  showSnackBar("Open the Setting", this)

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermissions() {

        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), 100
        )

    }

//    override fun onItemClick(
//        dt: Long,
//        susnet: Long,
//        sunrise: Long,
//        moonrise: Long,
//        moonset: Long,
//        humidity: Int,
//        wind_speed: Float,
//        min: Float,
//        max: Float,
//        dsp: String
//    ) {
//        val action = WeatherScreenDirections.actionWeatherScreenToWeatherDescription(
//            dt.toString(),
//            susnet.toString(),
//            sunrise.toString(),
//            moonrise.toString(),
//            moonset.toString(),
//            humidity.toString(),
//            wind_speed.toString(),
//            windDegree,
//            min.toInt(),
//            max.toInt(),
//            dsp,
//            wbinding.cTemp.text.toString()
//
//        )
//        findNavController().navigate(action)
//    }


}