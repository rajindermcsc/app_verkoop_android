package com.verkoopapp.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.github.nkzawa.socketio.client.Ack
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.verkoopapp.R
import com.verkoopapp.VerkoopApplication
import com.verkoopapp.models.AddItemResponse
import com.verkoopapp.models.LogOutRequest
import com.verkoopapp.models.WalletHistoryResponse
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.DeleteCommentDialog
import com.verkoopapp.utils.SelectionListener
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.my_profile_details_row.*
import kotlinx.android.synthetic.main.my_wallet_activity.*
import kotlinx.android.synthetic.main.setting_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

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
            tvEditProfile.isEnabled = false
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
            Handler().postDelayed(Runnable {
                tvEditProfile.isEnabled = true
            }, 1000)
        }
        tvChangePassword.setOnClickListener {
            tvChangePassword.isEnabled = false
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            Handler().postDelayed(Runnable {
                tvChangePassword.isEnabled = true
            }, 1000)
        }
        tvNotification.setOnClickListener {
            tvNotification.isEnabled = false
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
            Handler().postDelayed(Runnable {
                tvNotification.isEnabled = true
            }, 1000)
        }
        tvBanner.setOnClickListener {
            tvBanner.isEnabled = false
            val intent = Intent(this, ViewAllBannerActivity::class.java)
            startActivity(intent)
            Handler().postDelayed(Runnable {
                tvBanner.isEnabled = true
            }, 1000)
        }
        tvAboutVerkoop.setOnClickListener {
            tvAboutVerkoop.isEnabled = false
            val intent = Intent(this, VerkoopPoliciesActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM, 1)
            startActivity(intent)
            Handler().postDelayed(Runnable {
                tvAboutVerkoop.isEnabled = true
            }, 1000)
        }
        tvTermsService.setOnClickListener {
            tvTermsService.isEnabled = false
            val intent = Intent(this, VerkoopPoliciesActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM, 2)
            startActivity(intent)
            Handler().postDelayed(Runnable {
                tvTermsService.isEnabled = true
            }, 1000)
        }
        tvPrivacyPolicy.setOnClickListener {
            val intent = Intent(this, VerkoopPoliciesActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM, 3)
            startActivity(intent)
        }
        tvContactUs.setOnClickListener {
            tvContactUs.isEnabled = false
            val intent = Intent(this, VerkoopPoliciesActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM, 4)
            startActivity(intent)
            Handler().postDelayed(Runnable {
                tvContactUs.isEnabled = true
            }, 1000)
        }
        tvHelpCenter.setOnClickListener {
            tvHelpCenter.isEnabled = false
            val intent = Intent(this, VerkoopPoliciesActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM, 5)
            startActivity(intent)
            Handler().postDelayed(Runnable {
                tvHelpCenter.isEnabled = true
            }, 1000)
        }
        tvPrivatePolicy.setOnClickListener {
            val intent = Intent(this, VerkoopPoliciesActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM, 6)
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
        if (Utils.isOnline(this)) {
            callInit()
            callLogOutApi()
            com.verkoopapp.utils.Utils.clearPreferences(this@SettingActivity)
//            val intent = Intent(this@SettingActivity, WalkThroughActivity::class.java)
//            startActivity(intent)
//            finishAffinity()
        } else {
            Utils.showSimpleMessage(this, "Please check your internet connection")
        }
    }

    private fun callLogOutApi() {
        ServiceHelper().getLogOutService(LogOutRequest(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt()), object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                val responseWallet = response.body() as AddItemResponse
                if (responseWallet.message.equals("Logged out successfully")) {
                    val intent = Intent(this@SettingActivity, WalkThroughActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                } else {
                    Utils.showSimpleMessage(this@SettingActivity, responseWallet.message!!).show()
                }
            }

            override fun onFailure(msg: String?) {
                pbProgressWallet.visibility = View.GONE
                Utils.showSimpleMessage(this@SettingActivity, msg!!).show()
            }
        })
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
            jsonObject.put("user_id", Utils.getPreferencesString(applicationContext, AppConstants.USER_ID).toInt())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }
}