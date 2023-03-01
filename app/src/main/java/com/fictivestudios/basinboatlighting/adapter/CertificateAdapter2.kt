package com.fictivestudios.basinboatlighting.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fictivestudios.basinboatlighting.databinding.ImagecardBinding
import com.fictivestudios.basinboatlighting.models.profile.LicenseImage

class CertificateAdapter2(
    var context: Context,
    var liscensCertificate: ArrayList<LicenseImage>,
    val mItemClickListener: CertificateAdapter2.ItemClickListener
    ) : RecyclerView.Adapter<CertificateViewHolder2>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateViewHolder2 {

        val certificates =
            ImagecardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CertificateViewHolder2(certificates,mItemClickListener)

    }
    interface ItemClickListener {
        fun onItemClick(position: Int)

    }
    override fun onBindViewHolder(holder: CertificateViewHolder2, position: Int) {

         holder.imagecardBinding.delgoal.visibility = View.GONE
        holder.bind(liscensCertificate.get(position))

    }

    override fun getItemCount(): Int {
        return liscensCertificate.size
    }
}

class CertificateViewHolder2(
    val imagecardBinding: ImagecardBinding, val listner: CertificateAdapter2.ItemClickListener
) :
    RecyclerView.ViewHolder(imagecardBinding.root) {


    @SuppressLint("CheckResult")
    fun bind(item: LicenseImage) {

        Glide.with(itemView.context).load(item.license_image).into(imagecardBinding.certificate)
//        /*imagecardBinding.delgoal.setOnClickListener {
//          listner.onItemClick(item.id!!)
//
//        }*/
    }





}