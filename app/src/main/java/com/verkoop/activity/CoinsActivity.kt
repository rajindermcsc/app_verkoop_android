package com.verkoop.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.verkoop.R
import com.verkoop.adapter.GetCoinAdapter
import kotlinx.android.synthetic.main.coins_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*


class CoinsActivity:AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.coins_activity)
        setData()
        setAdapter()
    }

    private fun setData() {
        ivLeftLocation.setOnClickListener { onBackPressed() }
        tvHeaderLoc.text=getString(R.string.coins)
        llHistory.setOnClickListener {vpGetCoin.currentItem=1  }
        llCoin.setOnClickListener {vpGetCoin.currentItem=0  }
    }
    private fun setAdapter() {
        val mAdapter = GetCoinAdapter(supportFragmentManager)
        vpGetCoin.adapter = mAdapter
        vpGetCoin.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
               highlightIndicator(position)
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    private fun highlightIndicator(position: Int) {
        if(position==0){
            ivTipCoin.visibility=View.VISIBLE
            ivTipHistory.visibility=View.INVISIBLE
            tvGetCoin.setTextColor(ContextCompat.getColor(this,R.color.yellow))
            tvHistory.setTextColor(ContextCompat.getColor(this,R.color.light_gray))
        }else{
            ivTipCoin.visibility=View.INVISIBLE
            ivTipHistory.visibility=View.VISIBLE
            tvHistory.setTextColor(ContextCompat.getColor(this,R.color.yellow))
            tvGetCoin.setTextColor(ContextCompat.getColor(this,R.color.light_gray))
        }
    }
}