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
import android.widget.Toast
import com.verkoopapp.models.WalletHistoryResponse
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.ResponseHelper
import com.verkoopapp.utils.Utils
import retrofit2.Response

import com.verkoopapp.utils.SignatureHelper
import com.wirecard.ecom.Client
import com.wirecard.ecom.card.model.CardPayment
import com.wirecard.ecom.model.TransactionState
import com.wirecard.ecom.model.TransactionType
import com.wirecard.ecom.model.out.PaymentResponse
import de.wirecard.paymentsdk.*
import de.wirecard.paymentsdk.models.PaymentPageStyle
import de.wirecard.paymentsdk.models.WirecardCardPayment
import java.math.BigDecimal
import java.util.*

class MyWalletActivity : AppCompatActivity() {
    private lateinit var paymentHistoryAdapter: PaymentHistoryAdapter
        private var wirecardCardPayment : WirecardCardPayment?=null
    private lateinit var wirecardClient: WirecardClient
    val timestamp = SignatureHelper.generateTimestamp()!!
    //private val merchantID = "F5785ECF-1EAE-40A0-9D37-93E2E8A4BAB3"
    // private val secretKey = "1DBBBAAE-958E-4346-A27A-6BB5171CEEDC"
    private val merchantID = "33f6d473-3036-4ca5-acb5-8c64dac862d1"
    //    private val merchantID = "F5785ECF-1EAE-40A0-9D37-93E2E8A4BAB3"
    private val secretKey = "9e0130f6-2e1e-4185-b0d5-dc69079c75cc"
    private val currency = "USD"
    private val requestID = UUID.randomUUID().toString()
    val URL_EE_TEST = "https://api-test.wirecard.com"
    private val transactionType = WirecardTransactionType.PURCHASE
    var amount:BigDecimal?=null

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

    private var wirecardResponseListener = object : WirecardResponseListener {
        override fun onResponse(paymentResponse: WirecardPaymentResponse) {
            // handle server response
            if (paymentResponse.transactionState == TransactionState.SUCCESS) {
                // handle successful transaction
                getWalletHistoryApi()
                Log.e("<<success>>",paymentResponse.authorizationCode)
            } else {
                Log.e("<<successElse>>",paymentResponse.authorizationCode)
                // handle unsuccessful transaction
            }
        }

        override fun onError(responseError: WirecardResponseError) {
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
            }
        }
    }
    private fun setPayment(amountTotal:Int) {
        amount=BigDecimal(amountTotal)
        val signature = SignatureHelper.generateSignature(timestamp, merchantID, requestID, transactionType.value, amount, currency, secretKey)
        wirecardCardPayment =  WirecardCardPayment(signature, timestamp, requestID,merchantID, transactionType, amount, currency)
        val environment = WirecardEnvironment.TEST.value
        try {
            wirecardClient = WirecardClientBuilder.newInstance(this, URL_EE_TEST)
                    .build()
        } catch (exception: WirecardException) {
            //device is rooted
        }
        val style = PaymentPageStyle()
        style.payButtonBackgroundResourceId = R.color.colorPrimary
        style.toolbarResourceId =  R.color.colorPrimary
        style.inputLabelTextColor=R.color.dark_black
        wirecardClient.makePayment(wirecardCardPayment,style,wirecardResponseListener)
//    Client(this, URL_EE_TEST)
//            .startPayment(getCardPayment(false))
    }
    fun getCardPayment(isAnimated: Boolean): CardPayment {
        val timestamp = SignatureHelper.generateTimestamp()
        val merchantID = "33f6d473-3036-4ca5-acb5-8c64dac862d1"
        val secretKey = "9e0130f6-2e1e-4185-b0d5-dc69079c75cc"
        val requestID = UUID.randomUUID().toString()
        val transactionType = TransactionType.PURCHASE
        val amount = BigDecimal(5)
        val currency = "EUR"
        val signature = SignatureHelper.generateSignature(timestamp, merchantID, requestID, transactionType.value, amount, currency, secretKey)
        val cardPayment = CardPayment.Builder()
                .setSignature(signature)
                .setMerchantAccountId(merchantID)
                .setRequestId(requestID)
                .setAmount(amount)
                .setTransactionType(transactionType)
                .setCurrency(currency)
                .build()
        cardPayment.requireManualCardBrandSelection = true
        cardPayment.animatedCardPayment = isAnimated
        return cardPayment
    }

    private fun setAdapter() {
        val mManager = LinearLayoutManager(this)
        rvHistory.layoutManager = mManager
        paymentHistoryAdapter = PaymentHistoryAdapter(this, 0)
        rvHistory.adapter = paymentHistoryAdapter
    }

    private fun setData() {
        ivLeftLocation.setOnClickListener { onBackPressed() }
        tvHeaderLoc.text = getString(R.string.my_wallet)
        tvAddMoney.setOnClickListener {
            val intent = Intent(this, AddMoneyDialogActivity::class.java)
            startActivityForResult(intent, 2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getStringExtra(AppConstants.INTENT_RESULT)
                val amountMoney = data.getIntExtra(AppConstants.AMOUNT, 0)
                if (Utils.isOnline(this)) {
                    Log.e("activityResult", "success")
                    setPayment(amountMoney)

                } else {
                    Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == Client.PAYMENT_SDK_REQUEST_CODE) {
            val paymentSdkResponse = data!!.getSerializableExtra(Client.EXTRA_PAYMENT_SDK_RESPONSE)
            if (paymentSdkResponse is PaymentResponse) {
                val formattedResponse = ResponseHelper.getFormattedResponse(paymentSdkResponse)
                Toast.makeText(this, formattedResponse, Toast.LENGTH_SHORT).show()
            }
        }
    }//on

    private fun getWalletHistoryApi() {
        pbProgressWallet.visibility = View.VISIBLE
        ServiceHelper().getWalletHistoryService(Utils.getPreferencesString(this, AppConstants.USER_ID), object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                pbProgressWallet.visibility = View.GONE
                val responseWallet = response.body() as WalletHistoryResponse
                tvTotalAmount.text = responseWallet.amount.toString()
                Utils.saveIntPreferences(this@MyWalletActivity, AppConstants.AMOUNT, responseWallet.amount)
                if (responseWallet.data!!.isNotEmpty()) {
                    paymentHistoryAdapter.setData(responseWallet.data!!)
                    paymentHistoryAdapter.notifyDataSetChanged()

                } else {
                    Utils.showSimpleMessage(this@MyWalletActivity, "No data found.").show()
                }

            }

            override fun onFailure(msg: String?) {
                pbProgressWallet.visibility = View.GONE
                Utils.showSimpleMessage(this@MyWalletActivity, msg!!).show()
            }
        })

    }
}