package com.verkoop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoop.R
import kotlinx.android.extensions.LayoutContainer


class ChatAdapter(context:Context):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val layoutInflater:LayoutInflater= LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view=layoutInflater.inflate(R.layout.chat_row,parent,false)
        return ReciceMessageHolder(view)
    }

    override fun getItemCount(): Int {
        return 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class ReciceMessageHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView),LayoutContainer{

    }

}