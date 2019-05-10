package com.verkoop.activity


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.daimajia.swipe.SwipeLayout
import com.github.nkzawa.socketio.client.Ack
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.verkoop.R
import com.verkoop.VerkoopApplication
import com.verkoop.adapter.ChatInboxAdapter
import com.verkoop.fragment.BuyingFragment
import com.verkoop.fragment.ChatInboxFragment
import com.verkoop.fragment.SellingFragment
import com.verkoop.models.ChatInboxResponse
import com.verkoop.offlinechatdata.DbHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.chat_inbox_activity.*
import kotlinx.android.synthetic.main.toolbar_chat.*
import org.json.JSONException
import org.json.JSONObject


class ChatInboxActivity : AppCompatActivity(), ChatInboxAdapter.DeleteChatCallBack {
    private val socket: Socket? = VerkoopApplication.getAppSocket()
    private var chatInboxFragment: ChatInboxFragment? = null
    private var buyingFragment: BuyingFragment? = null
    private var sellingFragment: SellingFragment? = null
    private var fragmentList = ArrayList<Fragment>()
    private lateinit var chatInboxAdapter: ChatInboxAdapter
    private var chatInboxType: Int = 0
    private var dbHelper: DbHelper?=null
    private var chatInboxAllList = ArrayList<ChatInboxResponse>()
    private var adapterInboxAllList = ArrayList<ChatInboxResponse>()
    private val archivedChatList = ArrayList<ChatInboxResponse>()
    private val sellerList = ArrayList<ChatInboxResponse>()
    private val buyerList = ArrayList<ChatInboxResponse>()

