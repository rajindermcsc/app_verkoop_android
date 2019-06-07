package com.verkoopapp.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.verkoopapp.R
import com.verkoopapp.adapter.GetCoinAdapter
import com.verkoopapp.fragment.*
import kotlinx.android.synthetic.main.coins_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*


class CoinsActivity:AppCompatActivity(), GetCoinsFragment.CoinUpdateCallBack {
    private var fragmentList = ArrayList<Fragment>()
    private var getCoinFragment: GetCoinsFragment? = null
    private var getHistoryFragment: HistoryFragment? = null
    override fun updateHistoryList(totalCoin: Int, type: Int) {
        if(type==2){
            tvTotalCoin.text=((tvTotalCoin.text.toString()).toInt()+totalCoin).toString()
            getHistoryFragment!!.refreshApi()
        }else if(type!=0) {
            getHistoryFragment!!.refreshApi()
        }else{
            tvTotalCoin.text=totalCoin.toString()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.coins_activity)
        getCoinFragment = GetCoinsFragment.newInstance()
        getHistoryFragment = HistoryFragment.newInstance()
        fragmentList.add(getCoinFragment!!)
        fragmentList.add(getHistoryFragment!!)
        setData()
        setAdapter()
    }

    private fun setData() {
        ivLeftLocation.setOnClickListener { onBackPressed() }
        tvHeaderLoc.text=getString(R.string.coins)
        llHistory.setOnClickListener {vpGetCoin.currentItem=1  }
        llCoin.setOnClickListener {vpGetCoin.currentItem=0  }
    }
    private fun setAdapter() {
        val mAdapter = GetCoinAdapter(supportFragmentManager,fragmentList)
        vpGetCoin.adapter = mAdapter
        vpGetCoin.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
               highlightIndicator(position)
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    private fun highlightIndicator(position: Int) {
        if(position==0){
            ivTipCoin.visibility=View.VISIBLE
            ivTipHistory.visibility=View.INVISIBLE
            tvGetCoin.setTextColor(ContextCompat.getColor(this,R.color.yellow))
            tvHistory.setTextColor(ContextCompat.getColor(this,R.color.light_gray))
        }else{
            ivTipCoin.visibility=View.INVISIBLE
            ivTipHistory.visibility=View.VISIBLE
            tvHistory.setTextColor(ContextCompat.getColor(this,R.color.yellow))
            tvGetCoin.setTextColor(ContextCompat.getColor(this,R.color.light_gray))
        }
    }
}