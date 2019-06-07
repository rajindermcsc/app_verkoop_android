package com.verkoopapp.activity

import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.view.WindowManager
import com.verkoopapp.R
import com.verkoopapp.adapter.AdvertPackageAdapter
import com.verkoopapp.models.AdvertPlanActivity
import com.verkoopapp.models.ProfileUpdateResponse
import com.verkoopapp.models.UploadBannerRequest
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.*
import kotlinx.android.synthetic.main.advert_package_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import retrofit2.Response


class AdvertPackagesActivity : AppCompatActivity(), AdvertPackageAdapter.SubmitBannerCallBack {
    private lateinit var advertPackageAdapter: AdvertPackageAdapter
    private var imageUrl = ""
    override fun planSelectionClick(planId: Int) {
        submitDialog(planId)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.advert_package_activity)
        imageUrl = intent.getStringExtra(AppConstants.IMAGE_URL)
         val display = windowManager.defaultDisplay
         val size =  Point()
         display.getSize(size)
         val width = size.x
        setData()
        setAdapter(width)
        if (Utils.isOnline(this)) {
            getAdvertPlanApi()
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }

    private fun setData() {
        tvHeaderLoc.text = getString(R.string.add)
        ivLeftLocation.setOnClickListener { onBackPressed() }
    }

    private fun setAdapter(width: Int) {
        val mLayoutManager = GridLayoutManager(this, 3)
        rvAdvertPackages.layoutManager = mLayoutManager
        advertPackageAdapter = AdvertPackageAdapter(this, width)
        rvAdvertPackages.adapter = advertPackageAdapter
    }

    private fun getAdvertPlanApi() {
        pbAdvertPackage.visibility = View.VISIBLE
        ServiceHelper().getAdvertisementPlanService(object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                pbAdvertPackage.visibility = View.GONE
                val responseWallet = response.body() as AdvertPlanActivity
                if (responseWallet.data.isNotEmpty()) {
                    advertPackageAdapter.setData(responseWallet.data)
                    advertPackageAdapter.notifyDataSetChanged()

                } else {
                    Utils.showSimpleMessage(this@AdvertPackagesActivity, "No data found.").show()
                }
            }

            override fun onFailure(msg: String?) {
                pbAdvertPackage.visibility = View.GONE
                Utils.showSimpleMessage(this@AdvertPackagesActivity, msg!!).show()
            }
        })

    }

    private fun submitDialog(planId: Int) {
        val proceedDialog = ProceedDialog(this, "", object : SelectionListener {
            override fun leaveClick() {
                updateProfileData(planId)
            }
        })
        proceedDialog.show()
    }

    private fun updateProfileData(planId: Int) {
        val uploadBannerRequest = UploadBannerRequest(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), planId, imageUrl)
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbAdvertPackage.visibility = View.VISIBLE
        ServiceHelper().updateBannerService(uploadBannerRequest, object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pbAdvertPackage.visibility = View.GONE
                val homeDataResponse = response.body() as ProfileUpdateResponse
                Utils.showToast(this@AdvertPackagesActivity, homeDataResponse.message)
                setDialogBox()

            }

            override fun onFailure(msg: String?) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pbAdvertPackage.visibility = View.GONE
                Utils.showSimpleMessage(this@AdvertPackagesActivity, msg!!).show()
            }
        })

    }

    private fun setDialogBox() {
        val shareDialog = WarningDialog(this, object : SelectionListener {
            override fun leaveClick() {
                val intentBack = Intent(this@AdvertPackagesActivity, ViewAllBannerActivity::class.java)
                intentBack.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intentBack)
            }
        })
        shareDialog.show()

    }
    /* override fun onBackPressed() {
         val returnIntent = Intent()
         setResult(Activity.RESULT_CANCELED, returnIntent)
         finish()
     }*/
}