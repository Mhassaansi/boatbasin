package com.fictivestudios.basinboatlighting.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.activities.HomeActivity
import com.fictivestudios.basinboatlighting.activities.RegistrationActivity
import com.fictivestudios.basinboatlighting.databinding.Titlebarbinding

class Titlebar : RelativeLayout {

    var binding: Titlebarbinding? = null


    constructor(context: Context) : super(context) {
        initLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initLayout(context)
    }

    fun initLayout(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DataBindingUtil.inflate(inflater, R.layout.titlebar, this, true)
    }

    fun hideTitleBar() {
        resetTitlebar()
    }

    fun resetTitlebar() {
        binding?.rlTitlebarMainLayout?.visibility = View.GONE
    }


    fun setTitleregistartion1(getActivityContext: RegistrationActivity, title: String) {
        binding?.rlTitlebarMainLayout?.visibility = View.VISIBLE
        binding?.tvTitletext?.text = title
        binding?.tvTitle?.visibility = View.GONE
        binding?.BackCircle?.visibility = View.VISIBLE
        binding?.ivHelp?.visibility = View.GONE
        binding?.BackCircle!!.setOnClickListener {
            getActivityContext.onBackPressed()
        }

    }
    fun setTitleHome(getActivityContext: HomeActivity, title: String) {
        binding?.rlTitlebarMainLayout?.visibility = View.VISIBLE
        binding?.tvTitletext?.text = title
        binding?.tvTitle?.visibility = View.GONE
        binding?.BackCircle?.visibility = View.VISIBLE
        binding?.ivHelp?.visibility = View.GONE
        binding?.BackCircle!!.setOnClickListener {
            getActivityContext.onBackPressed()
        }

    }


}