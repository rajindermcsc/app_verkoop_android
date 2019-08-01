package com.verkoopapp.activity

import android.content.Intent
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import io.branch.referral.Branch
import org.json.JSONException


class SplashActivity : AppCompatActivity() {
    private var id = 0
    private var type = 0

    private fun setAppIntent() {
        Handler().postDelayed({
            if (!TextUtils.isEmpty(Utils.getPreferencesString(this, AppConstants.USER_ID))) {
                if (!TextUtils.isEmpty(Utils.getPreferencesString(this, AppConstants.COMING_FROM)) && Utils.getPreferencesString(this, AppConstants.COMING_FROM).equals("category_screen", ignoreCase = true)) {
                    val intent = Intent(this, PickOptionActivity::class.java)
                    intent.putExtra(AppConstants.ID, id)
                    intent.putExtra(AppConstants.TYPE, type)
                    startActivity(intent)
                    finish()
                } else if (!TextUtils.isEmpty(Utils.getPreferencesString(this, AppConstants.COMING_FROM)) && Utils.getPreferencesString(this, AppConstants.COMING_FROM).equals("PickOptionActivity", ignoreCase = true)) {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra(AppConstants.ID, id)
                    intent.putExtra(AppConstants.TYPE, type)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, CategoriesActivity::class.java)
                    intent.putExtra(AppConstants.ID, id)
                    intent.putExtra(AppConstants.TYPE, type)
                    startActivity(intent)
                    finish()
                }
            } else {
                val intent = Intent(this, WalkThroughActivity::class.java)
                intent.putExtra(AppConstants.ID, id)
                intent.putExtra(AppConstants.TYPE, type)
                startActivity(intent)
                finish()
            }

        }, 2000)
    }

    override fun onStart() {
        super.onStart()
        branchInit()
    }

    private fun branchInit() {
        // Branch init
        Branch.getInstance().initSession({ referringParams, error ->
            if (error == null) {
                Log.e("BRANCH SDK", referringParams.toString())
                Log.e("<<<LinkRequest>>>", Gson().toJson(referringParams))
                Log.e("<<<LinkRequestString>>>", referringParams.toString())
                Log.i("BRANCH SDK", referringParams.toString())
                try {
                    type = referringParams.getString("type").toInt()
                    if (type == 1) {
                        id = referringParams.getString("product_id").toInt()
                    } else if (type == 2) {
                        id = referringParams.getString("user_id").toInt()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            } else {
                Log.e("BRANCH SDK", error.message)
            }
            setAppIntent()
        }, this.intent.data, this)
    }

    public override fun onNewIntent(intent: Intent) {
        this.intent = intent
    }


}