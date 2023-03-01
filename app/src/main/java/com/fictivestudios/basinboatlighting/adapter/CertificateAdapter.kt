package com.fictivestudios.basinboatlighting.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fictivestudios.basinboatlighting.databinding.ImagecardBinding

class CertificateAdapter(
    var context: Context,
    // var liscensCertificate: ArrayList<Uri>,
    var liscensCertificate: ArrayList<String>,
    val mItemClickListener: ItemClickListener
) : RecyclerView.Adapter<CertificateViewHolder>() {

    interface ItemClickListener {
        fun onItemClick(id: Int, pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateViewHolder {

        val certificate =
            ImagecardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CertificateViewHolder(certificate, mItemClickListener)

    }

    override fun onBindViewHolder(holder: CertificateViewHolder, position: Int) {


        holder.bind(liscensCertificate.get(position))


    }

    override fun getItemCount(): Int {
        return liscensCertificate.size
    }
}

class CertificateViewHolder(
    val imagecardBinding: ImagecardBinding,
    val listner: CertificateAdapter.ItemClickListener
) :
    RecyclerView.ViewHolder(imagecardBinding.root) {


    @SuppressLint("CheckResult")
    fun bind(item: String) {

        Glide.with(itemView.context).load(item).into(imagecardBinding.certificate)

        imagecardBinding.delgoal.setOnClickListener {

            listner.onItemClick(1, adapterPosition)
        }

    }


}