    override fun deleteChat(senderId: Int, receiverId: Int, itemId: Int, type: Int, adapterPosition: Int,swipe: SwipeLayout) {
        if (Utils.isOnline(this)) {
          if(type==0){
              deleteChatDialogBox(senderId,receiverId,itemId,adapterPosition,swipe)
          }else if(type==1){
              setArchiveChatEvent(senderId,receiverId,itemId,adapterPosition,swipe)
          }
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }

    private fun setArchiveChatEvent(senderId: Int, receiverId: Int, itemId: Int, adapterPosition: Int, swipe: SwipeLayout) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id",senderId)
            jsonObject.put("receiver_id", receiverId)
            jsonObject.put("item_id", itemId)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            socket?.emit(AppConstants.ARCHIVE_CHAT_EVENT, jsonObject, Ack {
                Log.e("<<<Response>>>", Gson().toJson(it[0]))
                val data = it[0] as JSONObject
                runOnUiThread {
                    if (data.getString("status") == "1") {
                        runOnUiThread {
                            Utils.showToast(this, "offerCreated")
                           // DbHelper.deleteAllChatMessage(realm, unFriendFriendRequest.getUser_id(), unFriendFriendRequest.getFriend_id())
                          //  DbHelper.updateChatAllTable(realm, userId)
                            chatInboxAdapter.mItemManger.removeShownLayouts(swipe)
                            adapterInboxAllList.removeAt(adapterPosition)
                            chatInboxAdapter.notifyItemRemoved(adapterPosition)
                            chatInboxAdapter.notifyItemRangeChanged(adapterPosition, adapterInboxAllList.size)
                        }
                    } else {

                    }
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun deleteChatDialogBox(senderId: Int, receiverId: Int, itemId: Int, adapterPosition: Int, swipe: SwipeLayout) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id",senderId)
            jsonObject.put("receiver_id", receiverId)
            jsonObject.put("item_id", itemId)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            socket?.emit(AppConstants.DELETE_CHAT_EVENT, jsonObject, Ack {
                Log.e("<<<Response>>>", Gson().toJson(it[0]))
                val data = it[0] as JSONObject
                runOnUiThread {
                    if (data.getString("status") == "1") {
                        runOnUiThread {
                            chatInboxAdapter.mItemManger.removeShownLayouts(swipe)
                            adapterInboxAllList.removeAt(adapterPosition)
                            chatInboxAdapter.notifyItemRemoved(adapterPosition)
                            chatInboxAdapter.notifyItemRangeChanged(adapterPosition, adapterInboxAllList.size)

                        }
                    } else {

                    }
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_inbox_activity)
        dbHelper=DbHelper()
        chatInboxFragment = ChatInboxFragment.newInstance()
        buyingFragment = BuyingFragment.newInstance()
        sellingFragment = SellingFragment.newInstance()
        fragmentList.add(chatInboxFragment!!)
        fragmentList.add(buyingFragment!!)
        fragmentList.add(sellingFragment!!)
        setData()
        setAdapter()
        if (Utils.isOnline(this)) {
            getChatHistory()
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            setOfflineData()
        }
    }

    private fun setOfflineData() {
        val  chatInboxList= ArrayList<ChatInboxResponse>()
        val result = dbHelper!!.getAddChatList()
        for (i in result.indices) {
            val dataBean =ChatInboxResponse(
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
        getArchiveLIst(chatInboxList)
    }

    private fun getChatHistory() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put(AppConstants.SENDER_ID, Utils.getPreferencesString(this, AppConstants.USER_ID))
            jsonObject.put(AppConstants.ITEM_ID, 0)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            socket?.emit(AppConstants.INBOX_LIST_EVENT, jsonObject, Ack {
                Log.e("<<<Response>>>", Gson().toJson(it[0]))
                val data = it[0] as JSONObject
                runOnUiThread {
                  val  chatInboxList= ArrayList<ChatInboxResponse>()
                    if (data.getString("status") == "1") {
                        runOnUiThread {
                            try {
                                val listdata = java.util.ArrayList<JSONObject>()
                                try {
                                    val chatHistory = data.getJSONArray("data")

                                    if (chatHistory != null) {
                                        for (i in 0 until chatHistory.length()) {
                                            listdata.add(chatHistory.getJSONObject(i))
                                        }
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }

                                for (data2 in listdata) {
                                    try {
                                        val inboxData = ChatInboxResponse(
                                                data2.getInt("is_archive"),
                                                data2.getInt("is_delete"),
                                                data2.getString("chat_user_id"),
                                                data2.getString("timestamp"),
                                                data2.getInt("types"),
                                                data2.getInt("unread_count"),
                                                data2.getInt("sender_id"),
                                                data2.getInt("receiver_id"),
                                                data2.getInt("item_id"),
                                                data2.getInt("message_id"),
                                                data2.getString("username"),
                                                data2.getString("profile_pic"),
                                                data2.getString("message"),
                                                data2.getInt("user_id"),
                                                data2.getInt("is_sold"),
                                                data2.getString("item_name"),
                                                data2.getInt("offer_status"),
                                                data2.getString("url"))
                                        chatInboxList.add(inboxData)
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                }
                                dbHelper!!.deleteAllChatData()
                                dbHelper!!.insertAllChatData(chatInboxList)
                                getArchiveLIst(chatInboxList)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    } else {

                    }
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun getArchiveLIst(chatInboxList: ArrayList<ChatInboxResponse>) {
        for (i in chatInboxList.indices) {
            if (chatInboxList[i].is_archive == 1) {
                archivedChatList.add(chatInboxList[i])
            } else {
                chatInboxAllList.add(chatInboxList[i])
                if (chatInboxList[i].user_id == Utils.getPreferencesString(this, AppConstants.USER_ID).toInt()) {
                    sellerList.add(chatInboxList[i])
                } else {
                    buyerList.add(chatInboxList[i])
                }
            }
        }
        adapterInboxAllList.addAll(chatInboxAllList)
       chatInboxAdapter.setData(adapterInboxAllList)
       chatInboxAdapter.notifyDataSetChanged()

    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(this)
        rvChatInbox.layoutManager = layoutManager
        chatInboxAdapter = ChatInboxAdapter(this, chatInboxType)
        rvChatInbox.adapter = chatInboxAdapter
    }

    private fun setData() {
        ivLeftLocation.setOnClickListener { onBackPressed() }
        tvRight.setOnClickListener {
        val intent=Intent(this,ArchivedChatActivity::class.java)
            startActivity(intent)
        }
        llAll.setOnClickListener {
            setNothing()
            tvAll.setTextColor(ContextCompat.getColor(this, R.color.dark_black))
            vAll.visibility = View.VISIBLE
            chatInboxType = 0
            chatInboxAdapter.mItemManger.closeAllItems()
            adapterInboxAllList.clear()
            adapterInboxAllList.addAll(chatInboxAllList)
            chatInboxAdapter.setData(adapterInboxAllList)
            chatInboxAdapter.notifyDataSetChanged()
        }
        llBuying.setOnClickListener {
            setNothing()
            tvBuying.setTextColor(ContextCompat.getColor(this, R.color.dark_black))
            vBuying.visibility = View.VISIBLE
            chatInboxType = 1
            chatInboxAdapter.mItemManger.closeAllItems()
            adapterInboxAllList.clear()
            adapterInboxAllList.addAll(buyerList)
            chatInboxAdapter.setData(adapterInboxAllList)
            chatInboxAdapter.notifyDataSetChanged()
        }
        llSelling.setOnClickListener {
            setNothing()
            tvSelling.setTextColor(ContextCompat.getColor(this, R.color.dark_black))
            vSelling.visibility = View.VISIBLE
            chatInboxType = 2
            chatInboxAdapter.mItemManger.closeAllItems()
            adapterInboxAllList.clear()
            adapterInboxAllList.addAll(sellerList)
            chatInboxAdapter.setData(adapterInboxAllList)
            chatInboxAdapter.notifyDataSetChanged()
        }
    }

    private fun setNothing() {
        tvAll.setTextColor(ContextCompat.getColor(this, R.color.light_gray))
        tvBuying.setTextColor(ContextCompat.getColor(this, R.color.light_gray))
        tvSelling.setTextColor(ContextCompat.getColor(this, R.color.light_gray))
        vAll.visibility = View.INVISIBLE
        vBuying.visibility = View.INVISIBLE
        vSelling.visibility = View.INVISIBLE
    }
}