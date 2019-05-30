package com.verkoop.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.github.florent37.viewanimator.ViewAnimator

import com.verkoop.R
import com.verkoop.models.AddMoneyRequest
import com.verkoop.models.DisLikeResponse
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.KeyboardUtil
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.add_money_dialog_activity.*
import retrofit2.Response
import android.app.Activity
import android.content.Intent




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
        pbProgressAddMoney.indeterminateDrawable.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY)
        tvProceed.setOnClickListener {
            if (Utils.isOnline(this)) {
          if(!TextUtils.isEmpty(etAmount.text.toString())) {
              KeyboardUtil.hideKeyboard(this)
              callUpdateWalletApi()
          }
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
        }
        cancel.setOnClickListener { onBackPressed() }
    }

    private fun callUpdateWalletApi() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbProgressAddMoney.visibility = View.VISIBLE
        ServiceHelper().addMoneyService(AddMoneyRequest(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), (etAmount.text.toString()).toDouble(), ""),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressAddMoney.visibility = View.GONE
                        val loginResponse = response.body() as DisLikeResponse
                        val returnIntent = Intent()
                        returnIntent.putExtra(AppConstants.INTENT_RESULT, "success")
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                      //  onBackPressed()
                    }

                    override fun onFailure(msg: String?) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressAddMoney.visibility = View.GONE
                        Utils.showSimpleMessage(this@AddMoneyDialogActivity, msg!!).show()
                    }
                })

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
                    val returnIntent = Intent()
                    setResult(Activity.RESULT_CANCELED, returnIntent)
                    finish()
                    overridePendingTransition(0, 0)
                }
                .start()
    }
}