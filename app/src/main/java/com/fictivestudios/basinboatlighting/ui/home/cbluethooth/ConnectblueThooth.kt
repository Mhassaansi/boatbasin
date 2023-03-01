package com.fictivestudios.basinboatlighting.ui.home.cbluethooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommercemvvmpractice2.utilities.extensions.showToast
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.activities.HomeActivity.Companion.navController
import com.fictivestudios.basinboatlighting.activities.RegistrationActivity
import com.fictivestudios.basinboatlighting.adapter.BlueThoothAdapter
import com.fictivestudios.basinboatlighting.base.BaseFragment
import com.fictivestudios.basinboatlighting.databinding.ConnectbluethoothBinding
import com.fictivestudios.basinboatlighting.utils.Titlebar
import com.fictivestudios.basinboatlighting.utils.resizeDialogView
import com.fictivestudios.getmefit.Networking.ApiService
import com.fictivestudios.getmefit.data.response.models.CommonResponse
import com.fictivestudios.getmefit.uitilites.Constants
import com.fictivestudios.getmefit.uitilites.Constants.Companion.BLUETHOOTHGATT
import com.fictivestudios.tafcha.Utils.PreferenceData
import com.fictivestudios.tafcha.Utils.PreferenceUtils
import com.fictivestudios.tafcha.networkSetup.callhandler.CallHandler
import com.fictivestudios.tafcha.networkSetup.retrofitsetup.RetrofitSetup
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.*


class ConnectblueThooth : BaseFragment(), BlueThoothAdapter.ItemClickListener {

    private lateinit var connectbluethoothBinding: ConnectbluethoothBinding
    val PERMISSION_REQUEST_CODE = 3

    override fun setTitlebar(titlebar: Titlebar) {
    }

    var devicesList = ArrayList<BluetoothDevice>()


    var mBluetoothDevice: BluetoothDevice? = null

    private val bluetoothAdapter: BluetoothAdapter by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager =
            getActivityContext?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private var mScanning: Boolean = false

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        connectbluethoothBinding =
            DataBindingUtil.inflate(inflater, R.layout.connectbluethooth, container, false)


        if (PreferenceData.retrieveProfileData(requireContext()).is_subscribed == 0) {
            subscribeDialog()
        }


        PreferenceUtils.saveBoolean("lb", false)
        PreferenceUtils.saveBoolean("fb", false)
        PreferenceUtils.saveBoolean("nb", false)


        getActivityContext?.hideButtonHome()
        getActivityContext?.hideBttomBar()



        init()

        connectbluethoothBinding.btonoff.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                PreferenceUtils.saveBoolean("btONOFF", true)

                connectbluethoothBinding.switchonoff.text = "ON"

