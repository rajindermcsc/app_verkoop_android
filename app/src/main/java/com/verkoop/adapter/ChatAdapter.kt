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

import kotlinx.android.synthetic.main.chat_offer_left_row.*
import kotlinx.android.synthetic.main.chat_offer_right_row.*
import kotlinx.android.synthetic.main.chat_text_left_row.*
import kotlinx.android.synthetic.main.chat_text_right_row.*


class ChatAdapter(private val context:Context ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var chatHistoryList = ArrayList<ChatData>()
    val ROW_TEXT_RIGHT = 0
    val ROW_TEXT_LEFT = 1
    val ROW_IMAGE_RIGHT = 2
    val ROW_IMAGE_LERFT = 3
    val ROW_OFFER_RIGHT = 4
    val ROW_OFFER_LERFT = 5


    override fun getItemViewType(position: Int): Int {
        return if (chatHistoryList[position].type == 0 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_TEXT_RIGHT
        } else if (chatHistoryList[position].type == 0 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_TEXT_LEFT
        } else if (chatHistoryList[position].type == 2 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_OFFER_RIGHT
        } else if (chatHistoryList[position].type == 2 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_OFFER_LERFT
        } else if (chatHistoryList[position].type == 3 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_OFFER_RIGHT
        } else if (chatHistoryList[position].type == 3 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_OFFER_LERFT
        } else if (chatHistoryList[position].type == 4 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_OFFER_RIGHT
        } else if (chatHistoryList[position].type == 4 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_OFFER_LERFT
        }else if (chatHistoryList[position].type == 5 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_OFFER_RIGHT
        } else if (chatHistoryList[position].type == 5 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            ROW_OFFER_LERFT
        } else {
            ROW_TEXT_LEFT
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        when (viewType) {
            ROW_TEXT_RIGHT -> {
                view = layoutInflater.inflate(R.layout.chat_text_right_row, parent, false)
                return RightTextHolder(view)
            }
            ROW_TEXT_LEFT -> {
                view = layoutInflater.inflate(R.layout.chat_text_left_row, parent, false)
                return LeftTextHolder(view)
            }
            ROW_OFFER_RIGHT -> {
                view = layoutInflater.inflate(R.layout.chat_offer_right_row, parent, false)
                return RightOfferHolder(view)
            }
            ROW_OFFER_LERFT -> {
                view = layoutInflater.inflate(R.layout.chat_offer_left_row, parent, false)
                return LeftOfferHolder(view)
            }
            else -> {
                view = layoutInflater.inflate(R.layout.chat_text_left_row, parent, false)
                return LeftTextHolder(view)
            }
        }
    }


    override fun getItemCount(): Int {
        return chatHistoryList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (chatHistoryList[position].type == 0 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as RightTextHolder).bind(chatHistoryList[position])
        } else if (chatHistoryList[position].type == 0 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as LeftTextHolder).bindLeft(chatHistoryList[position])
        } else if (chatHistoryList[position].type == 2 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as RightOfferHolder).bind(chatHistoryList[position])
        } else if (chatHistoryList[position].type == 2 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as LeftOfferHolder).bindLeft(chatHistoryList[position])
        } else if (chatHistoryList[position].type == 3 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as RightOfferHolder).bind(chatHistoryList[position])
        } else if (chatHistoryList[position].type == 3 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as LeftOfferHolder).bindLeft(chatHistoryList[position])
        } else if (chatHistoryList[position].type == 4 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as RightOfferHolder).bind(chatHistoryList[position])
        } else if (chatHistoryList[position].type == 4 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as LeftOfferHolder).bindLeft(chatHistoryList[position])
        } else if (chatHistoryList[position].type == 5 && chatHistoryList[position].senderId == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as RightOfferHolder).bind(chatHistoryList[position])
        } else if (chatHistoryList[position].type == 5 && chatHistoryList[position].senderId != Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
            (holder as LeftOfferHolder).bindLeft(chatHistoryList[position])
        } else {
            (holder as LeftTextHolder).bindLeft(chatHistoryList[position])
        }
    }

    inner class LeftTextHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bindLeft(chatData: ChatData) {
            tvLeftMssg.text = chatData.message
            tvLeftTime.text = Utils.setDate(chatData.timeStamp)
        }

    }

  inner  class RightTextHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(chatData: ChatData) {
            tvRightMssg.text = chatData.message
            tvRightTime.text = Utils.setDate(chatData.timeStamp)

        }

    }

   inner class RightOfferHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(chatData: ChatData) {
            if (chatData.type == 2) {
                tvRightOfferMssg.text = "MADE AN OFFER"
            } else if (chatData.type == 3) {
                tvRightOfferMssg.text = "ACCEPTED OFFER"
            } else if (chatData.type == 4) {
                tvRightOfferMssg.text = "DECLINED OFFER"
            }else if (chatData.type == 5) {
                tvRightOfferMssg.text = "CANCELLED OFFER"
            }
            tvRightPrice.text = StringBuilder().append("$").append(chatData.message)
            tvRightOfferTime.text = Utils.setDate(chatData.timeStamp)
        }
    }

    inner class LeftOfferHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bindLeft(chatData: ChatData) {
            if (chatData.type == 2) {
                tvLeftOfferMssg.text = "MADE AN OFFER"
            } else if (chatData.type == 3) {
                tvLeftOfferMssg.text = "ACCEPTED OFFER"
            } else if (chatData.type == 4) {
                tvLeftOfferMssg.text = "DECLINED OFFER"
            } else if (chatData.type == 5) {
                tvLeftOfferMssg.text = "CANCELLED OFFER"
            }
            tvLeftOfferPrice.text = StringBuilder().append("$").append(chatData.message)
            tvLeftOfferTime.text = Utils.setDate(chatData.timeStamp)
        }

    }

    fun setData(chatList: ArrayList<ChatData>) {
        chatHistoryList = chatList
    }

}