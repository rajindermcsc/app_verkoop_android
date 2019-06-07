package com.verkoopapp.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter



class HomePagerAdapter(fm: FragmentManager, private val pages: Int,private val fragmentList: ArrayList<Fragment>) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> fragmentList[position]
            1 -> fragmentList[position]
            2 -> fragmentList[position]
            else -> null
        }
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

}