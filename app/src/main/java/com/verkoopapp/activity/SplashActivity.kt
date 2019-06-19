package com.verkoopapp.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils

import com.google.gson.Gson
import io.branch.referral.Branch

import org.json.JSONException



class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun setAppIntent() {
        Handler().postDelayed({
            if (!TextUtils.isEmpty(Utils.getPreferencesString(this, AppConstants.USER_ID))) {
                if (!TextUtils.isEmpty(Utils.getPreferencesString(this, AppConstants.COMING_FROM)) && Utils.getPreferencesString(this, AppConstants.COMING_FROM).equals("category_screen", ignoreCase = true)) {
                    val intent = Intent(this, PickOptionActivity::class.java)
                    // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }else if(!TextUtils.isEmpty(Utils.getPreferencesString(this, AppConstants.COMING_FROM)) && Utils.getPreferencesString(this, AppConstants.COMING_FROM).equals("PickOptionActivity", ignoreCase = true)){
                    val intent = Intent(this, HomeActivity::class.java)
                    //  intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }else{
                    val intent = Intent(this, CategoriesActivity::class.java)
                    // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            } else {
                val intent = Intent(this, WalkThroughActivity::class.java)
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
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
                 val   meetingId = referringParams.getString("product_id")
                    Log.e("<<ProductId>>", meetingId)
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