package com.verkoop.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.github.florent37.viewanimator.ViewAnimator

import com.verkoop.R
import com.verkoop.utils.KeyboardUtil
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.add_money_dialog_activity.*



class AddMoneyDialogActivity:AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_money_dialog_activity)
        KeyboardUtil(this, llParentAddMoney)
        setData()
        setAnimation()
    }
    private fun setAnimation() {
        ViewAnimator
                .animate(flParentAdd)
                .translationY(1000f, 0f)
                .duration(400)
                .start()
    }
    private fun setData() {
        tvProceed.setOnClickListener { if (Utils.isOnline(this)) {
           // if (isValidate()) {
                KeyboardUtil.hideKeyboard(this)
               // callChangePasswordApi()
            //}
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }}
        cancel.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        ViewAnimator
                .animate(flParentAdd)
                .translationY(0f, 2000f)
                .duration(400)
                .andAnimate(llParentAddMoney)
                .alpha(1f, 0f)
                .duration(500)
                .onStop {
                    llParentAddMoney.visibility = View.GONE
                    finish()
                    overridePendingTransition(0, 0)
                }
                .start()
    }
}