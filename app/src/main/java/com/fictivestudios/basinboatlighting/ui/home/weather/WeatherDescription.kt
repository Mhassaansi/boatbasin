package com.fictivestudios.basinboatlighting.ui.home.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.example.ecommercemvvmpractice2.utilities.extensions.showToast
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.base.BaseFragment
import com.fictivestudios.basinboatlighting.databinding.WeatherinfoBinding

import com.fictivestudios.basinboatlighting.utils.*

class WeatherDescription : BaseFragment() {
    private lateinit var wBinding: WeatherinfoBinding

    private val wargs:WeatherDescriptionArgs by navArgs<WeatherDescriptionArgs>()


    override fun setTitlebar(titlebar: Titlebar) {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        wBinding = DataBindingUtil.inflate(inflater, R.layout.weatherinfo, container, false)

        wBinding.backwether.setOnClickListener {

            getActivityContext?.onBackPressed()

        }

        init()

        return wBinding.root

    }

    fun init(){


        wBinding.cdate.text = getDayOfWeekmonthyear(wargs.dt.toLong())
        wBinding.cTemp.text = wargs.cTemp

        wBinding.sunsetval.text = getTime(wargs.sunset.toLong())
        wBinding.sunriseval.text = getTime(wargs.sunrise.toLong())
        wBinding.moonriseval.text = getTime(wargs.moonrise.toLong())
        wBinding.moonsetval.text = getTime(wargs.moonset.toLong())
        wBinding.humidityval.text = wargs.humidity+ " %"
        wBinding.windspeedval.text = wargs.windspeed+ " mph"
        wBinding.winddegreeval.text = wargs.winddegree+ " \u00B0"
        wBinding.mintempval.text = wargs.min.toString()+" \u2109"
        wBinding.maxtempval.text = wargs.max.toString()+" \u2109"
        wBinding.wforcasteval.text = wargs.desp


    }

}