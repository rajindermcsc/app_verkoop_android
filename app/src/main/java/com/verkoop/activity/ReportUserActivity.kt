package com.verkoop.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.WindowManager
import com.github.florent37.viewanimator.ViewAnimator
import com.verkoop.R
import com.verkoop.adapter.ReportListAdapter
import com.verkoop.models.*
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.report_user_activity.*
import retrofit2.Response


class ReportUserActivity:AppCompatActivity(), ReportListAdapter.OnSelectedCallBack {
    private var regionId:Int=0
    private var itemId:Int=0
    override fun onSelection(reportId: Int, description: String) {
        tvReason.text=description
        regionId=reportId
    }

    private var reportList = ArrayList<ReportResponse>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_user_activity)
        reportList=intent.getParcelableArrayListExtra(AppConstants.REPORT_LIST)
        itemId=intent.getIntExtra(AppConstants.ITEM_ID,0)
        setAdapter()
        setData()
        setAnimation()
    }
    private fun setAnimation() {
        ViewAnimator
                .animate(flParentReport)
                .translationY(1000f, 0f)
                .duration(700)
                .start()
    }
    private fun setAdapter() {
        val mManager=LinearLayoutManager(this)
        rvReasonList.layoutManager=mManager
        val reportListAdapter= ReportListAdapter(this,reportList)
        rvReasonList.adapter=reportListAdapter
    }

    private fun setData() {
        pbProgressReport.indeterminateDrawable.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY)
        tvCancel.setOnClickListener {
            onBackPressed()
        }

        tvSubmit.setOnClickListener {
            if(regionId!=0){
                if (Utils.isOnline(this)) {
                  callReportSubmitApi()
                } else {
                    Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()

                }
            }else{
                Utils.showSimpleMessage(this, "Please select a valid region for report.").show()
            }
        }
    }

    private fun callReportSubmitApi() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbProgressReport.visibility=View.VISIBLE
        var reportUserResponse= ReportUserRequest(Utils.getPreferencesString(this,AppConstants.USER_ID),itemId,regionId)
        ServiceHelper().reportAddedService(reportUserResponse,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressReport.visibility=View.GONE
                        val loginResponse = response.body() as DisLikeResponse
                        Utils.showSimpleMessage(this@ReportUserActivity, "Report submitted to verkoop.").show()
                        onBackPressed()
                    }

                    override fun onFailure(msg: String?) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressReport.visibility=View.GONE
                        Utils.showSimpleMessage(this@ReportUserActivity, msg!!).show()
                    }
                })
    }

    override fun onBackPressed() {
        ViewAnimator
                .animate(flParentReport)
                .translationY(0f, 2000f)
                .duration(700)
                .andAnimate(llParentReport)
                .alpha(1f,0f)
                .duration(600)
                .onStop {
                    llParentReport.visibility= View.GONE
                    val returnIntent = Intent()
                    setResult(Activity.RESULT_CANCELED, returnIntent)
                    finish()
                    overridePendingTransition(0,0)
                }
                .start()

    }
}