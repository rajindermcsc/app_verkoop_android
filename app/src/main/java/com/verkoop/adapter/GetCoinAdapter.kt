package com.verkoop.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.verkoop.fragment.GetCoinsFragment
import com.verkoop.fragment.HistoryFragment


class GetCoinAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return if(position==0){
            GetCoinsFragment.newInstance()
        }else{
            HistoryFragment.newInstance()
        }
    }


    override fun getCount(): Int {
        return 2
    }
}