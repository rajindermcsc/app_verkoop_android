package com.verkoopapp.activity

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.Token
import com.stripe.android.view.CardInputWidget
import com.stripe.android.view.CardMultilineWidget
import com.verkoopapp.R
import com.verkoopapp.models.AddMoneyRequest
import com.verkoopapp.models.UpdateWalletResponse
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.activity_stripe_card_payment.*
import kotlinx.android.synthetic.main.my_wallet_activity.*
import retrofit2.Response
import java.lang.Exception

class StripeCardPaymentActivity : AppCompatActivity() {
    private lateinit var cardInputWidget: CardMultilineWidget
    var token: String? = null
    var amountMoney: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stripe_card_payment)
        cardInputWidget = findViewById(R.id.card_input_widget)
        amountMoney = intent.getIntExtra(AppConstants.AMOUNT, 0)
        tvPurchase.setOnClickListener {
            tvPurchase.isEnabled = false
            setStripePayment()
        }
    }

    private fun setStripePayment() {
        if (cardInputWidget.card == null) {
            tvPurchase.isEnabled = true
            Toast.makeText(this, "Please fill the card details correctly", Toast.LENGTH_SHORT).show()
        }
        val card = cardInputWidget.card ?: return

        val stripe = Stripe(applicationContext,
                "pk_test_IkEuiX8PBSrxqDOnx7W79ubE006HXByoRc")

        stripe.createToken(card, object : ApiResultCallback<Token> {
            override fun onSuccess(result: Token) {
                Log.d("Stripe", result.toString())
                token = result.id
                callUpdateWalletApi(amountMoney.toString())
            }

            override fun onError(e: Exception) {
                Log.d("Stripe", e.printStackTrace().toString())
                if (e.message!!.contains("Please check your internet")) {
                    Utils.showSimpleMessage(this@StripeCardPaymentActivity, "Please check your internet connection")
                }
                tvPurchase.isEnabled = true
            }

        })
    }

    private fun callUpdateWalletApi(amount: String?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbProgressStripeWallet.visibility = View.VISIBLE
        ServiceHelper().addMoneyService(AddMoneyRequest(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), amount!!.toDouble(), token!!, "USD"),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        tvPurchase.isEnabled = true
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressStripeWallet.visibility = View.GONE
                        val loginResponse = response.body() as UpdateWalletResponse
                        Utils.showToast(this@StripeCardPaymentActivity, loginResponse.message)
                        val returnIntent = Intent()
                        returnIntent.putExtra(AppConstants.INTENT_RESULT, "success")
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                        //  onBackPressed()
                    }

                    override fun onFailure(msg: String?) {
                        tvPurchase.isEnabled = true
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressStripeWallet.visibility = View.GONE
                        Utils.showSimpleMessage(this@StripeCardPaymentActivity, msg!!).show()
                    }
                })

    }

}
