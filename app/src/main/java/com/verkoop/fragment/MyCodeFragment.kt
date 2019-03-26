package com.verkoop.fragment

import android.os.Bundle
import com.ksmtrivia.common.BaseFragment

/**
 * Created by intel on 25-03-2019.
 */
class MyCodeFragment:BaseFragment(){
    private  var TAG=MyCodeFragment::class.java.simpleName.toString()
    override fun getTitle(): Int {
        return 0
    }

    override fun getFragmentTag(): String? {
       return TAG
    }

    companion object {
        fun newInstance():MyCodeFragment{
            val agr=Bundle()
            val fragment=MyCodeFragment()
            fragment.arguments=agr
            return fragment
        }
    }
}