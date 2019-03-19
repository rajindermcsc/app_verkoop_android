package com.verkoop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoop.R
import kotlinx.android.extensions.LayoutContainer


class CoinListAdapter(private val context: Context,private val rvCoinList:RecyclerView):RecyclerView.Adapter<CoinListAdapter.ViewHolder>(){
    private val minflater:LayoutInflater=LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
       val view=minflater.inflate(R.layout.coin_row,parent,false)
        val params = rvCoinList.layoutParams
        params.width = rvCoinList.width / 3
        params.height = params.width
        view.layoutParams = params
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 9
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        //val modal=bind
    }

    inner class ViewHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView),LayoutContainer{

    }

}