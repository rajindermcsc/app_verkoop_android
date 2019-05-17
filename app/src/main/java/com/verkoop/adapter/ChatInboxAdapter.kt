package com.verkoop.adapter

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl
import com.daimajia.swipe.interfaces.SwipeAdapterInterface
import com.daimajia.swipe.interfaces.SwipeItemMangerInterface
import com.daimajia.swipe.util.Attributes
import com.squareup.picasso.Picasso
import com.verkoop.R
import com.verkoop.activity.ArchivedChatActivity
import com.verkoop.activity.ChatActivity
import com.verkoop.activity.ChatInboxActivity
import com.verkoop.models.ChatInboxResponse
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.chat_inbox_row.*
import kotlinx.android.synthetic.main.chat_offer_right_row.*
import org.apache.commons.lang3.StringEscapeUtils


class ChatInboxAdapter(private val context: Context, private val chatInboxType: Int) : RecyclerView.Adapter<ChatInboxAdapter.ViewHolder>(), SwipeAdapterInterface, SwipeItemMangerInterface {
    private var chatInboxAllList = ArrayList<ChatInboxResponse>()
    private var layoutInflater: LayoutInflater = LayoutInflater.from(context)
    var mItemManger = SwipeItemRecyclerMangerImpl(this)
    private lateinit var deleteChatCallBack: DeleteChatCallBack

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe
    }

    override fun closeAllExcept(layout: SwipeLayout?) {

    }

    override fun setMode(mode: Attributes.Mode?) {

    }

    override fun closeAllItems() {

    }

    override fun removeShownLayouts(layout: SwipeLayout?) {

    }

    override fun getOpenItems(): MutableList<Int>? {
        return null
    }

    override fun isOpen(position: Int): Boolean {
        return false
    }

    override fun openItem(position: Int) {

    }

    override fun getMode(): Attributes.Mode? {
        return null
    }

    override fun getOpenLayouts(): MutableList<SwipeLayout>? {
        return null
    }

    override fun closeItem(position: Int) {

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = layoutInflater.inflate(R.layout.chat_inbox_row, parent, false)
        if (chatInboxType != 1) {
            deleteChatCallBack = context as ChatInboxActivity
        } else {
            deleteChatCallBack = context as ArchivedChatActivity
        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chatInboxAllList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = chatInboxAllList[position]
        holder.bind(data)
    }

    inner class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(data: ChatInboxResponse) {
            if (chatInboxType == 1) {
                tvArchive.visibility = View.GONE
                tvDelete.text = context.getString(R.string.un_arciver)
            } else {
                tvArchive.visibility = View.VISIBLE
                tvDelete.text = context.getString(R.string.delete)
            }
            mItemManger.bindView(itemView, adapterPosition)
            tvChatUserName.text = data.username
            tvProductName.text = data.item_name
            tvLastMssgTime.text = Utils.setDate(data.timestamp)
            if (!TextUtils.isEmpty(data.profile_pic)) {
                Picasso.with(context).load(AppConstants.IMAGE_URL + data.profile_pic)
                        .resize(720, 720)
                        .centerInside()
                        .error(R.mipmap.pic_placeholder)
                        .placeholder(R.mipmap.pic_placeholder)
                        .into(ivProfilePicChat)
            }
            if (!TextUtils.isEmpty(data.url)) {
                Picasso.with(context).load(AppConstants.IMAGE_URL + data.url)
                        .resize(720, 720)
                        .centerInside()
                        .error(R.mipmap.post_placeholder)
                        .placeholder(R.mipmap.post_placeholder)
                        .into(ivImageChat)
            }

            if (data.is_sold == 1) {
                tvSoldChat.visibility = View.VISIBLE
            } else {
                tvSoldChat.visibility = View.GONE
            }

            tvDelete.setOnClickListener {
                mItemManger.closeAllItems()
                deleteChatCallBack.deleteChat(data.sender_id, data.receiver_id, data.item_id, 0, adapterPosition, swipe)

            }
            tvArchive.setOnClickListener {
                mItemManger.closeAllItems()
                deleteChatCallBack.deleteChat(data.sender_id, data.receiver_id, data.item_id, 1, adapterPosition, swipe)
            }
            llParentChat.setOnClickListener {

                val intent = Intent(context, ChatActivity::class.java)
                if (data.sender_id == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
                    intent.putExtra(AppConstants.USER_ID, data.receiver_id)
                } else {
                    intent.putExtra(AppConstants.USER_ID, data.sender_id)
                }
                if (data.user_id == Utils.getPreferencesString(context, AppConstants.USER_ID).toInt()) {
                    intent.putExtra(AppConstants.IS_MY_PRODUCT, true)
                }
                intent.putExtra(AppConstants.USER_NAME, data.username)
                intent.putExtra(AppConstants.ITEM_ID, data.item_id)
                intent.putExtra(AppConstants.PROFILE_URL, data.profile_pic)
                intent.putExtra(AppConstants.PRODUCT_URL, data.url)
                intent.putExtra(AppConstants.PRODUCT_PRICE,data.item_price.toDouble())
                intent.putExtra(AppConstants.OFFERED_PRICE,data.offer_price.toDouble())
                intent.putExtra(AppConstants.IS_SOLD,data.is_sold)
                intent.putExtra(AppConstants.PRODUCT_NAME, data.item_name)
                context.startActivity(intent)

            }
            if (data.types == 2) {
                tvLastMssg.text  = "MADE AN OFFER"
            } else if (data.types  == 3) {
                tvLastMssg.text = "ACCEPTED OFFER"
            } else if (data.types  == 4) {
                tvLastMssg.text  = "DECLINED OFFER"
            }else if (data.types == 5) {
                tvLastMssg.text  = "CANCELLED OFFER"
            }else{
                tvLastMssg.text =  StringEscapeUtils.unescapeJava(data.message)
              //  tvLastMssg.text  = data.message
            }
            if (data.user_id==Utils.getPreferencesString(context,AppConstants.USER_ID).toInt()&&data.offer_status == 0) {
                tvOfferDes.text=StringBuilder().append("Offered you $").append(data.offer_price)
                tvStatus.visibility=View.GONE
            }else if(data.user_id!=Utils.getPreferencesString(context,AppConstants.USER_ID).toInt()&&data.offer_status == 0) {
                tvOfferDes.text=StringBuilder().append("You made an offer $").append(data.offer_price)
                tvStatus.visibility=View.GONE
            }else if(data.user_id==Utils.getPreferencesString(context,AppConstants.USER_ID).toInt()&&data.offer_status == 1) {
                tvOfferDes.text=StringBuilder().append("Offered you $").append(data.offer_price)
                tvStatus.visibility=View.VISIBLE
                tvStatus.text=context.getString(R.string.accepetd)
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.accept_offer))
            }else if(data.user_id!=Utils.getPreferencesString(context,AppConstants.USER_ID).toInt()&&data.offer_status == 1) {
                tvOfferDes.text=StringBuilder().append("You offered $").append(data.offer_price)
                tvStatus.visibility=View.VISIBLE
                tvStatus.text=context.getString(R.string.accepetd)
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.accept_offer))
            }else if(data.user_id==Utils.getPreferencesString(context,AppConstants.USER_ID).toInt()&&data.offer_status == 2) {
                tvOfferDes.text=StringBuilder().append("Offered you $").append(data.offer_price)
                tvStatus.visibility=View.VISIBLE
                tvStatus.text=context.getString(R.string.declined)
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            }else if(data.user_id!=Utils.getPreferencesString(context,AppConstants.USER_ID).toInt()&&data.offer_status == 2) {
                tvOfferDes.text=StringBuilder().append("You offered $").append(data.offer_price)
                tvStatus.visibility=View.VISIBLE
                tvStatus.text=context.getString(R.string.declined)
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            }else{
                llAcceptOffer.visibility=View.GONE
            }
        }
    }

    fun setData(chatInboxData: ArrayList<ChatInboxResponse>) {
        chatInboxAllList = chatInboxData
    }

    interface DeleteChatCallBack {
        fun deleteChat(senderId: Int, receiverId: Int, itemId: Int, type: Int, adapterPosition: Int, swipe: SwipeLayout)
    }
}