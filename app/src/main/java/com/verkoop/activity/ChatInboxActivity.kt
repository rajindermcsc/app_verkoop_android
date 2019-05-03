package com.verkoop.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.verkoop.R
import com.verkoop.adapter.HomePagerAdapter
import com.verkoop.fragment.*
import kotlinx.android.synthetic.main.chat_inbox_activity.*
import kotlinx.android.synthetic.main.home_activity.*


class ChatInboxActivity:AppCompatActivity(){
    private var chatInboxFragment: ChatInboxFragment? = null
    private var buyingFragment: BuyingFragment? = null
    private var sellingFragment: SellingFragment? = null
    private var fragmentList = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_inbox_activity)
        chatInboxFragment = ChatInboxFragment.newInstance()
        buyingFragment = BuyingFragment.newInstance()
        sellingFragment = SellingFragment.newInstance()
        fragmentList.add(chatInboxFragment!!)
        fragmentList.add(buyingFragment!!)
        fragmentList.add(sellingFragment!!)
        setData()

    }

    private fun setData() {
        val adapter = HomePagerAdapter(supportFragmentManager, 3, fragmentList)
        vpChatInbox.adapter = adapter
        llAll.setOnClickListener {
            setNothing()
            tvAll.setTextColor(ContextCompat.getColor(this,R.color.text_gray_))
            vAll.visibility=View.VISIBLE
        }
        llBuying.setOnClickListener {
            setNothing()
            tvBuying.setTextColor(ContextCompat.getColor(this,R.color.text_gray_))
            vBuying.visibility=View.VISIBLE
        }
        llSelling.setOnClickListener {
            setNothing()
            tvSelling.setTextColor(ContextCompat.getColor(this,R.color.text_gray_))
            vSelling.visibility=View.VISIBLE
        }
    }

    private fun setNothing() {
        tvAll.setTextColor(ContextCompat.getColor(this,R.color.light_gray))
        tvBuying.setTextColor(ContextCompat.getColor(this,R.color.light_gray))
        tvSelling.setTextColor(ContextCompat.getColor(this,R.color.light_gray))
        vAll.visibility=View.INVISIBLE
        vBuying.visibility=View.INVISIBLE
        vSelling.visibility=View.INVISIBLE
    }

}