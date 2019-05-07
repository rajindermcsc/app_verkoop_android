package com.verkoop.activity


import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View

import com.verkoop.R
import android.view.ViewTreeObserver
import com.github.nkzawa.socketio.client.Ack
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.verkoop.VerkoopApplication
import com.verkoop.adapter.ChatAdapter
import com.verkoop.models.ChatData
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.chat_activity.*
import kotlinx.android.synthetic.main.toolbar_bottom.*
import kotlinx.android.synthetic.main.toolbar_product_details.*
import org.json.JSONException
import org.json.JSONObject

class ChatActivity: AppCompatActivity() {
    private val socket: Socket? = VerkoopApplication.getAppSocket()
    private var senderId=0
    private var itemId=0
    private var userName=""
    private var profileUrl=""
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)
        senderId=intent.getIntExtra(AppConstants.USER_ID,0)
        itemId=intent.getIntExtra(AppConstants.ITEM_ID,0)
        userName=intent.getStringExtra(AppConstants.USER_NAME)
        profileUrl=intent.getStringExtra(AppConstants.PROFILE_URL)
        initKeyBoardListener()
        setData()
        setAdapter()

    }

    private fun setAdapter() {
        val layoutManager=LinearLayoutManager(this)
        rvChatList.layoutManager=layoutManager
        chatAdapter= ChatAdapter(this)
        rvChatList.adapter=chatAdapter

    }

    private fun setData() {
        tvUserName.text = userName
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
            if(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt()!=senderId) {
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
                sendMssg()
            } else {
                Utils.showSimpleMessage(this, getString(R.string.enter_mssg)).show()
            }
        }

    }
    private fun sendMssg() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id",Utils.getPreferencesString(this,AppConstants.USER_ID))
            jsonObject.put("receiver_id", senderId)
            jsonObject.put("item_id", itemId)
            jsonObject.put("message", etMssg.text.toString().trim())
            jsonObject.put("type",0 )
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
                        llParentChat.visibility=View.GONE

                    } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {
                        Log.e("Pasha", "HIDE")
                        llParentChat.visibility=View.VISIBLE
                    }

                }
                // Save current decor view height for the next call.
                lastVisibleDecorViewHeight = visibleDecorViewHeight
            }
        })
    }
}