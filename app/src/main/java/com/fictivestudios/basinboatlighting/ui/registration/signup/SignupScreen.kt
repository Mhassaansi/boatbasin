package com.fictivestudios.basinboatlighting.ui.registration.signup

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommercemvvmpractice2.utilities.extensions.showToast
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.base.BaseFragment
import com.fictivestudios.basinboatlighting.databinding.SignupBinding
import com.fictivestudios.basinboatlighting.models.signup.SignupResponse
import com.fictivestudios.basinboatlighting.utils.*
import com.fictivestudios.getmefit.Networking.ApiService
import com.fictivestudios.getmefit.Networking.getJsonRequestBody
import com.fictivestudios.getmefit.uitilites.Constants
import com.fictivestudios.tafcha.networkSetup.callhandler.CallHandler
import com.fictivestudios.tafcha.networkSetup.retrofitsetup.RetrofitSetup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response


class SignupScreen : BaseFragment(){

    private lateinit var signupBinding: SignupBinding
    private val signupViewModel by viewModels<SignupViewModel>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun setTitlebar(titlebar: Titlebar) {

    }



    var latitude: String? = null
    var longitude: String? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        signupBinding = DataBindingUtil.inflate(inflater, R.layout.signup, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivitContextRegistration!!)

        if(checkPermissions()){

            currentLocation()

        }
        else{

            requestPermissions()

        }

        signupBinding.backButton.setOnClickListener {

            getActivitContextRegistration!!.onBackPressed()

        }

        signupBinding.signupscreen.setOnClickListener {
            hideKeyboard()

        }

        signupBinding.atc.movementMethod = LinkMovementMethod.getInstance()
        val spans: Spannable = signupBinding.atc.text as Spannable
        val clickSpan: ClickableSpan = object : ClickableSpan() {


            override fun onClick(widget: View) {

                val action = SignupScreenDirections.actionSignupScreenToTermsandConditionTest()
                findNavController().navigate(action)


            }
        }
        val TEXT_BEFORE_LINK = 21
        val TEXT_LINK = 18
        spans.setSpan(
            clickSpan,
            TEXT_BEFORE_LINK,
            TEXT_BEFORE_LINK + TEXT_LINK,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        //  spans.setSpan(clickSpan, 129,133, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


        val spans1: Spannable = signupBinding.atc.text as Spannable
        val clickSpan1: ClickableSpan = object : ClickableSpan() {


            override fun onClick(widget: View) {

                val action = SignupScreenDirections.actionSignupScreenToPrivacyPolicyTest()
                findNavController().navigate(action)

            }
        }
        val TEXT_BEFORE_LINK1 = 44
        val TEXT_LINK1 = 14
        spans1.setSpan(
            clickSpan1,
            TEXT_BEFORE_LINK1,
            TEXT_BEFORE_LINK1 + TEXT_LINK1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )


        signupBinding.registartionButton.setOnClickListener {
            if (validation()) {
                Registartion()
            }
        }

        signupBinding.signuppassvisibilty.setOnClickListener {

            showPassword1()

        }

        signupBinding.signuprepassvisibilty.setOnClickListener {
            showPassword2()

        }

        return signupBinding.root

    }


    fun validation(): Boolean {

        if (signupBinding.fname.text?.isBlank()!!) {
            showToast("First Name field cannot be empty.", getActivitContextRegistration)
            return false
        } else if (signupBinding.lname.text?.isBlank()!!) {
            showToast("Last Name field cannot be empty.", getActivitContextRegistration)
            return false
        } else if (signupBinding.email.text?.isBlank()!!) {

            showToast("Email field cannot be empty.", getActivitContextRegistration)

            return false
        } else if (signupBinding.email.text.toString().isNotValidEmail()) {
            showToast("Invalid email format", getActivitContextRegistration)
            return false

        } else if (signupBinding.password.text.toString().isBlank()) {
            showToast("Password cannot be empty", getActivitContextRegistration)
            return false
        }
//        else if () {
//            showToast(
//                "Password should have one special characters one uppercase letter and number",
//                getActivitContextRegistration
//            )
//            return false
//        }
        else if (signupBinding.password.text?.length!! <= 7 && signupBinding.password.text.toString().isNotValidPassword() ) {
            showToast(
                "Password should be of 8 characters long should contain uppercase, lowercase, number and special character.",
                getActivitContextRegistration
            )
            return false
        } else if (signupBinding.cpassword.text.toString().isBlank()) {
            showToast("Confirm password cannot be empty", getActivitContextRegistration)
            return false
        } else if (signupBinding.password.text?.toString() != signupBinding.cpassword.text.toString()) {
            showToast(
                "Password not match with confirm password field",
                getActivitContextRegistration
            )
            return false
        } else if (signupBinding.acceptTandC.isChecked == false) {
            showToast("Please accept terms and privacy policy", getActivitContextRegistration)
            return false
        }
        return true
    }


