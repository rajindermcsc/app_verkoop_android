package com.verkoop.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.verkoop.R
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.android.synthetic.main.signup_activity.*

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)
        setData()
        ccp.setCountryForPhoneCode(1)
        /* setTTFfont(countryCodePicker)
         fun setTTFfont(ccp: CountryCodePicker) {
             val typeFace = Typeface.createFromAsset(this.assets, "fonts/Nexa Light.otf")
             ccp.setTypeFace(typeFace)
         }*/
    }

    private fun setData() {
        tvLoginS.setOnClickListener { onBackPressed() }
        tvSignUpS.setOnClickListener {
            if (isValidate()) {
            Utils.showToast(this,"work in progress.")
            } else {

            }
        }
        etName.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    ivName.setImageResource(R.mipmap.username_enable)
                    vName.setBackgroundColor(ContextCompat.getColor(this@SignUpActivity, R.color.colorPrimary))
                } else {
                    ivName.setImageResource(R.mipmap.username_disable)
                    vName.setBackgroundColor(ContextCompat.getColor(this@SignUpActivity, R.color.light_gray))
                }
            }
        })
        etEmailS.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    ivEmailS.setImageResource(R.mipmap.email_enable)
                    vEmailS.setBackgroundColor(ContextCompat.getColor(this@SignUpActivity, R.color.colorPrimary))
                } else {
                    ivEmailS.setImageResource(R.mipmap.email_disable)
                    vEmailS.setBackgroundColor(ContextCompat.getColor(this@SignUpActivity, R.color.light_gray))
                }
            }
        })
        etPasswordS.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    ivPasswordS.setImageResource(R.mipmap.password_enable)
                    vPasswordS.setBackgroundColor(ContextCompat.getColor(this@SignUpActivity, R.color.colorPrimary))
                } else {
                    ivPasswordS.setImageResource(R.mipmap.password_disable)
                    vPasswordS.setBackgroundColor(ContextCompat.getColor(this@SignUpActivity, R.color.light_gray))
                }
            }
        })
        etConfPassword.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    ivConfPassword.setImageResource(R.mipmap.password_enable)
                    vConfPassword.setBackgroundColor(ContextCompat.getColor(this@SignUpActivity, R.color.colorPrimary))
                } else {
                    ivConfPassword.setImageResource(R.mipmap.password_disable)
                    vConfPassword.setBackgroundColor(ContextCompat.getColor(this@SignUpActivity, R.color.light_gray))
                }
            }
        })
    }

    private fun isValidate(): Boolean {
        return if (TextUtils.isEmpty(etName.text.toString().trim())) {
            Utils.showSimpleMessage(this,getString(R.string.enter_name)).show()
            false
        } else if (TextUtils.isEmpty(etEmailS.text.toString())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_email)).show()
            false
        }else if (!Utils.emailValidator(etEmailS.text.toString().trim())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_valid_email)).show()
            false
        } else if (TextUtils.isEmpty(etPasswordS.text.toString().trim())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_password)).show()
            false
        } else if (TextUtils.isEmpty(etConfPassword.text.toString().trim())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_con_password)).show()
            false
        } else if (etPasswordS.text.toString().trim() != etConfPassword.text.toString().trim()) {
            Utils.showSimpleMessage(this, getString(R.string.match_password)).show()
            false
        } else {
            true
        }
    }
}