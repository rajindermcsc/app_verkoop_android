package com.verkoop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.verkoop.R
import com.verkoop.models.ChatData
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.chat_text_left_row.*
import kotlinx.android.synthetic.main.chat_text_right_row.*


class ChatAdapter(private  val context:Context):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val layoutInflater:LayoutInflater= LayoutInflater.from(context)
    private var chatHistoryList= ArrayList<ChatData>()
    val ROW_TEXT_RIGHT = 0
    val ROW_TEXT_LEFT = 1
    val ROW_IMAGE_RIGHT = 2
    val ROW_IMAGE_LERFT = 3


    override fun getItemViewType(position: Int): Int {
        return if(chatHistoryList[position].type==0&&chatHistoryList[position].senderId==Utils.getPreferencesString(context,AppConstants.USER_ID).toInt()){
            ROW_TEXT_RIGHT
        }else if(chatHistoryList[position].type==0&&chatHistoryList[position].senderId!=Utils.getPreferencesString(context,AppConstants.USER_ID).toInt()){
            ROW_TEXT_LEFT
        }else{
            ROW_TEXT_LEFT
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return if(viewType==ROW_TEXT_RIGHT){
            view=layoutInflater.inflate(R.layout.chat_text_right_row,parent,false)
            RightTextHolder(view)
        }else{
            view=layoutInflater.inflate(R.layout.chat_text_left_row,parent,false)
            LeftTextHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return chatHistoryList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(chatHistoryList[position].type==0&&chatHistoryList[position].senderId==Utils.getPreferencesString(context,AppConstants.USER_ID).toInt()){
            (holder as RightTextHolder).bind(chatHistoryList[position])
        }else{
         //   (holder as LeftTextHolder).bindLeft(chatHistoryList[position])
        }
    }

    class LeftTextHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView),LayoutContainer{
        fun bindLeft(chatData: ChatData) {
            tvLeftMssg.text=chatData.message
        }

    }

    class RightTextHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView),LayoutContainer{
        fun bind(chatData: ChatData) {
            tvRightMssg.text=chatData.message
        }

    }

    fun setData(chatList: ArrayList<ChatData>) {
        chatHistoryList=chatList
    }

}