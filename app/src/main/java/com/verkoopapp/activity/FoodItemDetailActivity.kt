package com.verkoopapp.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.verkoopapp.R
import kotlinx.android.synthetic.main.activity_food_item_detail.*
import kotlinx.android.synthetic.main.toolbar_location.view.*

class FoodItemDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_item_detail)


        item_tool_lyt.tvHeaderLoc.text = getString(R.string.item_details)
        item_tool_lyt.ivRight.setImageDrawable(getDrawable(R.mipmap.ic_heart_white))
        item_tool_lyt.ivLeftLocation.setOnClickListener {
            finish()
        }

        tv_add_order.setOnClickListener {
            val intent = Intent(this, CheckoutActivity::class.java)
            startActivity(intent)
        }

    }
}