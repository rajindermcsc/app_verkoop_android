package com.verkoop.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.verkoop.R
import com.verkoop.adapter.BannerAdapter
import kotlinx.android.synthetic.main.toolbar_location.*
import kotlinx.android.synthetic.main.view_all_banner_activity.*


class ViewAllBannerActivity:AppCompatActivity(){
    private lateinit var bannerListAdapter:BannerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_all_banner_activity)
        setData()
        setAdapter()
    }

    private fun setAdapter() {
     val mLayoutManager=LinearLayoutManager(this)
        rvBanner.layoutManager=mLayoutManager
        bannerListAdapter= BannerAdapter(this)
        rvBanner.adapter=bannerListAdapter
    }

    private fun setData() {
        tvHeaderLoc.text=getString(R.string.advert_list)
        ivLeftLocation.setOnClickListener { onBackPressed() }
        ivRight.setImageResource(R.drawable.ic_add_black_24dp)
        ivRight.visibility= View.VISIBLE
        ivRight.setOnClickListener {
            val intent = Intent(this, UploadBannerActivity::class.java)
            startActivity(intent)
        }
    }

}