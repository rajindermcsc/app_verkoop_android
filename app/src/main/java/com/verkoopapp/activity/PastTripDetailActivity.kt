package com.verkoopapp.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.verkoopapp.R
import kotlinx.android.synthetic.main.activity_past_trip_detail.*

class PastTripDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_trip_detail)
        img_back.setOnClickListener {
            finish()
        }
    }
}