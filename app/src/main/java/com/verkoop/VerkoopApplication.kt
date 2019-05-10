package com.verkoop

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import android.util.Log
import com.github.nkzawa.socketio.client.Ack
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.verkoop.models.ChatData
import com.verkoop.models.SocketCheckConnectionEvent
import com.verkoop.models.SocketOnReceiveEvent
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Loading
import com.verkoop.utils.Utils
import io.realm.Realm
import io.realm.RealmConfiguration
import org.acra.ACRA
import org.acra.ReportingInteractionMode
import org.acra.annotation.ReportsCrashes
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject





@ReportsCrashes(mailTo = "anmol@mobilecoderz.com", mode = ReportingInteractionMode.TOAST, resToastText = R.string.application_crash)// my email here

class VerkoopApplication : Application() {
     private var loadDialog: Loading? = null
    private val socket: Socket? = IO.socket(AppConstants.SOCKET_URL)
    val loader: Loading
        get() {
            if (loadDialog == null)
                loadDialog = Loading()
            return loadDialog!!
        }

     override fun onCreate() {
         super.onCreate()
         instance = this
         initSocket()
         Realm.init(this)
         val config = RealmConfiguration.Builder()
                 .deleteRealmIfMigrationNeeded()
                 .name("verkoop.db")
                 .schemaVersion(0)
                 .build()
         Realm.setDefaultConfiguration(config)
     }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
        ACRA.init(this)

    }
    companion object {
        lateinit var instance: VerkoopApplication
        operator fun get(context: Context): VerkoopApplication {
            return context.applicationContext as VerkoopApplication
        }

        fun getAppSocket(): Socket? {
            return if (instance.socket != null) {
                instance.socket
            } else {
                instance.initSocket()
                instance.socket
            }
        }
    }

    private fun initSocket() {
        if (socket != null /*&& AppPreferences.isLoggedIn*/) {
            socket.on(Socket.EVENT_CONNECT) { _ ->
                        Log.e("<<<<<SOCKET>>>>>", "CONNECTED")
                        /* Socket Init */

               /* socket.emit(AppConstants.INIT_USER_ID, getObj(), Ack {
                    Log.e("<<<ACKRESPONSE--5>>>", Gson().toJson(it[0]))
                })*/
                EventBus.getDefault().post(SocketCheckConnectionEvent("connect",AppConstants.SOCKET_CONNECT))

                    }
                    .on(AppConstants.RECEIVE_MESSAGE){
                        //* Room event listener *//*
                        Log.e("<<<receiveChat>>>",  Gson().toJson(it[0]))
                        EventBus.getDefault().post(SocketOnReceiveEvent(it,AppConstants.RECEIVE_MESSAGE))
                    }
                   /* .on(Constants.ROOM_EVENTS){
                        *//* Room event listener *//*
                        Log.e("<<<ACKRESPONSE--4>>>",  Gson().toJson(it[0]))
                        EventBus.getDefault().post(SocketOnReceiveEvent(it,Constants.ROOM_EVENTS))
                    }
                    .on(Constants.TIMER_EVENT){
                        *//* timer listener *//*
                        Log.e("<<<ACKRESPONSE--3>>>",  Gson().toJson(it[0]))
                        EventBus.getDefault().post(SocketOnReceiveEvent(it,Constants.TIMER_EVENT))
                    }
                    .on(Constants.QUES_EVENT){
                        *//* question listener *//*
                        Log.e("<<<ACKRESPONSE--2>>>",  Gson().toJson(it[0]))
                        EventBus.getDefault().post(SocketOnReceiveEvent(it,Constants.QUES_EVENT))
                    }
                    .on(Constants.ANS_EVENT){
                        *//* myAnswer listener *//*
                        Log.e("<<<ACKRESPONSE--1>>>",  Gson().toJson(it[0]))
                        EventBus.getDefault().post(SocketOnReceiveEvent(it,Constants.ANS_EVENT))
                    }.on(Constants.RECEIVE_MESSAGE){
                        *//* myAnswer listener *//*
                        Log.e("<<<ACKRESPONSE--->>>",  Gson().toJson(it[0]))
                        EventBus.getDefault().post(SocketOnReceiveEvent(it,Constants.RECEIVE_MESSAGE))
                    }*/
                    .on(Socket.EVENT_DISCONNECT) { _ ->
                        /* Socket disconnect listener */
                        Log.e("<<<<<SOCKET>>>>>", "DISCONNECTED")
                    }
            socket.connect()
        }
    }


}