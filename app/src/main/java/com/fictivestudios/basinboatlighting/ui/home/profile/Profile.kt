package com.fictivestudios.basinboatlighting.ui.home.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ecommercemvvmpractice2.utilities.extensions.showToast
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.adapter.CertificateAdapter2
import com.fictivestudios.basinboatlighting.base.BaseFragment
import com.fictivestudios.basinboatlighting.databinding.ProfileBinding
import com.fictivestudios.basinboatlighting.models.profile.LicenseImage
import com.fictivestudios.basinboatlighting.models.profile.ProfileMain
import com.fictivestudios.basinboatlighting.utils.Titlebar
import com.fictivestudios.getmefit.Networking.ApiService
import com.fictivestudios.getmefit.uitilites.Constants
import com.fictivestudios.tafcha.Utils.PreferenceUtils
import com.fictivestudios.tafcha.networkSetup.callhandler.CallHandler
import com.fictivestudios.tafcha.networkSetup.retrofitsetup.RetrofitSetup
import kotlinx.coroutines.launch
import retrofit2.Response

class Profile : BaseFragment(), CertificateAdapter2.ItemClickListener {

    private lateinit var profileBinding: ProfileBinding
    private val profileBindingviewModel by viewModels<ProfileViewModel>()
    private var certificateList = ArrayList<LicenseImage>()
    var profileData: ProfileMain? = null

    override fun setTitlebar(titlebar: Titlebar) {

    }


    override fun onCreateView(
         inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        profileBinding = DataBindingUtil.inflate(inflater, R.layout.profile, container, false)
        profileBinding.lifecycleOwner = this
        profileBinding.profileViewModel = profileBindingviewModel


        getActivityContext!!.showButtonHome()
        getActivityContext!!.showBttomBar()

        getProfileData()

        profileBinding.pencil.setOnClickListener {


            if (profileData != null) {
                val action = ProfileDirections.actionProfileToEditProfile(profileData!!)
                findNavController().navigate(action)
            }

//            else {
//
//                showToast(profileData.toString(), requireActivity())
//            }


        }

        return profileBinding.root
    }


    fun liscenceAdapter(list: ArrayList<LicenseImage>) {

        val certificateAdapter = CertificateAdapter2(getActivityContext!!, list,this)
        profileBinding.recylerViewCertificate.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL, false
            )
            profileBinding.recylerViewCertificate.adapter = certificateAdapter
            profileBinding.recylerViewCertificate.adapter!!.notifyDataSetChanged()
        }

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

                                    if (response.body()?.data?.avatar != "" && response.body()?.data?.avatar != null) {
                                        Glide.with(getActivityContext!!).load(response.body()?.data?.avatar).into(profileBinding.profileImage)
                                    } else {

                                        profileBinding.profileImage.setImageResource(R.drawable.dummyprofile)
                                    }



                                    profileBinding.fname.text =
                                        response.body()?.data?.first_name + " " + response.body()?.data?.last_name
                                    profileBinding.firstname.text =
                                        response.body()?.data?.first_name
                                    profileBinding.lastnamename.text =
                                        response.body()?.data?.last_name
                                    profileBinding.contact.text =
                                        response.body()?.data?.emergency_number
                                    profileBinding.email.text = response.body()?.data?.email

                                    response.body()?.data!!.license_images?.let {
                                        liscenceAdapter(
                                            it as ArrayList<LicenseImage>
                                        )
                                    }

                                    profileData = response.body()


                                } else {


                                    response.body()!!.message?.let {
                                        showToast(
                                            it,
                                            requireActivity()
                                        )
                                    }


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


    override fun onItemClick(id: Int) {

    }


}