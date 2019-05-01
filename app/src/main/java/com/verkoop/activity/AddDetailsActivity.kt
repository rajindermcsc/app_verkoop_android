package com.verkoop.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.location.LocationManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.WindowManager
import com.verkoop.BuildConfig
import com.verkoop.R
import com.verkoop.R.string.type
import com.verkoop.adapter.SelectedImageAdapter
import com.verkoop.models.*
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.*
import kotlinx.android.synthetic.main.add_details_activity.*
import kotlinx.android.synthetic.main.details_toolbar.*
import retrofit2.Response
import java.io.File


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class AddDetailsActivity : AppCompatActivity(), SelectedImageAdapter.SelectedImageCount {
    private val REQUEST_CODE = 11
    private var imageList = ArrayList<SelectedImage>()
    private var addItemRequest: AddItemRequest? = null
    private var additionalInfo: AdditionalInfo? = null
    private var additionalEditInfo: AdditionalInfoResponse? = null
    private var dataIntent: DataItems? = null
    private var selectedImageList = ArrayList<ImageModal>()
    private val realPath = java.util.ArrayList<String>()
    private var uri: Uri? = null
    private var imageCount = 0
    private var comingFrom = 0
    private var categoryId = 0
    private var parentId = 0
    private var itemId = 0
    private var categoryName = ""
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var address = ""
    private var itemType = 1
    private var carBrandName = ""
    private var carType = ""
    private var carBrandId = 0
    private var carTypeId = 0
    private var zoneName = ""
    private var zoneId = 0
    private var isFocus: Boolean = false
    private var rejectImageList = ArrayList<Int>()
    private var imageIdList = ArrayList<String>()
    private var screenType = 0
    private var directOwner = 0
    private var totalBadRoom: Int = 0
    private var totalBatchRoom: Int = 0
    private var postalCode: Int = 0

    override fun selectDetailCount(count: Int, position: Int, imageId: Int) {
        rejectImageList.add(position)
        if (imageId > 0) {
            imageIdList.add(imageId.toString())
        }
        if (count > 0) {
            tvCountDetail.text = StringBuilder().append(" ").append(count.toString()).append(" ").append("/ 10")
        } else {
            tvCountDetail.text = StringBuilder().append(" ").append(getString(R.string.multiple))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_details_activity)
        comingFrom = intent.getIntExtra(AppConstants.COMING_FROM, 0)
        setData()
        setSelect()
        if (comingFrom == 1) {
            setEditItemDetail()
        } else {
            setIntentData()
        }
    }

    private fun setEditItemDetail() {
        dataIntent = intent.getParcelableExtra(AppConstants.PRODUCT_DETAIL)
        tvCountDetail.text = StringBuilder().append(" ").append(dataIntent!!.items_image.size.toString()).append(" ").append("/ 10")
        //setListData(imageList)
        itemId = dataIntent!!.id

        if (dataIntent != null) {
            if (!TextUtils.isEmpty(dataIntent!!.name)) {
                etNameDetail.setText(dataIntent!!.name)
            }
            if (!TextUtils.isEmpty(dataIntent!!.price.toString())) {
                etPrice.setText(StringBuilder().append("$").append(dataIntent!!.price))
            }
            if (!TextUtils.isEmpty(dataIntent!!.description)) {
                etDescriptionDetail.setText(dataIntent!!.description)
            }
            if (!TextUtils.isEmpty(dataIntent!!.item_type.toString())) {
                itemType = dataIntent!!.item_type
                if (itemType == 1) {
                    setSelect()
                } else {
                    setSelection()
                    itemType = 2
                    ivUsedDetail.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.used_active))
                    tvUsedDetail.setTextColor(ContextCompat.getColor(this, R.color.white))
                    llUsedDetail.background = ContextCompat.getDrawable(this, R.drawable.red_rectangle_shape)
                }
            }
            if (dataIntent!!.latitude != 0.0) {
                lat = dataIntent!!.latitude


            }
            if (dataIntent!!.longitude != 0.0) {
                lng = dataIntent!!.longitude
            }
            if (!TextUtils.isEmpty(dataIntent!!.address)) {
                address = dataIntent!!.address!!
                tvPlaceAddress.text = dataIntent!!.address
            }
            if (!TextUtils.isEmpty(dataIntent!!.category_id.toString())) {
                categoryId = dataIntent!!.category_id
            }
            if (!TextUtils.isEmpty(dataIntent!!.category_name)) {
                categoryName = dataIntent!!.category_name
                tvCategory.text = categoryName
                tvCategory.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                vSelectCategory.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
            }

            if (!TextUtils.isEmpty(dataIntent!!.meet_up.toString())) {
                val meetUp = dataIntent!!.meet_up
                if (meetUp == 1) {
                    cbNearBy.isChecked = true
                    tvPlaceAddress.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    expansionLayout.expand(true)
                } else {
                    cbNearBy.isChecked = false
                    expansionLayout.collapse(true)
                }
            }

          //  screenType = dataIntent!!.type
         //   setScreenType(screenType)
            if (dataIntent!!.type == 1) {
                carBrandId = dataIntent!!.brand_id
                carTypeId = dataIntent!!.car_type_id
                additionalEditInfo = dataIntent!!.additional_info
                if (!TextUtils.isEmpty(additionalEditInfo!!.brand_name)) {
                    carBrandName = additionalEditInfo!!.brand_name!!
                    carType = additionalEditInfo!!.car_type!!
                    tvBrand.text = carBrandName + ", " + carType
                    tvBrand.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    vBrand.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
                }

                if (!TextUtils.isEmpty(additionalEditInfo!!.registration_year)) {
                    etRegistrationYear.setText(additionalEditInfo!!.registration_year)
                }
                directOwner = additionalEditInfo!!.direct_owner
                if (directOwner == 1) {
                    switchDirectOwner.setChecked(true, false)
                } else {
                    switchDirectOwner.setChecked(false, false)
                }

            } else if (dataIntent!!.type == 2) {
                zoneId = dataIntent!!.zone_id
                additionalEditInfo = dataIntent!!.additional_info
                if (!TextUtils.isEmpty(additionalEditInfo!!.zone)) {
                    zoneName = additionalEditInfo!!.zone!!
                    tvZone.text = zoneName

                    tvZone.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    vZone.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
                }

                if (!TextUtils.isEmpty(additionalEditInfo!!.street_name)) {
                    etStreetName.setText(additionalEditInfo!!.street_name)
                }
                if (!TextUtils.isEmpty(additionalEditInfo!!.postal_code.toString())) {
                    etPostalCode.setText(additionalEditInfo!!.postal_code.toString())
                }
                if (!TextUtils.isEmpty(additionalEditInfo!!.area)) {
                    etArea.setText(additionalEditInfo!!.area)
                }
                totalBadRoom = additionalEditInfo!!.bedroom
                totalBatchRoom = additionalEditInfo!!.bathroom
                tvBathroomCount.text = totalBatchRoom.toString()
                tvBedRoomCount.text = totalBadRoom.toString()
            }

            if (dataIntent!!.items_image.isNotEmpty()) {
                val imageList = ArrayList<SelectedImage>()
                for (i in dataIntent!!.items_image.indices) {
                    val selectedImage = SelectedImage(dataIntent!!.items_image[i].url, 0, false, dataIntent!!.items_image[i].image_id)
                    imageList.add(selectedImage)
                }
                setListData(imageList, 1)
            }
            screenType = dataIntent!!.type
            setScreenType(screenType)
        }

    }

    private fun setIntentData() {
        imageList = intent.getParcelableArrayListExtra(AppConstants.SELECTED_LIST)
        tvCountDetail.text = StringBuilder().append(" ").append(imageList.size.toString()).append(" ").append("/ 10")
        setListData(imageList, 0)
        addItemRequest = intent.getParcelableExtra(AppConstants.POST_DATA)
        if (addItemRequest != null) {
            if (!TextUtils.isEmpty(addItemRequest!!.name)) {
                etNameDetail.setText(addItemRequest!!.name)
            }
            if (!TextUtils.isEmpty(addItemRequest!!.price)) {
                etPrice.setText(StringBuilder().append("$").append(addItemRequest!!.price))
            }
            if (!TextUtils.isEmpty(addItemRequest!!.description)) {
                etDescriptionDetail.setText(addItemRequest!!.description)
            }
            if (!TextUtils.isEmpty(addItemRequest!!.item_type)) {
                itemType = addItemRequest!!.item_type.toInt()
                if (itemType == 1) {
                    setSelect()
                } else {
                    setSelection()
                    itemType = 2
                    ivUsedDetail.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.used_active))
                    tvUsedDetail.setTextColor(ContextCompat.getColor(this, R.color.white))
                    llUsedDetail.background = ContextCompat.getDrawable(this, R.drawable.red_rectangle_shape)
                }
            }
            if (!TextUtils.isEmpty(addItemRequest!!.Latitude)) {
                lat = addItemRequest!!.Latitude!!.toDouble()


            }
            if (!TextUtils.isEmpty(addItemRequest!!.Longitude)) {
                lng = addItemRequest!!.Longitude!!.toDouble()
            }
            if (!TextUtils.isEmpty(addItemRequest!!.Address)) {
                address = addItemRequest!!.Address!!
                tvPlaceAddress.text = addItemRequest!!.Address
            }
            if (!TextUtils.isEmpty(addItemRequest!!.categoriesId)) {
                categoryId = addItemRequest!!.categoriesId.toInt()
            }
            if (!TextUtils.isEmpty(addItemRequest!!.categoryName)) {
                categoryName = addItemRequest!!.categoryName
                tvCategory.text = categoryName
                tvCategory.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                vSelectCategory.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
            }

            if (!TextUtils.isEmpty(addItemRequest!!.meet_up)) {
                val meetUp = addItemRequest!!.meet_up!!.toInt()
                if (meetUp == 1) {
                    cbNearBy.isChecked = true
                    tvPlaceAddress.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    expansionLayout.expand(true)
                } else {
                    cbNearBy.isChecked = false
                    expansionLayout.collapse(true)
                }
            }
            screenType = addItemRequest!!.type
            setScreenType(screenType)
            if (addItemRequest!!.type == 1) {
                additionalInfo = addItemRequest!!.additional_info
                if (!TextUtils.isEmpty(additionalInfo!!.brand_name)) {
                    carBrandName = additionalInfo!!.brand_name!!
                    carType = additionalInfo!!.car_type!!
                    tvBrand.text = carBrandName + ", " + carType
                    carBrandId = additionalInfo!!.car_brand_id
                    carTypeId = additionalInfo!!.car_type_id
                    tvBrand.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    vBrand.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
                }

                if (!TextUtils.isEmpty(additionalInfo!!.registration_year)) {
                    etRegistrationYear.setText(additionalInfo!!.registration_year)
                }
                directOwner = additionalInfo!!.direct_owner
                if (directOwner == 1) {
                    switchDirectOwner.setChecked(true, false)
                } else {
                    switchDirectOwner.setChecked(false, false)
                }

            } else if (addItemRequest!!.type == 2) {
                additionalInfo = addItemRequest!!.additional_info
                if (!TextUtils.isEmpty(additionalInfo!!.zone)) {
                    zoneName = additionalInfo!!.zone!!
                    tvZone.text = zoneName
                    zoneId = additionalInfo!!.zone_id
                    tvZone.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    vZone.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
                }

                if (!TextUtils.isEmpty(additionalInfo!!.street_name)) {
                    etStreetName.setText(additionalInfo!!.street_name)
                }
                if (!TextUtils.isEmpty(additionalInfo!!.postal_code.toString())) {
                    etPostalCode.setText(additionalInfo!!.postal_code.toString())
                }
                if (!TextUtils.isEmpty(additionalInfo!!.area)) {
                    etArea.setText(additionalInfo!!.area)
                }
                totalBadRoom = additionalInfo!!.bedroom
                totalBatchRoom = additionalInfo!!.bathroom
                tvBathroomCount.text = totalBatchRoom.toString()
                tvBedRoomCount.text = totalBadRoom.toString()
            }
        } else {
            val intent = Intent(this, SelectCategoryDialogActivity::class.java)
            startActivityForResult(intent, 1)
            overridePendingTransition(0, 0)
        }

    }

    private fun setData() {
        ivMinusBedroom.setOnClickListener {
            if (totalBadRoom != 0) {
                totalBadRoom -= 1
                tvBedRoomCount.text = totalBadRoom.toString()
            }

        }
        ivAddBedRoom.setOnClickListener {
            if (totalBadRoom <= 9) {
                totalBadRoom += 1
                tvBedRoomCount.text = totalBadRoom.toString()
            }
        }
        ivMinusBathRoom.setOnClickListener {
            if (totalBatchRoom != 0) {
                totalBatchRoom -= 1
                tvBathroomCount.text = totalBatchRoom.toString()
            }

        }

        ivAddBathRoom.setOnClickListener {
            if (totalBatchRoom <= 9) {
                totalBatchRoom += 1
                tvBathroomCount.text = totalBatchRoom.toString()
            }
        }
        llSelectZone.setOnClickListener {
            val intent = Intent(this, CarBrandActivity::class.java)
            intent.putExtra(AppConstants.TYPE, 3)
            intent.putExtra(AppConstants.CAR_TYPE, carType)
            intent.putExtra(AppConstants.CAR_TYPE_ID, carTypeId)
            intent.putExtra(AppConstants.CAR_BRAND_NAME, carBrandName)
            intent.putExtra(AppConstants.CAR_BRAND_ID, carBrandId)
            intent.putExtra(AppConstants.ZONE, zoneName)
            intent.putExtra(AppConstants.ZONE_ID, zoneId)
            startActivityForResult(intent, 14)
        }
        switchDirectOwner.setOnStateChangeListener { process, state, jtb ->
            if (state == com.nightonke.jellytogglebutton.State.LEFT) {
                directOwner = 0
            }
            if (state == com.nightonke.jellytogglebutton.State.RIGHT) {
                directOwner = 1
            }
        }
        val font = Typeface.createFromAsset(assets, "fonts/gothicb.ttf")
        cbNearBy.typeface = font
        llSelectBrand.setOnClickListener {
            val intent = Intent(this, CarBrandActivity::class.java)
            intent.putExtra(AppConstants.TYPE, 0)
            intent.putExtra(AppConstants.CAR_TYPE, carType)
            intent.putExtra(AppConstants.CAR_TYPE_ID, carTypeId)
            intent.putExtra(AppConstants.CAR_BRAND_NAME, carBrandName)
            intent.putExtra(AppConstants.CAR_BRAND_ID, carBrandId)
            startActivityForResult(intent, 13)
        }
        llLocation.setOnClickListener {
            val intent = Intent(this, SearchLocationActivity::class.java)
            startActivityForResult(intent, 12)
        }
        cbNearBy.setOnCheckedChangeListener({ _, isChecked ->
            if (isChecked) {
                checkLocationOption()
            } else {
                expansionLayout.collapse(true)
            }
        })

        tvSave.setOnClickListener {
            if (Utils.isOnline(this@AddDetailsActivity)) {
                if (isValidate()) {
                    if (comingFrom != 1) {
                        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        imageCount = 0
                        realPath.clear()
                        grabImage(comingFrom, screenType)
                    } else {
                        updateProductDetail()
                    }
                }
            } else {
                Utils.showSimpleMessage(this@AddDetailsActivity, getString(R.string.check_internet)).show()
            }

        }
        iv_left.setOnClickListener { onBackPressed() }
        llSelectCategory.setOnClickListener {
            val intent = Intent(this, SelectCategoryDialogActivity::class.java)
            startActivityForResult(intent, 1)
            overridePendingTransition(0, 0)
        }
        llNewDetails.setOnClickListener {
            setSelection()
            setSelect()
        }
        llUsedDetail.setOnClickListener {
            setSelection()
            itemType = 2
            ivUsedDetail.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.used_active))
            tvUsedDetail.setTextColor(ContextCompat.getColor(this, R.color.white))
            llUsedDetail.background = ContextCompat.getDrawable(this, R.drawable.red_rectangle_shape)
        }
        etNameDetail.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    vNameDetail.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
                } else {
                    vNameDetail.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.light_gray))
                }
            }
        })

        etRegistrationYear.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    vRegistrationYear.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
                } else {
                    vRegistrationYear.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.light_gray))
                }
            }
        })
        etStreetName.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    vStreetName.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
                } else {
                    vStreetName.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.light_gray))
                }
            }
        })
        etPostalCode.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    vPostalCode.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
                } else {
                    vPostalCode.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.light_gray))
                }
            }
        })
        etArea.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    vArea.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
                } else {
                    vArea.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.light_gray))
                }
            }
        })
        etPrice.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isFocus = true
                if (etPrice.text.toString().isEmpty()) {
                    etPrice.setText(this@AddDetailsActivity.getString(R.string.dollar))
                    etPrice.setSelection(1)
                }
            } else {
                isFocus = false
                if (etPrice.length() == 1) {
                    etPrice.setSelection(0)
                    etPrice.setText("")
                    etPrice.hint = getString(R.string.price)
                }
            }
        }
        etPrice.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (isFocus) {
                    if (etPrice.length() == 0) {
                        etPrice.setText("$")
                        etPrice.setSelection(1)
                    }
                }
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {

            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    vPrice.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
                } else {
                    vPrice.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.light_gray))
                }
            }
        })

        etDescriptionDetail.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            }

            override fun afterTextChanged(arg0: Editable) {
                if (arg0.isNotEmpty()) {
                    vDescription.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
                } else {
                    vDescription.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.light_gray))
                }
            }
        })

    }

    private fun updateProductDetail() {
        val sampleList = ArrayList<ImageModal>()
        for (i in selectedImageList.indices) {
            if (TextUtils.isEmpty(selectedImageList[i].imageUrl) || selectedImageList[i].iseditable) {
                sampleList.add(selectedImageList[i])
            }
        }
        selectedImageList.clear()
        selectedImageList.addAll(sampleList)
        Log.e("<<UpLoadList>>", selectedImageList.size.toString())
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if (selectedImageList.size == 1) {
            pbProgressAdd.visibility = View.VISIBLE
            val realPath = ArrayList<String>()
            updateProductApi(realPath)
        } else {
            imageCount = 0
            realPath.clear()
            grabImage(comingFrom, screenType)
        }
    }

    private fun checkLocationOption() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        } else {
            if (checkPermission()) {
                expansionLayout.expand(true)
            }
        }
    }

    private fun isValidate(): Boolean {
        return when {
            TextUtils.isEmpty(etNameDetail.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_item_name)).show()
                false
            }
            categoryId == 0 -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_category_name)).show()
                false
            }
            screenType == 1 && carBrandId == 0 && carTypeId == 0 -> {
                Utils.showSimpleMessage(this, "Please select car brand and type.").show()
                false
            }
            screenType == 2 && zoneId == 0 -> {
                Utils.showSimpleMessage(this, "Please select zone.").show()
                false
            }
            screenType == 2 && TextUtils.isEmpty(etStreetName.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, "Please enter street name.").show()
                false
            }
            screenType == 2 && TextUtils.isEmpty(etPostalCode.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, "Please enter postal code.").show()
                false
            }
            TextUtils.isEmpty(etPrice.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_price)).show()
                false
            }
            etPrice.text.toString().trim().length <= 1 -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_price)).show()
                false
            }
            screenType == 2 && TextUtils.isEmpty(etArea.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, "Please enter area.").show()
                false
            }
            screenType == 2 && totalBadRoom <= 0 -> {
                Utils.showSimpleMessage(this, "Please enter bedroom count.").show()
                false
            }
            screenType == 2 && totalBatchRoom <= 0 -> {
                Utils.showSimpleMessage(this, "Please enter bathroom count.").show()
                false
            }
            screenType == 1 && TextUtils.isEmpty(etRegistrationYear.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, "Please enter registration year.").show()
                false
            }
            TextUtils.isEmpty(etDescriptionDetail.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_description)).show()
                false
            }
            selectedImageList.size < 2 -> {
                Utils.showSimpleMessage(this, getString(R.string.upload_image)).show()
                false
            }
            else -> true
        }
    }

    private fun shareDialog() {
        val shareDialog = ShareDialog(this, "", "", object : SharePostListener {
            override fun onWhatAppClick() {
                Utils.showToast(this@AddDetailsActivity, "whatApp")

            }

            override fun onFacebookClick() {
                Utils.showToast(this@AddDetailsActivity, "facebook")
            }

            override fun onShareClick() {
                Utils.showToast(this@AddDetailsActivity, "share")
            }

        })
        shareDialog.show()
    }

    private fun setSelection() {
        ivNewDetails.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.new_inactive))
        tvNewDetails.setTextColor(ContextCompat.getColor(this, R.color.gray_light))
        llNewDetails.background = ContextCompat.getDrawable(this, R.drawable.item_type)
        ivUsedDetail.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.used_inactive))
        tvUsedDetail.setTextColor(ContextCompat.getColor(this, R.color.gray_light))
        llUsedDetail.background = ContextCompat.getDrawable(this, R.drawable.item_type)
    }

    private fun setSelect() {
        itemType = 1
        ivNewDetails.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.new_active))
        tvNewDetails.setTextColor(ContextCompat.getColor(this, R.color.white))
        llNewDetails.background = ContextCompat.getDrawable(this, R.drawable.red_rectangle_shape)
    }

    private fun setAdapter(comingFrom: Int) {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        rvImageList.layoutParams.height = width / 3
        val mManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvImageList.layoutManager = mManager
        val selectedImageAdapter = SelectedImageAdapter(this, selectedImageList, flList, imageList.size, comingFrom)
        rvImageList.adapter = selectedImageAdapter
    }

    private fun setListData(imageList: ArrayList<SelectedImage>, comingFrom: Int) {
        selectedImageList.clear()
        for (i in imageList.indices) {
            val imageModal = ImageModal(imageList[i].imageUrl, false, false, 0, imageList[i].adapterPosition, imageList[i].isEditable, imageList[i].imageId)
            selectedImageList.add(imageModal)
        }
        if (selectedImageList.size < 10) {
            val imageModal = ImageModal("", false, true, 0, 0, false, 0)
            selectedImageList.add(imageModal)
        }
        setAdapter(comingFrom)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode === 1) {
            if (resultCode === Activity.RESULT_OK) {
                categoryName = data!!.getStringExtra(AppConstants.CATEGORY_NAME)
                categoryId = data.getIntExtra(AppConstants.SUB_CATEGORY_ID, 0)
                parentId = data.getIntExtra(AppConstants.CATEGORY_ID, 0)
                tvCategory.text = categoryName
                tvCategory.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                vSelectCategory.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
                when (parentId) {
                    85 -> {
                        screenType = 1
                        llSelectBrand.visibility = View.VISIBLE
                        vBrand.visibility = View.VISIBLE
                        llCarFields.visibility = View.VISIBLE

                        llProperties.visibility = View.GONE
                        llProperty.visibility = View.GONE

                        llItemCondition.visibility = View.GONE
                        tvItemCondition.visibility = View.GONE

                        tvDealOption.visibility = View.GONE
                        exLayout.visibility = View.GONE

                    }
                    24 -> {
                        screenType = 2
                        llProperties.visibility = View.VISIBLE
                        llProperty.visibility = View.VISIBLE

                        llItemCondition.visibility = View.GONE
                        tvItemCondition.visibility = View.GONE

                        llSelectBrand.visibility = View.GONE
                        vBrand.visibility = View.GONE
                        llCarFields.visibility = View.GONE

                        tvDealOption.visibility = View.GONE
                        exLayout.visibility = View.GONE
                    }
                    else -> {
                        screenType = 0
                        llSelectBrand.visibility = View.GONE
                        vBrand.visibility = View.GONE
                        llCarFields.visibility = View.GONE

                        llProperties.visibility = View.GONE
                        llProperty.visibility = View.GONE

                        llItemCondition.visibility = View.VISIBLE
                        tvItemCondition.visibility = View.VISIBLE

                        tvDealOption.visibility = View.VISIBLE
                        exLayout.visibility = View.VISIBLE
                    }
                }
            }
            if (resultCode === Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }

        }

        if (requestCode == 12) {
            if (resultCode == Activity.RESULT_OK) {
                address = data!!.getStringExtra(AppConstants.ADDRESS)
                lat = data.getDoubleExtra(AppConstants.LATITUDE, 0.0)
                lng = data.getDoubleExtra(AppConstants.LONGITUDE, 0.0)
                Log.e("<<lat>>", lat.toString())
                Log.e("<<lng>>", lng.toString())
                cbNearBy.isChecked = true
                tvPlaceAddress.text = address
                tvPlaceAddress.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                expansionLayout.collapse(true)
                cbNearBy.isChecked = false
            }
        }
        if (requestCode == 13) {
            if (resultCode == Activity.RESULT_OK) {
                carBrandName = data!!.getStringExtra(AppConstants.CAR_BRAND_NAME)
                carType = data.getStringExtra(AppConstants.CAR_TYPE)
                carBrandId = data.getIntExtra(AppConstants.CAR_BRAND_ID, 0)
                carTypeId = data.getIntExtra(AppConstants.CAR_TYPE_ID, 0)
                tvBrand.text = carBrandName + ", " + carType
                tvBrand.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                vBrand.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                expansionLayout.collapse(true)
                cbNearBy.isChecked = false
            }
        }
        if (requestCode == 14) {
            if (resultCode == Activity.RESULT_OK) {
                zoneName = data!!.getStringExtra(AppConstants.ZONE)
                zoneId = data.getIntExtra(AppConstants.ZONE_ID, 0)
                tvZone.text = zoneName
                tvZone.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                vZone.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                expansionLayout.collapse(true)
                cbNearBy.isChecked = false
            }
        }
        if (requestCode === REQUEST_CODE && resultCode === 0) {
            val provider = Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
            if (!TextUtils.isEmpty(provider)) {
                if (checkPermission()) {
                    expansionLayout.expand(true)
                }

            } else {
                expansionLayout.collapse(true)
                cbNearBy.isChecked = false
            }
        }
        if (requestCode === 2) {
            if (resultCode === Activity.RESULT_OK) {
                imageList.clear()
                imageList = data!!.getParcelableArrayListExtra(AppConstants.SELECTED_LIST)
                tvCountDetail.text = StringBuilder().append(" ").append(imageList.size.toString()).append(" ").append("/ 10")
                setListData(imageList, 1)
            }
            if (resultCode === Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }

        }
    }

    private fun setScreenType(parentId: Int) {
        when (parentId) {
            1 -> {
                screenType = 1
                llSelectBrand.visibility = View.VISIBLE
                vBrand.visibility = View.VISIBLE
                llCarFields.visibility = View.VISIBLE

                llProperties.visibility = View.GONE
                llProperty.visibility = View.GONE

                llItemCondition.visibility = View.GONE
                tvItemCondition.visibility = View.GONE

                tvDealOption.visibility = View.GONE
                exLayout.visibility = View.GONE
                llLocation.visibility = View.GONE

            }
            2 -> {
                screenType = 2
                llProperties.visibility = View.VISIBLE
                llProperty.visibility = View.VISIBLE

                llItemCondition.visibility = View.GONE
                tvItemCondition.visibility = View.GONE

                llSelectBrand.visibility = View.GONE
                vBrand.visibility = View.GONE
                llCarFields.visibility = View.GONE

                tvDealOption.visibility = View.GONE
                exLayout.visibility = View.GONE
                llLocation.visibility = View.GONE
            }
            else -> {
                screenType = 0
                llSelectBrand.visibility = View.GONE
                vBrand.visibility = View.GONE
                llCarFields.visibility = View.GONE

                llProperties.visibility = View.GONE
                llProperty.visibility = View.GONE

                llItemCondition.visibility = View.VISIBLE
                tvItemCondition.visibility = View.VISIBLE

                tvDealOption.visibility = View.VISIBLE
                exLayout.visibility = View.VISIBLE
                llLocation.visibility = View.VISIBLE
            }
        }
    }

    fun grabImage(comingFrom: Int, screenType: Int) {
        class Converter : AsyncTask<Void, Void, Bitmap>() {

            override fun onPreExecute() {
                super.onPreExecute()
                pbProgressAdd.visibility = View.VISIBLE
            }

            override fun doInBackground(vararg params: Void): Bitmap? {
                val uriTemp = FileProvider.getUriForFile(this@AddDetailsActivity, BuildConfig.APPLICATION_ID + ".provider", File(Utils.getRealPathFromUri(this@AddDetailsActivity, Uri.parse(selectedImageList[imageCount].imageUrl))))
                contentResolver.notifyChange(uriTemp, null)
                val cr = contentResolver
                var bmp: Bitmap? = null
                try {
                    bmp = android.provider.MediaStore.Images.Media.getBitmap(cr, uriTemp)
                    val scaledBitmap = Utils.scaleDown(bmp, 1024f, true)
                    //  uri = Utils.getImageUri(this@AddDetailsActivity,CommonUtils.rotateImageIfRequired(this@AddDetailsActivity,scaledBitmap,Uri.parse(selectedImageList[imageCount].imageUrl)))
                    uri = Uri.parse(Utils.saveTempBitmap(this@AddDetailsActivity, CommonUtils.rotateImageIfRequired(this@AddDetailsActivity, scaledBitmap, Uri.parse(selectedImageList[imageCount].imageUrl))))
                    realPath.add(Utils.getRealPathFromURI(this@AddDetailsActivity, uri!!))
                } catch (e: Exception) {
                    Log.e("<<<LOG>>>", "Failed to load", e)
                }
                return bmp
            }

            override fun onPostExecute(aVoid: Bitmap) {
                super.onPostExecute(aVoid)
                if (imageCount == selectedImageList.size - 2) {
                    //   pbProgress.setVisibility(View.GONE)
                    imageCount = 0
                    Log.e("<<RealImagePath>>", realPath.toString())
                    if (Utils.isOnline(this@AddDetailsActivity)) {
                        if (comingFrom != 1) {
                            /*Api call*/
                            uploadImageItem(realPath, screenType)
                        } else {
                            updateProductApi(realPath)
                        }
                    } else {
                        Utils.showSimpleMessage(this@AddDetailsActivity, getString(R.string.check_internet)).show()
                    }

                } else if (imageCount < selectedImageList.size - 1) {
                    imageCount++
                    grabImage(comingFrom, screenType)
                }
            }

        }
        Converter().execute()
    }

    private fun updateProductApi(realPath: java.util.ArrayList<String>) {
        var meetUp = "0"
        if (cbNearBy.isChecked && lat != 0.0 && lng != 0.0) {
            meetUp = "1"
        }
        val editItemRequest: EditItemRequest?
        if (screenType == 1) {
            val additionalInfo = AdditionalInfo(carBrandId, carBrandName, carType, carTypeId, etRegistrationYear.text.toString(), directOwner)
            editItemRequest = EditItemRequest(realPath, imageIdList.toString().replace("[", "").replace("]", ""), categoryId.toString(), categoryName, etNameDetail.text.toString(), etPrice.text.toString().replace(this@AddDetailsActivity.getString(R.string.dollar), "").trim(), itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), lat.toString(), lng.toString(), address, "0",itemId, screenType, additionalInfo, zoneId, carBrandId, carTypeId)

        } else if (screenType == 2) {
            val additionalInfo = AdditionalInfo(carBrandId, carBrandName, carType, carTypeId, etRegistrationYear.text.toString(), directOwner, zoneName, zoneId, etStreetName.text.toString(), (etPostalCode.text.toString()).toInt(), etArea.text.toString(), totalBadRoom, totalBatchRoom)
            editItemRequest = EditItemRequest(realPath, imageIdList.toString().replace("[", "").replace("]", ""), categoryId.toString(), categoryName, etNameDetail.text.toString(), etPrice.text.toString().replace(this@AddDetailsActivity.getString(R.string.dollar), "").trim(), itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), lat.toString(), lng.toString(), address, "0",itemId, screenType, additionalInfo, zoneId)

        } else {
            if (cbNearBy.isChecked) {
                val additionalInfo = AdditionalInfo(carBrandId, carBrandName, carType, carTypeId,"", directOwner)
                editItemRequest = EditItemRequest(realPath, imageIdList.toString().replace("[", "").replace("]", ""), categoryId.toString(), categoryName, etNameDetail.text.toString(), etPrice.text.toString().replace(this@AddDetailsActivity.getString(R.string.dollar), "").trim(), itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), lat.toString(), lng.toString(), address, "1", itemId,screenType,additionalInfo)
            } else {
                val additionalInfo = AdditionalInfo(carBrandId, carBrandName, carType, carTypeId,"", directOwner)
                editItemRequest = EditItemRequest(realPath, imageIdList.toString().replace("[", "").replace("]", ""), categoryId.toString(), categoryName, etNameDetail.text.toString(), etPrice.text.toString().replace(this@AddDetailsActivity.getString(R.string.dollar), "").trim(), itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), "0.0", "0.0", "", "0", itemId,screenType,additionalInfo)
            }
        }
      //  dasds
      //  val editItemRequest = EditItemRequest(realPath, imageIdList.toString().replace("[", "").replace("]", ""), categoryId.toString(), categoryName, etNameDetail.text.toString(), etPrice.text.toString().replace(this@AddDetailsActivity.getString(R.string.dollar), "").trim(), itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), lat.toString(), lng.toString(), address, meetUp, itemId,type)

        ServiceHelper().editItemsApi(editItemRequest,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        pbProgressAdd.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        val categoriesResponse = response.body() as AddItemResponse
                        // Utils.showToast(this@AddDetailsActivity, categoriesResponse.message)
                        // shareDialog()
                        Utils.showToast(this@AddDetailsActivity, categoriesResponse.message)
                        val returnIntent = Intent()
                        returnIntent.putExtra(AppConstants.TYPE, "UpdateItem")
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }

                    override fun onFailure(msg: String?) {
                        pbProgressAdd.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        Utils.showSimpleMessage(this@AddDetailsActivity, msg!!).show()
                    }
                })

    }

    private fun uploadImageItem(realPath: java.util.ArrayList<String>, screenType: Int) {
        var addItemRequest: AddItemRequest? = null
        if (screenType == 1) {
            val additionalInfo = AdditionalInfo(carBrandId, carBrandName, carType, carTypeId, etRegistrationYear.text.toString(), directOwner)
            addItemRequest = AddItemRequest(realPath, categoryId.toString(), categoryName, etNameDetail.text.toString(), etPrice.text.toString().replace(this@AddDetailsActivity.getString(R.string.dollar), "").trim(), itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), lat.toString(), lng.toString(), address, "0", screenType, additionalInfo, zoneId, carBrandId, carTypeId)

        } else if (screenType == 2) {
            val additionalInfo = AdditionalInfo(carBrandId, carBrandName, carType, carTypeId, etRegistrationYear.text.toString(), directOwner, zoneName, zoneId, etStreetName.text.toString(), (etPostalCode.text.toString()).toInt(), etArea.text.toString(), totalBadRoom, totalBatchRoom)
            addItemRequest = AddItemRequest(realPath, categoryId.toString(), categoryName, etNameDetail.text.toString(), etPrice.text.toString().replace(this@AddDetailsActivity.getString(R.string.dollar), "").trim(), itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), lat.toString(), lng.toString(), address, "0", screenType, additionalInfo, zoneId)

        } else {
            if (cbNearBy.isChecked) {
                val additionalInfo = AdditionalInfo(carBrandId, carBrandName, carType, carTypeId,"", directOwner)
                addItemRequest = AddItemRequest(realPath, categoryId.toString(), categoryName, etNameDetail.text.toString(), etPrice.text.toString().replace(this@AddDetailsActivity.getString(R.string.dollar), "").trim(), itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), lat.toString(), lng.toString(), address, "1", screenType,additionalInfo)
            } else {
                val additionalInfo = AdditionalInfo(carBrandId, carBrandName, carType, carTypeId,"", directOwner)
                addItemRequest = AddItemRequest(realPath, categoryId.toString(), categoryName, etNameDetail.text.toString(), etPrice.text.toString().replace(this@AddDetailsActivity.getString(R.string.dollar), "").trim(), itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), "0.0", "0.0", "", "0", screenType,additionalInfo
                )
            }
        }

        ServiceHelper().addItemsApi(addItemRequest,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        pbProgressAdd.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        val categoriesResponse = response.body() as AddItemResponse
                        Utils.showToast(this@AddDetailsActivity, categoriesResponse.message)
                        // shareDialog()

                        val returnIntent = Intent()
                        returnIntent.putExtra(AppConstants.TRANSACTION, 1)
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }

                    override fun onFailure(msg: String?) {
                        pbProgressAdd.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        Utils.showSimpleMessage(this@AddDetailsActivity, msg!!).show()
                    }
                })
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

    override
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            29 -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        expansionLayout.expand(true)
                        Utils.showToast(this, "Permission")
                    } else {
                        cbNearBy.isChecked = false
                        expansionLayout.collapse(true)
                        PermissionCheck(this).showPermissionDialog()

                    }
                }
            }
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.gps_permission))
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

    override fun onBackPressed() {
        val sendList = ArrayList<String>()
        for (i in selectedImageList.indices) {
            if (!TextUtils.isEmpty(selectedImageList[i].imageUrl)) {
                sendList.add(selectedImageList[i].imageUrl)
            }
        }
        // val additionalInfo=AdditionalInfo(carBrandId,carBrandName,carType,carTypeId,etRegistrationYear.text.toString(),directOwner)
        val addItemRequest: AddItemRequest
        if (screenType != 0) {
            if (!TextUtils.isEmpty(etPostalCode.text.toString())) {
                postalCode = (etPostalCode.text.toString()).toInt()
            }
            val additionalInfo = AdditionalInfo(carBrandId, carBrandName, carType, carTypeId, etRegistrationYear.text.toString(), directOwner, zoneName, zoneId, etStreetName.text.toString(), postalCode, etArea.text.toString(), totalBadRoom, totalBatchRoom)
            addItemRequest = AddItemRequest(sendList, categoryId.toString(), categoryName, etNameDetail.text.toString(), etPrice.text.toString().replace(this@AddDetailsActivity.getString(R.string.dollar), "").trim(), itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), lat.toString(), lng.toString(), address, "1", screenType, additionalInfo)

        } else {
            if (cbNearBy.isChecked) {
                addItemRequest = AddItemRequest(sendList, categoryId.toString(), categoryName, etNameDetail.text.toString(), etPrice.text.toString().replace(this@AddDetailsActivity.getString(R.string.dollar), "").trim(), itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), lat.toString(), lng.toString(), address, "1", screenType)
            } else {
                addItemRequest = AddItemRequest(sendList, categoryId.toString(), categoryName, etNameDetail.text.toString(), etPrice.text.toString().replace(this@AddDetailsActivity.getString(R.string.dollar), "").trim(), itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID), "0.0", "0.0", "", "0", screenType)
            }
        }
        val returnIntent = Intent()
        returnIntent.putExtra(AppConstants.POST_DATA, addItemRequest)
        returnIntent.putIntegerArrayListExtra(AppConstants.REJECT_LIST, rejectImageList)
        returnIntent.putExtra(AppConstants.TRANSACTION, 2)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}