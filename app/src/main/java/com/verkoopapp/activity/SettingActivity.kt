package com.verkoopapp.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.verkoopapp.R
import com.verkoopapp.utils.DeleteCommentDialog
import com.verkoopapp.utils.SelectionListener
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.setting_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*

class SettingActivity : AppCompatActivity() {

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
        tvPrivatePolicy.setOnClickListener { }
        tvHelpCenter.setOnClickListener { }
        tvContactUs.setOnClickListener { }
        tvAboutVerkoop.setOnClickListener { }
        tvTermsService.setOnClickListener { }
        tvPrivacyPolicy.setOnClickListener { }
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
        com.verkoopapp.utils.Utils.clearPreferences(this@SettingActivity)
        val intent = Intent(this@SettingActivity, WalkThroughActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}