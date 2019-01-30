package com.verkoop.adapter

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoop.R
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by intel on 30-01-2019.
 */
class GalleryAdapter(context: Context):RecyclerView.Adapter<GalleryAdapter.ViewHolder>(){
    private var mInflater:LayoutInflater= LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val view=mInflater.inflate(R.layout.gallery_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return 0
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
       holder.bind()
    }

    inner class ViewHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView),LayoutContainer{
        fun bind() {

        }

    }

}