package com.verkoopapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.verkoopapp.R
import kotlinx.android.synthetic.main.activity_food_filter.*
import kotlinx.android.synthetic.main.toolbar_chat.view.*

class FoodFilterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_filter)

        filter_tool_lyt.ivLeftLocation.setOnClickListener {
            finish()
        }
        filter_tool_lyt.tvHeaderLoc.text = getString(R.string.filter)

        tv_apply.setOnClickListener {
            val intent = Intent(this, FoodHomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}