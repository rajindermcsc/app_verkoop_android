package com.verkoop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoop.R
import com.verkoop.models.ResultLocation
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.search_location_row.*


class LocationSearchAdapter(context: Context):RecyclerView.Adapter<LocationSearchAdapter.ViewHolder>(){
     var layoutInflater: LayoutInflater= LayoutInflater.from(context)
    private var searchResultList=ArrayList<ResultLocation>()
    private var type:String = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val view=layoutInflater.inflate(R.layout.search_location_row,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return searchResultList.size
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
      val modal=searchResultList[position]
        holder.bind(modal)
    }
    inner  class ViewHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView),LayoutContainer{
        fun bind(modal: ResultLocation) {
            tvNameLocation.text = modal.name
            if(!type.equals("search",ignoreCase = true)) {
                tvFullAddress.text = modal.vicinity
            }else{
                tvFullAddress.text = modal.formatted_address
            }
        }

    }

    fun setData(results: ArrayList<ResultLocation>, comingState: String) {
        searchResultList=results
        type=comingState
    }
}