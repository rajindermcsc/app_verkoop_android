package com.verkoop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoop.R
import kotlinx.android.extensions.LayoutContainer


class CarBodyTypeAdapter(context:Context,private val recyclerView:RecyclerView):RecyclerView.Adapter<CarBodyTypeAdapter.ViewHolder>(){
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val view=mLayoutInflater.inflate(R.layout.details_sub_category,parent,false)
        val params = view.layoutParams
        params.width = recyclerView.width / 3
        params.height = params.width
        view.layoutParams = params
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
      return 10
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {

    }

    inner class ViewHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView),LayoutContainer {

    }

}