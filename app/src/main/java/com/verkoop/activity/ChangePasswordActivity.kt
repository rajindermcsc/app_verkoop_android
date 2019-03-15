package com.verkoop.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import com.github.florent37.viewanimator.ViewAnimator
import com.verkoop.R
import com.verkoop.utils.KeyboardUtil
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.change_password.*

class ChangePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_password)
        KeyboardUtil(this, llParentPass)
        setData()
        setAnimation()
    }

    private fun setData() {
        ivFinishPass.setOnClickListener { onBackPressed() }
        tvSavePass.setOnClickListener {
            if(isValidate()){
                Utils.showToast(this,"work in progress.")
            }
        }
    }

    private fun setAnimation() {
        ViewAnimator
                .animate(flParentPass)
                .translationY(1000f, 0f)
                .duration(700)
                .start()
    }

    override fun onBackPressed() {
        ViewAnimator
                .animate(flParentPass)
                .translationY(0f, 2000f)
                .duration(700)
                .andAnimate(llParentPass)
                .alpha(1f, 0f)
                .duration(600)
                .onStop {
                    llParentPass.visibility = View.GONE
                    finish()
                    overridePendingTransition(0, 0)
                }
                .start()
    }
    private fun isValidate(): Boolean {
        return when {
            TextUtils.isEmpty(etCurrentPassword.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, "Please enter current password.").show()
                false
            }
            etCurrentPassword.text.toString().trim().length < 7 -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_password_length)).show()
                false
            }
            TextUtils.isEmpty(etNewPassword.text.toString().trim()) -> {
                Utils.showSimpleMessage(this,"Please enter new password.").show()
                false
            }
            etNewPassword.text.toString().trim().length < 7 -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_password_length)).show()
                false
            }
            TextUtils.isEmpty(etConfirmPassword.text.toString().trim()) -> {
                Utils.showSimpleMessage(this,"Please enter confirm password.").show()
                false
            }
            etNewPassword.text.toString().trim() != etConfirmPassword.text.toString().trim() -> {
                Utils.showSimpleMessage(this, getString(R.string.match_password)).show()
                false
            }
            else -> true
        }
    }

}