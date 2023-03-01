package com.fictivestudios.basinboatlighting.ui.registration.aboutus

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
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
import com.example.ecommercemvvmpractice2.utilities.extensions.showToast
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.base.BaseFragment
import com.fictivestudios.basinboatlighting.databinding.AboutsBinding
import com.fictivestudios.basinboatlighting.models.content.Content
import com.fictivestudios.basinboatlighting.ui.registration.signup.SignupScreenDirections
import com.fictivestudios.basinboatlighting.utils.Titlebar
import com.fictivestudios.getmefit.Networking.ApiService
import com.fictivestudios.getmefit.uitilites.Constants
import com.fictivestudios.tafcha.Utils.PreferenceUtils
import com.fictivestudios.tafcha.networkSetup.callhandler.CallHandler
import com.fictivestudios.tafcha.networkSetup.retrofitsetup.RetrofitSetup
import kotlinx.coroutines.launch
import retrofit2.Response

class AboutUs : BaseFragment() {

    private lateinit var aboutsBinding: AboutsBinding
    private val aboutUsViewModel by viewModels<AboutUsViewModel>()

    override fun setTitlebar(titlebar: Titlebar) {



    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        aboutsBinding = DataBindingUtil.inflate(inflater, R.layout.abouts, container, false)

        aboutsBinding.lifecycleOwner = this

        aboutsBinding.termsconditionviewModel = aboutUsViewModel

        getActivityContext!!.hideButtonHome()
        getActivityContext!!.hideBttomBar()

        aboutsBinding.ivback.setOnClickListener {

            getActivityContext?.onBackPressed()

        }


//        aboutsBinding.continueButton.setOnClickListener {
//            getActivityContext?.onBackPressed()
//        }


        aboutsBinding.redirect.movementMethod = LinkMovementMethod.getInstance()
        val spans: Spannable = aboutsBinding.redirect.text as Spannable
        val clickSpan: ClickableSpan = object : ClickableSpan() {


            override fun onClick(widget: View) {

//                val url = "https://basinboatlighting.com/"
//                val intent = Intent(Intent.ACTION_VIEW)
//                intent.data = Uri.parse(url)
//                startActivity(intent)


            }
        }
        val TEXT_BEFORE_LINK = 0
        val TEXT_LINK = 13
        spans.setSpan(
            clickSpan,
            TEXT_BEFORE_LINK,
            TEXT_BEFORE_LINK + TEXT_LINK,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        aboutUs("au")

        return aboutsBinding.root

    }

    private fun aboutUs(aus: String) {

        lifecycleScope.launch {
            if (Constants.isNetworkConnected(requireActivity(), true)) {
                try {

                    RetrofitSetup().callApi(requireActivity(),
                        true, false,
                        "${PreferenceUtils.getString("token")}",
                        object : CallHandler<Response<Content>> {
                            override suspend fun sendRequest(apiInterFace: ApiService): Response<Content> {
                                return apiInterFace.content(aus)
                            }

                            override fun success(response: Response<Content>) {
                                if (response.body()!!.status == 1) {

                                 //   aboutsBinding.aboutusstext.text = response.body()?.data?.content

                                    aboutsBinding.aboutusstext.setText(Html.fromHtml(response.body()?.data?.content))

                                } else {
                                    showToast(response.body()?.message.toString(),getActivityContext)


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