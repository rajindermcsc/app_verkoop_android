package com.verkoop.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.verkoop.R
import com.verkoop.adapter.QRScannerAdapter
import com.verkoop.fragment.MyCodeFragment
import kotlinx.android.synthetic.main.qr_scann_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*


class QRScannerActivity:AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_scann_activity)
        setViewPager()
        setData()
    }

    private fun setViewPager() {
        val mAdapter = QRScannerAdapter(supportFragmentManager)
        mAdapter.addFrag( MyCodeFragment.newInstance(), "MY CODE")
        mAdapter.addFrag(MyCodeFragment.newInstance(), "SCAN CODE")
        vpQrScanner.adapter = mAdapter
    }

    private fun setData() {
        ivLeftLocation.setOnClickListener { onBackPressed() }
        tvHeaderLoc.text = getString(R.string.qr_code)
        tabs.setupWithViewPager(vpQrScanner)
    }
}