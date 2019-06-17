package com.verkoopapp

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.verkoopapp.models.SocketCheckConnectionEvent
import com.verkoopapp.models.SocketOnReceiveEvent
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Loading
import io.branch.referral.Branch
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import io.realm.RealmConfiguration

import org.greenrobot.eventbus.EventBus


//
// @ReportsCrashes(mailTo = "anmol@mobilecoderz.com", mode = ReportingInteractionMode.TOAST, resToastText = R.string.application_crash)// my email here

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
         Fabric.with(this, Crashlytics())
         Branch.getAutoInstance(this)
         Realm.init(this)
         initSocket()
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
     //   ACRA.init(this)

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
        if (socket != null ) {
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
                    .on(Socket.EVENT_DISCONNECT) { _ ->
                        /* Socket disconnect listener */
                        Log.e("<<<<<SOCKET>>>>>", "DISCONNECTED")
                    }
            socket.connect()
        }
    }


}