                if(checkPermissions())
                {
                   requestPermission()
                }
                else{
                    setDeviceBluetoothDiscoverable()
                    scanLeDevice(true)

                }





            } else {

                connectbluethoothBinding.switchonoff.text = "OFF"
                bluetoothAdapter.isEnabled == false
                scanLeDevice(false)
                if (!devicesList.isNullOrEmpty()) {
                    devicesList.clear()
                    connectbluethoothBinding.btdevList.adapter?.notifyDataSetChanged()
                } else {

                }


            }

        }






        return connectbluethoothBinding.root


    }

    private fun subscribeDialog() {

        var dialog = Dialog(context as Activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.subscribe_dialog)

        resizeDialogView(dialog, 70, requireActivity())
        dialog.show()


        var yes: Button? = dialog.findViewById<Button>(R.id.btnyes)
        var no: Button? = dialog.findViewById<Button>(R.id.btnno)

        yes?.setOnClickListener {

            dialog.dismiss()
            logout()

        }

        no?.setOnClickListener {

            dialog.dismiss()

            findNavController().navigate(R.id.inAppPurchaseFragment)


        }


    }


    private fun logout() {

        lifecycleScope.launch {
            if (Constants.isNetworkConnected(requireActivity(), true)) {
                try {

                    RetrofitSetup().callApi(requireActivity(),
                        true,
                        false,
                        "${PreferenceUtils.getString("token")}",
                        object : CallHandler<Response<CommonResponse>> {
                            override suspend fun sendRequest(apiInterFace: ApiService): Response<CommonResponse> {
                                return apiInterFace.logout()
                            }

                            override fun success(response: Response<CommonResponse>) {
                                if (response.body()?.status == 1) {
                                    PreferenceData.clearPreference(requireActivity())
                                    PreferenceUtils.saveBoolean("isLogin", false)
                                    PreferenceUtils.saveBoolean("btONOFF", false)
                                    PreferenceUtils.saveBoolean("lb", false)
                                    PreferenceUtils.saveBoolean("fb", false)
                                    PreferenceUtils.saveBoolean("nb", false)
                                    PreferenceUtils.saveString("token", null)
                                    showToast(
                                        response.body()?.message.toString(),
                                        requireActivity()
                                    )
                                    startActivity(Intent(context, RegistrationActivity::class.java))
                                    getActivityContext?.finish()
                                } else {
                                    showToast(
                                        response.body()?.message.toString(),
                                        requireActivity()
                                    )
                                }
                            }

                            override fun error(message: String) {
                                RetrofitSetup().hideLoader()
                                //  showToast("Error",requireActivity())
                            }

                        }
                    )


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun init() {

        if (PreferenceUtils.getBoolean("btONOFF")) {

            connectbluethoothBinding.btonoff.isChecked = true


           if (checkPermissions()) {
               requestPermission()

        } else {
               setDeviceBluetoothDiscoverable()
               if (bluetoothAdapter.isEnabled) {
                   scanLeDevice(true)
               }

       }

             //allowLocationDetectionPermissions()



        } else {
            connectbluethoothBinding.switchonoff.text = "OFF"
            connectbluethoothBinding.btonoff.isChecked = false
        }

    }


//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String?>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
//                scanLeDevice(true)
//            } else {
//                showSnackBar("Open Settings Allow Search Nearby Devices", getActivityContext)
//
//            }
//        }
//
//    }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            FINE_LOCATION_PERMISSION_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                    scanLeDevice(true)
                } else {
                    showSnackBar("Open the Setting", getActivityContext)
                }
                return
            }
        }
    }


