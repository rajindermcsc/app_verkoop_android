package com.verkoopapp.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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


}