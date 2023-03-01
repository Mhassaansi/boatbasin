package com.fictivestudios.basinboatlighting.ui.home.settings

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommercemvvmpractice2.utilities.extensions.showToast
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.activities.RegistrationActivity
import com.fictivestudios.basinboatlighting.base.BaseFragment
import com.fictivestudios.basinboatlighting.databinding.SettingsBinding
import com.fictivestudios.basinboatlighting.utils.Titlebar
import com.fictivestudios.basinboatlighting.utils.checkGPSEnable
import com.fictivestudios.basinboatlighting.utils.checkPermissionslocation
import com.fictivestudios.basinboatlighting.utils.resizeDialogView
import com.fictivestudios.getmefit.Networking.ApiService
import com.fictivestudios.getmefit.Networking.getJsonRequestBody
import com.fictivestudios.getmefit.data.response.models.CommonResponse
import com.fictivestudios.getmefit.uitilites.Constants
import com.fictivestudios.tafcha.Utils.PreferenceData
import com.fictivestudios.tafcha.Utils.PreferenceUtils
import com.fictivestudios.tafcha.networkSetup.callhandler.CallHandler
import com.fictivestudios.tafcha.networkSetup.retrofitsetup.RetrofitSetup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class Settings : BaseFragment() {

    private lateinit var settingsBinding: SettingsBinding
    private val settingViewModel by viewModels<SettingsViewModel>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var manager: LocationManager? = null
    var latitude: String? = null
    var longitude: String? = null
    override fun setTitlebar(titlebar: Titlebar) {

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        settingsBinding = DataBindingUtil.inflate(inflater, R.layout.settings, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivityContext!!)
        manager = getActivityContext?.getSystemService(Context.LOCATION_SERVICE) as LocationManager


        getActivityContext!!.showButtonHome()
        getActivityContext!!.showBttomBar()




        settingsBinding.ivHelp.setOnClickListener {

            val action = SettingsDirections.actionSettingsToAboutUs()
            findNavController().navigate(action)

        }



        settingsBinding.lightbirdmainswitch.setOnClickListener() {

//                _, isChecked ->


            if (settingsBinding.lightbirdmainswitch.isChecked == true) {
                PreferenceUtils.saveBoolean("mainLight", true)
                allowPermisson("lightBirdMainLed")

            } else {
                PreferenceUtils.saveBoolean("mainLight", false)
                allowPermisson("lightBirdMainLed")
            }
        }
        settingsBinding.flashbirdswitch.setOnClickListener() {
//                _, isChecked ->


            if (settingsBinding.flashbirdswitch.isChecked == true) {
                PreferenceUtils.saveBoolean("flash", true)
                allowPermisson("flashyBirdFlasher")

            } else {
                PreferenceUtils.saveBoolean("flash", false)
                allowPermisson("flashyBirdFlasher")

            }
        }
        settingsBinding.navbirdswitch.setOnClickListener() {
//                _, isChecked ->


            if (settingsBinding.flashbirdswitch.isChecked == true) {

                PreferenceUtils.saveBoolean("navBird", true)
                allowPermisson("navBirdNavLight")

            } else {

                PreferenceUtils.saveBoolean("navBird", false)
                allowPermisson("navBirdNavLight")

            }
        }
        settingsBinding.loudswitch.setOnClickListener() {
//                _, isChecked ->


            if (settingsBinding.loudswitch.isChecked == true) {

                PreferenceUtils.saveBoolean("loudswitch", true)
                allowPermisson("loudBirdHorn")
            } else {

                PreferenceUtils.saveBoolean("loudswitch", false)
                allowPermisson("loudBirdHorn")
            }
        }
        settingsBinding.pnoti.setOnClickListener() {


            if (settingsBinding.pnoti.isChecked == true) {

                PreferenceUtils.saveBoolean("pnoti", true)
                allowPermisson("receivePushNotification")
            } else {

                PreferenceUtils.saveBoolean("pnoti", false)
                allowPermisson("receivePushNotification")
            }
        }
        settingsBinding.loc.setOnClickListener() {
            if (!manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getActivityContext?.let { it1 -> checkGPSEnable(it1) }

            } else {
                if (settingsBinding.loc.isChecked == true) {

                    if (checkPermissionslocation(getActivityContext!!)) {
                        PreferenceUtils.saveBoolean("loc", true)
                        allowPermisson("location")
                        currentLocation()
                    } else {
                        requestPermissions()
                    }


                } else {


                    if (checkPermissionslocation(getActivityContext!!)) {
                        PreferenceUtils.saveBoolean("loc", true)
                        allowPermisson("location")

                    } else {
                        requestPermissions()
                    }
                }

            }


        }


        settingsBinding.cpassword.setOnClickListener {
            val action = SettingsDirections.actionSettingsToUpdatePassword()
            findNavController().navigate(action)
        }
        settingsBinding.ppolicy.setOnClickListener {
            val action = SettingsDirections.actionSettingsToPrivacyPolicy()
            findNavController().navigate(action)
        }
        settingsBinding.termscondition.setOnClickListener {
            val action = SettingsDirections.actionSettingsToTermsCondition()
            findNavController().navigate(action)

        }

        settingsBinding.inApp.setOnClickListener {
            val action = SettingsDirections.actionSettingsToTermsCondition()
            findNavController().navigate(R.id.inAppPurchaseFragment)

        }
        settingsBinding.signout.setOnClickListener {

            alertDialog("Are you sure want to Logout",false)


        }

        settingsBinding.inApp.setOnClickListener {

            alertDialog("Are you sure want to Delete Account",true)
        }



        init()

        return settingsBinding.root


    }

    private fun deleteAccount() {

        lifecycleScope.launch {
            if (Constants.isNetworkConnected(requireActivity(), true)) {
                try {

                    RetrofitSetup().callApi(requireActivity(),
                        true,
                        false,
                        "${PreferenceUtils.getString("token")}",
                        object : CallHandler<Response<CommonResponse>> {
                            override suspend fun sendRequest(apiInterFace: ApiService): Response<CommonResponse> {
                                return apiInterFace.deleteProfile()
                            }

                            override fun success(response: Response<CommonResponse>) {
                                if (response.body()?.status == 1) {
                                    PreferenceData.clearPreference(requireActivity())
                                    PreferenceUtils.saveBoolean("isLogin", false)
                                    PreferenceUtils.saveBoolean("btONOFF", false)
                                    PreferenceUtils.saveBoolean("lb", false)
                                    PreferenceUtils.saveBoolean("fb", false)
                                    PreferenceUtils.saveBoolean("nb", false)
                                    PreferenceUtils.saveString("token", null)
                                    showToast(
                                        response.body()?.message.toString(),
                                        requireActivity()
                                    )
                                    startActivity(Intent(context, RegistrationActivity::class.java))
                                    getActivityContext?.finish()
                                } else {
                                    showToast(
                                        response.body()?.message.toString(),
                                        requireActivity()
                                    )
                                }
                            }

                            override fun error(message: String) {
                                RetrofitSetup().hideLoader()
                                //  showToast("Error",requireActivity())
                            }

                        }
                    )


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    fun init() {

        if (PreferenceUtils.getBoolean("mainLight")) {

            settingsBinding.lightbirdmainswitch.isChecked = true

        }
        if (!PreferenceUtils.getBoolean("mainLight")) {

            settingsBinding.lightbirdmainswitch.isChecked = false
        }
        if (PreferenceUtils.getBoolean("flash")) {

            settingsBinding.flashbirdswitch.isChecked = true

        }
        if (!PreferenceUtils.getBoolean("flash")) {

            settingsBinding.flashbirdswitch.isChecked = false
        }
        if (PreferenceUtils.getBoolean("navBird")) {

            settingsBinding.navbirdswitch.isChecked = true

        }
        if (!PreferenceUtils.getBoolean("navBird")) {

            settingsBinding.navbirdswitch.isChecked = false
        }
        if (PreferenceUtils.getBoolean("loudswitch")) {

            settingsBinding.loudswitch.isChecked = true

        }
        if (!PreferenceUtils.getBoolean("loudswitch")) {

            settingsBinding.loudswitch.isChecked = false
        }
        if (PreferenceUtils.getBoolean("pnoti")) {
            settingsBinding.pnoti.isChecked = true
        }
        if (!PreferenceUtils.getBoolean("pnoti")) {

            settingsBinding.pnoti.isChecked = false
        }
        if (PreferenceUtils.getBoolean("loc")) {
            settingsBinding.loc.isChecked = true
        }
        if (!PreferenceUtils.getBoolean("loc")) {

            settingsBinding.loc.isChecked = false
        }


    }


    private fun alertDialog(textString:String,isDelete:Boolean) {

        var dialog = Dialog(context as Activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.subscribe_dialog)

        resizeDialogView(dialog, 70, requireActivity())
        dialog.show()


        var yes: Button? = dialog.findViewById<Button>(R.id.btnyes)
        var no: Button? = dialog.findViewById<Button>(R.id.btnno)
        var text: TextView? = dialog.findViewById<Button>(R.id.text)

        text?.text = textString

        yes?.setOnClickListener {

            dialog.dismiss()

            if (isDelete)
            {
                deleteAccount()
            }
            else{
                logout()
            }


        }

        no?.setOnClickListener {

            dialog.dismiss()


        }


    }

    private fun logout() {

        lifecycleScope.launch {
            if (Constants.isNetworkConnected(requireActivity(), true)) {
                try {

                    RetrofitSetup().callApi(requireActivity(),
                        true,
                        false,
                        "${PreferenceUtils.getString("token")}",
                        object : CallHandler<Response<CommonResponse>> {
                            override suspend fun sendRequest(apiInterFace: ApiService): Response<CommonResponse> {
                                return apiInterFace.logout()
                            }

                            override fun success(response: Response<CommonResponse>) {
                                if (response.body()?.status == 1) {
                                    PreferenceData.clearPreference(requireActivity())
                                    PreferenceUtils.saveBoolean("isLogin", false)
                                    PreferenceUtils.saveBoolean("btONOFF", false)
                                    PreferenceUtils.saveBoolean("lb", false)
                                    PreferenceUtils.saveBoolean("fb", false)
                                    PreferenceUtils.saveBoolean("nb", false)
                                    PreferenceUtils.saveString("token", null)
                                    showToast(
                                        response.body()?.message.toString(),
                                        requireActivity()
                                    )
                                    startActivity(Intent(context, RegistrationActivity::class.java))
                                    getActivityContext?.finish()
                                } else {
                                    showToast(
                                        response.body()?.message.toString(),
                                        requireActivity()
                                    )
                                }
                            }

                            override fun error(message: String) {
                                RetrofitSetup().hideLoader()
                                //  showToast("Error",requireActivity())
                            }

                        }
                    )


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

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
                updateLocation()

            }

    }


    private fun updateLocation() {

        lifecycleScope.launch {
            if (Constants.isNetworkConnected(requireActivity(), true)) {
                try {

                    RetrofitSetup().callApi(requireActivity(),
                        true,
                        false,
                        "${PreferenceUtils.getBoolean("token")}",
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

}