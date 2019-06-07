package com.verkoopapp.activity


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import com.github.florent37.viewanimator.ViewAnimator
import com.squareup.picasso.Picasso
import com.verkoopapp.R
import com.verkoopapp.models.DisLikeResponse
import com.verkoopapp.models.SendQrCodeRequest
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.KeyboardUtil
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.transfer_coin_dialog_activity.*
import retrofit2.Response


class TransferCoinsActivity : AppCompatActivity() {
    var userName = ""
    var imageUrl = ""
    var qrCode = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transfer_coin_dialog_activity)
        KeyboardUtil(this, llParentTrans)
        userName = intent.getStringExtra(AppConstants.USER_NAME)
        imageUrl = intent.getStringExtra(AppConstants.IMAGE_URL)
        qrCode = intent.getStringExtra(AppConstants.QR_CODE)
        setData()
        setAnimation()
    }

    private fun setData() {
        tvTransferMssg.text = StringBuffer().append(getString(R.string.send_money)).append(" ").append(userName)
        if (!TextUtils.isEmpty(imageUrl)) {
            Picasso.with(this@TransferCoinsActivity).load(AppConstants.IMAGE_URL + imageUrl)
                    .resize(720, 720)
                    .centerInside()
                    .error(R.mipmap.pic_placeholder)
                    .placeholder(R.mipmap.pic_placeholder)
                    .into(ivUserPicSend)
        }
        tvSaveYTransfer.setOnClickListener {
            if (Utils.isOnline(this@TransferCoinsActivity)) {
                if (!TextUtils.isEmpty(etAmountTrans.text.toString())) {
                    getUserInfo()
                } else {
                    Utils.showSimpleMessage(this@TransferCoinsActivity, "Please enter amount.").show()
                }
            } else {
                Utils.showSimpleMessage(this@TransferCoinsActivity, getString(R.string.check_internet)).show()
            }
        }
        ivFinishTrans.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getUserInfo() {
        ServiceHelper().sendQrService(SendQrCodeRequest(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), (etAmountTrans.text.toString()).toInt(), qrCode), object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                val responseBanner = response.body() as DisLikeResponse?
                if (responseBanner != null) {
                    Utils.showToast(this@TransferCoinsActivity, responseBanner.message)
                    onBackPressed()
                } else {
                    Utils.showSimpleMessage(this@TransferCoinsActivity, "No data found.").show()
                }
            }

            override fun onFailure(msg: String?) {
                Utils.showSimpleMessage(this@TransferCoinsActivity, msg!!).show()
            }
        })

    }

    private fun setAnimation() {
        ViewAnimator
                .animate(flParentTrans)
                .translationY(1000f, 0f)
                .duration(400)
                .start()
    }

    override fun onBackPressed() {
        ViewAnimator
                .animate(flParentTrans)
                .translationY(0f, 2000f)
                .duration(400)
                .andAnimate(llParentTrans)
                .alpha(1f, 0f)
                .duration(500)
                .onStop {
                    llParentTrans.visibility = View.GONE
                    finish()
                    overridePendingTransition(0, 0)
                }
                .start()
    }
}