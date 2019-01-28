package com.verkoop.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.verkoop.R
import com.verkoop.adapter.FilterAdapter
import kotlinx.android.synthetic.main.filter_activity.*
import kotlinx.android.synthetic.main.toolbar_filter.*


class FilterActivity:AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filter_activity)
        setData()
        setItemList()
    }

    private fun setData() {
        iv_left.setOnClickListener { onBackPressed() }
    }
    private fun setItemList() {
        val mManager= LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvFilter.layoutManager=mManager
        val filterAdapter= FilterAdapter(this)
        rvFilter.adapter=filterAdapter
    }
}