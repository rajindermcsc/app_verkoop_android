package com.verkoopapp.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.verkoopapp.R
import com.verkoopapp.adapter.PaymentHistoryAdapter
import kotlinx.android.synthetic.main.my_wallet_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import android.app.Activity
import android.util.Log
import android.view.View
import com.verkoopapp.models.WalletHistoryResponse
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import retrofit2.Response

import de.wirecard.paymentsdk.models.WirecardCardPayment
import de.wirecard.paymentsdk.api.models.json.helpers.TransactionState
import com.verkoopapp.utils.SignatureHelper
import de.wirecard.paymentsdk.*
import de.wirecard.paymentsdk.models.PaymentPageStyle
import java.math.BigDecimal
import java.util.*


class MyWalletActivity : AppCompatActivity() {
    private lateinit var paymentHistoryAdapter: PaymentHistoryAdapter
    private var wirecardCardPayment : WirecardCardPayment?=null
    private lateinit var wirecardClient: WirecardClient
    val timestamp = SignatureHelper.generateTimestamp()!!
    private val merchantID = "F5785ECF-1EAE-40A0-9D37-93E2E8A4BAB3"
    private val secretKey = "1DBBBAAE-958E-4346-A27A-6BB5171CEEDC"
    private val currency = "ZAR"
    private val requestID = UUID.randomUUID().toString()
    private val transactionType = WirecardTransactionType.PURCHASE
    val amount = BigDecimal(10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_wallet_activity)
        setAdapter()
        setData()
        setPayment()
        if (Utils.isOnline(this)) {
                getWalletHistoryApi()
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }
    private var wirecardResponseListener = object : WirecardResponseListener {
        override fun onResponse(paymentResponse: WirecardPaymentResponse) {
            // handle server response
            if (paymentResponse.transactionState == TransactionState.SUCCESS) {
                // handle successful transaction
                Log.e("<<success>>",paymentResponse.authorizationCode)
            } else {
                Log.e("<<successElse>>",paymentResponse.authorizationCode)
                // handle unsuccessful transaction
            }
        }

        override fun onError(responseError: WirecardResponseError) {
            // handle error
            when (responseError.errorCode) {
                WirecardErrorCode.ERROR_CODE_GENERAL -> {
                    val detailedMessage = responseError.errorMessage
                    Log.e("<<failure>>",detailedMessage)
                }
                WirecardErrorCode.ERROR_CODE_INVALID_PAYMENT_DATA -> {
                    Log.e("<<failure>>","ERROR_CODE_INVALID_PAYMENT_DATA")
                }
                WirecardErrorCode.ERROR_CODE_NETWORK_ISSUE -> {
                    val detailedMessage = responseError.errorMessage
                    Log.e("<<failure>>",detailedMessage)
                }
                WirecardErrorCode.ERROR_CODE_USER_CANCELED -> {
                    Log.e("<<failure>>","ERROR_CODE_USER_CANCELED")
                }
            }//...
            //...
            //...
            //...
        }
    }
    private fun setPayment() {
        val signature = SignatureHelper.generateSignature(timestamp, merchantID, requestID, "purchase", amount, currency, secretKey)
        wirecardCardPayment =  WirecardCardPayment(signature, timestamp, requestID,merchantID, transactionType, amount, currency)
        val environment = WirecardEnvironment.TEST.value
        try {
            wirecardClient = WirecardClientBuilder.newInstance(this, environment)
                    .build()
        } catch (exception: WirecardException) {
            //device is rooted
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
            val style = PaymentPageStyle()
            style.payButtonBackgroundResourceId = R.color.colorPrimary
            style.toolbarResourceId =  R.color.colorPrimary
            style.inputLabelTextColor=R.color.dark_black
            wirecardClient.makePayment(wirecardCardPayment,style,wirecardResponseListener)
          //  val intent = Intent(this, AddMoneyDialogActivity::class.java)
         //   startActivityForResult(intent,2)
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
                Utils.saveIntPreferences(this@MyWalletActivity, AppConstants.AMOUNT, responseWallet.amount)
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