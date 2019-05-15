package com.verkoop.activity


import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import com.github.nkzawa.socketio.client.Ack
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.squareup.picasso.Picasso
import com.verkoop.R
import com.verkoop.VerkoopApplication
import com.verkoop.adapter.ChatAdapter
import com.verkoop.models.ChatData
import com.verkoop.models.SocketOnReceiveEvent
import com.verkoop.offlinechatdata.ChatResponse
import com.verkoop.offlinechatdata.DbHelper
import com.verkoop.utils.*
import io.realm.RealmResults
import kotlinx.android.synthetic.main.chat_activity.*
import kotlinx.android.synthetic.main.toolbar_bottom.*
import kotlinx.android.synthetic.main.toolbar_location.*
import kotlinx.android.synthetic.main.toolbar_product_details.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONException
import org.json.JSONObject


class ChatActivity : AppCompatActivity() {
    private val socket: Socket? = VerkoopApplication.getAppSocket()
    private var powerMenu: PowerMenu? = null
    private var senderId = 0
    private var itemId = 0
    private var isSold = 0
    private var userName = ""
    private var profileUrl = ""
    private var price: Double = 0.0
    private var productUrl = ""
    private var productName = ""
    private lateinit var chatAdapter: ChatAdapter
    private var shareDialog: CreatOfferDialog? = null
    private var chatList = ArrayList<ChatData>()
    private var dbHelper: DbHelper? = null
    private var chatHistoryModels: RealmResults<ChatResponse>? = null
    private var isMyProduct:Boolean=false
    private var chatUserId=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)
        senderId = intent.getIntExtra(AppConstants.USER_ID, 0)
        itemId = intent.getIntExtra(AppConstants.ITEM_ID, 0)
        price = intent.getDoubleExtra(AppConstants.PRODUCT_PRICE,0.0)
        userName = intent.getStringExtra(AppConstants.USER_NAME)
        profileUrl = intent.getStringExtra(AppConstants.PROFILE_URL)
        productUrl = intent.getStringExtra(AppConstants.PRODUCT_URL)
        productName = intent.getStringExtra(AppConstants.PRODUCT_NAME)
        isMyProduct = intent.getBooleanExtra(AppConstants.IS_MY_PRODUCT,false)
        isSold = intent.getIntExtra(AppConstants.IS_SOLD,0)

        if(socket!!.connected()) {
            directChat()
        }
        if(isSold==1){
            llViewOffer.visibility=View.GONE
        }else{
            llViewOffer.visibility=View.GONE
        }
        dbHelper = DbHelper()
        initKeyBoardListener()
        setData()
        setAdapter()
        if (Utils.isOnline(this)) {
            setOfflineHistory()
            getChatHistory()
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            setOfflineHistory()
        }

    }

    private fun directChat() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id", Utils.getPreferencesString(this, AppConstants.USER_ID))
            jsonObject.put("receiver_id", senderId)
            jsonObject.put("item_id", itemId)
            jsonObject.put("price", price.toString())
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            socket?.emit(AppConstants.DIRECT_CHAT, jsonObject, Ack {
                Log.e("<<<Chat_User_Id>>>", Gson().toJson(it[0]))
                val data = it[0] as JSONObject
                runOnUiThread {
                    if (data.getString("status") == "1") {
                        runOnUiThread {
                            chatUserId=data.getInt("chat_user_id")
                        }
                    }
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun setOfflineHistory() {
        chatList.clear()
        chatHistoryModels = dbHelper!!.getChatHistoryList(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), senderId,itemId)
        for (i in chatHistoryModels!!.indices) {
            val chatData = ChatData(chatHistoryModels!![i]!!.message_id,
                    chatHistoryModels!![i]!!.sender_id,
                    chatHistoryModels!![i]!!.receiver_id,
                    chatHistoryModels!![i]!!.message!!,
                    chatHistoryModels!![i]!!.timestamp!!,
                    chatHistoryModels!![i]!!.type,
                    chatHistoryModels!![i]!!.item_id,
                    chatHistoryModels!![i]!!.chat_user_id,
                    chatHistoryModels!![i]!!.is_read)
            chatList.add(chatData)
        }
        chatAdapter.setData(chatList)
        chatAdapter.notifyDataSetChanged()
        rvChatList.scrollToPosition(chatList.size - 1)

    }

    private fun getChatHistory() {
        chatHistoryModels = dbHelper!!.getChatHistoryList( Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), senderId,itemId)
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id", Utils.getPreferencesString(this, AppConstants.USER_ID))
            jsonObject.put("receiver_id", senderId)
            if (chatHistoryModels!!.size > 0) {
              //  jsonObject.put("message_id", chatHistoryModels!![chatHistoryModels!!.size - 1]!!.message_id)
                jsonObject.put("message_id",chatHistoryModels!!.last()!!.message_id)
            } else {
                jsonObject.put("message_id", "0")
            }
            jsonObject.put("item_id", itemId)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            socket?.emit(AppConstants.CHAT_LIST, jsonObject, Ack {
                Log.e("<<<Response>>>", Gson().toJson(it[0]))
                val data = it[0] as JSONObject
                runOnUiThread {
                    if (data.getString("status") == "1") {
                        chatList.clear()
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
                                                data.getInt("item_id"),
                                                data2.getInt("chat_user_id"),
                                                data2.getInt("is_read"))
                                        chatList.add(chatData)
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }

                                }
                                dbHelper!!.chatHistoryInsertData(chatList)
                                chatAdapter.setData(chatList)
                                chatAdapter.notifyDataSetChanged()
                                rvChatList.scrollToPosition(chatList.size - 1)
                                setOfflineHistory()
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                       // if(isSold!=1){
                            makeOfferManage(data.getInt("offer_status"),data.getInt("is_block"))
                           // llViewOffer.visibility=View.VISIBLE
                      // }

                    } else {

                    }
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun makeOfferManage(offerStatus: Int, blockStatus: Int) {
        if(isSold!=1) {
            if (isMyProduct && offerStatus == 5) {
                llViewOffer.visibility = View.GONE
            } else if (!isMyProduct && offerStatus == 5) {
                llViewOffer.visibility = View.VISIBLE
                tvViewProfile.text = getString(R.string.view_seller)
                tvMakeOffer.text = getString(R.string.make_offer)
            } else if (isMyProduct && offerStatus == 0) {
                llViewOffer.visibility = View.VISIBLE
                tvViewProfile.text = getString(R.string.decline_offer)
                tvMakeOffer.text = getString(R.string.accept_offer)
            } else if (!isMyProduct && offerStatus == 0) {
                llViewOffer.visibility = View.VISIBLE
                tvViewProfile.text = getString(R.string.edit_offer)
                tvMakeOffer.text = getString(R.string.cancel_offer)
            } else if (isMyProduct && offerStatus == 1) {
                llViewOffer.visibility = View.VISIBLE
                tvMakeOffer.visibility = View.GONE
                tvViewProfile.text = getString(R.string.leave_review)
            } else if (!isMyProduct && offerStatus == 1) {
                llViewOffer.visibility = View.VISIBLE
                tvMakeOffer.visibility = View.GONE
                tvViewProfile.text = getString(R.string.leave_review_seller)
            } else if (isMyProduct && offerStatus == 2) {
                llViewOffer.visibility = View.GONE
            } else if (!isMyProduct && offerStatus == 2) {
                llViewOffer.visibility = View.VISIBLE
                tvViewProfile.text = getString(R.string.view_seller)
                tvMakeOffer.text = getString(R.string.make_offer)
            }
        }else{
            if (isMyProduct && offerStatus == 1) {
                llViewOffer.visibility = View.VISIBLE
                tvMakeOffer.visibility = View.GONE
                tvViewProfile.text = getString(R.string.leave_review)
            } else if (!isMyProduct && offerStatus == 1) {
                llViewOffer.visibility = View.VISIBLE
                tvMakeOffer.visibility = View.GONE
                tvViewProfile.text = getString(R.string.leave_review_seller)
            }
        }
    }


    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(this)
        rvChatList.layoutManager = layoutManager
        chatAdapter = ChatAdapter(this)
        rvChatList.adapter = chatAdapter
        rvChatList.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom)
                layoutManager.smoothScrollToPosition(rvChatList, null, chatAdapter.itemCount)
        }

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
            if(tvMakeOffer.text.toString().equals(getString(R.string.make_offer),ignoreCase = true)) {
                /*0=Make an offer*/
                makeOffer(0,price)
            }else if(tvMakeOffer.text.toString().equals(getString(R.string.accept_offer),ignoreCase = true)){
              val  lastOffer = dbHelper!!.getOfferPriceLast( Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), senderId,itemId,2)
                acceptOfferDialogBox(lastOffer!!.message!!)
            }else if(tvMakeOffer.text.toString().equals(getString(R.string.cancel_offer),ignoreCase = true)){
                val  lastOffer = dbHelper!!.getOfferPriceLast( Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), senderId,itemId,2)
                cancelOfferDialogBox(lastOffer!!.message!!)
            }
        }

        tvViewProfile.setOnClickListener {
            if(tvViewProfile.text.toString().equals(getString(R.string.view_seller),ignoreCase = true)) {
                val reportIntent = Intent(this, UserProfileActivity::class.java)
                reportIntent.putExtra(AppConstants.USER_ID, senderId)
                reportIntent.putExtra(AppConstants.USER_NAME, userName)
                startActivity(reportIntent)
            }else if(tvViewProfile.text.toString().equals(getString(R.string.decline_offer),ignoreCase = true)){
                val  lastOffer = dbHelper!!.getOfferPriceLast( Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), senderId,itemId,2)
               declineOfferDialogBox(lastOffer!!.message!!)
            }else if(tvViewProfile.text.toString().equals(getString(R.string.edit_offer),ignoreCase = true)){
                /*1=Edit an offer*/
                val  lastOffer = dbHelper!!.getOfferPriceLast( Utils.getPreferencesString(this@ChatActivity, AppConstants.USER_ID).toInt(), senderId,itemId,2)
                makeOffer(1,lastOffer!!.message!!.toDouble())
            }else if(tvViewProfile.text.toString().equals(getString(R.string.leave_review),ignoreCase = true)){

            }

        }
        ivRightProduct.setOnClickListener {
            Utils.showToast(this,"work in Progress")
            openPowerMenu()
        }
    }
    private fun openPowerMenu() {
        powerMenu = PowerMenu.Builder(this)
                .addItem(PowerMenuItem("Block user", false)) // add an item.
                .addItem(PowerMenuItem("Report user", false)) // aad an item list.
                .addItem(PowerMenuItem("Archive Chat", false)) // aad an item list.
                .addItem(PowerMenuItem("Delete Chat", false)) // aad an item list.
                .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT) // Animation start point (TOP | LEFT).
                .setMenuRadius(1f) // sets the corner radius.
                .setMenuShadow(10f) // sets the shadow.
                .setTextColor(ContextCompat.getColor(this, R.color.black_dark))
                .setMenuColor(Color.WHITE)
                .setDivider(ContextCompat.getDrawable(this, R.drawable.horizontal_line))
                .setDividerHeight(1)
                .setSelectedMenuColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setOnMenuItemClickListener(onMenuItemClickListener)
                .build()
        powerMenu!!.showAsAnchorRightTop(ivRightProduct)
    }

    private val onMenuItemClickListener = OnMenuItemClickListener<PowerMenuItem> { position, item ->
        powerMenu!!.selectedPosition = position
        powerMenu!!.dismiss()

       /* if (position == 1) {
            val reportIntent = Intent(this, ReportUserActivity::class.java)
            reportIntent.putParcelableArrayListExtra(AppConstants.REPORT_LIST, reportsList)
            reportIntent.putExtra(AppConstants.COMING_FROM,1)
            reportIntent.putExtra(AppConstants.ITEM_ID,userId)
            startActivity(reportIntent)
        } else {
            if(!isBlockClick) {
                if (item.getTitle().equals("Block user", ignoreCase = true)) {
                    blockUserDialog()
                } else if (item.getTitle().equals("Unblock user", ignoreCase = true)) {
                    unBlockUserDialog()
                }
            }
        }*/
    }
    private fun declineOfferDialogBox(price:String) {
        val shareDialog = DeleteCommentDialog(this,"Decline offer?","Are you sure you want to decline the offer?",object : SelectionListener {
            override fun leaveClick() {
                if(socket!!.connected()) {
                    declineOfferEvent(price)
                }
            }
        })
        shareDialog.show()
    }

    private fun cancelOfferDialogBox(price:String) {
        val shareDialog = DeleteCommentDialog(this,getString(R.string.cancel_offer),"Are you sure you want to cancel your offer?",object : SelectionListener {
            override fun leaveClick() {
                if(socket!!.connected()) {
                   cancelOfferEvent(price)
                }
            }
        })
        shareDialog.show()
    }

    private fun acceptOfferDialogBox(price:String) {
        val shareDialog = DeleteCommentDialog(this,getString(R.string.confirm_accpt_offer),"Are you sure you want to delete this Chat?",object : SelectionListener {
            override fun leaveClick() {
                if(socket!!.connected()) {
                    acceptOfferEvent(price)
                }
            }
        })
        shareDialog.show()
    }



    private fun makeOffer(type:Int,priceOffer:Double) {
        shareDialog = CreatOfferDialog(priceOffer, this, object : MakeOfferListener {
            override fun makeOfferClick(offerPrice: Double) {
                Utils.showToast(this@ChatActivity, offerPrice.toString())
                if(socket!!.connected()) {
                    if(type!=1) {
                        makeOfferEvent(offerPrice)
                    }else{
                      //  val  lastOffer = dbHelper!!.getOfferPriceLast( Utils.getPreferencesString(this@ChatActivity, AppConstants.USER_ID).toInt(), senderId,itemId,2)
                        editOfferEvent(offerPrice)
                    }
                }
            }

        })
        shareDialog!!.show()
    }

    private fun declineOfferEvent(price:String) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id", Utils.getPreferencesString(this, AppConstants.USER_ID))
            jsonObject.put("receiver_id", senderId)
            jsonObject.put("item_id", itemId)
            jsonObject.put("message",price)
            jsonObject.put("type", 4)
            jsonObject.put("chat_user_id", chatUserId)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            socket?.emit(AppConstants.DECLINE_OFFER_EVENT, jsonObject, Ack {
                Log.e("<<<Response>>>", Gson().toJson(it[0]))
                val data = it[0] as JSONObject
                runOnUiThread {
                    if (data.getString("status") == "1") {
                        runOnUiThread {
                            saveDataToDb(data)
                            if(data.getInt("type")==4){
                                makeOfferManage(2,0)
                            }
                            try {
                                val chatData = ChatData(data.getInt("message_id"),
                                        data.getInt("sender_id"),
                                        data.getInt("receiver_id"),
                                        data.getString("message"),
                                        data.getString("timestamp"),
                                        data.getInt("type"),
                                        data.getInt("item_id"),
                                        data.getInt("chat_user_id"),0)
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

                    }
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun cancelOfferEvent(price:String) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id", Utils.getPreferencesString(this, AppConstants.USER_ID))
            jsonObject.put("receiver_id", senderId)
            jsonObject.put("item_id", itemId)
            jsonObject.put("message",price)
            jsonObject.put("type", 5)
            jsonObject.put("chat_user_id", chatUserId)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            socket?.emit(AppConstants.CANCEL_OFFER_EVENT, jsonObject, Ack {
                Log.e("<<<Response>>>", Gson().toJson(it[0]))
                val data = it[0] as JSONObject
                runOnUiThread {
                    if (data.getString("status") == "1") {
                        runOnUiThread {
                            saveDataToDb(data)
                            if( data.getInt("type")==5) {
                                makeOfferManage(5, 0)
                            }
                            try {
                                val chatData = ChatData(data.getInt("message_id"),
                                        data.getInt("sender_id"),
                                        data.getInt("receiver_id"),
                                        data.getString("message"),
                                        data.getString("timestamp"),
                                        data.getInt("type"),
                                        data.getInt("item_id"),
                                        data.getInt("chat_user_id"),0)
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

                    }
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun editOfferEvent(price: Double) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id", Utils.getPreferencesString(this, AppConstants.USER_ID))
            jsonObject.put("receiver_id", senderId)
            jsonObject.put("item_id", itemId)
            jsonObject.put("message", price.toString())
            jsonObject.put("type", 2)
            jsonObject.put("price", price)
            jsonObject.put("chat_user_id", chatUserId)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            socket?.emit(AppConstants.EDIT_OFFER_EVENT, jsonObject, Ack {
                Log.e("<<<Response>>>", Gson().toJson(it[0]))
                val data = it[0] as JSONObject
                runOnUiThread {
                    if (data.getString("status") == "1") {
                        runOnUiThread {
                            saveDataToDb(data)
                            if( data.getInt("type")==2) {
                                makeOfferManage(0, 0)
                            }
                            try {
                                val chatData = ChatData(data.getInt("message_id"),
                                        data.getInt("sender_id"),
                                        data.getInt("receiver_id"),
                                        data.getString("message"),
                                        data.getString("timestamp"),
                                        data.getInt("type"),
                                        data.getInt("item_id"),
                                        data.getInt("chat_user_id"),0)
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

                    }
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun acceptOfferEvent(price:String) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id", Utils.getPreferencesString(this, AppConstants.USER_ID))
            jsonObject.put("receiver_id", senderId)
            jsonObject.put("item_id", itemId)
            jsonObject.put("message", price)
            jsonObject.put("type", 3)
            jsonObject.put("chat_user_id", chatUserId)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            socket?.emit(AppConstants.ACCEPT_OFFER_EVENT, jsonObject, Ack {
                Log.e("<<<Response>>>", Gson().toJson(it[0]))
                val data = it[0] as JSONObject
                runOnUiThread {
                    if (data.getString("status") == "1") {
                        runOnUiThread {
                            saveDataToDb(data)
                            if( data.getInt("type")==3) {
                                makeOfferManage(1, 0)
                            }
                            try {
                                val chatData = ChatData(data.getInt("message_id"),
                                        data.getInt("sender_id"),
                                        data.getInt("receiver_id"),
                                        data.getString("message"),
                                        data.getString("timestamp"),
                                        data.getInt("type"),
                                        data.getInt("item_id"),
                                        data.getInt("chat_user_id"),0)
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

                    }
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun makeOfferEvent(price: Double) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id", Utils.getPreferencesString(this, AppConstants.USER_ID))
            jsonObject.put("receiver_id", senderId)
            jsonObject.put("item_id", itemId)
            jsonObject.put("message", price.toString())
            jsonObject.put("type", 2)
            jsonObject.put("price", price)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            socket?.emit(AppConstants.MAKE_OFFER_EVENT, jsonObject, Ack {
                Log.e("<<<Response>>>", Gson().toJson(it[0]))
                val data = it[0] as JSONObject
                runOnUiThread {
                    if (data.getString("status") == "1") {
                        runOnUiThread {
                            saveDataToDb(data)
                            if( data.getInt("type")==2) {
                                makeOfferManage(0, 0)
                            }
                            try {
                                val chatData = ChatData(data.getInt("message_id"),
                                        data.getInt("sender_id"),
                                        data.getInt("receiver_id"),
                                        data.getString("message"),
                                        data.getString("timestamp"),
                                        data.getInt("type"),
                                        data.getInt("item_id"),
                                        data.getInt("chat_user_id"),0)
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
            jsonObject.put("chat_user_id", chatUserId)
            ivSend.isEnabled = false
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            socket?.emit(AppConstants.SEND_MESSAGES, jsonObject, Ack {
                Log.e("<<<Response>>>", Gson().toJson(it[0]))
                val data = it[0] as JSONObject
                runOnUiThread {
                    ivSend.isEnabled = true
                    if (data.getString("status") == "1") {
                        saveDataToDb(data)
                            try {
                                val chatData = ChatData(data.getInt("message_id"),
                                        data.getInt("sender_id"),
                                        data.getInt("receiver_id"),
                                        data.getString("message"),
                                        data.getString("timestamp"),
                                        data.getInt("type"),
                                        data.getInt("item_id"),
                                        data.getInt("chat_user_id"),0)
                                chatList.add(chatData)
                                chatAdapter.setData(chatList)
                                chatAdapter.notifyDataSetChanged()
                                rvChatList.scrollToPosition(chatList.size - 1)
                                etMssg.text = null
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                    } else {
                        ivSend.isEnabled = true
                    }
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun saveDataToDb(data: JSONObject) {
        val addToDb=ArrayList<ChatData>()
        try {
            val chatData = ChatData(data.getInt("message_id"),
                    data.getInt("sender_id"),
                    data.getInt("receiver_id"),
                    data.getString("message"),
                    data.getString("timestamp"),
                    data.getInt("type"),
                    data.getInt("item_id"),
                    data.getInt("chat_user_id"),
                    0)
            addToDb.add(chatData)
            dbHelper!!.chatHistoryInsertData( addToDb)
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
                Log.e("<<<ReceiveResponse>>>", Gson().toJson(model.args[0]))
                runOnUiThread {
                    try {
                        saveDataToDb(data)
                        if( data.getInt("type")==2){
                            makeOfferManage(0,0)
                        }else if(data.getInt("type")==3){
                            makeOfferManage(1,0)
                        }else if(data.getInt("type")==4){
                            makeOfferManage(2,0)
                        }else if(data.getInt("type")==5){
                            makeOfferManage(5,0)
                        }

                     //   val chatHistory = data.getJSONObject("messages")
                       // if (data.getString("sender_id").equals(senderId)) {
                            val chatData = ChatData(data.getInt("message_id"),
                                    data.getInt("sender_id"),
                                    data.getInt("receiver_id"),
                                    data.getString("message"),
                                    data.getString("timestamp"),
                                    data.getInt("type"),
                                    data.getInt("item_id"),
                                    data.getInt("chat_user_id"),
                                   0)
                            chatList.add(chatData)
                            chatAdapter.setData(chatList)
                            chatAdapter.notifyDataSetChanged()
                            rvChatList.scrollToPosition(chatList.size - 1)

                       /* } else {
                            *//* val mNotificationManager = KSMNotificationManager(applicationContext)
                             val intent = Intent(this, SplashActivity::class.java)
                             intent.putExtra("type", "1")
                             mNotificationManager.showSmallNotification(chatHistory.getString("senderName"), StringEscapeUtils.unescapeJava(chatHistory.getString("message")), intent)*//*
                        }*/
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