    private fun Registartion() = getFirebaseToken { token ->

        lifecycleScope.launch {
            if (Constants.isNetworkConnected(getActivitContextRegistration!!, true)) {
                try {

                    RetrofitSetup().callApi(getActivitContextRegistration!!, true, false, "",
                        object : CallHandler<Response<SignupResponse>> {
                            override suspend fun sendRequest(apiInterFace: ApiService): Response<SignupResponse> {

                                return apiInterFace.registration(
                                    JSONObject().apply {
                                        put("first_name", signupBinding.fname.text.toString())
                                        put("last_name", signupBinding.lname.text.toString())
                                        put("email", signupBinding.email.text.toString())
                                        put("password", signupBinding.password.text.toString())
                                        put(
                                            "confrim_password",
                                            signupBinding.password.text.toString()
                                        )
                                        put("device_type", "android")
                                        put("device_token", token)

                                        if (longitude != null && latitude != null) {
                                            put("lat", longitude)
                                            put("lang", latitude)
                                            put("is_allowed_location", 1)
                                        } else {

                                            put("lat", 00.00)
                                            put("lang", 00.00)
                                            put("is_allowed_location", 0)
                                        }


                                    }.toString().getJsonRequestBody()
                                )
                            }

                            override fun success(response: Response<SignupResponse>) {

                                if (response.body()?.status == 1) {
                                    val action =
                                        SignupScreenDirections.actionSignupScreenToOtpVerification(
                                            "",
                                            "ACCOUNT_VERIFICATION",
                                            "${signupBinding.email.text.toString()}"
                                        )
                                    findNavController().navigate(action)
                                    //   "${response.body()?.data?.id}"

                                } else {

                                    showToast(
                                        response.body()!!.message,
                                        getActivitContextRegistration
                                    )

                                }


                            }

                            override fun error(message: String) {
                                showToast(message, getActivitContextRegistration)
                            }


                        }
                    )

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(
                    getActivitContextRegistration,
                    R.string.internetconnection,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    fun showPassword1() {


        if (signupBinding.password.transformationMethod.equals(PasswordTransformationMethod.getInstance())) {
            signupBinding.signuppassvisibilty.setImageResource(R.drawable.visibility_off)
            signupBinding.password.transformationMethod =
                HideReturnsTransformationMethod.getInstance()

        } else {
            signupBinding.signuppassvisibilty.setImageResource(R.drawable.visibility)

            //Hide Password
            signupBinding.password.transformationMethod = PasswordTransformationMethod.getInstance()

        }
    }

    fun showPassword2() {


        if (signupBinding.cpassword.transformationMethod.equals(PasswordTransformationMethod.getInstance())) {
            signupBinding.signuprepassvisibilty.setImageResource(R.drawable.visibility_off)
            signupBinding.cpassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()

        } else {
            signupBinding.signuprepassvisibilty.setImageResource(R.drawable.visibility)

            //Hide Password
            signupBinding.cpassword.transformationMethod =
                PasswordTransformationMethod.getInstance()

        }
    }


    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            getActivitContextRegistration!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            getActivitContextRegistration!!,
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

    private fun  currentLocation(){

        if (ActivityCompat.checkSelfPermission(
                getActivitContextRegistration!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getActivitContextRegistration!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                latitude =  location?.latitude.toString()
                longitude = location?.longitude.toString()

            }

    }

}





