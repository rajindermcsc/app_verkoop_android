package com.verkoop.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import android.view.View.OnFocusChangeListener
import android.view.WindowManager
import com.verkoop.BuildConfig
import com.verkoop.R
import com.verkoop.VerkoopApplication
import com.verkoop.adapter.SelectedImageAdapter
import com.verkoop.models.AddItemRequest
import com.verkoop.models.AddItemResponse
import com.verkoop.models.ImageModal
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.*
import kotlinx.android.synthetic.main.add_details_activity.*
import kotlinx.android.synthetic.main.details_toolbar.*
import retrofit2.Response
import java.io.File


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class AddDetailsActivity : AppCompatActivity(), SelectedImageAdapter.SelectedImageCount {
    private val REQUEST_CODE = 11
    private var imageList = ArrayList<String>()
    private var selectedImageList = ArrayList<ImageModal>()
    private val realPath = java.util.ArrayList<String>()
    private var uri: Uri? = null
    private var imageCount = 0
    private var categoryId = 0
    private var lat=""
    private var lng=""
    private var address=""
    private var itemType = 1

    override fun selectDetailCount(count: Int, position: Int) {
        if (count > 0) {
            tvCountDetail.text = StringBuilder().append(" ").append(count.toString()).append(" ").append("/ 10")
        } else {
            tvCountDetail.text = StringBuilder().append(" ").append(getString(R.string.multiple))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_details_activity)
        imageList = intent.getStringArrayListExtra(AppConstants.SELECTED_LIST)
        tvCountDetail.text = StringBuilder().append(" ").append(imageList.size.toString()).append(" ").append("/ 10")
        setListData(imageList)
        setData()
        setSelect()
    }


    private fun setData() {
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
                    window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    //  pbProgress.setVisibility(View.VISIBLE)
                    imageCount = 0
                    grabImage()
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
        etPrice.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etPrice.setText(this@AddDetailsActivity.getString(R.string.dollar))
                etPrice.setSelection(1)
            }
        }
        etPrice.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (etPrice.length() == 0) {
                    etPrice.setText("$")
                    etPrice.setSelection(1)
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
            TextUtils.isEmpty(etPrice.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_price)).show()
                false
            }
            etPrice.text.toString().trim().length <= 1 -> {
                Utils.showSimpleMessage(this, getString(R.string.enter_price)).show()
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

    private fun setAdapter() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        rvImageList.layoutParams.height = width / 3
        val mManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvImageList.layoutManager = mManager
        val selectedImageAdapter = SelectedImageAdapter(this, selectedImageList, flList, imageList.size)
        rvImageList.adapter = selectedImageAdapter
    }

    private fun setListData(imageList: ArrayList<String>) {
        for (i in imageList.indices) {
            val imageModal = ImageModal(imageList[i], false, false, 0)
            selectedImageList.add(imageModal)
        }
        if (selectedImageList.size < 10) {
            val imageModal = ImageModal("", false, true, 0)
            selectedImageList.add(imageModal)
        }
        setAdapter()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode === 1) {
            if (resultCode === Activity.RESULT_OK) {
                val categoryName = data!!.getStringExtra(AppConstants.CATEGORY_NAME)
                categoryId = data.getIntExtra(AppConstants.CATEGORY_ID, 0)
                tvCategory.text = categoryName
                tvCategory.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                vSelectCategory.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
            }
            if (resultCode === Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }

        }
        if (requestCode == 12) {
            if (resultCode == Activity.RESULT_OK) {
                 address = data!!.getStringExtra (AppConstants.ADDRESS)
                 lat = data.getStringExtra (AppConstants.LATITUDE)
                 lng = data.getStringExtra (AppConstants.LONGITUDE)
                cbNearBy.isChecked = true
                tvPlaceAddress.text=address
                tvPlaceAddress.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary))
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

    }

    fun grabImage() {
        class Converter : AsyncTask<Void, Void, Bitmap>() {

            override fun onPreExecute() {
                super.onPreExecute()
                VerkoopApplication.instance.loader.show(this@AddDetailsActivity)
            }

            override fun doInBackground(vararg params: Void): Bitmap? {
                val uriTemp = FileProvider.getUriForFile(this@AddDetailsActivity, BuildConfig.APPLICATION_ID + ".provider", File(Utils.getRealPathFromUri(this@AddDetailsActivity, Uri.parse(selectedImageList[imageCount].imageUrl))))
                contentResolver.notifyChange(uriTemp, null)
                val cr = contentResolver
                var bmp: Bitmap? = null
                try {
                    bmp = android.provider.MediaStore.Images.Media.getBitmap(cr, uriTemp)
                    val scaledBitmap = Utils.scaleDown(bmp, 1024f, true)
                    uri = Utils.getImageUri(this@AddDetailsActivity,CommonUtils.rotateImageIfRequired(this@AddDetailsActivity,scaledBitmap,Uri.parse(selectedImageList[imageCount].imageUrl)))
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
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //    pbProgress.setVisibility(View.VISIBLE)

                        /*Api call*/
                        uploadImageItem(realPath)
                    } else {
                        Utils.showSimpleMessage(this@AddDetailsActivity, getString(R.string.check_internet)).show()
                    }

                } else if (imageCount < selectedImageList.size - 1) {
                    imageCount++
                    grabImage()
                }
            }

            private fun uploadImageItem(realPath: java.util.ArrayList<String>) {
                val addItemRequest:AddItemRequest = if(cbNearBy.isChecked){
                    AddItemRequest(realPath, categoryId.toString(), etNameDetail.text.toString(), etPrice.text.toString().replace(this@AddDetailsActivity.getString(R.string.dollar), "").trim(), itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID),lat,lng,address,"1")
                }else{
                    AddItemRequest(realPath, categoryId.toString(), etNameDetail.text.toString(), etPrice.text.toString().replace(this@AddDetailsActivity.getString(R.string.dollar), "").trim(), itemType.toString(), etDescriptionDetail.text.toString(), Utils.getPreferencesString(this@AddDetailsActivity, AppConstants.USER_ID),"","","","0")
                }

                ServiceHelper().addItemsApi(addItemRequest,
                        object : ServiceHelper.OnResponse {
                            override fun onSuccess(response: Response<*>) {
                                VerkoopApplication.instance.loader.hide(this@AddDetailsActivity)
                                val categoriesResponse = response.body() as AddItemResponse
                                Utils.showToast(this@AddDetailsActivity, categoriesResponse.message)
                                // shareDialog()
                                val returnIntent = Intent()
                                returnIntent.putExtra(AppConstants.TRANSACTION, 1)
                                setResult(Activity.RESULT_OK, returnIntent)
                                finish()
                            }

                            override fun onFailure(msg: String?) {
                                VerkoopApplication.instance.loader.hide(this@AddDetailsActivity)
                                Utils.showSimpleMessage(this@AddDetailsActivity, msg!!).show()
                            }
                        })
            }
        }
        Converter().execute()
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
}