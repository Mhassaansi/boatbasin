package com.fictivestudios.basinboatlighting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.fictivestudios.basinboatlighting.R
import com.fictivestudios.basinboatlighting.databinding.HomecardBinding
import com.fictivestudios.basinboatlighting.models.HomeCardList
import com.fictivestudios.tafcha.Utils.PreferenceUtils


class HomeAdapter(
    var context: Context,
    var homeList: ArrayList<HomeCardList>,
    val mItemClickListener: ItemClickListener

) : RecyclerView.Adapter<HomeViewHolder>() {

    interface ItemClickListener {
        fun onItemClick(name: String, status: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {

        val homeCard = HomecardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return HomeViewHolder(homeCard, mItemClickListener)

    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val item = homeList.get(position)



        holder.bind(item)


    }

    override fun getItemCount(): Int {


        return homeList.size


    }


}


class HomeViewHolder(
    val homecardBinding: HomecardBinding,
    val listner: HomeAdapter.ItemClickListener
) :
    RecyclerView.ViewHolder(homecardBinding.root) {



    fun bind(item: HomeCardList) {


        homecardBinding.homecardname1.text = item.equipmentName
        homecardBinding.homecardname2.text = item.equipmentDescription
        homecardBinding.homecardimg.setImageResource(item.equipmentImage)


        if(item.equipmentName.equals("brightBird") && PreferenceUtils.getBoolean("mainLight")){
            if(PreferenceUtils.getBoolean("lb")){

                homecardBinding.homeswitch.isChecked = true
            }
        }
            else if(item.equipmentName.equals("flashyBird") && PreferenceUtils.getBoolean("flash")){

            if(PreferenceUtils.getBoolean("fb")){

                homecardBinding.homeswitch.isChecked = true
            }
            }
            else if(item.equipmentName.equals("navBird") && PreferenceUtils.getBoolean("navBird")){
            if(PreferenceUtils.getBoolean("nb")){
                homecardBinding.homeswitch.isChecked = true
            }
            }
        homecardBinding.homeswitch.setOnCheckedChangeListener { _, isChecked ->


            if (isChecked) {



                listner.onItemClick(item.equipmentName, "On")
                homecardBinding.homecardname3.text = "On"

            } else {
                listner.onItemClick(item.equipmentName, "Off")
                homecardBinding.homecardname3.text = " Off"

            }
        }
        if (item.equipmentName.equals("loudBird") && PreferenceUtils.getBoolean("loudswitch")) {

            homecardBinding.homeswitch.visibility = View.GONE
            homecardBinding.radioId.visibility = View.VISIBLE
            homecardBinding.radioId.setBackgroundResource(R.drawable.horn)


            homecardBinding.radioId.setOnTouchListener(OnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                      if(homecardBinding.homecardname3.text =="Off")
                      {
                          homecardBinding.homecardname3.text = "On"
                          listner.onItemClick(
                              item.equipmentName,
                              "On"
                          )
                      }
                    }
                    MotionEvent.ACTION_UP -> {

                        if(homecardBinding.homecardname3.text =="On")
                        {
                            homecardBinding.homecardname3.text = "Off"
                            listner.onItemClick(item.equipmentName, "Off")
                        }

                    }
                }
                true
            })





        }


    }


}