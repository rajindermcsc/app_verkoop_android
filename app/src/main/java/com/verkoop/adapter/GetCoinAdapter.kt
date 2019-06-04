package com.verkoop.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.verkoop.fragment.GetCoinsFragment
import com.verkoop.fragment.HistoryFragment


class GetCoinAdapter(fm: FragmentManager,private val fragmentList: ArrayList<Fragment>) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }


    override fun getCount(): Int {
        return fragmentList.size
    }
}