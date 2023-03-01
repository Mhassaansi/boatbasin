package com.fictivestudios.basinboatlighting.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.databinding.RegisterBinding
import com.fictivestudios.basinboatlighting.utils.Titlebar


class RegistrationActivity : BaseActivity() {

    private lateinit var registerBinding: RegisterBinding
    private lateinit var navController: NavController

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        registerBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val nvHostFragment = supportFragmentManager.findFragmentById(R.id.nav_registration) as NavHostFragment
        navController = nvHostFragment.findNavController()

if(checkPermissions()){

}
        else{
            requestPermissions()
        }


    }

    override fun setMainFrameLayoutID() {

    }


    fun mainHideTitle() {
        registerBinding.titlebar.visibility = View.GONE
    }

    fun mainShowTitle() {
        registerBinding.titlebar.visibility = View.VISIBLE
    }

    fun getTitlebarRegister(): Titlebar {
        return registerBinding.titlebar
    }

    override fun onBackPressed() {
        when (navController.currentDestination?.id) {

             R.id.loginScreen -> {
                navController.navigate(R.id.exitDialog2)

            }
            else -> navController.popBackStack()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


            }
            else{
              //  showSnackBar("Open the Setting", this)

            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermissions() {

        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 100)

    }

}