package com.verkoop.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.verkoop.R
import com.verkoop.adapter.PaymentHistoryAdapter
import kotlinx.android.synthetic.main.my_wallet_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import android.app.Activity
import android.util.Log
import android.view.View
import com.verkoop.models.WalletHistoryResponse
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import retrofit2.Response


class MyWalletActivity : AppCompatActivity() {
    private lateinit var paymentHistoryAdapter: PaymentHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_wallet_activity)
        setAdapter()
        setData()
        if (Utils.isOnline(this)) {
                getWalletHistoryApi()
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }

    private fun setAdapter() {
        val mManager = LinearLayoutManager(this)
        rvHistory.layoutManager = mManager
        paymentHistoryAdapter = PaymentHistoryAdapter(this,0)
        rvHistory.adapter = paymentHistoryAdapter
    }

    private fun setData() {
        ivLeftLocation.setOnClickListener { onBackPressed() }
        tvHeaderLoc.text = getString(R.string.my_wallet)
        tvAddMoney.setOnClickListener {
            val intent = Intent(this, AddMoneyDialogActivity::class.java)
            startActivityForResult(intent,2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getStringExtra(AppConstants.INTENT_RESULT)
                if (Utils.isOnline(this)) {
                    Log.e("activityResult","success")
                    getWalletHistoryApi()
                } else {
                    Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//on

    private fun getWalletHistoryApi() {
        pbProgressWallet.visibility= View.VISIBLE
        ServiceHelper().getWalletHistoryService(Utils.getPreferencesString(this,AppConstants.USER_ID),object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                pbProgressWallet.visibility= View.GONE
                val responseWallet = response.body() as WalletHistoryResponse
                tvTotalAmount.text=responseWallet.amount.toString()
                if (responseWallet.data!!.isNotEmpty()) {
                    paymentHistoryAdapter.setData(responseWallet.data!!)
                    paymentHistoryAdapter.notifyDataSetChanged()

                }else{
                    Utils.showSimpleMessage(this@MyWalletActivity, "No data found.").show()
                }

            }

            override fun onFailure(msg: String?) {
                pbProgressWallet.visibility= View.GONE
                Utils.showSimpleMessage(this@MyWalletActivity, msg!!).show()
            }
        })

    }
}