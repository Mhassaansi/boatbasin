package com.fictivestudios.basinboatlighting.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.base.BaseFragment

abstract class BaseActivity : AppCompatActivity() {


    var baseFragment: BaseFragment? = null
    var mainFrameLayoutID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    abstract fun setMainFrameLayoutID()

    fun replaceFragment(
        frag: BaseFragment,
        tag: String,
        isAddToBackStack: Boolean,
        animate: Boolean
    ) {

        baseFragment = frag
        val transaction = supportFragmentManager.beginTransaction()

        if (animate) {
//            transaction.setCustomAnimations(
//                R.anim.slide_in_right,
//                R.anim.slide_out_left,
//                R.anim.slide_in_left,
//                R.anim.slide_out_right
//            )
        }
        transaction.replace(mainFrameLayoutID, frag)

        if (isAddToBackStack) {
            transaction.addToBackStack(null).commit()
        } else {
            transaction.commitAllowingStateLoss()
        }
    }

    fun AddFragment(frag: BaseFragment, tag: String, isAddToBackStack: Boolean, animate: Boolean) {
        baseFragment = frag
        val transaction = supportFragmentManager.beginTransaction()

        if (animate) {
            //transaction.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out)
//            transaction.setCustomAnimations(
//                R.anim.slide_in_right,
//                R.anim.slide_out_left,
//                R.anim.slide_in_left,
//                R.anim.slide_out_right
//            )
        }
        transaction.add(mainFrameLayoutID, frag)

        if (isAddToBackStack) {
            transaction.addToBackStack(null).commit()
        } else {
            transaction.commitAllowingStateLoss()
        }
    }
}