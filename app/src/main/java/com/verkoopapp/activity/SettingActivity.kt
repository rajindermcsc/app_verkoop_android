package com.verkoopapp.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.nkzawa.socketio.client.Ack
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.verkoopapp.R
import com.verkoopapp.VerkoopApplication
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.DeleteCommentDialog
import com.verkoopapp.utils.SelectionListener
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.setting_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import org.json.JSONException
import org.json.JSONObject

class SettingActivity : AppCompatActivity() {
    private var socket: Socket? = VerkoopApplication.getAppSocket()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_activity)
        setData()
    }

    private fun setData() {
        tvHeaderLoc.text = getString(R.string.settings)
        ivLeftLocation.setOnClickListener {
            onBackPressed()
        }
        tvLogout.setOnClickListener {
            logOutDialogBox()
        }
        tvEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
        tvChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
        tvNotification.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }
        tvBanner.setOnClickListener {
            val intent = Intent(this, ViewAllBannerActivity::class.java)
            startActivity(intent)
        }
        tvAboutVerkoop.setOnClickListener {
            val intent = Intent(this, VerkoopPoliciesActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM,1)
            startActivity(intent)
        }
        tvTermsService.setOnClickListener {
            val intent = Intent(this, VerkoopPoliciesActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM,2)
            startActivity(intent)
        }
        tvPrivacyPolicy.setOnClickListener {
            val intent = Intent(this, VerkoopPoliciesActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM,3)
            startActivity(intent)
        }
        tvContactUs.setOnClickListener {
            val intent = Intent(this, VerkoopPoliciesActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM,4)
            startActivity(intent)
        }
        tvHelpCenter.setOnClickListener {
            val intent = Intent(this, VerkoopPoliciesActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM,5)
            startActivity(intent)
        }
        tvPrivatePolicy.setOnClickListener {
            val intent = Intent(this, VerkoopPoliciesActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM,6)
            startActivity(intent)
        }
        tvDeActivateAcct.setOnClickListener { }

    }

    private fun logOutDialogBox() {
        val shareDialog = DeleteCommentDialog(this, getString(R.string.logout_heading), getString(R.string.logout_des), object : SelectionListener {
            override fun leaveClick() {
                if (Utils.isOnline(this@SettingActivity)) {
                    logout()
                } else {
                    Utils.showSimpleMessage(this@SettingActivity, getString(R.string.check_internet)).show()
                }

            }
        })
        shareDialog.show()
    }

    private fun logout() {
        callInit()
        com.verkoopapp.utils.Utils.clearPreferences(this@SettingActivity)
        val intent = Intent(this@SettingActivity, WalkThroughActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
    private fun callInit() {
        socket?.emit(AppConstants.SOCKET_DISCONNECT, getObj(), Ack {
            Log.e("<<<DisConnectLogOut>>>", Gson().toJson(it[0]))
        })

    }
    private fun getObj(): Any {
        val jsonObject: JSONObject?
        jsonObject = JSONObject()
        try {
            jsonObject.put("user_id", Utils.getPreferencesString(applicationContext,AppConstants.USER_ID).toInt())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }
}