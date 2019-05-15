package com.verkoop.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.daimajia.swipe.SwipeLayout
import com.github.nkzawa.socketio.client.Ack
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.verkoop.R
import com.verkoop.VerkoopApplication
import com.verkoop.adapter.ChatInboxAdapter
import com.verkoop.models.ChatInboxResponse
import com.verkoop.offlinechatdata.DbHelper
import com.verkoop.utils.AppConstants
import kotlinx.android.synthetic.main.favourites_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import org.json.JSONException
import org.json.JSONObject

class ArchivedChatActivity : AppCompatActivity(), ChatInboxAdapter.DeleteChatCallBack {
    private val socket: Socket? = VerkoopApplication.getAppSocket()
    private var chatInboxList = ArrayList<ChatInboxResponse>()
    override fun deleteChat(senderId: Int, receiverId: Int, itemId: Int, type: Int, adapterPosition: Int, swipe: SwipeLayout) {
        if(socket!!.connected()) {
            setArchiveChatEvent(senderId, receiverId, itemId, adapterPosition, swipe)
        }
    }

    private var dbHelper: DbHelper?=null
    private lateinit var chatInboxAdapter: ChatInboxAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favourites_activity)
        dbHelper= DbHelper()
        steAdapter()
        if(socket!!.connected()) {
            setOfflineData()
        }
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
         chatInboxList = ArrayList<ChatInboxResponse>()
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
                    result[i]!!.url!!,
                    result[i]!!.offer_price!!,
                    result[i]!!.item_price!!
            )
            chatInboxList.add(dataBean)
        }
        chatInboxAdapter.setData(chatInboxList)
        chatInboxAdapter.notifyDataSetChanged()
    }

    private fun setArchiveChatEvent(senderId: Int, receiverId: Int, itemId: Int, adapterPosition: Int, swipe: SwipeLayout) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id",senderId)
            jsonObject.put("receiver_id", receiverId)
            jsonObject.put("item_id", itemId)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            socket?.emit(AppConstants.UN_ARCHIVE_CHAT_EVENT, jsonObject, Ack {
                Log.e("<<<Response>>>", Gson().toJson(it[0]))
                val data = it[0] as JSONObject
                runOnUiThread {
                    if (data.getString("status") == "1") {
                        runOnUiThread {
                            dbHelper!!.unArchiveChat(senderId,receiverId,itemId)
                            chatInboxAdapter.mItemManger.removeShownLayouts(swipe)
                            chatInboxList.removeAt(adapterPosition)
                            chatInboxAdapter.notifyItemRemoved(adapterPosition)
                            chatInboxAdapter.notifyItemRangeChanged(adapterPosition, chatInboxList.size)
                        }
                    } else {

                    }
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}