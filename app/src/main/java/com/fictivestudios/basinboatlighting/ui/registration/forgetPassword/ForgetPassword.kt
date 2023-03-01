package com.fictivestudios.basinboatlighting.ui.registration.forgetPassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommercemvvmpractice2.utilities.extensions.showToast
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.base.BaseFragment
import com.fictivestudios.basinboatlighting.databinding.ForgetpasswordBinding
import com.fictivestudios.basinboatlighting.models.signup.SignupResponse
import com.fictivestudios.basinboatlighting.utils.*
import com.fictivestudios.getmefit.Networking.ApiService
import com.fictivestudios.getmefit.Networking.getJsonRequestBody
import com.fictivestudios.getmefit.uitilites.Constants
import com.fictivestudios.tafcha.networkSetup.callhandler.CallHandler
import com.fictivestudios.tafcha.networkSetup.retrofitsetup.RetrofitSetup
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class ForgetPassword  : BaseFragment(){

    private lateinit var forgetpasswordBinding: ForgetpasswordBinding
    private val forgetViewModel by viewModels<ForgetPasswordViewModel>()


    override fun setTitlebar(titlebar: Titlebar) {

    }

    fun validation():Boolean{

        return if (forgetpasswordBinding.email.text?.isBlank()!!) {

            showToast("Email field cannot be empty.",requireActivity())

            return false
        }

        else if (forgetpasswordBinding.email.text?.toString()?.isNotValidEmail()!!) {
            showToast("Invalid email format.",requireActivity())
            return false

        }
        else{

            true

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        forgetpasswordBinding = DataBindingUtil.inflate(inflater, R.layout.forgetpassword,container,false)
        //forgetpasswordBinding.forgetloginViewModel = forgetViewModel
        forgetpasswordBinding.lifecycleOwner = this


        forgetpasswordBinding.forgetScreen.setOnClickListener {
            hideKeyboard()
        }

        forgetpasswordBinding.loginButton.setOnClickListener {

            if(validation()){

                forgetPasswordApi()

            }
        }


        return forgetpasswordBinding.root

    }



    private fun forgetPasswordApi() {
        if(Constants.isNetworkConnected(requireActivity(), true)) {
            lifecycleScope.launch {
                try {

                    RetrofitSetup().callApi(requireActivity(), true, false,"",
                        object : CallHandler<Response<SignupResponse>> {
                            override suspend fun sendRequest(apiInterFace: ApiService): Response<SignupResponse> {
                                return apiInterFace.forgetPassword(
                                    JSONObject().apply {
                                        put("email",forgetpasswordBinding.email.text)
                                    }.toString().getJsonRequestBody()
                                )
                            }

                            override fun success(response: Response<SignupResponse>) {
                                if (response.body()!!.status == 1) {
                                   // forgetpasswordBinding.email.text?.clear()
                                    showToast(response.body()!!.message,requireActivity())
                                    val action = ForgetPasswordDirections.actionForgetPasswordToOtpVerification("forget","PASSWORD_RESET",
                                        forgetpasswordBinding.email.text.toString())
                                    findNavController().navigate(action)


                                } else {
                                    showToast( response.body()!!.message,requireActivity())
                                }


                            }

                            override fun error(message: String) {
                                showToast(message,requireActivity())
                            }

                        })


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

}