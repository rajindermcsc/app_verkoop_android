package com.verkoop.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.verkoop.fragment.HomeFragment


class HomePagerAdapter(fm: FragmentManager, private val pages: Int) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> HomeFragment.newInstance()
            1 -> HomeFragment.newInstance()
            2 -> HomeFragment.newInstance()
            else -> null
        }
    }

    override fun getCount(): Int {
        return pages
    }

}