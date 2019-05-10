package com.verkoop.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.daimajia.swipe.SwipeLayout
import com.verkoop.R
import com.verkoop.adapter.ChatInboxAdapter
import com.verkoop.models.ChatInboxResponse
import com.verkoop.offlinechatdata.DbHelper
import kotlinx.android.synthetic.main.favourites_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*

class ArchivedChatActivity : AppCompatActivity(), ChatInboxAdapter.DeleteChatCallBack {

    override fun deleteChat(senderId: Int, receiverId: Int, itemId: Int, type: Int, adapterPosition: Int, swipe: SwipeLayout) {

    }

    private var dbHelper: DbHelper?=null
    private lateinit var chatInboxAdapter: ChatInboxAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favourites_activity)
        dbHelper= DbHelper()
        steAdapter()
        setOfflineData()

    }

    private fun steAdapter() {
        val layoutManager = LinearLayoutManager(this)
        rvFavouriteList.layoutManager = layoutManager
        chatInboxAdapter = ChatInboxAdapter(this, 1)
        rvFavouriteList.adapter = chatInboxAdapter
        tvHeaderLoc.text = getString(R.string.arch_chat)
        ivLeftLocation.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setOfflineData() {
        val chatInboxList = ArrayList<ChatInboxResponse>()
        val result = dbHelper!!.getArchiveList(1)
        for (i in result.indices) {
            val dataBean = ChatInboxResponse(
                    result[i]!!.is_archive,
                    result[i]!!.is_delete,
                    result[i]!!.chat_user_id!!,
                    result[i]!!.timestamp!!,
                    result[i]!!.types,
                    result[i]!!.unread_count,
                    result[i]!!.sender_id,
                    result[i]!!.receiver_id,
                    result[i]!!.item_id,
                    result[i]!!.message_id,
                    result[i]!!.username!!,
                    result[i]!!.profile_pic!!,
                    result[i]!!.message!!,
                    result[i]!!.user_id,
                    result[i]!!.is_sold,
                    result[i]!!.item_name!!,
                    result[i]!!.offer_status,
                    result[i]!!.url!!
            )
            chatInboxList.add(dataBean)
        }
        chatInboxAdapter.setData(chatInboxList)
        chatInboxAdapter.notifyDataSetChanged()
    }
}