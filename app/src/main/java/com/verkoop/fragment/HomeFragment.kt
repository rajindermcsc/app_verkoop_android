package com.verkoop.fragment

import android.content.Context
import android.os.Bundle
import com.ksmtrivia.common.BaseFragment
import com.verkoop.activity.HomeActivity


class HomeFragment:BaseFragment(){
    val TAG = HomeFragment::class.java.simpleName.toString()
    private lateinit var homeActivity: HomeActivity
    override fun getTitle(): Int {
        return 0
    }

    override fun getFragmentTag(): String? {
       return TAG
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        homeActivity=activity as HomeActivity
    }
    companion object {
        fun newInstance(): HomeFragment {
            val args = Bundle()
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }
}