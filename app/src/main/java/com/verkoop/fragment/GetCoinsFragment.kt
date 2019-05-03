package com.verkoop.fragment

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ksmtrivia.common.BaseFragment
import com.verkoop.R
import com.verkoop.activity.CoinsActivity
import com.verkoop.adapter.CoinListAdapter
import kotlinx.android.synthetic.main.get_coin_fragment.*


class GetCoinsFragment:BaseFragment(){
    private lateinit var coinsActivity: CoinsActivity
    private val TAG=GetCoinsFragment::class.java.simpleName


    override fun getTitle(): Int {
        return 0
    }

    override fun getFragmentTag(): String? {
        return TAG
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        coinsActivity=activity as CoinsActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.get_coin_fragment,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
    }

    private fun setAdapter() {
        val layoutManager=GridLayoutManager(coinsActivity,3)
        rvCoinsList.layoutManager=layoutManager
        val getCoinAdapter= CoinListAdapter(coinsActivity,rvCoinsList)
        rvCoinsList.adapter=getCoinAdapter
    }

    companion object {
        fun newInstance():GetCoinsFragment{
            val arg = Bundle()
            val fragment = GetCoinsFragment()
            fragment.arguments = arg
            return fragment
        }
    }
}