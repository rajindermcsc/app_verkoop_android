package com.verkoop.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ksmtrivia.common.BaseFragment
import com.verkoop.R

class ActivitiesFragment:BaseFragment(){
     val TAG=ActivitiesFragment::class.java.simpleName.toString()
    override fun getTitle(): Int {
        return 0
    }

    override fun getFragmentTag(): String? {
       return TAG
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activities_fragment,container,false)
    }
    companion object {
        fun newInstance():ActivitiesFragment{
            val args = Bundle()
            val fragment=ActivitiesFragment()
            fragment.arguments = args
            return fragment
        }
    }

}