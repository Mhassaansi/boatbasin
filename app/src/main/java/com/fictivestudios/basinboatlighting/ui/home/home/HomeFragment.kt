package com.fictivestudios.basinboatlighting.ui.home.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommercemvvmpractice2.utilities.extensions.showToast
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.adapter.HomeAdapter
import com.fictivestudios.basinboatlighting.base.BaseFragment
import com.fictivestudios.basinboatlighting.databinding.HomeBinding
import com.fictivestudios.basinboatlighting.models.HomeCardList
import com.fictivestudios.basinboatlighting.models.profile.ProfileMain
import com.fictivestudios.basinboatlighting.utils.Titlebar
import com.fictivestudios.basinboatlighting.utils.checkGPSEnable
import com.fictivestudios.basinboatlighting.utils.checkPermissionslocation
import com.fictivestudios.basinboatlighting.utils.resizeDialogView
import com.fictivestudios.getmefit.Networking.ApiService
import com.fictivestudios.getmefit.Networking.getJsonRequestBody
import com.fictivestudios.getmefit.data.response.models.CommonResponse
import com.fictivestudios.getmefit.uitilites.Constants
import com.fictivestudios.getmefit.uitilites.Constants.Companion.BLUETHOOTHGATT
import com.fictivestudios.tafcha.Utils.PreferenceUtils
import com.fictivestudios.tafcha.networkSetup.callhandler.CallHandler
import com.fictivestudios.tafcha.networkSetup.retrofitsetup.RetrofitSetup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.util.*

class HomeFragment : BaseFragment(), HomeAdapter.ItemClickListener {

    private lateinit var homeBinding: HomeBinding
    private val homeViewModel by viewModels<HomeFragmentViewModel>()
    private lateinit var homeCardDataList: ArrayList<HomeCardList>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var manager: LocationManager? = null
    var latitude: String? = null
    var longitude: String? = null


    var isLightOn = false
    var isLedflasingOn = false
    var isLed9wOn = false
    var isHornOn = false

    var isSos = false


    var gatt: BluetoothGatt? = null


    override fun setTitlebar(titlebar: Titlebar) {


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::homeBinding.isInitialized) {
            homeBinding = DataBindingUtil.inflate(inflater, R.layout.home, container, false)
            manager =
                getActivityContext?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(getActivityContext!!)

            init()
        }


        getActivityContext?.showButtonHome()
        getActivityContext?.showBttomBar()

        if (BLUETHOOTHGATT != null) {

            gatt = BLUETHOOTHGATT
            Log.d("Blutethoothgatt", gatt.toString())
        } else {


        }


        homeBinding.weather.setOnClickListener {

            val action = HomeFragmentDirections.actionHomeFragmentToWeatherScreen()
            findNavController().navigate(action)

        }


        homeBinding.ivHelp.setOnClickListener {

            val action = HomeFragmentDirections.actionHomeFragmentToAboutUs()
            findNavController().navigate(action)

        }

        homeBinding.homelogo.setOnClickListener {
            val url = "https://basinboatlighting.com/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)

        }


        homeBinding.sbt.setOnClickListener {

            val action = HomeFragmentDirections.actionHomeFragmentToConnectblueThooth()
            findNavController().navigate(action)


        }

        homeBinding.sos.setOnClickListener {
            showImagePickerDialog()

        }
        homeBinding.sosred.setOnClickListener {

            isSos = false
            closeHorn()


        }





