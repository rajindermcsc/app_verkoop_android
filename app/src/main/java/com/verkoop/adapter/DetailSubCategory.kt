package com.verkoop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoop.R
import kotlinx.android.extensions.LayoutContainer


class DetailSubCategory(context:Context):RecyclerView.Adapter<DetailSubCategory.ViewHolder>(){
    private var mInflater:LayoutInflater= LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
       var view=mInflater.inflate(R.layout.details_sub_category,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return 10
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
       holder.bind()
    }
    inner class ViewHolder(override val containerView: View):RecyclerView.ViewHolder(containerView),LayoutContainer{
        fun bind() {

        }

    }
}