package com.verkoop.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.verkoop.R
import com.verkoop.adapter.AdvertPackageAdapter
import com.verkoop.models.AdvertPlanActivity
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.advert_package_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import retrofit2.Response


class AdvertPackagesActivity : AppCompatActivity() {
    private lateinit var advertPackageAdapter: AdvertPackageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.advert_package_activity)
        setData()
        setAdapter()
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

    private fun setAdapter() {
        val mLayoutManager = GridLayoutManager(this, 3)
        rvAdvertPackages.layoutManager = mLayoutManager
        advertPackageAdapter = AdvertPackageAdapter(this, rvAdvertPackages)
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

    /* override fun onBackPressed() {
         val returnIntent = Intent()
         setResult(Activity.RESULT_CANCELED, returnIntent)
         finish()
     }*/
}