        return homeBinding.root


    }


    fun openHorn() {

        if (isSos) {
            homeBinding.sos.visibility = View.INVISIBLE
            homeBinding.sosred.visibility = View.VISIBLE
            isHornOn = true
            isLedflasingOn = true


            lifecycleScope.launch(Dispatchers.Main) {
                writeCharacteristic("AA55F401BB", gatt!!)
                delay(100)
                writeCharacteristic("AA55F201BB", gatt!!)
                delay(1000)
                close()


            }
        }
    }


    fun open() {
        if (isSos) {
            lifecycleScope.launch {

                writeCharacteristic("AA55F401BB", gatt!!)
                delay(100)
                writeCharacteristic("AA55F201BB", gatt!!)
                delay(1000)
                close()

            }
        } else {
            isSos = false
            closeHorn()

        }

    }

    fun close() {
        lifecycleScope.launch(Dispatchers.Main) {

            writeCharacteristic("AA55F400BB", gatt!!)
            delay(100)
            writeCharacteristic("AA55F200BB", gatt!!)
            delay(1000)
            open()
        }
    }


    fun closeHorn() {
        // showToast("closerHorn start", requireActivity())
        if (!isSos) {
            homeBinding.sos.visibility = View.VISIBLE
            homeBinding.sosred.visibility = View.INVISIBLE

            isHornOn = false
            isLedflasingOn = false

            lifecycleScope.launch(Dispatchers.Main) {
                delay(100)
                writeCharacteristic("AA55F400BB", gatt!!)
                delay(100)
                writeCharacteristic("AA55F200BB", gatt!!)
                //   showToast("closerHorn Called", requireActivity())
            }

        }

    }


    fun init() {

        homeCardDataList = ArrayList()
        homeCardDataList.add(HomeCardList("brightBird", "Main LED", R.drawable.lightbird))
        homeCardDataList.add(HomeCardList("flashyBird", "Flasher", R.drawable.flashbird))
        homeCardDataList.add(HomeCardList("navBird", "Nav Light", R.drawable.navbird))
        homeCardDataList.add(HomeCardList("loudBird", "Horn", R.drawable.baja))

        getProfileData()

    }

    private fun getProfileData() {

        lifecycleScope.launch {
            if (Constants.isNetworkConnected(requireActivity(), true)) {
                try {

                    RetrofitSetup().callApi(requireActivity(),
                        true, false,
                        PreferenceUtils.getString("token"),
                        object : CallHandler<Response<ProfileMain>> {
                            override suspend fun sendRequest(apiInterFace: ApiService): Response<ProfileMain> {

                                return apiInterFace.profile()

                            }

                            override fun success(response: Response<ProfileMain>) {

                                if (response.body()?.status == 1) {


                                    if (response.body()?.data?.is_allowed_light_bird == 1) {

                                        PreferenceUtils.saveBoolean("mainLight", true)

                                    } else if (response.body()?.data?.is_allowed_light_bird == 0) {

                                        PreferenceUtils.saveBoolean("mainLight", false)

                                    }
                                    if (response.body()?.data?.is_allowed_flashy_bird == 1) {

                                        PreferenceUtils.saveBoolean("flash", true)

                                    } else if (response.body()?.data?.is_allowed_flashy_bird == 0) {

                                        PreferenceUtils.saveBoolean("flash", false)

                                    }
                                    if (response.body()?.data?.is_allowed_nav_bird == 1) {

                                        PreferenceUtils.saveBoolean("navBird", true)


                                    } else if (response.body()?.data?.is_allowed_nav_bird == 0) {

                                        PreferenceUtils.saveBoolean("navBird", false)

                                    }
                                    if (response.body()?.data?.is_allowed_loud_bird == 1) {

                                        PreferenceUtils.saveBoolean("loudswitch", true)

                                    } else if (response.body()?.data?.is_allowed_loud_bird == 0) {

                                        PreferenceUtils.saveBoolean("loudswitch", false)

                                    }
                                    if (response.body()?.data?.is_allowed_location == 1) {

                                        PreferenceUtils.saveBoolean("loc", true)

                                    } else if (response.body()?.data?.is_allowed_location == 0) {

                                        PreferenceUtils.saveBoolean("loc", false)

                                    }
                                    if (response.body()?.data?.is_allowed_push_notification == 1) {

                                        PreferenceUtils.saveBoolean("pnoti", true)

                                    } else if (response.body()?.data?.is_allowed_push_notification == 0) {

                                        PreferenceUtils.saveBoolean("pnoti", false)

                                    }

                                    setUpHomeAdapter()

                                }


                            }

                            override fun error(message: String) {
                                RetrofitSetup().hideLoader()
                                Toast.makeText(requireActivity(), "Error", Toast.LENGTH_LONG).show()
                            }


                        })

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setUpHomeAdapter() {


        val dummyList = ArrayList<HomeCardList>(homeCardDataList)
        for (item in dummyList) {
            if (item.equipmentName == ("brightBird") && !PreferenceUtils.getBoolean("mainLight")) {
                homeCardDataList.remove(item)
            }
            if (item.equipmentName == ("flashyBird") && !PreferenceUtils.getBoolean("flash")) {
                homeCardDataList.remove(item)
            }
            if (item.equipmentName == ("navBird") && !PreferenceUtils.getBoolean("navBird")) {
                homeCardDataList.remove(item)
            }
            if (item.equipmentName == ("loudBird") && !PreferenceUtils.getBoolean("loudswitch")) {
                homeCardDataList.remove(item)
            }
        }

        val homeDailyExerciesAdapter = HomeAdapter(getActivityContext!!, homeCardDataList, this)
        homeBinding.homecardrecyle.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL, false
            )
            homeBinding.homecardrecyle.adapter = homeDailyExerciesAdapter
            // homeBinding.homecardrecyle.adapter!!.notifyDataSetChanged()
        }
    }

    fun String.decodeHex(): ByteArray {
        check(length % 2 == 0) { "Must have an even length" }

        return chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }

    @SuppressLint("MissingPermission")
    fun writeCharacteristic(data: String, gatt: BluetoothGatt) {


        val value: ByteArray = data.decodeHex()
        val mCustomService: BluetoothGattService =
            gatt.getService(UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb"))

        if (mCustomService == null) {
            Log.w("mylog", "Custom BLE Service not found")
            return
        }

        Log.w("mylog", "service uuid: $mCustomService")

        /*get the read characteristic from the service*/
        val characteristic =
            mCustomService.getCharacteristic(UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb"))

        characteristic.value = value
        Log.w("mylog", "service uuid: $characteristic")
        gatt.writeCharacteristic(characteristic)
    }

    override fun onItemClick(name: String, status: String) {

        if (name == ("brightBird") && PreferenceUtils.getBoolean("mainLight")) {
            try {
                if (status == "On") {

//                    if (!isLightOn) {
//                        isLightOn = true
//                       PreferenceUtils.saveBoolean("lb",true)
//                        writeCharacteristic("AA55F101BB", gatt!!)
//
//                    }
                    PreferenceUtils.saveBoolean("lb", true)
                    writeCharacteristic("AA55F101BB", gatt!!)

                } else {

//                    if (isLightOn) {
//                        isLightOn = false
//                        PreferenceUtils.saveBoolean("lb",false)
//                        writeCharacteristic("AA55F100BB", gatt!!)
//
//                    }

                    PreferenceUtils.saveBoolean("lb", false)
                    writeCharacteristic("AA55F100BB", gatt!!)
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (name == ("flashyBird") && PreferenceUtils.getBoolean("flash")) {
            try {
                if (status == "On") {

//                    if (!isLedflasingOn) {
//                        isLedflasingOn = true
//                        PreferenceUtils.saveBoolean("fb",true)
//                        writeCharacteristic("AA55F201BB", gatt!!)
//                    }
                    PreferenceUtils.saveBoolean("fb", true)
                    writeCharacteristic("AA55F201BB", gatt!!)

                } else {
//                    if (isLedflasingOn) {
//
//                        isLedflasingOn = false
//                        PreferenceUtils.saveBoolean("fb",false)
//                        writeCharacteristic("AA55F200BB", gatt!!)
//                    }
                    PreferenceUtils.saveBoolean("fb", false)
                    writeCharacteristic("AA55F200BB", gatt!!)
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (name == ("navBird") && PreferenceUtils.getBoolean("navBird")) {
            try {

                if (status == "On") {

//                    if (!isLed9wOn) {
//                        isLed9wOn = true
//                        PreferenceUtils.saveBoolean("nb",true)
//                        writeCharacteristic("AA55F301BB", gatt!!)
//                    }
                    PreferenceUtils.saveBoolean("nb", true)
                    writeCharacteristic("AA55F301BB", gatt!!)


                } else {
//                    if (isLed9wOn) {
//                        isLed9wOn = false
//                        PreferenceUtils.saveBoolean("nb",false)
//                        writeCharacteristic("AA55F300BB", gatt!!)
//                    }

                    PreferenceUtils.saveBoolean("nb", false)
                    writeCharacteristic("AA55F300BB", gatt!!)
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (name == ("loudBird") && PreferenceUtils.getBoolean("loudswitch")) {
            try {

                if (status == "On") {

                    if (!isHornOn) {
                        isHornOn = true
                        writeCharacteristic("AA55F401BB", gatt!!)
                    }
                } else {
                    if (isHornOn) {
                        isHornOn = false
                        writeCharacteristic("AA55F400BB", gatt!!)
                    }

                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun sosNotification() {

        lifecycleScope.launch(Dispatchers.IO) {
            if (Constants.isNetworkConnected(getActivityContext!!, true)) {
                try {

                    RetrofitSetup().callApi(getActivityContext!!,
                        true,
                        false,
                        PreferenceUtils.getString("token"),
                        object : CallHandler<Response<CommonResponse>> {
                            override suspend fun sendRequest(apiInterFace: ApiService): Response<CommonResponse> {
                                return apiInterFace.sosNotification()
                            }

                            override fun success(response: Response<CommonResponse>) {
                                if (response.body()?.status == 1) {
                                    Toast.makeText(
                                        getActivityContext!!, response.body()!!.message,
                                        Toast.LENGTH_LONG
                                    ).show()


                                } else {
                                    Toast.makeText(
                                        getActivityContext!!, response.body()!!.message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
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

    private fun showImagePickerDialog() {

        var dialog = Dialog(context as Activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.sosdialog)

        resizeDialogView(dialog, 70, requireActivity())
        dialog.show()


        var yes: Button? = dialog.findViewById<Button>(R.id.btnExit)
        var no: Button? = dialog.findViewById<Button>(R.id.btnClose)

        yes?.setOnClickListener {

            if (checkPermissionslocation(getActivityContext!!)) {

                if (PreferenceUtils.getBoolean("loc")) {
                    if (!manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        getActivityContext?.let { it1 -> checkGPSEnable(it1) }
                    } else {
                        isSos = true
                        openHorn()
                        currentLocation()


                    }

                } else {
                    allowPermisson("location")

                }


            } else {
                requestPermissions()

            }


            dialog.dismiss()

        }

        no?.setOnClickListener {

            dialog.dismiss()
        }
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
                latitude = location?.latitude.toString()
                longitude = location?.longitude.toString()

                //  showToast(latitude.toString() +" "+ longitude.toString(),getActivityContext)
                updateLocation()

            }

    }

    private fun allowPermisson(type: String) {

        lifecycleScope.launch {
            if (Constants.isNetworkConnected(requireActivity(), true)) {
                try {

                    RetrofitSetup().callApi(requireActivity(),
                        true,
                        false,
                        "${PreferenceUtils.getString("token")}",
                        object : CallHandler<Response<CommonResponse>> {
                            override suspend fun sendRequest(apiInterFace: ApiService): Response<CommonResponse> {

                                return apiInterFace.allowPermission(
                                    JSONObject().apply {
                                        put("type", type)
                                    }.toString().getJsonRequestBody()
                                )
                            }

                            override fun success(response: Response<CommonResponse>) {

                                if (response.body()?.status == 1) {
                                    PreferenceUtils.saveBoolean("loc", true)
                                    showToast(
                                        response.body()?.message.toString(),
                                        requireActivity()
                                    )


                                } else {
                                    showToast(response.body()?.message!!, requireActivity())

                                }

                            }

                            override fun error(message: String) {
                                //RetrofitSetup().hideLoader()
                                //Toast.makeText(requireActivity(), "Error", Toast.LENGTH_LONG).show()
                                showToast(message, requireActivity())
                            }


                        }
                    )

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                showToast(R.string.internetconnection.toString(), getActivityContext)

            }
        }
    }


    private fun updateLocation() {

        lifecycleScope.launch {
            if (Constants.isNetworkConnected(requireActivity(), true)) {
                try {

                    RetrofitSetup().callApi(requireActivity(),
                        true,
                        false,
                        "${PreferenceUtils.getString("token")}",
                        object : CallHandler<Response<CommonResponse>> {
                            override suspend fun sendRequest(apiInterFace: ApiService): Response<CommonResponse> {

                                return apiInterFace.updateLocation(
                                    JSONObject().apply {
                                        put("lat", latitude)
                                        put("lang", longitude)


                                    }.toString().getJsonRequestBody()
                                )
                            }

                            override fun success(response: Response<CommonResponse>) {

                                if (response.body()?.status == 1) {
                                    sosNotification()
                                    showToast(
                                        response.body()?.message.toString(),
                                        requireActivity()
                                    )


                                } else {
                                    showToast(response.body()?.message!!, requireActivity())

                                }

                            }

                            override fun error(message: String) {
                                //RetrofitSetup().hideLoader()
                                //Toast.makeText(requireActivity(), "Error", Toast.LENGTH_LONG).show()
                                showToast(message, requireActivity())
                            }


                        }
                    )

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                showToast(R.string.internetconnection.toString(), getActivityContext)

            }
        }
    }


}


//    fun sosClick() {
//
//        while (isSos) {
//            homeBinding.sos.visibility = View.INVISIBLE
//            homeBinding.sosred.visibility = View.VISIBLE
//            //opensos
//            lifecycleScope.launch(Dispatchers.Main) {
//
//                writeCharacteristic("AA55F401BB", gatt!!)
//                delay(100)
//                writeCharacteristic("AA55F201BB", gatt!!)
//
//                delay(1000)
//                //close sos
//
//                writeCharacteristic("AA55F400BB", gatt!!)
//                delay(100)
//                writeCharacteristic("AA55F200BB", gatt!!)
//                delay(1000)
//            }
//        }
//
//        if(!isSos){
//            homeBinding.sos.visibility = View.VISIBLE
//            homeBinding.sosred.visibility = View.INVISIBLE
//
//            lifecycleScope.launch {
//                writeCharacteristic("AA55F400BB", gatt!!)
//                delay(100)
//                writeCharacteristic("AA55F200BB", gatt!!)
//
//            }
//        }
//
//
//
//    }


//        homeBinding.sos.setOnClickListener {
//            if (PreferenceUtils.getBoolean("loc")) {
//                homeBinding.sos.isEnabled = false
//
//               if(!isHornOn){
//                   lifecycleScope.launch {
//                       isHornOn = true
//                       writeCharacteristic("AA55F401BB", gatt!!)
//                       delay(1500)
//                       isHornOn = false
//                       writeCharacteristic("AA55F400BB", gatt!!)
//                       delay(1000)
//                       isHornOn = true
//                       writeCharacteristic("AA55F401BB", gatt!!)
//                       delay(1500)
//                       isHornOn = true
//                       writeCharacteristic("AA55F400BB", gatt!!)
//                       delay(1000)
//                       isHornOn = true
//                       writeCharacteristic("AA55F401BB", gatt!!)
//                       delay(1500)
//                       isHornOn = false
//                       writeCharacteristic("AA55F400BB", gatt!!)
//                       delay(1000)
//                       sosNotification()
//                   }
//               }
//            }
//            else {
//                showToast("Your location permission is off", getActivityContext!!)
//
//            }
//
//
//        }
