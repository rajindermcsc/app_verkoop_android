package com.verkoop.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ksmtrivia.common.BaseFragment
import com.verkoop.R

class HistoryFragment:BaseFragment(){
    private val TAG=HistoryFragment::class.java.simpleName
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
        return inflater.inflate(R.layout.history_fragment,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance():HistoryFragment{
            val arg = Bundle()
            val fragment = HistoryFragment()
            fragment.arguments = arg
            return fragment
        }
    }

}