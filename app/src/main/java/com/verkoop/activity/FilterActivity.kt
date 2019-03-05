package com.verkoop.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.google.android.gms.location.*
import com.verkoop.R
import com.verkoop.adapter.FilterAdapter
import com.verkoop.models.FilterRequest
import com.verkoop.utils.PermissionCheck
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.filter_activity.*
import kotlinx.android.synthetic.main.toolbar_filter.*
import kotlinx.android.synthetic.main.add_details_activity.*
import android.app.Activity
import com.verkoop.utils.AppConstants


class FilterActivity : AppCompatActivity() {
    private var sortNumber=0
    private val REQUEST_CODE = 12
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val INTERVAL: Long = 50000
    private val FASTEST_INTERVAL: Long = 1000
    private lateinit var mLocationRequest: LocationRequest
    private var  lat=""
    private var  lng=""
    private var condition:String=""
    private var itemType:String=""
    private var meetUp:Int=0
    private var isFocus: Boolean=false
    private var filterRequest: FilterRequest? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filter_activity)
        setData()
        setItemList()

    }

    private fun setData() {
        val font = Typeface.createFromAsset(assets, "fonts/gothic.ttf")
        rbNearBy.typeface = font
        rbPopular.typeface = font
        rbRecentlyAdded.typeface = font
        rbPriceHigh.typeface = font
        rbPriceLow.typeface = font
        iv_leftGallery.setOnClickListener { onBackPressed() }
        llNew.setOnClickListener {
            if(TextUtils.isEmpty(condition)){
                setSelection()
                setSelectNew()
            }else{
                if(condition.equals("New",ignoreCase = true)){
                    setSelection()
                }else{
                    setSelection()
                    setSelectNew()
                }
            }
        }

        llUsed.setOnClickListener {
            if(TextUtils.isEmpty(condition)){
                setSelection()
                setSelectUsed()
            }else{
                if(condition.equals(getString(R.string.used),ignoreCase = true)){
                    setSelection()
                }else{
                    setSelection()
                    setSelectUsed()
                }
            }

        }
        cbNearByFilter.setOnCheckedChangeListener({ _, isChecked ->
            meetUp = if (isChecked) {
                1
            } else {
                0
            }
        })
        rbGroup.setOnCheckedChangeListener({ group, checkedId ->
            when (checkedId) {
                R.id.rbNearBy -> {
                    checkLocationOption()
                    sortNumber=1
                }
                R.id.rbPopular -> sortNumber=2
                R.id.rbRecentlyAdded -> sortNumber=3
                R.id.rbPriceHigh -> sortNumber=4
                R.id.rbPriceLow -> sortNumber=5
            }
        })
        etMinPrice.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isFocus = true
                if (etMinPrice.text.toString().isEmpty()) {
                    etMinPrice.setText(this@FilterActivity.getString(R.string.dollar))
                    etMinPrice.setSelection(1)
                }
            } else {
                isFocus = false
                if (etMinPrice.length() == 1) {
                    etMinPrice.setSelection(0)
                    etMinPrice.setText("")
                }
            }
        }
        etMinPrice.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if(isFocus) {
                    if (etMinPrice.length() == 0) {
                        etMinPrice.setText("$")
                        etMinPrice.setSelection(1)
                    }
                }
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

            }

            override fun afterTextChanged(arg0: Editable) {

            }
        })
        etMaxPrice.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isFocus = true
                if (etMaxPrice.text.toString().isEmpty()) {
                    etMaxPrice.setText(this@FilterActivity.getString(R.string.dollar))
                    etMaxPrice.setSelection(1)
                }
            } else {
                isFocus = false
                if (etMaxPrice.length() == 1) {
                    etMaxPrice.setSelection(0)
                    etMaxPrice.setText("")
                }
            }
        }
        etMaxPrice.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if(isFocus) {
                    if (etMaxPrice.length() == 0) {
                        etMaxPrice.setText("$")
                        etMaxPrice.setSelection(1)
                    }
                }
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

            }

            override fun afterTextChanged(arg0: Editable) {

            }
        })
        tvApply.setOnClickListener {
            itemType = when {
                condition.equals("New",ignoreCase = true) -> "1"
                condition.equals(getString(R.string.used),ignoreCase = true) -> "2"
                else -> ""
            }
           val filterRequest = FilterRequest("ed",0,"2",sortNumber.toString(),lat,lng,itemType,meetUp.toString(),etMinPrice.text.toString(),etMaxPrice.text.toString() )
            val returnIntent = Intent()
            returnIntent.putExtra(AppConstants.POST_DATA, filterRequest)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }

    private fun setSelectNew() {
        ivNew.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.new_active))
        tvNew.setTextColor(ContextCompat.getColor(this, R.color.white))
        llNew.background = ContextCompat.getDrawable(this, R.drawable.red_rectangle_shape)
        condition="New"
    }

    private fun setSelectUsed() {
        ivUsed.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.used_active))
        tvUsed.setTextColor(ContextCompat.getColor(this, R.color.white))
        llUsed.background = ContextCompat.getDrawable(this, R.drawable.red_rectangle_shape)
        condition=getString(R.string.used)
    }

    private fun checkLocationOption() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        } else {
            if (checkPermission()) {
                if (Utils.isOnline(this@FilterActivity)) {
                    getLocation()
                } else {
                    Utils.showSimpleMessage(this@FilterActivity, getString(R.string.check_internet)).show()
                }
            }
        }
    }

    private fun setSelection() {
        ivNew.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.new_inactive))
        tvNew.setTextColor(ContextCompat.getColor(this, R.color.gray_light))
        llNew.background = ContextCompat.getDrawable(this, R.drawable.item_type)
        ivUsed.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.used_inactive))
        tvUsed.setTextColor(ContextCompat.getColor(this, R.color.gray_light))
        llUsed.background = ContextCompat.getDrawable(this, R.drawable.item_type)
        condition=""
    }

    private fun setItemList() {
        val mManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvFilter.layoutManager = mManager
        val filterAdapter = FilterAdapter(this)
        rvFilter.adapter = filterAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == 0) {
            val provider = Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
            if (!TextUtils.isEmpty(provider)) {
                if (checkPermission()) {
                    if (Utils.isOnline(this@FilterActivity)) {
                        getLocation()
                    } else {
                        Utils.showSimpleMessage(this@FilterActivity, getString(R.string.check_internet)).show()
                    }
                }
            } else {
                cbNearBy.isChecked = false
            }
        }
    }

    override
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            29 -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (Utils.isOnline(this@FilterActivity)) {
                            getLocation()
                        } else {
                            Utils.showSimpleMessage(this@FilterActivity, getString(R.string.check_internet)).show()
                        }

                    } else {
                        rbNearBy.isChecked = false
                        PermissionCheck(this).showPermissionDialog()

                    }
                }
            }
        }
    }

    private fun getLocation() {
        pbPrgFilter.visibility = View.VISIBLE
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
            lat=locationResult.lastLocation.latitude.toString()
            lng=locationResult.lastLocation.longitude.toString()
            Log.e("LatLng",lat+""+lng)
            pbPrgFilter.visibility=View.GONE
          /*  if (mFusedLocationProviderClient != null) {
                mFusedLocationProviderClient!!.removeLocationUpdates(this@FilterActivity.mLocationCallback)
            }*/
        }
    }


    private fun checkPermission(): Boolean {
        val permissionCheck = PermissionCheck(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionCheck.checkLocationPermission())
                return true
        } else
            return true
        return false
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            , REQUEST_CODE)
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                    cbNearBy.isChecked = false
                }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    override fun onStop() {
        super.onStop()
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
        }
    }

}