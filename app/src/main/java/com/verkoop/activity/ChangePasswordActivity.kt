package com.verkoop.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.github.florent37.viewanimator.ViewAnimator
import com.verkoop.R
import com.verkoop.models.DisLikeResponse
import com.verkoop.models.UpdatePasswordRequest
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.KeyboardUtil
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.change_password.*
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_password)
        KeyboardUtil(this, llParentPass)
        setData()
        setAnimation()
    }

    private fun setData() {
        pbProgressChange.indeterminateDrawable.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY)
        ivFinishPass.setOnClickListener { onBackPressed() }
        tvSavePass.setOnClickListener {
            if (Utils.isOnline(this)) {
                //   Utils.hideKeyboardOnOutSideTouch(tvSavePass, this@ChangePasswordActivity)
                if (isValidate()) {
                    KeyboardUtil.hideKeyboard(this)
                    callChangePasswordApi()
                }
            } else {
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            }
        }
    }

    private fun callChangePasswordApi() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbProgressChange.visibility = View.VISIBLE
        ServiceHelper().updatePasswordService(UpdatePasswordRequest(Utils.getPreferencesString(this, AppConstants.USER_ID), etCurrentPassword.text.toString(), etNewPassword.text.toString()),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressChange.visibility = View.GONE
                        val loginResponse = response.body() as DisLikeResponse
                        Utils.showToast(this@ChangePasswordActivity, getString(R.string.changed))
                        onBackPressed()
                    }

                    override fun onFailure(msg: String?) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressChange.visibility = View.GONE
                        Utils.showSimpleMessage(this@ChangePasswordActivity, msg!!).show()
                    }
                })

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
                Utils.showSimpleMessage(this, "Please enter new password.").show()
                false
            }
            etNewPassword.text.toString().trim().length < 7 -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_password_length)).show()
                false
            }
            TextUtils.isEmpty(etConfirmPassword.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, "Please enter confirm password.").show()
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