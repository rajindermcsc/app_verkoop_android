package com.verkoop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.verkoop.R
import com.verkoop.models.DataBanner
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.all_banner_row.*


class BannerAdapter(private val context: Context):RecyclerView.Adapter<BannerAdapter.ViewHolder>(){
    private val mInflater:LayoutInflater= LayoutInflater.from(context)
    private var bannerList= ArrayList<DataBanner>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val view=mInflater.inflate(R.layout.all_banner_row,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bannerList.size
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val data=bannerList[position]
        holder.bind(data)
    }

    inner class ViewHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView!!),LayoutContainer{
        fun bind(data: DataBanner) {
            tvPurchaseData.text=StringBuffer().append(": ").append(Utils.convertDate("yyyy-MM-dd hh:mm:ss", data.updated_at, "MMMM dd, yyyy"))
            tvExpiredDate.text=StringBuffer().append(": ").append(Utils.dateDifference("yyyy-MM-dd hh:mm:ss",Utils.convertDate("yyyy-MM-dd hh:mm:ss", data.updated_at, "MMMM dd, yyyy"),data.day))
            Picasso.with(context).load(AppConstants.IMAGE_URL+data.image)
                    .resize(1024 , 1024)
                    .centerCrop()
                    .error(R.mipmap.post_placeholder)
                    .placeholder(R.mipmap.post_placeholder)
                    .into(ivProductImageHome)
        }

    }

    fun setData(data: ArrayList<DataBanner>) {
        bannerList=data
    }
}