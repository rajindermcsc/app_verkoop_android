package com.verkoop.activity


import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import com.github.nkzawa.socketio.client.Ack
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.verkoop.R
import com.verkoop.VerkoopApplication
import com.verkoop.adapter.ChatAdapter
import com.verkoop.models.ChatData
import com.verkoop.models.SocketOnReceiveEvent
import com.verkoop.utils.AppConstants
import com.verkoop.utils.CreatOfferDialog
import com.verkoop.utils.MakeOfferListener
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.chat_activity.*
import kotlinx.android.synthetic.main.toolbar_bottom.*
import kotlinx.android.synthetic.main.toolbar_product_details.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONException
import org.json.JSONObject


class ChatActivity : AppCompatActivity() {
    private val socket: Socket? = VerkoopApplication.getAppSocket()
    private var senderId = 0
    private var itemId = 0
    private var userName = ""
    private var profileUrl = ""
    private var price: Double = 0.0
    private var productUrl = ""
    private var productName = ""
    private lateinit var chatAdapter: ChatAdapter
    private var shareDialog: CreatOfferDialog? = null
    private var chatList = ArrayList<ChatData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)
        senderId = intent.getIntExtra(AppConstants.USER_ID, 0)
        itemId = intent.getIntExtra(AppConstants.ITEM_ID, 0)
        price = intent.getDoubleExtra(AppConstants.PRODUCT_PRICE, 0.0)
        userName = intent.getStringExtra(AppConstants.USER_NAME)
        profileUrl = intent.getStringExtra(AppConstants.PROFILE_URL)
        productUrl = intent.getStringExtra(AppConstants.PRODUCT_URL)
        productName = intent.getStringExtra(AppConstants.PRODUCT_NAME)
        initKeyBoardListener()
        setData()
        setAdapter()
        getChatHistory()
    }

    private fun getChatHistory() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("user_id", Utils.getPreferencesString(this, AppConstants.USER_ID))
            jsonObject.put("message_id", 0)
            jsonObject.put("item_id", itemId)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            socket?.emit(AppConstants.CHAT_LIST, jsonObject, Ack {
                Log.e("<<<Response>>>", Gson().toJson(it[0]))
                val data = it[0] as JSONObject
                runOnUiThread {
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
                                        val chatData = ChatData(data2.getInt("message_id"),
                                                data2.getInt("sender_id"),
                                                data2.getInt("receiver_id"),
                                                data2.getString("message"),
                                                data2.getString("timestamp"),
                                                data2.getInt("type"),
                                                0,
                                                data2.getInt("chat_user_id"))
                                        chatList.add(chatData)
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }

                                }
                                chatAdapter.setData(chatList)
                                chatAdapter.notifyDataSetChanged()
                                rvChatList.scrollToPosition(chatList.size - 1)

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

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(this)
        rvChatList.layoutManager = layoutManager
        chatAdapter = ChatAdapter(this)
        rvChatList.adapter = chatAdapter

    }

    private fun setData() {
        tvUserName.text = userName
        tvProductDes.text = productName
        tvProducePrice.text = price.toString()
        if (!TextUtils.isEmpty(productUrl)) {
            Picasso.with(this@ChatActivity).load(AppConstants.IMAGE_URL + productUrl)
                    .resize(720, 720)
                    .centerInside()
                    .error(R.mipmap.post_placeholder)
                    .placeholder(R.mipmap.post_placeholder)
                    .into(ivProductImageHome)
        }
        if (!TextUtils.isEmpty(profileUrl)) {
            Picasso.with(this@ChatActivity).load(AppConstants.IMAGE_URL + profileUrl)
                    .resize(720, 720)
                    .centerInside()
                    .error(R.mipmap.pic_placeholder)
                    .placeholder(R.mipmap.pic_placeholder)
                    .into(ivProfileTool)
        } else {
            Picasso.with(this@ChatActivity).load(R.mipmap.pic_placeholder)
                    .resize(720, 720)
                    .centerInside()
                    .error(R.mipmap.pic_placeholder)
                    .placeholder(R.mipmap.pic_placeholder)
                    .into(ivProfileTool)
        }
        ivLeft.setOnClickListener { onBackPressed() }
        llChatDetails.setOnClickListener {
            if (Utils.getPreferencesString(this, AppConstants.USER_ID).toInt() != senderId) {
                val reportIntent = Intent(this, UserProfileActivity::class.java)
                reportIntent.putExtra(AppConstants.USER_ID, senderId)
                reportIntent.putExtra(AppConstants.USER_NAME, userName)
                startActivity(reportIntent)
            }
        }
        llParentChat.setOnClickListener {
            val intent = Intent(this, ProductDetailsActivity::class.java)
            intent.putExtra(AppConstants.ITEM_ID, itemId)
            startActivity(intent)
        }
        ivSend.setOnClickListener {
            if (!TextUtils.isEmpty(etMssg.text.toString().trim())) {
                if (socket!!.connected()) {
                    sendMssgEvent()
                } else {
                    Utils.showSimpleMessage(this, "Socket Disconnected.").show()
                }
            } else {
                Utils.showSimpleMessage(this, getString(R.string.enter_mssg)).show()
            }
        }
        tvMakeOffer.setOnClickListener {
            makeOffer()
        }
        tvViewProfile.setOnClickListener {
            val reportIntent = Intent(this, UserProfileActivity::class.java)
            reportIntent.putExtra(AppConstants.USER_ID, senderId)
            reportIntent.putExtra(AppConstants.USER_NAME, userName)
            startActivity(reportIntent)
        }

    }

    private fun makeOffer() {
        shareDialog = CreatOfferDialog(price, this, object : MakeOfferListener {
            override fun makeOfferClick(offerPrice: Double) {
                Utils.showToast(this@ChatActivity, offerPrice.toString())
                makeOfferEvent(price)
            }

        })
        shareDialog!!.show()
    }

    private fun makeOfferEvent(price: Double) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id", Utils.getPreferencesString(this, AppConstants.USER_ID))
            jsonObject.put("receiver_id", senderId)
            jsonObject.put("item_id", itemId)
            jsonObject.put("message", "Make offer")
            jsonObject.put("type", 2)
            jsonObject.put("price", price)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            socket?.emit(AppConstants.MAKE_OFFER_EVENT, jsonObject, Ack {
                Log.e("<<<Response>>>", Gson().toJson(it[0]))
                val data = it[0] as JSONObject
                runOnUiThread {
                    if (data.getString("status") == "1") {
                        runOnUiThread {
                            Utils.showToast(this, "offerCreated")
                            /*   try {
                                   val chatData = ChatData(data.getInt("message_id"),
                                           data.getInt("sender_id"),
                                           data.getInt("receiver_id"),
                                           data.getString("message"),
                                           data.getString("timestamp"),
                                           data.getInt("type"),
                                           data.getInt("item_id"),
                                           data.getInt("chat_user_id"))
                                   etMssg.text = null
                               } catch (e: JSONException) {
                                   e.printStackTrace()
                               }*/
                        }
                    } else {

                    }
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun sendMssgEvent() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id", Utils.getPreferencesString(this, AppConstants.USER_ID).toInt())
            jsonObject.put("receiver_id", senderId)
            jsonObject.put("item_id", itemId)
            jsonObject.put("message", etMssg.text.toString().trim())
            jsonObject.put("type", 0)
            etMssg.isEnabled = false
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            socket?.emit(AppConstants.SEND_MESSAGES, jsonObject, Ack {
                Log.e("<<<Response>>>", Gson().toJson(it[0]))
                val data = it[0] as JSONObject
                runOnUiThread {
                    etMssg.isEnabled = true
                    if (data.getString("status") == "1") {
                        runOnUiThread {
                            try {
                                val chatData = ChatData(data.getInt("message_id"),
                                        data.getInt("sender_id"),
                                        data.getInt("receiver_id"),
                                        data.getString("message"),
                                        data.getString("timestamp"),
                                        data.getInt("type"),
                                        data.getInt("item_id"),
                                        data.getInt("chat_user_id"))
                                chatList.add(chatData)
                                chatAdapter.setData(chatList)
                                chatAdapter.notifyDataSetChanged()
                                rvChatList.scrollToPosition(chatList.size - 1)
                                etMssg.text = null
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        etMssg.isEnabled = true
                    }
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    @Subscribe
    fun onEventReceived(model: SocketOnReceiveEvent) {
        val tag = model.tag
        when (tag) {
            AppConstants.RECEIVE_MESSAGE -> {
                val data = model.args[0] as JSONObject
                runOnUiThread {
                    try {
                        val chatHistory = data.getJSONObject("messages")
                        if (chatHistory.getString("senderId").equals(senderId)) {
                            val chatData = ChatData(chatHistory.getInt("message_id"),
                                    chatHistory.getInt("sender_id"),
                                    chatHistory.getInt("receiver_id"),
                                    chatHistory.getString("message"),
                                    chatHistory.getString("timestamp"),
                                    chatHistory.getInt("type"),
                                    0,
                                    chatHistory.getInt("chat_user_id"))
                            chatList.add(chatData)
                            chatAdapter.setData(chatList)
                            chatAdapter.notifyDataSetChanged()
                            rvChatList.scrollToPosition(chatList.size - 1)
                        } else {
                            /* val mNotificationManager = KSMNotificationManager(applicationContext)
                             val intent = Intent(this, SplashActivity::class.java)
                             intent.putExtra("type", "1")
                             mNotificationManager.showSmallNotification(chatHistory.getString("senderName"), StringEscapeUtils.unescapeJava(chatHistory.getString("message")), intent)*/
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun initKeyBoardListener() {
        // Threshold for minimal keyboard height.
        val MIN_KEYBOARD_HEIGHT_PX = 150
        // Top-level window decor view.
        val decorView = window.decorView
        // Register global layout listener.
        decorView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            // Retrieve visible rectangle inside window.
            private val windowVisibleDisplayFrame = Rect()
            private var lastVisibleDecorViewHeight: Int = 0

            override fun onGlobalLayout() {
                decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame)
                val visibleDecorViewHeight = windowVisibleDisplayFrame.height()

                if (lastVisibleDecorViewHeight != 0) {
                    if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {
                        Log.e("Pasha", "SHOW")
                        llParentChat.visibility = View.GONE
                        if (shareDialog != null) {
                            shareDialog!!.showDialog(1)
                        }

                    } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {
                        Log.e("Pasha", "HIDE")
                        llParentChat.visibility = View.VISIBLE
                        if (shareDialog != null) {
                            shareDialog!!.showDialog(0)
                        }

                    }

                }
                // Save current decor view height for the next call.
                lastVisibleDecorViewHeight = visibleDecorViewHeight
            }
        })
    }
}