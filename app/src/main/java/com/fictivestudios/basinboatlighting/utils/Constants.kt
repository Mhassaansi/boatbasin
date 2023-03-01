package com.fictivestudios.getmefit.uitilites

import android.bluetooth.BluetoothGatt
import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast


class Constants {
    companion object {

        const val IS_USER = "is_user"
         var IS_LOGIN = false

        var BLUETHOOTHGATT : BluetoothGatt? = null


        var height:Int=0
        var weight:Int=0

        fun isValidEmail(str: String): Boolean{
            return android.util.Patterns.EMAIL_ADDRESS.matcher(str).matches()
        }

        fun isNetworkConnected(context: Context, showToast: Boolean):Boolean{
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cm.activeNetworkInfo == null) {
                if(showToast)
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                return false
            }
            return true
        }

    }
}