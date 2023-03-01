package com.fictivestudios.basinboatlighting.ui.registration.privacypolicy

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.base.BaseFragment
import com.fictivestudios.basinboatlighting.databinding.OtpverificationBinding
import com.fictivestudios.basinboatlighting.databinding.PrivacypolicyBinding
import com.fictivestudios.basinboatlighting.models.content.Content
import com.fictivestudios.basinboatlighting.ui.registration.optveification.OtpVerificationViewModel
import com.fictivestudios.basinboatlighting.utils.Titlebar
import com.fictivestudios.getmefit.Networking.ApiService
import com.fictivestudios.getmefit.uitilites.Constants
import com.fictivestudios.tafcha.Utils.PreferenceUtils
import com.fictivestudios.tafcha.networkSetup.callhandler.CallHandler
import com.fictivestudios.tafcha.networkSetup.retrofitsetup.RetrofitSetup
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception

class PrivacyPolicy : BaseFragment() {

    private lateinit var pivacypolicyrBinding: PrivacypolicyBinding
    private val pivacypolicyViewModel by viewModels<PrivacyPolicyViewModel>()

    override fun setTitlebar(titlebar: Titlebar) {
        titlebar.setTitleHome(getActivityContext!!,R.string.ppolicy.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        pivacypolicyrBinding = DataBindingUtil.inflate(inflater, R.layout.privacypolicy,container,false)
        pivacypolicyrBinding.lifecycleOwner = this
        pivacypolicyrBinding.privacyviewModel = pivacypolicyViewModel

        getActivityContext!!.hideBttomBar()
        getActivityContext!!.hideButtonHome()

        pivacypolicyrBinding.ivback.setOnClickListener {
            getActivityContext!!.onBackPressed()

        }

        privacyPolicy("pp")

        return pivacypolicyrBinding.root

    }


    private fun privacyPolicy(pp:String){

        lifecycleScope.launch {
            if (Constants.isNetworkConnected(requireActivity(), true)) {
                try {

                    RetrofitSetup().callApi(requireActivity(),
                        true,false,
                        "",
                        object : CallHandler<Response<Content>> {
                            override suspend fun sendRequest(apiInterFace: ApiService): Response<Content> {
                                return apiInterFace.content(pp)
                            }

                            override fun success(response: Response<Content>) {
                                if (response.body()!!.status == 1) {
                                    //  mView.pp_text.setText(response.body()!!.content[0].content)
                                  //  pivacypolicyrBinding.privacypolicy.setText(response.body()?.data?.content)
                                    pivacypolicyrBinding.privacypolicy.setText(Html.fromHtml(response.body()?.data?.content))

                                } else {
                                    Toast.makeText(
                                        requireActivity(), "Error",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                            override fun error(message: String) {
                                RetrofitSetup().hideLoader()
                                Toast.makeText(requireActivity(), "Error", Toast.LENGTH_LONG).show()
                            }

                        }
                    )


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

}