package com.fictivestudios.basinboatlighting.ui.home.editprofile


import android.Manifest
import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ecommercemvvmpractice2.utilities.extensions.showToast
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.adapter.CertificateAdapter
import com.fictivestudios.basinboatlighting.base.BaseFragment
import com.fictivestudios.basinboatlighting.databinding.EditprofileBinding
import com.fictivestudios.basinboatlighting.models.profile.LicenseImage
import com.fictivestudios.basinboatlighting.models.profile.ProfileMain
import com.fictivestudios.basinboatlighting.utils.*
import com.fictivestudios.getmefit.Networking.ApiService
import com.fictivestudios.getmefit.Networking.getFormDataBody
import com.fictivestudios.getmefit.Networking.getJsonRequestBody
import com.fictivestudios.getmefit.Networking.getPartMap
import com.fictivestudios.getmefit.data.response.models.CommonResponse
import com.fictivestudios.getmefit.uitilites.Constants
import com.fictivestudios.tafcha.Utils.PreferenceUtils
import com.fictivestudios.tafcha.networkSetup.callhandler.CallHandler
import com.fictivestudios.tafcha.networkSetup.retrofitsetup.RetrofitSetup
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File


//import dagger.hilt.android.AndroidEntryPoint
//
//
//@AndroidEntryPoint
class EditProfile : BaseFragment(), CertificateAdapter.ItemClickListener {
    var selectedImageAdapter: CertificateAdapter? = null
    private var fileTemporaryProfilePicture: File? = null
    var image: ObservableField<String> = ObservableField("")
    val PICK_IMAGE_MULTIPLE = 1
    val SELECT_IMAGE1 = 2
    val PERMISSION_REQUEST_CODE = 3
    val SELECT_IMAGE2 = 4


    var selectedImageList = ArrayList<Uri>()
    var certrifcateString = ArrayList<String>()
    var certificatesdata = ArrayList<LicenseImage>()


    private lateinit var editProfileBinding: EditprofileBinding
    private val editProfileViewModel by viewModels<EditProfileViewModel>()
    private val editProfileArgs: EditProfileArgs by navArgs<EditProfileArgs>()
    private var profileArgs: ProfileMain? = null

    override fun setTitlebar(titlebar: Titlebar) {

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        editProfileBinding =
            DataBindingUtil.inflate(inflater, R.layout.editprofile, container, false)
        editProfileBinding.lifecycleOwner = this
        editProfileBinding.editProfleViewmodel = editProfileViewModel

        profileArgs = editProfileArgs.profileData

        selectedImageList = ArrayList()

        editProfileBinding.editProfileScreen.setOnClickListener {
            hideKeyboard()

        }

        lifecycleScope.launch(Dispatchers.Main) {

            getActivityContext!!.hideButtonHome()
            getActivityContext!!.hideBttomBar()
        }

        editProfileBinding.questions.setOnClickListener {

            getActivityContext?.onBackPressed()

        }
        editProfileBinding.uploadLicense.setOnClickListener {

            if (checkPermissions()) {
                showImagePickerDialog(true)

            } else {

                requestPermission()

            }


        }

        editProfileBinding.pencil.setOnClickListener {


            if (checkPermissions()) {
                showImagePickerDialog(false)

            } else {

                requestPermission()

            }


        }
        // PhoneNumberUtils.formatNumber(editProfileBinding.contact.text.toString(), "US")
        editProfileBinding.editButton.setOnClickListener {

            if(validation()){
                updateUserApi()
            }

        }

        loadProfileData()
        certificatesdata.clear()
        certificatesdata = profileArgs?.data?.license_images!!



        for (i in certificatesdata) {
            certrifcateString.add(i.license_image!!)

        }

        liscenceAdapter()



        return editProfileBinding.root

    }

