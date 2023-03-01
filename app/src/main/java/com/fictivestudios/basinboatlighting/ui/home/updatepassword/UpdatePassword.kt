package com.fictivestudios.basinboatlighting.ui.home.updatepassword

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ecommercemvvmpractice2.utilities.extensions.showToast
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.base.BaseFragment
import com.fictivestudios.basinboatlighting.databinding.UpdatepasswordBinding
import com.fictivestudios.basinboatlighting.utils.Titlebar
import com.fictivestudios.basinboatlighting.utils.hideKeyboard
import com.fictivestudios.basinboatlighting.utils.isNotValidPassword
import com.fictivestudios.getmefit.Networking.ApiService
import com.fictivestudios.getmefit.Networking.getJsonRequestBody
import com.fictivestudios.getmefit.data.response.models.CommonResponse
import com.fictivestudios.getmefit.uitilites.Constants
import com.fictivestudios.tafcha.Utils.PreferenceUtils
import com.fictivestudios.tafcha.networkSetup.callhandler.CallHandler
import com.fictivestudios.tafcha.networkSetup.retrofitsetup.RetrofitSetup
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class UpdatePassword : BaseFragment() {

    private lateinit var updatePassword: UpdatepasswordBinding
    private val updatePasswordViewModel by viewModels<UpdatePasswordViewModel>()

    override fun setTitlebar(titlebar: Titlebar) {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        updatePassword =
            DataBindingUtil.inflate(inflater, R.layout.updatepassword, container, false)
        updatePassword.updateViewModel = updatePasswordViewModel
        updatePassword.lifecycleOwner = this

        updatePassword.updatePaswwordscreen.setOnClickListener {
            hideKeyboard()

        }

        getActivityContext!!.hideBttomBar()
        getActivityContext!!.hideButtonHome()



        updatePassword.back.setOnClickListener {
            getActivityContext!!.onBackPressed()
        }

        updatePassword.upoldpass.setOnClickListener {
            showPassword1()
        }

        updatePassword.upnewpass.setOnClickListener {

            showPassword2()
        }

        updatePassword.upconfirmpass.setOnClickListener {

            showPassword3()
        }


        updatePassword.updatePassword.setOnClickListener {
            if (validation()) {
                updatePassword()
            }

        }


        return updatePassword.root

    }


    fun validation(): Boolean {

        if (updatePassword.oldPassword.text?.isBlank()!!) {
            showToast("Old Password cannot be empty", getActivityContext)
            return false
        }
        else if (updatePassword.password.text?.isBlank()!!) {
            showToast("Password cannot be empty", getActivityContext)
            return false
        }

        else if (updatePassword.password.text?.length!! <= 7 && updatePassword.password.text.toString().isNotValidPassword()) {
            showToast(
                "Password should be of 8 characters long should contain uppercase, lowercase, number and special character.",
                getActivityContext
            )
            return false

        }

//        else if (updatePassword.password.text?.length!! <= 7) {
//            showToast(
//                "Password should be of 8 characters long",
//                getActivitContextRegistration
//            )
//            return false
//        }
        else if (updatePassword.cpassword.text?.isBlank()!!) {


            showToast("Confirm Password cannot be empty", getActivityContext)

            return false
        } else if (updatePassword.password.text!!.toString() != updatePassword.cpassword.text!!.toString()) {
            showToast("Password not match with confirm Password field", requireActivity())
            return false
        } else {
            return true
        }


    }


    fun showPassword1() {


        if (updatePassword.oldPassword.transformationMethod.equals(
                PasswordTransformationMethod.getInstance()
            )
        ) {
            updatePassword.upoldpass.setImageResource(R.drawable.visibility_off)
            updatePassword.oldPassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()

        } else {
            updatePassword.upoldpass.setImageResource(R.drawable.visibility)

            //Hide Password
            updatePassword.oldPassword.transformationMethod =
                PasswordTransformationMethod.getInstance()

        }
    }


    fun showPassword2() {


        if (updatePassword.password.transformationMethod.equals(
                PasswordTransformationMethod.getInstance()
            )
        ) {
            updatePassword.upnewpass.setImageResource(R.drawable.visibility_off)
            updatePassword.password.transformationMethod =
                HideReturnsTransformationMethod.getInstance()

        } else {
            updatePassword.upnewpass.setImageResource(R.drawable.visibility)

            //Hide Password
            updatePassword.password.transformationMethod =
                PasswordTransformationMethod.getInstance()

        }
    }


    fun showPassword3() {


        if (updatePassword.cpassword.transformationMethod.equals(PasswordTransformationMethod.getInstance())) {
            updatePassword.upconfirmpass.setImageResource(R.drawable.visibility_off)
            updatePassword.cpassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()

        } else {
            updatePassword.upconfirmpass.setImageResource(R.drawable.visibility)

            //Hide Password
            updatePassword.cpassword.transformationMethod =
                PasswordTransformationMethod.getInstance()

        }
    }


    private fun updatePassword() {

        lifecycleScope.launch {
            if (Constants.isNetworkConnected(requireActivity(), true)) {
                try {

                    RetrofitSetup().callApi(requireActivity(),
                        true,
                        false,
                        "${PreferenceUtils.getString("token")}",
                        object : CallHandler<Response<CommonResponse>> {
                            override suspend fun sendRequest(apiInterFace: ApiService): Response<CommonResponse> {

                                return apiInterFace.changePassword(
                                    JSONObject().apply {
                                        put("new_password", updatePassword.password.text.toString())
                                        put(
                                            "old_password",
                                            updatePassword.oldPassword.text.toString()
                                        )
                                    }.toString().getJsonRequestBody()
                                )
                            }

                            override fun success(response: Response<CommonResponse>) {

                                if (response.body()?.status == 1) {
                                    showToast(response.body()?.message!!, requireActivity())
                                    getActivityContext!!.onBackPressed()
                                } else {
                                    showToast(response.body()?.message!!, requireActivity())
                                }

                            }

                            override fun error(message: String) {

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