package com.verkoop.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.verkoop.R
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.pick_option_ctivity.*


class PickOptionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pick_option_ctivity)
        setData()
    }

    private fun setData() {
        tvSellNow.setOnClickListener {
            Utils.savePreferencesString(this,AppConstants.COMING_FROM,"PickOptionActivity")
            val intent = Intent(this@PickOptionActivity, HomeActivity::class.java)
            intent.putExtra(AppConstants.PICK_OPTION,1)
            startActivity(intent)
            finish()
        }
        tvShopNow.setOnClickListener {
            Utils.savePreferencesString(this,AppConstants.COMING_FROM,"PickOptionActivity")
            val intent = Intent(this@PickOptionActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        tvMayBeLater.setOnClickListener {
            Utils.savePreferencesString(this, AppConstants.COMING_FROM,"PickOptionActivity")
            val intent = Intent(this@PickOptionActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}