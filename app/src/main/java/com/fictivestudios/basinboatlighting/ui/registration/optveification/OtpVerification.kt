package com.fictivestudios.basinboatlighting.ui.registration.optveification

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ecommercemvvmpractice2.utilities.extensions.showToast
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.activities.HomeActivity
import com.fictivestudios.basinboatlighting.base.BaseFragment
import com.fictivestudios.basinboatlighting.databinding.OtpverificationBinding
import com.fictivestudios.basinboatlighting.models.login.LoginResponse
import com.fictivestudios.basinboatlighting.models.signup.SignupResponse
import com.fictivestudios.basinboatlighting.ui.registration.login.LoginScreenDirections
import com.fictivestudios.basinboatlighting.utils.Titlebar
import com.fictivestudios.basinboatlighting.utils.getFirebaseToken
import com.fictivestudios.basinboatlighting.utils.hideKeyboard
import com.fictivestudios.basinboatlighting.utils.startTimer
import com.fictivestudios.getmefit.Networking.ApiService
import com.fictivestudios.getmefit.Networking.getJsonRequestBody
import com.fictivestudios.getmefit.data.response.models.CommonResponse
import com.fictivestudios.getmefit.uitilites.Constants
import com.fictivestudios.tafcha.Utils.PreferenceData
import com.fictivestudios.tafcha.Utils.PreferenceUtils
import com.fictivestudios.tafcha.networkSetup.callhandler.CallHandler
import com.fictivestudios.tafcha.networkSetup.retrofitsetup.RetrofitSetup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response



class OtpVerification : BaseFragment() {
    private lateinit var otpverificationBinding: OtpverificationBinding
    private val otpViewModel by viewModels<OtpVerificationViewModel>()
    private val email:OtpVerificationArgs by navArgs<OtpVerificationArgs>()
    private val averification:OtpVerificationArgs by navArgs<OtpVerificationArgs>()
    private val foget:OtpVerificationArgs by navArgs<OtpVerificationArgs>()

    override fun setTitlebar(titlebar: Titlebar) {

    }


    fun validation():Boolean{

       return  if(otpverificationBinding.enterOtp.text?.isBlank()!!){
            showToast("OTP field cannot be empty",requireActivity())
            return false
        }
        else{

            return true
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


       otpverificationBinding = DataBindingUtil.inflate(inflater, R.layout.otpverification,container,false)
        otpverificationBinding.lifecycleOwner = this
        otpverificationBinding.otpVerificationViewModel = otpViewModel


        otpverificationBinding.otpscreen.setOnClickListener {
            hideKeyboard()

        }

        otpverificationBinding.loginButton.setOnClickListener {

            if (validation()){
                verifyOTP()
            }
        }






        lifecycleScope.launch(Dispatchers.Main) {
            otpverificationBinding.dontrecevietext.movementMethod = LinkMovementMethod()
            val span : Spannable = otpverificationBinding.dontrecevietext.text as Spannable
            val clickSpan : ClickableSpan = object : ClickableSpan(){
                override fun onClick(widget: View) {
                    if(foget.forget!=null){
                       resendOTPEmail()
                    }
                    else{
                        resendOTP()

                    }

                }

            }

            val TEXT_BEFORE_LINK = 20
            val TEXT_LINK = 6


            span.setSpan(
                clickSpan,
                TEXT_BEFORE_LINK,
                TEXT_BEFORE_LINK + TEXT_LINK,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        startTimer(otpverificationBinding.otpTimmer,60)


        return otpverificationBinding.root

    }



    private fun verifyOTP()  {
        if(Constants.isNetworkConnected(requireActivity(), true)) {
            lifecycleScope.launch {
                try {
                    RetrofitSetup().callApi(
                        requireActivity(), true,false, "",
                        object : CallHandler<Response<LoginResponse>> {
                            override suspend fun sendRequest(apiInterFace: ApiService): Response<LoginResponse> {
                                return apiInterFace.verifyOtp(
                                    JSONObject().apply {
                                        put("reference_code", otpverificationBinding.enterOtp.text)
                                        if(averification.averification == "ACCOUNT_VERIFICATION"){
                                            put("type","ACCOUNT_VERIFICATION")
                                        }
                                        else{
                                            put("type","PASSWORD_RESET")
                                        }
                                        put("email", email.email)




                                    }.toString().getJsonRequestBody()
                                )
                            }

                            override fun success(response: Response<LoginResponse>) {


                                if(response.body()!!.status==1){


                                    if(foget.forget==""){
                                        PreferenceUtils.saveString("token",response.body()?.bearer_token!!)
                                        `PreferenceData`.storeProfileData(requireActivity(),response.body()?.data!!)
                                        val intent = Intent(requireActivity(),HomeActivity::class.java)
                                        startActivity(intent)
                                    }
                                    else{
                                       // PreferenceUtils.saveString("token",response.body()?.bearer_token!!)
                                        val action = OtpVerificationDirections.actionOtpVerificationToResetPassword(email.email)
                                        findNavController().navigate(action)
                                    }
                                }
                                else{

                                    showToast(response.body()?.message.toString(),requireActivity())

                                }


                            }

                            override fun error(message: String) {
                                showToast(message,requireActivity())
                                //RetrofitSetup().hideLoader()
                                //.makeText(requireActivity(), "Error", Toast.LENGTH_LONG).show()
                            }
                        })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun resendOTP() {
        lifecycleScope.launch {
            if (Constants.isNetworkConnected(requireActivity(), true)) {
                try {
                    RetrofitSetup().callApi(
                        requireActivity(), true, false,"",
                        object : CallHandler<Response<CommonResponse>> {
                            override suspend fun sendRequest(apiInterFace: ApiService): Response<CommonResponse> {
                                return apiInterFace.resend_verifyOtp(
                                    JSONObject().apply {
                                        put("email", email.email)
                                    }.toString().getJsonRequestBody()
                                )
                            }

                            override fun success(response: Response<CommonResponse>) {
                                if(response.body()?.status==1){
                                    showToast(response.body()?.message.toString(),requireActivity())
                                }
                                else{

                                    showToast(response.body()?.message.toString(),requireActivity())

                                }

                            }

                            override fun error(message: String) {
                                showToast(message,requireActivity())
                                //RetrofitSetup().hideLoader()
                                // Toast.makeText(requireActivity(), "Error: "+message, Toast.LENGTH_LONG).show()
                            }
                        })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
    }

    private fun resendOTPEmail() {
        lifecycleScope.launch {
            if (Constants.isNetworkConnected(requireActivity(), true)) {
                try {
                    RetrofitSetup().callApi(
                        requireActivity(), true, false,"",
                        object : CallHandler<Response<SignupResponse>> {
                            override suspend fun sendRequest(apiInterFace: ApiService): Response<SignupResponse> {
                                return apiInterFace.forgetPassword(
                                    JSONObject().apply {
                                        put("email", email.email)
                                    }.toString().getJsonRequestBody()
                                )
                            }

                            override fun success(response: Response<SignupResponse>) {
                                if(response.body()?.status==1){
                                    showToast(response.body()?.message.toString(),requireActivity())
                                }
                                else{

                                    showToast(response.body()?.message.toString(),requireActivity())

                                }

                            }

                            override fun error(message: String) {
                                showToast(message,requireActivity())
                                //RetrofitSetup().hideLoader()
                                // Toast.makeText(requireActivity(), "Error: "+message, Toast.LENGTH_LONG).show()
                            }
                        })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
    }


}