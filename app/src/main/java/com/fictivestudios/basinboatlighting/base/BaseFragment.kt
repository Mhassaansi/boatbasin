package com.fictivestudios.basinboatlighting.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fictivestudios.basinboatlighting.activities.HomeActivity
import com.fictivestudios.basinboatlighting.activities.RegistrationActivity
import com.fictivestudios.basinboatlighting.utils.Titlebar

abstract class BaseFragment : Fragment() {

    open var getActivityContext: HomeActivity? = null
    open var getActivitContextRegistration: RegistrationActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    fun getActivityContext(): Activity? {
        return getActivityContext
    }

    fun getActivitContextRegistration(): RegistrationActivity? {

        return getActivitContextRegistration
    }


    abstract fun setTitlebar(titlebar: Titlebar)

    override fun onAttach(context : Context) {
        super.onAttach(context)
        if (context is HomeActivity) {
            val contex = context as HomeActivity?
            if (contex != null)
                getActivityContext = context
        }
        else if (context is RegistrationActivity) {
            val context = context as RegistrationActivity?
            if (context != null) {
                getActivitContextRegistration = context
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (getActivityContext != null) {
           setTitlebar(getActivityContext!!.getTitlebar())
        } else if (getActivitContextRegistration != null) {
            setTitlebar(getActivitContextRegistration!!.getTitlebarRegister())
        }

    }



}