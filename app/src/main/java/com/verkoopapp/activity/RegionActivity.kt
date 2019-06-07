package com.verkoopapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.verkoopapp.R
import com.verkoopapp.adapter.RegionAdapter
import com.verkoopapp.models.City
import com.verkoopapp.models.Country
import com.verkoopapp.models.State
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.CommonUtils
import kotlinx.android.synthetic.main.selarch_location_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import org.json.JSONException
import org.json.JSONObject

class RegionActivity : AppCompatActivity(), RegionAdapter.ClickEventCallBack {
    var stateName: String = ""
    var countryName: String = ""
    var countryId: Int = 0
    var stateId: Int = 0
    var cityId: Int = 0
    override fun onSelectRegion(regionName: String, regionId: Int, coming: Int, cityList: ArrayList<City>) {
        stateName = regionName
        stateId = regionId
        val i = Intent(this, StateActivity::class.java)
        i.putParcelableArrayListExtra(AppConstants.CITY_LIST, cityList)
        startActivityForResult(i, 1)
        overridePendingTransition(0, 0)
    }

    val countryList = ArrayList<Country>()
    val statesList = ArrayList<State>()
    private lateinit var regionAdapter: RegionAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.selarch_location_activity)
        setAdapter()
        getStateList()
        setData()
    }

    private fun setAdapter() {
        val mManager = LinearLayoutManager(this)
        rvLocation.layoutManager = mManager
        regionAdapter = RegionAdapter(this, 0)
        cityId = intent.getIntExtra(AppConstants.CITY_ID, 0)
        stateId = intent.getIntExtra(AppConstants.STATE_ID, 0)
        rvLocation.adapter = regionAdapter
    }


    private fun getStateList() {
        try {
            val obj = JSONObject(CommonUtils.loadJSONFromAsset(this))
            val m_jArry = obj.getJSONArray("country")
            for (i in 0 until m_jArry.length()) {
                val jo_inside = m_jArry.getJSONObject(i)
                val states = jo_inside.getJSONArray("states")
                for (j in 0 until states.length()) {
                    val citiesList = ArrayList<City>()
                    val stateList = states.getJSONObject(j)
                    val citys = stateList.getJSONArray("cities")
                    for (k in 0 until citys.length()) {
                        val cityList = citys.getJSONObject(k)
                        val cities = City(cityList.getInt("id"), false, cityList.getString("name"))
                        citiesList.add(cities)
                    }
                    Log.e("cityList", citiesList.toString())
                    val state = State(stateList.getInt("id"), stateList.getString("name"), false, citiesList)
                    statesList.add(state)
                    Log.e("StateList", statesList.toString())
                }
                val country = Country(jo_inside.getInt("id"), jo_inside.getString("name"), statesList)
                countryList.add(country)
                countryName = countryList[0].name
                countryId = countryList[0].id
                if (stateId != 0 && cityId != 0) {
                    for (k in countryList[0].states.indices) {
                        if (countryList[0].states[k].id == stateId) {
                            countryList[0].states[k].isSelected = true
                            updateCity(countryList[0].states[k].cities)
                            break
                        }
                    }
                }
                regionAdapter.setData(countryList[0].states)
                regionAdapter.notifyDataSetChanged()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun updateCity(cityList: ArrayList<City>) {
        for (i in cityList.indices){
            if (cityList[i].id == cityId) {
                cityList[i].isSelected = true
                break
            }
        }

    }

    private fun updateCity(k: Int) {}

    private fun setData() {
        tvHeaderLoc.text = getString(R.string.region)
        ivLeftLocation.setOnClickListener {
            onBackPressed()
        }
        etSearchPlace.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                Log.e("search", charSequence.toString())
                regionAdapter.filter.filter(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        /*  toolbarRegion.title = "Region"
          toolbarRegion.setLogo(R.mipmap.back)
          toolbarRegion.titleMarginStart = 70
          sv.setOnSearchViewListener(this)
          //toolbarRegion.setDisplayHomeAsUpEnabled(true)
          setSupportActionBar(toolbarRegion)*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val cityName = data!!.getStringExtra(AppConstants.CITY_NAME)
                val cityId = data.getIntExtra(AppConstants.CITY_ID, 0)
                val returnIntent = Intent()
                returnIntent.putExtra(AppConstants.CITY_NAME, cityName)
                returnIntent.putExtra(AppConstants.CITY_ID, cityId)
                returnIntent.putExtra(AppConstants.STATE_NAME, stateName)
                returnIntent.putExtra(AppConstants.STATE_ID, stateId)
                returnIntent.putExtra(AppConstants.COUNTRY_ID, countryId)
                returnIntent.putExtra(AppConstants.COUNTRY_NAME, countryName)
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
        //super.onBackPressed()
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }
}