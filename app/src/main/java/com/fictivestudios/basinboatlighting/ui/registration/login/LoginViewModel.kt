package com.fictivestudios.basinboatlighting.ui.registration.login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecommercemvvmpractice2.utilities.extensions.showToast
import com.fictivestudios.basinboatlighting.activities.RegistrationActivity

class LoginViewModel() : ViewModel(){


    private val passwordPattren = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\\\S+\$).{4,}\$"
    var emailFeild = MutableLiveData<String>()
    var passwordFeild = MutableLiveData<String>()

    var emailValidator = MutableLiveData<String>()
    var passwordValidator = MutableLiveData<String>()







}