package com.fictivestudios.basinboatlighting.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.databinding.BluethoothCardBinding

class BlueThoothAdapter(
    context: Context,
    deviceList: ArrayList<BluetoothDevice>,
    val mItemClickListener: ItemClickListener

): RecyclerView.Adapter<BlueThoothAdapter.DeviceViewHolder>() {

    private var devices = deviceList
    private var context = context
    interface ItemClickListener{
        fun onItemClick(position: BluetoothDevice)

    }
    inner class DeviceViewHolder(private val bluethoothCardBinding: BluethoothCardBinding,val listner: ItemClickListener):
        RecyclerView.ViewHolder(bluethoothCardBinding.root){
        @SuppressLint("MissingPermission")
        fun setData(device: BluetoothDevice){

            if (device.name.isNullOrEmpty())
            {
               bluethoothCardBinding.deviceName.text ="Unnamed Device"
            }
            else{
                bluethoothCardBinding.deviceName.text = device.name
            }
            bluethoothCardBinding.deviceNameStatus.text = "Not Connected"
        }


init {
    itemView.setOnClickListener {
        listner.onItemClick(position = devices[adapterPosition])
    }
}



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {


        val bData = BluethoothCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return DeviceViewHolder(bData,mItemClickListener)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.setData(devices.get(position))



    }

    override fun getItemCount(): Int {
        return devices.size
    }

}