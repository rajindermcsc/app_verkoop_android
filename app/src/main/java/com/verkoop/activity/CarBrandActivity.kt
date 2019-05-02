package com.verkoop.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.verkoop.R
import com.verkoop.adapter.CarBrandAdapter
import com.verkoop.models.CarBrandResponse
import com.verkoop.models.DataCarBrand
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.KeyboardUtil
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.add_details_activity.*
import kotlinx.android.synthetic.main.car_brand_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import okhttp3.internal.Util
import retrofit2.Response

class
CarBrandActivity : AppCompatActivity() {
    private lateinit var carBrandAdapter: CarBrandAdapter
    private var carBrandList = ArrayList<DataCarBrand>()
    private var comingFrom: Int = 0
    private var carBrand:String=""
    private var carBrandId:Int=0
    private var carType:String=""
    private var zone:String=""
    private var zoneId:Int=0
    private var carTypeId:Int=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.car_brand_activity)
        comingFrom = intent.getIntExtra(AppConstants.TYPE, 0)
        carBrand=intent.getStringExtra(AppConstants.CAR_BRAND_NAME)
        carBrandId=intent.getIntExtra(AppConstants.CAR_BRAND_ID,carBrandId)
        if(intent.getStringExtra(AppConstants.CAR_TYPE)!=null){
            carType=   intent.getStringExtra(AppConstants.CAR_TYPE)
        }

        carTypeId=intent.getIntExtra(AppConstants.CAR_TYPE_ID,0)
        if (comingFrom != 0) {
            tvHeaderLoc.text = getString(R.string.car_type)
            carBrand=intent.getStringExtra(AppConstants.CAR_BRAND_NAME)
            carBrandId=intent.getIntExtra(AppConstants.CAR_BRAND_ID,carBrandId)
        } else {
            tvHeaderLoc.text = getString(R.string.car_brand)
        }
        setAdapter(comingFrom)
        setData()
        if(comingFrom!=3) {
            if (Utils.isOnline(this)) {
                KeyboardUtil.hideKeyboard(this)
                getCarBrandApi()
            } else {
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            }
        }else{
            setZoneList()
        }
    }

    private fun setZoneList() {
        tvHeaderLoc.text = getString(R.string.zone)
        zone=intent.getStringExtra(AppConstants.ZONE)
        zoneId=intent.getIntExtra(AppConstants.ZONE_ID,0)
        val nameList = arrayOf("East", "West", "North East", "North", "Central")
        val subList = arrayOf(1,2,3,4,5)
        for (i in nameList.indices){
            val dataCar=DataCarBrand(subList[i],nameList[i])
            carBrandList.add(dataCar)
        }
        if(zoneId>0){
            carBrandList[zoneId-1].isSelected=true
        }
        carBrandAdapter.setData(carBrandList)
        carBrandAdapter.notifyDataSetChanged()
    }

    private fun setAdapter(comingFrom: Int) {
        val mManager = LinearLayoutManager(this)
        rvCarBrand.layoutManager = mManager
        // carBrandAdapter = RegionAdapter(this, 0)
        carBrandAdapter = CarBrandAdapter(this, comingFrom,carBrand,carBrandId,carTypeId)
        rvCarBrand.adapter = carBrandAdapter
    }

    private fun setData() {
        ivLeftLocation.setOnClickListener {
            onBackPressed()
        }
        etSearchBrand.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                carBrandAdapter.filter.filter(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                 carType = data.getStringExtra(AppConstants.CAR_TYPE)
                 carBrand = data.getStringExtra(AppConstants.CAR_BRAND_NAME)
                 carTypeId = data.getIntExtra(AppConstants.CAR_TYPE_ID, 0)
                 carBrandId = data.getIntExtra(AppConstants.CAR_BRAND_ID, 0)
                val returnIntent = Intent()
                returnIntent.putExtra(AppConstants.CAR_BRAND_NAME,carBrand)
                returnIntent.putExtra(AppConstants.CAR_BRAND_ID,carBrandId)
                returnIntent.putExtra(AppConstants.CAR_TYPE,carType)
                returnIntent.putExtra(AppConstants.CAR_TYPE_ID,carTypeId)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
                overridePendingTransition(0, 0)
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    override fun onBackPressed() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }

    private fun getCarBrandApi() {
        pbProgressBrand.visibility = View.VISIBLE
        ServiceHelper().getCarBrandService(comingFrom, object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                pbProgressBrand.visibility = View.GONE
                val responseFav = response.body() as CarBrandResponse
                if (responseFav.data.isNotEmpty()) {
                    carBrandList = responseFav.data
                    if(comingFrom==0){
                        if(carBrandId>0){
                            for(i in carBrandList.indices){
                                if(carBrandId==carBrandList[i].id){
                                    carBrandList[i].isSelected=true
                                }
                            }
                        }
                    }else{
                        if(carTypeId>0){
                            for(i in carBrandList.indices){
                                if(carTypeId==carBrandList[i].id){
                                    carBrandList[i].isSelected=true
                                }
                            }
                        }
                    }

                    carBrandAdapter.setData(carBrandList)
                    carBrandAdapter.notifyDataSetChanged()

                } else {
                    Utils.showSimpleMessage(this@CarBrandActivity, responseFav.message).show()
                }

            }

            override fun onFailure(msg: String?) {
                pbProgressBrand.visibility = View.GONE
                Utils.showSimpleMessage(this@CarBrandActivity, msg!!).show()
            }
        })

    }

}