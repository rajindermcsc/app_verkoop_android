package com.verkoopapp.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import com.verkoopapp.R
import com.verkoopapp.models.VerifyNumberRequest
import com.verkoopapp.models.VerifyNumberResponse
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.toolbar_location.*
import kotlinx.android.synthetic.main.verify_number_activity.*
import retrofit2.Response
import android.app.Activity




class VerifyNumberActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verify_number_activity)
        ccpPhone.setCountryForPhoneCode(27)
        setData()
    }

    private fun setData() {
        ivLeftLocation.setOnClickListener { }
        tvHeaderLoc.text = getString(R.string.verify_phone_no)
        tvGetCode.setOnClickListener {
            if (isValidate()) {
                if (Utils.isOnline(this)) {
                    verifyPhoneNoApi()
                } else {
                    Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
                }

            }
        }
    }

    private fun isValidate(): Boolean {
        return if (TextUtils.isEmpty(etPhoneNo.text.toString())) {
            Utils.showSimpleMessage(this, "Please enter phone number.").show()
            false
        } else if (etPhoneNo.text.toString().length < 10) {
            Utils.showSimpleMessage(this, "Please enter valid phone number.").show()
            false
        } else {
            true
        }
    }

    private fun verifyPhoneNoApi() {
        pbProgressPhone.visibility = View.VISIBLE
        ServiceHelper().verifyMobileNo(VerifyNumberRequest(ccpPhone.selectedCountryCodeWithPlus + etPhoneNo.text.toString().trim()), Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        val loginResponse = response.body() as VerifyNumberResponse
                        pbProgressPhone.visibility = View.GONE
                        val intent = Intent(this@VerifyNumberActivity, VerifyOtpDialogActivity::class.java)
                            intent.putExtra(AppConstants.PHONE_NO,ccpPhone.selectedCountryCodeWithPlus + etPhoneNo.text.toString().trim())
                        startActivityForResult(intent, 2)
                    }

                    override fun onFailure(msg: String?) {
                        pbProgressPhone.visibility = View.GONE
                        Utils.showSimpleMessage(this@VerifyNumberActivity, msg!!).show()
                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getStringExtra(AppConstants.PHONE_NO)
                val returnIntent = Intent()
                returnIntent.putExtra(AppConstants.PHONE_NO,result)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
                overridePendingTransition(0, 0)
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

}