//    @RequiresApi(Build.VERSION_CODES.S)
//    private fun allowLocationDetectionPermissions() {
//        if (ContextCompat.checkSelfPermission(
//                getActivityContext!!,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            )
//            == PackageManager.PERMISSION_DENIED
//        ) {
//            ActivityCompat.requestPermissions(
//                getActivityContext!!,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.BLUETOOTH_CONNECT), FINE_LOCATION_PERMISSION_REQUEST
//            )
//        }
//
//    }

    companion object {
        private const val FINE_LOCATION_PERMISSION_REQUEST = 1001
    }


    @SuppressLint("MissingPermission")
    private fun scanLeDevice(enable: Boolean) {
        when (enable) {
            true -> {
                // Stops scanning after a pre-defined scan period.
                Handler().postDelayed({
                    mScanning = false
                    bluetoothAdapter.bluetoothLeScanner?.stopScan(mLeScanCallback)



                    if (!devicesList.isNullOrEmpty()) {
                        val dummyList = ArrayList<BluetoothDevice>(devicesList)


                        for (item in dummyList) {

                            if (item.name.isNullOrEmpty()) {
                                devicesList.remove(item)
                            }

                        }


                        val adapter = BlueThoothAdapter(getActivityContext!!, devicesList, this)
                        connectbluethoothBinding.btdevList.apply {

                            layoutManager = LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.VERTICAL, false
                            )
                            connectbluethoothBinding.btdevList.adapter = adapter
                            connectbluethoothBinding.btdevList.adapter!!.notifyDataSetChanged()
                        }

                        //  connectbluethoothBinding.btdevList.adapter = adapter
                        /// adapter.notifyDataSetChanged()

                    }


                }, 4000)
                mScanning = true
                bluetoothAdapter.bluetoothLeScanner?.startScan(mLeScanCallback)

            }
            else -> {
                mScanning = false
                bluetoothAdapter.bluetoothLeScanner?.stopScan(mLeScanCallback)
            }
        }
    }


    private var mLeScanCallback: ScanCallback =
        object : ScanCallback() {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)


                result?.device?.let { devicesList.add(it) }
                //   showToast(devicesList.toString(),getActivityContext)

            }

            override fun onBatchScanResults(results: List<ScanResult?>?) {
                super.onBatchScanResults(results)


                Log.w("mylog", "device")


            }


        }

    private val gattCallback = object : BluetoothGattCallback() {

        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val deviceAddress = gatt.device.address

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.w("mylog", "Successfully connected to $deviceAddress")




                    Handler(Looper.getMainLooper()).post {
                        getActivityContext?.runOnUiThread {
                            showToast("Connected Successfully", getActivityContext)
                        }

                        val ans: Boolean = gatt.discoverServices()


                        BLUETHOOTHGATT = gatt

                        try {
                            val action =
                                ConnectblueThoothDirections.actionConnectblueThoothToHomeFragment()
                            navController.navigate(action)
                            //findNavController().navigate(action)
                        } catch (e: Exception) {

                        }






                        Log.d("mylog", "Discover Services started: $ans")
                    }


                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.w("mylog", "Successfully disconnected from $deviceAddress")
                    gatt.close()
                }
            } else {
                Log.w("mylog", "Error $status encountered for $deviceAddress! Disconnecting...")
                Toast.makeText(
                    getActivityContext,
                    "Error $status encountered for $deviceAddress! Disconnecting...",
                    Toast.LENGTH_SHORT
                ).show()
                gatt.close()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                for (gattService in gatt!!.services) {
                    Log.i(
                        "mylog",
                        "Service UUID Found: " + gattService.uuid.toString() + "name: " + gattService.type.toString()
                    )
                }

            }


        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
        }


    }


    private fun setDeviceBluetoothDiscoverable() {
        //no need to request bluetooth permission if  discoverability is requested
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        discoverableIntent.putExtra(
            BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
            0
        )// 0 to keep it always discoverable
        startActivity(discoverableIntent)
    }

    @SuppressLint("MissingPermission")
    private fun connectwithDevice(deviceAddress: BluetoothDevice) {
        with(deviceAddress) {
            android.util.Log.w("mylog", "Connecting to $deviceAddress")
            Toast.makeText(getActivityContext, "Connecting to $deviceAddress", Toast.LENGTH_SHORT)
                .show()
            this.connectGatt(getActivityContext, false, gattCallback)
            //navController.navigate(R.id.homeFragment)


        }
    }

    var serviceUUIDsList: List<UUID> = ArrayList()
    var characteristicUUIDsList: List<UUID> = ArrayList()
    var descriptorUUIDsList: List<UUID> = ArrayList()

    @SuppressLint("MissingPermission")
    private fun initScanning(bleScanner: BluetoothLeScanner) {

        bleScanner.startScan(getScanCallback())
    }

    private fun getScanCallback(): ScanCallback? {
        return object : ScanCallback() {
            override fun onScanResult(callbackType: Int, scanResult: ScanResult) {
                super.onScanResult(callbackType, scanResult)

                serviceUUIDsList = getServiceUUIDsList(scanResult)
                Log.i("mylog", "Service UUID Found: " + serviceUUIDsList.toString())

            }
        }
    }

    private fun getServiceUUIDsList(scanResult: ScanResult): List<UUID> {
        val parcelUuids = scanResult.scanRecord!!.serviceUuids
        val serviceList: MutableList<UUID> = ArrayList()
        for (i in parcelUuids.indices) {
            val serviceUUID = parcelUuids[i].uuid
            if (!serviceList.contains(serviceUUID)) serviceList.add(serviceUUID)
        }
        return serviceList
    }

    override fun onItemClick(deviceAddress: BluetoothDevice) {
        mBluetoothDevice = deviceAddress
        connectwithDevice(deviceAddress)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun  checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            getActivityContext!!,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_DENIED
//                &&
//                ActivityCompat.checkSelfPermission(
//            getActivityContext!!,
//            Manifest.permission.BLUETOOTH_SCAN
//        ) == PackageManager.PERMISSION_GRANTED

    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            getActivityContext!!,
            arrayOf(
               Manifest.permission.BLUETOOTH_CONNECT,
               // Manifest.permission.BLUETOOTH_SCAN
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    fun showSnackBar(message: String?, activity: Activity?) {
        if (null != activity && null != message) {
            Snackbar.make(
                activity.findViewById(android.R.id.content),
                message, Snackbar.LENGTH_SHORT
            ).setAction(message, View.OnClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                startActivity(intent)
            }).show()
        }
    }

}