    fun validation():Boolean {

        if (!editProfileBinding.contact.text?.length?.equals(10)!!){

            showToast("Phone number length should be 10 characters",requireActivity())

            return false

        }
        else {

            return true
        }



    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            getActivityContext!!,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            getActivityContext!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            getActivityContext!!,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            getActivityContext!!,
            arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, CAMERA),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

            } else {
                showSnackBar("Open the Setting", getActivityContext)

            }
        }

    }

    fun showSnackBar(message: String?, activity: Activity?) {
        if (null != activity && null != message) {
            Snackbar.make(
                activity.findViewById(android.R.id.content),
                message, Snackbar.LENGTH_SHORT
            ).setAction(message, View.OnClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", activity.getPackageName(), null)
                intent.data = uri
                startActivity(intent)
            }).show()
        }
    }

    private fun loadProfileData() {
        if (profileArgs?.data?.avatar != null && profileArgs?.data?.avatar != "") {

            Glide.with(requireActivity()).load(profileArgs?.data?.avatar)
                .into(editProfileBinding.profileImage)
        } else {

            editProfileBinding.profileImage.setImageResource(R.drawable.dummyprofile)
        }



        editProfileBinding.fname.text =
            profileArgs?.data?.first_name + " " + profileArgs?.data?.last_name
        editProfileBinding.firstname.setText(profileArgs?.data?.first_name)
        editProfileBinding.lastnamename.setText(profileArgs?.data?.last_name)

        var phonenumber = profileArgs?.data?.emergency_number
        val re = "[^A-Za-z0-9 ]".toRegex()
        phonenumber = re.replace(phonenumber!!, "")
        var replaced = phonenumber.replace(" ", "")
        replaced = replaced.drop(1)
//        val finalnum =   replaced.split("1","").toTypedArray()


        editProfileBinding.contact.setText(replaced)
        editProfileBinding.email.setText(profileArgs?.data?.email)

        // profileArgs?.data!!.license_images?.let { liscenceAdapter(it) }


    }


    @SuppressLint("NotifyDataSetChanged")
    fun liscenceAdapter() {
        Log.d("ArraySize", certrifcateString.toString())
        val certificateAdapter =
            certrifcateString.let { CertificateAdapter(getActivityContext!!, it, this) }
        editProfileBinding.liscensRecely.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL, false
            )
            editProfileBinding.liscensRecely.adapter = certificateAdapter
            editProfileBinding.liscensRecely.adapter?.notifyDataSetChanged()
        }

    }

    override fun onItemClick(id: Int, pos: Int) {

        if (pos > certificatesdata.size - 1) {
            certrifcateString.removeAt(pos)
            editProfileBinding.liscensRecely.adapter?.notifyDataSetChanged()

        } else {
            delete(certificatesdata[pos].id!!, pos)


        }

    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data : Intent?
    ) {

        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {

            if (requestCode == PICK_IMAGE_MULTIPLE) {
                if (data.clipData != null) {

                    var count = data.clipData?.itemCount

                    if (count != null) {
                        for (i in 0..count - 1) {
                            var imageUri: Uri = data.clipData?.getItemAt(i)?.uri!!
                            selectedImageList.add(imageUri)
                            certrifcateString.add(imageUri.toString())

                        }
                        Log.d("ArraysizeMultiple", certrifcateString.size.toString())


                        selectedImageAdapter = CertificateAdapter(
                            requireActivity(),
                            certrifcateString,
                            this@EditProfile
                        )
                        editProfileBinding.liscensRecely.adapter = selectedImageAdapter
                        editProfileBinding.liscensRecely.adapter?.notifyDataSetChanged()

                    }
                } else if (data.data != null) {
                    var imageUri: Uri? = data.data

                    if (imageUri != null) {
                        selectedImageList.add(imageUri)
                        certrifcateString.add(imageUri.toString())
                        selectedImageAdapter = CertificateAdapter(
                            requireActivity(),
                            certrifcateString,
                            this@EditProfile
                        )

                        editProfileBinding.liscensRecely.adapter = selectedImageAdapter
                        editProfileBinding.liscensRecely.adapter?.notifyDataSetChanged()
                    }


                }
            }
            else if (requestCode == SELECT_IMAGE2) {


                val bitmap: Bitmap? = getBitmapFromIntent(requireActivity(), data)
                val uri = bitmap?.let { getImageUri(requireContext(), it) }
               // editProfileBinding.profileImage.setImageBitmap(bitmap)
                editProfileBinding.profileImage.setImageURI(uri)
                image.set(activity?.let { uri?.let { it1 -> getMediaFilePathFor(it1, it) } })


            }
            else if (requestCode == SELECT_IMAGE1) {
                // val uri: Uri? = data?.data

                val bitmap: Bitmap? = getBitmapFromIntent(requireActivity(), data)

                val uri = bitmap?.let { getImageUri(requireContext(), it) }


                if (uri != null) {
                    selectedImageList.add(uri)
                    certrifcateString.add(uri.toString())
                    selectedImageAdapter = CertificateAdapter(
                        requireActivity(),
                        certrifcateString,
                        this@EditProfile
                    )

                    editProfileBinding.liscensRecely.adapter = selectedImageAdapter
                    editProfileBinding.liscensRecely.adapter?.notifyDataSetChanged()
                }


            } else {


                val uri: Uri = data.data!!
                image.set(activity?.let { getMediaFilePathFor(uri, it) })
                activity?.runOnUiThread {
                    Log.d("Camera++", "Camera ImageCamera Image")
                    editProfileBinding.profileImage.setImageURI(uri)
                }


            }


        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }

    }


    fun prepareFilePart(partName: String, file: File): MultipartBody.Part {
        val requestFile = file.asRequestBody("image/png".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }


    private fun updateUserApi() {
        lifecycleScope.launch {
            var part: MultipartBody.Part? = null
            val hashMap = HashMap<String, RequestBody>().apply {
                this["first_name"] = editProfileBinding.firstname.text.toString().getFormDataBody()
                this["last_name"] =
                    editProfileBinding.lastnamename.text.toString().getFormDataBody()
                this["contact"] =
                    "+1 ${PhoneNumberUtils.formatNumber(editProfileBinding.contact.text.toString(), "US")}"
                        .getFormDataBody()
                this["country_code"] = "+1".getFormDataBody()

            }
            if (!image.get().isNullOrEmpty()) {
                part = File(image.get() ?: "").getPartMap("avatar")
            }


            val listOfImages = ArrayList<MultipartBody.Part>()
            if (selectedImageList != null) {
                for (i in 0 until selectedImageList?.size!!) {


                    listOfImages.add(
                        prepareFilePart(
                            "license_images[$i]", /*File(selectedImageList!![i].path!!)*/
                            File(getMediaFilePathFor(selectedImageList[i], requireContext()))
                        )
                    )
                }
            }



            if (Constants.isNetworkConnected(requireActivity(), true)) {
                try {

                    RetrofitSetup().callApi(requireActivity(),
                        true,
                        false,
                        "${PreferenceUtils.getString("token")}",
                        object : CallHandler<Response<CommonResponse>> {
                            override suspend fun sendRequest(apiInterface: ApiService): Response<CommonResponse> {
                                return apiInterface.updateProfile(hashMap, part, listOfImages)
                            }

                            override fun success(response: Response<CommonResponse>) {
                                if (response.body()!!.status == 0) {
                                    showToast(response.body()?.message.toString(), requireContext())


                                } else if (response.body()?.status == 1) {

                                    showToast(response.body()?.message.toString(), requireContext())
                                    getActivityContext!!.onBackPressed()

                                } else {
                                    showToast(response.body()?.message.toString(), requireContext())
                                }
                            }

                            override fun error(message: String) {

                                RetrofitSetup().hideLoader()
                                showToast("Error", requireContext())
                            }
                        })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun delete(d_id: Int, pos: Int) {

        lifecycleScope.launch(Dispatchers.IO) {
            if (Constants.isNetworkConnected(getActivityContext!!, true)) {
                try {

                    RetrofitSetup().callApi(getActivityContext!!,
                        true,
                        false,
                        PreferenceUtils.getString("token"),
                        object : CallHandler<Response<CommonResponse>> {
                            override suspend fun sendRequest(apiInterFace: ApiService): Response<CommonResponse> {
                                return apiInterFace.deleteImage(
                                    JSONObject().apply {
                                        put("image_id", d_id)

                                    }.toString().getJsonRequestBody()

                                )
                            }

                            override fun success(response: Response<CommonResponse>) {
                                if (response.body()?.status == 1) {
                                    Toast.makeText(
                                        getActivityContext!!, response.body()!!.message,
                                        Toast.LENGTH_LONG
                                    ).show()

                                    certificatesdata.removeAt(pos)
                                    certrifcateString.removeAt(pos)
                                    editProfileBinding.liscensRecely.adapter?.notifyDataSetChanged()


                                } else {
                                    Toast.makeText(
                                        getActivityContext!!, response.body()!!.message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                            override fun error(message: String) {
                                RetrofitSetup().hideLoader()
                                Toast.makeText(
                                    getActivityContext!!, "Error",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }
                    )


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

    private fun showImagePickerDialog(isMultiple: Boolean) {

        var dialog = Dialog(context as Activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.imagepicker)

        resizeDialogView(dialog, 70, requireActivity())
        dialog.show()


        var camera: Button? = dialog.findViewById<Button>(R.id.camera)
        var gallery: Button? = dialog.findViewById<Button>(R.id.galery)

        camera?.setOnClickListener {

            if (isMultiple) {
                dialog.dismiss()
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, SELECT_IMAGE1)
            } else {
                dialog.dismiss()
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, SELECT_IMAGE2)

            }

        }

        gallery?.setOnClickListener {

            if (isMultiple) {
                dialog.dismiss()
                if (Build.VERSION.SDK_INT < 19) {
                    var intent = Intent()
                    intent.type = "image/*"
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    intent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(
                        Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE
                    )
                } else {
                    var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.type = "image/*"
                    startActivityForResult(intent, PICK_IMAGE_MULTIPLE)
                }
            } else {
                dialog.dismiss()
                if (Build.VERSION.SDK_INT < 19) {

                    val intent = Intent()
                    intent.action = Intent.ACTION_GET_CONTENT
                    intent.type = "image/png"
                    startActivityForResult(intent, SELECT_IMAGE2)
                } else {
                    val intent = Intent()
                    intent.action = Intent.ACTION_GET_CONTENT
                    intent.type = "image/png"
                    startActivityForResult(intent, SELECT_IMAGE2)

                }
            }


        }


    }

    fun decodeFile(path: String?): Bitmap? {
        try {
            // Decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, o)
            // The new size we want to scale to
            val REQUIRED_SIZE = 70

            // Find the correct scale value. It should be the power of
            // 2.
            var scale = 1
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE
                && o.outHeight / scale / 2 >= REQUIRED_SIZE
            ) scale *= 2

            // Decode with inSampleSize
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            return BitmapFactory.decodeFile(path, o2)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }

}