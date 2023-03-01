package com.fictivestudios.basinboatlighting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fictivestudios.basinboatlighting.databinding.WeathercardBinding
import com.fictivestudios.basinboatlighting.models.weather.Daily
import com.fictivestudios.basinboatlighting.utils.getDayOfWeek
import com.fictivestudios.basinboatlighting.utils.kelivntocelsius

class WeatherAdapter(
    context: Context,
   var  dList: ArrayList<Daily>,
    var mItemClickListener: ItemClickListener
) :   RecyclerView.Adapter<WeatherViewHolder>(){

    interface ItemClickListener{
            fun onItemClick(dt:Long, susnet:Long,sunrise:Long, moonrise:Long, moonset:Long,
                            humidity:Int, wind_speed:Float,windDegree:String,min:Float,max:Float,dsp:String)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {

        val daily = WeathercardBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return WeatherViewHolder(daily,mItemClickListener)

    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {

       holder.bind(dList.get(position))

    }

    override fun getItemCount(): Int {

        return dList.size

    }
}

class WeatherViewHolder(val  wcardBinding:WeathercardBinding, val listner : WeatherAdapter.ItemClickListener) :
    RecyclerView.ViewHolder(wcardBinding.root){


    fun bind(daily: Daily){



          wcardBinding.dow.text = getDayOfWeek(daily.dt!!.toLong())
          wcardBinding.min.text = daily.temp?.min?.toInt()?.let { it.toString()+" \u2109"
            //  +"\u2103"
          }

          wcardBinding.max.text = daily.temp?.max?.toInt()?.let { it.toString()+" \u2109"
             // +"\u2103"
          }

        wcardBinding.wcard.setOnClickListener {
            listner.onItemClick(
                daily.dt!!.toLong(),
                daily.sunrise!!.toLong()
                ,daily.sunset!!.toLong()
                ,daily.moonrise!!.toLong()
                ,daily.moonset!!.toLong()
                ,daily.humidity!!.toInt()
                ,daily.wind_speed!!.toFloat()
                , daily.wind_deg!!.toString()
                ,daily.temp?.min!!.toFloat()
                ,daily.temp?.max!!.toFloat(),
                daily.weather?.get(0)?.description.toString()

            )


        }


    }


}