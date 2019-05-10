package com.verkoop.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import com.google.android.gms.location.*
import com.verkoop.R
import com.verkoop.VerkoopApplication
import com.verkoop.adapter.LocationSearchAdapter
import com.verkoop.models.Location
import com.verkoop.models.PlaceApiResponse
import com.verkoop.models.PlaceSearchRequest
import com.verkoop.models.ResultLocation
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.AppConstants.GOOGLE_API_KEY
import com.verkoop.utils.SelectionListener
import com.verkoop.utils.Utils
import com.verkoop.utils.ResumeLocationDialog
import kotlinx.android.synthetic.main.selarch_location_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import retrofit2.Response


class SearchLocationActivity : AppCompatActivity(), LocationSearchAdapter.SelectedPlaceListener {

    override fun selectedAddress(address: String, location: Location) {
        val returnIntent = Intent()
        returnIntent.putExtra(AppConstants.ADDRESS, address)
        returnIntent.putExtra(AppConstants.LATITUDE, location.lat)
        returnIntent.putExtra(AppConstants.LONGITUDE, location.lng)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val INTERVAL: Long = 50000
    private val FASTEST_INTERVAL: Long = 1000
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var locationSearchAdapter: LocationSearchAdapter
    private var results = ArrayList<ResultLocation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.selarch_location_activity)
        setAdapter()
        setData()
        if (Utils.isOnline(this@SearchLocationActivity)) {
            getLocation()
        } else {
            Utils.showSimpleMessage(this@SearchLocationActivity, getString(R.string.check_internet)).show()
        }
    }

    private fun setAdapter() {
        val mManager = LinearLayoutManager(this)
        rvLocation.layoutManager = mManager
        locationSearchAdapter = LocationSearchAdapter(this)
        rvLocation.adapter = locationSearchAdapter
    }

    private fun getLocation() {
        pbProgress.visibility = View.VISIBLE
        mLocationRequest = LocationRequest()
        // Create the location request to start receiving updates
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = INTERVAL
        mLocationRequest.fastestInterval = FASTEST_INTERVAL
        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()
        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())

    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation
            // Utils.showToast(this@SearchLocationActivity,"Latitude : "+locationResult.lastLocation.latitude +" , Longitude : "+locationResult.lastLocation.longitude)
            val location = locationResult.lastLocation.latitude.toString() + "," + locationResult.lastLocation.longitude.toString()
            if (Utils.isOnline(this@SearchLocationActivity)) {
                getPlaceList(location)
            } else {
                Utils.showSimpleMessage(this@SearchLocationActivity, getString(R.string.check_internet)).show()
            }

        }
    }

    private fun setData() {
        ivLeftLocation.setOnClickListener { onBackPressed() }
        etSearchPlace.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (!TextUtils.isEmpty(etSearchPlace.text.toString())) {
                    if (Utils.isOnline(this)) {
                        searchItemApi()
                    } else {
                        Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
                    }
                }
                true
            } else false
        }
    }

    override fun onBackPressed() {
        resumeActivityDialog()
    }

    private fun resumeActivityDialog() {
        val shareDialog = ResumeLocationDialog(this, object : SelectionListener {
            override fun leaveClick() {
                val returnIntent = Intent()
                setResult(Activity.RESULT_CANCELED, returnIntent)
                finish()
            }
        })
        shareDialog.show()
    }

    /*get location Api from lat long*/
    private fun getPlaceList(locationLatLon: String) {
        val placeSearchRequest = PlaceSearchRequest(locationLatLon, "500", "", GOOGLE_API_KEY)
        ServiceHelper().getPlacesService(placeSearchRequest, object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                VerkoopApplication.instance.loader.hide(this@SearchLocationActivity)
                pbProgress.visibility = View.GONE
                mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
                val searchResult = response.body() as PlaceApiResponse
                results.clear()
                results = searchResult.results
                locationSearchAdapter.setData(results, getString(R.string.location))
                locationSearchAdapter.notifyDataSetChanged()

            }

            override fun onFailure(msg: String?) {
                pbProgress.visibility = View.GONE
                Utils.showSimpleMessage(this@SearchLocationActivity, msg!!).show()
                mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
            }
        })

    }

    /*search location Api*/
    private fun searchItemApi() {
        val placeSearchRequest = PlaceSearchRequest("", "", etSearchPlace.text.toString().trim(), GOOGLE_API_KEY)
        ServiceHelper().getSearchPlaceService(placeSearchRequest, object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                VerkoopApplication.instance.loader.hide(this@SearchLocationActivity)
                pbProgress.visibility = View.GONE
                mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
                val searchResult = response.body() as PlaceApiResponse
                results.clear()
                results = searchResult.results
                locationSearchAdapter.setData(results, getString(R.string.search))
                locationSearchAdapter.notifyDataSetChanged()

            }

            override fun onFailure(msg: String?) {
                pbProgress.visibility = View.GONE
                Utils.showSimpleMessage(this@SearchLocationActivity, msg!!).show()
                mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
            }
        })

    }
}