package com.verkoop.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.verkoop.R
import com.verkoop.adapter.HomePagerAdapter
import kotlinx.android.synthetic.main.home_fragment.*

class HomeActivity:AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_fragment)
        setData()
    }

    private fun setTabLayout() {
        bottomTabLayout.setButtonTextStyle(R.style.TextGray12)
        // set buttons from menu resource
        bottomTabLayout.setItems(R.menu.menu_bottom_layout)
        //set on selected tab listener.
        bottomTabLayout.setListener { position ->
            viewPager.currentItem = position
        }
        bottomTabLayout.setSelectedTab(R.id.menu_button1)
        //enable indicator
        bottomTabLayout.setIndicatorVisible(true)
        //indicator height
        bottomTabLayout.setIndicatorHeight(resources.getDimension(R.dimen.dp_2))
        //indicator color
        bottomTabLayout.setIndicatorColor(R.color.white)
        //indicator line color
        bottomTabLayout.setIndicatorLineColor(R.color.colorPrimary)
        //bottomTabLayout.setSelectedTab(R.id.menu_button5)
    }

    private fun setData() {
        val adapter = HomePagerAdapter(supportFragmentManager, 3)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 3
        setTabLayout()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}