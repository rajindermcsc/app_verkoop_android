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
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.SimpleAdapter
import android.widget.Toast
import com.stripe.android.Stripe
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.view.CardMultilineWidget
import com.stripe.example.controller.ErrorDialogHandler
import com.stripe.example.controller.ProgressDialogController
import com.verkoopapp.models.AddMoneyRequest
import com.verkoopapp.models.UpdateWalletResponse
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
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal
import java.util.*

class MyWalletstripeActivity : AppCompatActivity() {
    private lateinit var paymentHistoryAdapter: PaymentHistoryAdapter
    private var wirecardCardPayment: WirecardCardPayment? = null
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
    var amount: BigDecimal? = null

    private val compositeDisposable = CompositeDisposable()
    private val cardSources = ArrayList<Map<String, String>>()

    private lateinit var cardMultilineWidget: CardMultilineWidget
    private lateinit var progressDialogController: ProgressDialogController
    private lateinit var errorDialogHandler: ErrorDialogHandler
    private lateinit var simpleAdapter: SimpleAdapter


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
        cardMultilineWidget = findViewById(R.id.card_multiline_widget)
        progressDialogController = ProgressDialogController(supportFragmentManager, resources)

        errorDialogHandler = ErrorDialogHandler(this)

    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
    private fun saveCard() {
        val card = cardMultilineWidget.card ?: return

        val stripe = Stripe(applicationContext,
                "pk_live_0QE5t1AQdS0YOx0xAzfzd8Dq00AYl5mZ6X")
        val cardSourceParams = PaymentMethodCreateParams.create(card.toPaymentMethodParamsCard(), null)
        // Note: using this style of Observable creation results in us having a method that
        // will not be called until we subscribe to it.
        val tokenObservable = Observable.fromCallable { stripe.createPaymentMethodSynchronous(cardSourceParams) }

        compositeDisposable.add(tokenObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progressDialogController.show(R.string.progressMessage) }
                .doOnComplete { progressDialogController.dismiss() }
                .subscribe(
                        { addToList(it) },
                        { throwable -> errorDialogHandler.show(throwable.localizedMessage) }
                )
        )
    }

    private fun addToList(paymentMethod: PaymentMethod?) {
        if (paymentMethod?.card == null) {
            return
        }

        val paymentMethodCard = paymentMethod.card
        val endingIn = getString(R.string.endingIn)
        val map = hashMapOf(
                "last4" to endingIn + " " + paymentMethodCard!!.last4,
                "tokenId" to paymentMethod.id!!
        )
        cardSources.add(map)
    }


    private var wirecardResponseListener = object : WirecardResponseListener {
        override fun onResponse(paymentResponse: WirecardPaymentResponse) {
            // handle server response
            if (paymentResponse.transactionState == TransactionState.SUCCESS) {
                // handle successful transaction
                getWalletHistoryApi()
                Log.e("<<success>>", paymentResponse.authorizationCode)
            } else {
                Log.e("<<successElse>>", paymentResponse.authorizationCode)
                // handle unsuccessful transaction
            }
        }

        override fun onError(responseError: WirecardResponseError) {
            when (responseError.errorCode) {
                WirecardErrorCode.ERROR_CODE_GENERAL -> {
                    val detailedMessage = responseError.errorMessage
                    Log.e("<<failure>>", detailedMessage)
                }
                WirecardErrorCode.ERROR_CODE_INVALID_PAYMENT_DATA -> {
                    Log.e("<<failure>>", "ERROR_CODE_INVALID_PAYMENT_DATA")
                }
                WirecardErrorCode.ERROR_CODE_NETWORK_ISSUE -> {
                    val detailedMessage = responseError.errorMessage
                    Log.e("<<failure>>", detailedMessage)
                }
                WirecardErrorCode.ERROR_CODE_USER_CANCELED -> {
                    Log.e("<<" +
                            "" +
                            "" +
                            ">>", "ERROR_CODE_USER_CANCELED")
                }
            }
        }
    }

    private fun setPayment(amountTotal: Int) {
        amount = BigDecimal(amountTotal)
        val signature = SignatureHelper.generateSignature(timestamp, merchantID, requestID, transactionType.value, amount, currency, secretKey)
        wirecardCardPayment = WirecardCardPayment(signature, timestamp, requestID, merchantID, transactionType, amount, currency)
        val environment = WirecardEnvironment.TEST.value
        try {
            wirecardClient = WirecardClientBuilder.newInstance(this, URL_EE_TEST)
                    .build()
        } catch (exception: WirecardException) {
            //device is rooted
        }
        val style = PaymentPageStyle()
        style.payButtonBackgroundResourceId = R.color.colorPrimary
        style.toolbarResourceId = R.color.colorPrimary
        style.inputLabelTextColor = R.color.dark_black
//        wirecardClient.makePayment(wirecardCardPayment,style,wirecardResponseListener)
        Client(this, URL_EE_TEST)
                .startPayment(getCardPayment(false, amountTotal))
    }

    fun getCardPayment(isAnimated: Boolean, amountTotal: Int): CardPayment {
        val timestamp = SignatureHelper.generateTimestamp()
        val merchantID = "33f6d473-3036-4ca5-acb5-8c64dac862d1"
        val secretKey = "9e0130f6-2e1e-4185-b0d5-dc69079c75cc"
        val requestID = UUID.randomUUID().toString()
        val transactionType = TransactionType.PURCHASE
        val amount = BigDecimal(amountTotal)
        val currency = "USD"
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
            tvAddMoney.isEnabled = false
            Handler().postDelayed(Runnable {
                tvAddMoney.isEnabled = true
            }, 700)
//            val intent = Intent(this, AddMoneyDialogActivity::class.java)
//            startActivityForResult(intent, 2)
            saveCard()
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
                if (paymentSdkResponse.errorMessage.equals("User has canceled the action.")) {

                } else {
                    var formattedResponse: String = ResponseHelper.getFormattedResponse(paymentSdkResponse)
                    Toast.makeText(this, formattedResponse, Toast.LENGTH_SHORT).show()
                    if (!(paymentSdkResponse as PaymentResponse).payment!!.requestedAmount!!.amount!!.equals("")) {
                        val amount = (paymentSdkResponse as PaymentResponse).payment!!.requestedAmount!!.amount!!
                        callUpdateWalletApi(amount)
                    } else {
                        Toast.makeText(this@MyWalletstripeActivity, "Something went wrong", Toast.LENGTH_LONG)
                    }
                }
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
                Utils.saveIntPreferences(this@MyWalletstripeActivity, AppConstants.AMOUNT, responseWallet.amount)
                if (responseWallet.data!!.isNotEmpty()) {
                    paymentHistoryAdapter.setData(responseWallet.data!!)
                    paymentHistoryAdapter.notifyDataSetChanged()

                } else {
                    Utils.showSimpleMessage(this@MyWalletstripeActivity, "No data found.").show()
                }

            }

            override fun onFailure(msg: String?) {
                pbProgressWallet.visibility = View.GONE
                Utils.showSimpleMessage(this@MyWalletstripeActivity, msg!!).show()
            }
        })
    }

    private fun callUpdateWalletApi(amount: String?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbProgressWallet.visibility = View.VISIBLE
        ServiceHelper().addMoneyService(AddMoneyRequest(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), amount!!.toDouble(), "12345878632"),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressWallet.visibility = View.GONE
                        val loginResponse = response.body() as UpdateWalletResponse
                        Utils.showToast(this@MyWalletstripeActivity, loginResponse.message)
                        val returnIntent = Intent()
                        returnIntent.putExtra(AppConstants.INTENT_RESULT, "success")
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                        //  onBackPressed()
                    }

                    override fun onFailure(msg: String?) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressWallet.visibility = View.GONE
                        Utils.showSimpleMessage(this@MyWalletstripeActivity, msg!!).show()
                    }
                })

    }

}