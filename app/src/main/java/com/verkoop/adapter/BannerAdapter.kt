package com.verkoop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoop.R
import kotlinx.android.extensions.LayoutContainer



class BannerAdapter(context: Context):RecyclerView.Adapter<BannerAdapter.ViewHolder>(){
    private val mInflater:LayoutInflater= LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val view=mInflater.inflate(R.layout.all_banner_row,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
    }

    inner class ViewHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView),LayoutContainer{

    }
}