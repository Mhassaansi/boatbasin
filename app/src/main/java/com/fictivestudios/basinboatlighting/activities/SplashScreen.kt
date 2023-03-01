package com.fictivestudios.basinboatlighting.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.tafcha.Utils.PreferenceUtils
//import dagger.hilt.android.AndroidEntryPoint
//
//
//@AndroidEntryPoint
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        PreferenceUtils.init(this)


        if(PreferenceUtils.getBoolean("isLogin")){
            Handler().postDelayed({

                val intent = Intent(this,HomeActivity::class.java)
                startActivity(intent)
                finish()


            },3000)

        }
        else{

            Handler().postDelayed({

                val intent = Intent(this,RegistrationActivity::class.java)
                startActivity(intent)
                finish()

            },3000)

        }








    }
}