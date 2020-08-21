package com.verkoopapp.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.verkoopapp.R
import kotlinx.android.synthetic.main.activity_package_detail.*

class PackageDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package_detail)
        img_back.setOnClickListener {
            finish()
        }
    }
}