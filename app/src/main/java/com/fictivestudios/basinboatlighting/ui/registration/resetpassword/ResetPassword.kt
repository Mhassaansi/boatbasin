package com.fictivestudios.basinboatlighting.ui.registration.resetpassword

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ecommercemvvmpractice2.utilities.extensions.showToast
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.base.BaseFragment
import com.fictivestudios.basinboatlighting.databinding.ResetpasswordBinding
import com.fictivestudios.basinboatlighting.utils.Titlebar
import com.fictivestudios.basinboatlighting.utils.hideKeyboard
import com.fictivestudios.basinboatlighting.utils.isNotValidPassword
import com.fictivestudios.basinboatlighting.utils.validatePassword
import com.fictivestudios.getmefit.Networking.ApiService
import com.fictivestudios.getmefit.Networking.getJsonRequestBody
import com.fictivestudios.getmefit.data.response.models.CommonResponse
import com.fictivestudios.getmefit.uitilites.Constants
import com.fictivestudios.tafcha.networkSetup.callhandler.CallHandler
import com.fictivestudios.tafcha.networkSetup.retrofitsetup.RetrofitSetup
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response


class ResetPassword : BaseFragment() {


    private lateinit var resetpasswordBinding: ResetpasswordBinding
    private val resetPasswordViewModel by viewModels<ResetPasswordViewModel>()



    private val email:ResetPasswordArgs by navArgs<ResetPasswordArgs>()

    override fun setTitlebar(titlebar: Titlebar) {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        resetpasswordBinding =
            DataBindingUtil.inflate(inflater, R.layout.resetpassword, container, false)
        resetpasswordBinding.lifecycleOwner = this
        resetpasswordBinding.resetloginViewModel = resetPasswordViewModel


        resetpasswordBinding.resetPasswordScreen.setOnClickListener {

            hideKeyboard()

        }


        resetpasswordBinding.resetButton.setOnClickListener {
            if (validation()) {
                update_password()

            }

        }


        resetpasswordBinding.passvisibilty.setOnClickListener {
            showPassword1()
        }

        resetpasswordBinding.repassvisibilty.setOnClickListener {
            showPassword2()
        }

        return resetpasswordBinding.root

    }

    fun validation(): Boolean {

        if (resetpasswordBinding.password.text?.length == 0) {

            showToast("Password fielld cannot be empty", requireActivity())
            return false

        }

        else if (resetpasswordBinding.password.text?.length!! <= 7 && resetpasswordBinding.password.text?.toString()!!.isNotValidPassword()) {

            showToast("Password should be of 8 characters long should contain uppercase, lowercase, number and special character.", requireActivity())
            return false
        }


       else if (resetpasswordBinding.cpassword.text?.length == 0) {

            showToast("Confirm Password field cannot be empty", requireActivity())
            return false

        }

        else if (resetpasswordBinding.password.text?.toString() != resetpasswordBinding.cpassword.text!!.toString()) {
            showToast("Password not match with confirm password field", requireActivity())

            return false
        } else {

            return true
        }
    }


    private fun update_password() {
        lifecycleScope.launch {
            if (Constants.isNetworkConnected(requireActivity(), true)) {
                try {
                    RetrofitSetup().callApi(
                        requireActivity(), true, false, "",
                        object : CallHandler<Response<CommonResponse>> {
                            override suspend fun sendRequest(apiInterFace: ApiService): Response<CommonResponse> {
                                return apiInterFace.resetPassword(email.email,"123456",resetpasswordBinding.password.text.toString())
                            }

                            override fun success(response: Response<CommonResponse>) {
                                if (response.body()?.status == 1) {
                                    showToast(
                                        response.body()?.message.toString(),
                                        requireActivity()
                                    )

                                    val action = ResetPasswordDirections.actionResetPasswordToLoginScreen()
                                    findNavController().navigate(action)
                                } else {

                                    showToast(
                                        response.body()?.message.toString(),
                                        requireActivity()
                                    )

                                }

                            }

                            override fun error(message: String) {
                                showToast(message, requireActivity())

                            }
                        })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
    }

    fun showPassword1() {


        if (resetpasswordBinding.password.transformationMethod.equals(
                PasswordTransformationMethod.getInstance()
            )
        ) {
            resetpasswordBinding.passvisibilty.setImageResource(R.drawable.visibility_off)
            resetpasswordBinding.password.transformationMethod = HideReturnsTransformationMethod.getInstance()

        } else {
            resetpasswordBinding.passvisibilty.setImageResource(R.drawable.visibility)

            //Hide Password
            resetpasswordBinding.password.transformationMethod = PasswordTransformationMethod.getInstance()

        }
    }


    fun showPassword2() {


        if (resetpasswordBinding.cpassword.transformationMethod.equals(
                PasswordTransformationMethod.getInstance()
            )
        ) {
            resetpasswordBinding.repassvisibilty.setImageResource(R.drawable.visibility_off)
            resetpasswordBinding.cpassword.transformationMethod = HideReturnsTransformationMethod.getInstance()

        } else {
            resetpasswordBinding.repassvisibilty.setImageResource(R.drawable.visibility)

            //Hide Password
            resetpasswordBinding.cpassword.transformationMethod = PasswordTransformationMethod.getInstance()

        }
    }
}