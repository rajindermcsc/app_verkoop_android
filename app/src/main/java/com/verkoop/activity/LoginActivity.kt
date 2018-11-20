package com.verkoop.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.verkoop.R
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.login_activity.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        setData()

    }

    private fun setData() {
        tvLogin.setOnClickListener {
            if (isValidate()) {
                Utils.showToast(this, "Work in progress.")
            }
        }
        tvFacebook.setOnClickListener { Utils.showToast(this, "Work in progress.") }
        tvGoogle.setOnClickListener { Utils.showToast(this, "Work in progress.") }
        tvSignUp.setOnClickListener { var intent= Intent(this,SignUpActivity::class.java)
        startActivity(intent)}
        tvForgotPassword.setOnClickListener { Utils.showToast(this, "Work in progress.") }
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    ivEmail.setImageResource(R.mipmap.email_enable)
                    vEmail.setBackgroundColor(ContextCompat.getColor(this@LoginActivity, R.color.colorPrimary))
                } else {
                    ivEmail.setImageResource(R.mipmap.email_disable)
                    vEmail.setBackgroundColor(ContextCompat.getColor(this@LoginActivity, R.color.light_gray))
                }
            }
        })
        etPassword.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    ivPassword.setImageResource(R.mipmap.password_enable)
                    vPassword.setBackgroundColor(ContextCompat.getColor(this@LoginActivity, R.color.colorPrimary))
                } else {
                    ivPassword.setImageResource(R.mipmap.password_disable)
                    vPassword.setBackgroundColor(ContextCompat.getColor(this@LoginActivity, R.color.light_gray))
                }
            }
        })
    }

    private fun isValidate(): Boolean {
        return if (TextUtils.isEmpty(etEmail.text.toString())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_email)).show()
            false
        } else if (!Utils.emailValidator(etEmail.text.toString())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_valid_email)).show()
            false
        } else if (!Utils.isValidPassword(etPassword.text.toString())) {
            Utils.showSimpleMessage(this, getString(R.string.enter_password)).show()
            false
        } else {
            true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}