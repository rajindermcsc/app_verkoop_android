package com.verkoopapp.activity

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.verkoopapp.R
import com.verkoopapp.VerkoopApplication
import com.verkoopapp.models.SignUpRequest
import com.verkoopapp.models.SignUpResponse
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.signup_activity.*
import retrofit2.Response
import android.text.InputFilter
import android.text.Spanned



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
        val typefaceFont = Typeface.createFromAsset(assets, "fonts/gothicb.ttf")
        etPasswordS.typeface = typefaceFont
        etConfPassword.typeface = typefaceFont
        ccp.setTypeFace(typefaceFont)
        tvLoginS.setOnClickListener { onBackPressed() }
        tvSignUpS.setOnClickListener {
            if (Utils.isOnline(this)) {
                if (isValidate()) {
                    callSignUpApi()
                }
            } else {
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
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

        val filter = object : InputFilter {
            internal var canEnterSpace = false

            override fun filter(source: CharSequence, start: Int, end: Int,
                                dest: Spanned, dstart: Int, dend: Int): CharSequence {

                if (etName.text.toString() == "") {
                    canEnterSpace = false
                }

                val builder = StringBuilder()

                for (i in start until end) {
                    val currentChar = source[i]
                    if (!Character.isWhitespace(currentChar) ) {
                        builder.append(currentChar)
                    }
                }
                return builder.toString()
            }
        }
        etName.filters = arrayOf<InputFilter>(filter)
    }

    private fun isValidate(): Boolean {
        return if (TextUtils.isEmpty(etName.text.toString().trim())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_user_name)).show()
            false
        } else if (TextUtils.isEmpty(etEmailS.text.toString())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_email)).show()
            false
        } else if (!Utils.emailValidator(etEmailS.text.toString().trim())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_valid_email)).show()
            false
        } else if (TextUtils.isEmpty(etPasswordS.text.toString().trim())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_password)).show()
            false
        } else if (etPasswordS.text.toString().trim().length < 7) {
            Utils.showSimpleMessage(this, getString(R.string.enter_password_length)).show()
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

    private fun callSignUpApi() {
        VerkoopApplication.instance.loader.show(this)
        ServiceHelper().userSignUPService(SignUpRequest(etEmailS.text.toString().trim(), etName.text.toString().trim(), etConfPassword.text.toString().trim()
                ,"normal" ,ccp.selectedCountryName),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        VerkoopApplication.instance.loader.hide(this@SignUpActivity)
                        val loginResponse = response.body() as SignUpResponse
                        if(loginResponse.data!=null) {
                            setResponseData(loginResponse.data.userId.toString(), loginResponse.data.token, loginResponse.data.username, loginResponse.data.email, loginResponse.data.login_type, loginResponse.data.qrCode_image)
                        }
                    }

                    override fun onFailure(msg: String?) {
                        VerkoopApplication.instance.loader.hide(this@SignUpActivity)
                        Utils.showSimpleMessage(this@SignUpActivity, msg!!).show()
                    }
                })
    }

    private fun setResponseData(userId: String, api_token: String, firstName: String, email: String, loginType: String,qrCode: String) {
        Utils.savePreferencesString(this@SignUpActivity, AppConstants.USER_ID, userId)
        Utils.savePreferencesString(this@SignUpActivity, AppConstants.API_TOKEN, api_token)
        Utils.savePreferencesString(this@SignUpActivity, AppConstants.USER_NAME, firstName)
        Utils.savePreferencesString(this@SignUpActivity, AppConstants.QR_CODE, qrCode)
        if (!TextUtils.isEmpty(email)) {
            Utils.savePreferencesString(this@SignUpActivity, AppConstants.USER_EMAIL_ID, email)
        }
        Utils.savePreferencesString(this@SignUpActivity, AppConstants.LOGIN_TYPE, loginType)
        val intent = Intent(this@SignUpActivity, CategoriesActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}