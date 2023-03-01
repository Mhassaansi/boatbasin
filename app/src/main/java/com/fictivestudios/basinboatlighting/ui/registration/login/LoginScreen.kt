package com.fictivestudios.basinboatlighting.ui.registration.login


import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommercemvvmpractice2.utilities.extensions.showToast
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.activities.HomeActivity
import com.fictivestudios.basinboatlighting.base.BaseFragment
import com.fictivestudios.basinboatlighting.databinding.LoginBinding
import com.fictivestudios.basinboatlighting.models.login.LoginResponse
import com.fictivestudios.basinboatlighting.utils.Titlebar
import com.fictivestudios.basinboatlighting.utils.getFirebaseToken
import com.fictivestudios.basinboatlighting.utils.hideKeyboard
import com.fictivestudios.basinboatlighting.utils.isNotValidEmail
import com.fictivestudios.getmefit.Networking.ApiService
import com.fictivestudios.getmefit.Networking.getJsonRequestBody
import com.fictivestudios.getmefit.uitilites.Constants
import com.fictivestudios.tafcha.Utils.PreferenceData
import com.fictivestudios.tafcha.Utils.PreferenceUtils
import com.fictivestudios.tafcha.networkSetup.callhandler.CallHandler
import com.fictivestudios.tafcha.networkSetup.retrofitsetup.RetrofitSetup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response


class LoginScreen : BaseFragment() {

 private lateinit var loginBinding:LoginBinding




    override fun setTitlebar(titlebar: Titlebar) {

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


     loginBinding = DataBindingUtil.inflate(inflater, R.layout.login,container,false)
   //  loginBinding.loginViewModel = loginViewModel
        loginBinding.lifecycleOwner = this


       loginBinding.loginScreen.setOnClickListener {

           hideKeyboard()
       }



//        loginBinding.loginButton.setOnClickListener {
//
//            val intent = Intent(getActivitContextRegistration,HomeActivity::class.java)
//            startActivity(intent)
//            getActivitContextRegistration?.finish()
//
//        }

        lifecycleScope.launch(Dispatchers.Main) {
            loginBinding.donthaveaccount.movementMethod = LinkMovementMethod()
            val span : Spannable = loginBinding.donthaveaccount.text as Spannable
            val clickSpan : ClickableSpan = object :ClickableSpan(){
                override fun onClick(widget: View) {
                    val action = LoginScreenDirections.actionLoginScreenToSignupScreen()
                    findNavController().navigate(action)
                }

            }

            val TEXT_BEFORE_LINK = 22
            val TEXT_LINK = 11


            span.setSpan(
                clickSpan,
                TEXT_BEFORE_LINK,
                TEXT_BEFORE_LINK + TEXT_LINK,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }



       lifecycleScope.launch(Dispatchers.Main) {
           loginBinding.forgetpassword.movementMethod = LinkMovementMethod()
           val span1 : Spannable = loginBinding.forgetpassword.text as Spannable
           val clickSpan1 : ClickableSpan = object :ClickableSpan(){
               override fun onClick(widget: View) {
                   val action = LoginScreenDirections.actionLoginScreenToForgetPassword()
                   findNavController().navigate(action)

               }

           }

           val TEXT_BEFORE_LINK1 = 0
           val TEXT_LINK1 = 15


           span1.setSpan(
               clickSpan1,
               TEXT_BEFORE_LINK1,
               TEXT_BEFORE_LINK1 + TEXT_LINK1,
               Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
           )

       }


        loginBinding.loginButton.setOnClickListener {

            if(validation()){
                loginApi()
            }

        }

        loginBinding.siginpasssow.setOnClickListener {
            showPassword()

        }


        return loginBinding.root


    }



    fun validation():Boolean{

        return if (loginBinding.email.text?.isBlank()!!) {

            showToast("Email field cannot be empty.",getActivitContextRegistration)

            return false
        }
        else if (loginBinding.email.text.toString().isNotValidEmail()) {
            showToast("Email address is not valid.",getActivitContextRegistration)
            return false

        }
        else if (loginBinding.password.text?.isBlank()!!) {
            showToast("Password should not be empty",getActivitContextRegistration)
            return false
        }
        else {

         return true
        }


    }


    private fun loginApi() = getFirebaseToken { token ->

        lifecycleScope.launch {
            if (Constants.isNetworkConnected(requireActivity(), true)) {
                try {

                    RetrofitSetup().callApi(requireActivity(), true, false,"",
                        object : CallHandler<Response<LoginResponse>> {
                            override suspend fun sendRequest(apiInterFace: ApiService): Response<LoginResponse> {

                                return apiInterFace.Login(
                                    JSONObject().apply {
                                        put("email", loginBinding.email.text.toString())
                                        put("password", loginBinding.password.text.toString())
                                        put("device_type", "android")
                                        put("device_token", token)
                                    }.toString().getJsonRequestBody()
                                )
                            }

                            override fun success(response: Response<LoginResponse>) {

                                if(response.body()?.status == 1){
                                    PreferenceUtils.saveBoolean("isLogin",true)
                                    PreferenceUtils.saveString("token",response.body()?.bearer_token!!)
                                    PreferenceData.storeProfileData(requireActivity(),response.body()?.data!!)

                                    val intent = Intent(requireActivity(),HomeActivity::class.java)
                                    startActivity(intent)
                                    getActivitContextRegistration!!.finish()
                                }

                                else if(response.body()?.status == 0 && response.body()?.message == ""){


                                }

                                else{
                                    showToast(response.body()?.message!!,requireActivity())

                                }

                            }

                            override fun error(message: String) {

                                showToast(message,requireActivity())

                                if(message=="Account not verified Please Verify your account"){

                                    val action = LoginScreenDirections.actionLoginScreenToOtpVerification("","ACCOUNT_VERIFICATION",
                                        loginBinding.email.text.toString()
                                    )
                                    findNavController().navigate(action)
                                }
                            }


                        }
                    )

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else{
                activity?.resources?.getString(R.string.internetconnection)
                    ?.let { showToast(it,getActivitContextRegistration) }

            }
        }
    }

    fun showPassword(){

        if(loginBinding.password.transformationMethod.equals(PasswordTransformationMethod.getInstance())){
            loginBinding.siginpasssow.setImageResource(R.drawable.visibility_off)
            loginBinding.password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

        }
        else{
            loginBinding.siginpasssow.setImageResource(R.drawable.visibility)

            //Hide Password
            loginBinding.password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        }
    }


}