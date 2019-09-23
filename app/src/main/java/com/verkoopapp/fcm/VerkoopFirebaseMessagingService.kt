
package com.verkoopapp.fcm
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

import com.verkoopapp.activity.SplashActivity
import org.json.JSONException
import org.json.JSONObject


class VerkoopFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(refreshedToken: String?) {
        // Get updated InstanceID token.
        Log.d(VerkoopFirebaseMessagingService.TAG, "Refreshed token: " + refreshedToken!!)

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
     //   AppPreferences.deviceId = refreshedToken
        /*Utils.sendDeviceId(this)*/
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        if (remoteMessage!!.data.isNotEmpty()) {
            Log.e(TAG, "Data Payload: " + remoteMessage.data.toString())
            try {
                val json = JSONObject(remoteMessage.data)
                sendPushNotification(json)
//                sendPushNotification(remoteMessage.getData().get("title")!!, remoteMessage.getData().get("body")!!)
            } catch (e: Exception) {
                Log.e(TAG, "Exception: " + e.message)
            }

        }
    }

    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendPushNotification(json: JSONObject) {
//    private fun sendPushNotification(name: String,message:String) {
        //optionally we can display the json into log
      //  Log.e(TAG, "Notification JSON " + json.toString())
        try {
            //getting the json data
            val data = json.getJSONObject("data")

            //parsing json data
            val title = data.getString("title")
            //            String type = data.getString("type");
            val message = data.getString("message")
            val imageUrl = data.getString("image")

            //creating MyNotificationManager object
            val mNotificationManager = KSMNotificationManager(applicationContext)

            //creating an intent for the notification
            val intent = Intent(this, SplashActivity::class.java)
            intent.putExtra("type", "1")

            //if there is no image
            if (imageUrl == "null") {
                //displaying small notification
                mNotificationManager.showSmallNotification(title, message, intent)
            } else {
                //if there is an image
                //displaying a big notification
                mNotificationManager.showBigNotification(title, message, imageUrl, intent)
            }
        } catch (e: JSONException) {
            Log.e(TAG, "Json Exception: " + e.message)
        } catch (e: Exception) {
            Log.e(TAG, "Exception: " + e.message)
        }

    }

    companion object {
        private const val TAG = "FirebaseMsgService"
    }
}