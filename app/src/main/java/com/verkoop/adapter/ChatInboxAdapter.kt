package com.verkoop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoop.R
import kotlinx.android.extensions.LayoutContainer


class ChatInboxAdapter(context:Context):RecyclerView.Adapter<ChatInboxAdapter.ViewHolder>(){
    private  var layoutInflater: LayoutInflater= LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val view=layoutInflater.inflate(R.layout.chat_inbox_row,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 0
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    inner class ViewHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView),LayoutContainer{

    }
}