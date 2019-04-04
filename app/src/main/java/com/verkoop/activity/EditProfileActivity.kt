package com.verkoop.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.verkoop.R
import com.verkoop.models.MyProfileIngoResponse
import com.verkoop.models.ProfileUpdateRequest
import com.verkoop.models.ProfileUpdateResponse
import com.verkoop.network.ServiceHelper
import kotlinx.android.synthetic.main.edit_profile_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.verkoop.utils.*
import retrofit2.Response


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class EditProfileActivity : AppCompatActivity() {
    private var uriTemp: Uri? = null
    private var mCurrentPhotoPath: String? = null
    private var cityName:String=""
    private var stateName:String=""
    private var countryName:String=""
    private var gender:String=""
    private var stateId:Int=0
    private var cityId:Int=0
    private var countryId:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile_activity)
        setData()
        if (Utils.isOnline(this)) {
            getProfileDataApi()
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }

    private fun getProfileDataApi() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbProfileProgress.visibility = View.VISIBLE
        ServiceHelper().getMyProfileInfoService(Utils.getPreferencesString(this, AppConstants.USER_ID), object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pbProfileProgress.visibility = View.GONE
                val myProfileResponse = response.body() as MyProfileIngoResponse
                if(myProfileResponse.data!=null) {
                    etUserName.setText(myProfileResponse.data.username)
                    etFirstName.setText(myProfileResponse.data.first_name)
                    etLastName.setText(myProfileResponse.data.last_name)
                    etWebsite.setText(myProfileResponse.data.website)
                    etBio.setText(myProfileResponse.data.bio)
                    etMobileNo.setText(myProfileResponse.data.mobile_no)
                    if(!TextUtils.isEmpty(myProfileResponse.data.DOB)){
                        tvDate.text = myProfileResponse.data.DOB
                    }

                    when {
                        myProfileResponse.data.gender.equals("Male",ignoreCase = true) -> spinner1.setSelection(2)
                        myProfileResponse.data.gender.equals("Female",ignoreCase = true) -> spinner1.setSelection(1)
                        else -> spinner1.setSelection(0)
                    }
                    if(!TextUtils.isEmpty(myProfileResponse.data.profile_pic)){
                        Picasso.with(this@EditProfileActivity).load(AppConstants.IMAGE_URL+myProfileResponse.data.profile_pic)
                                .resize(720, 720)
                                .centerInside()
                                .error(R.mipmap.gallery_place)
                                .placeholder(R.mipmap.gallery_place)
                                .into(ivProfileImage)
                    }
                    if(!TextUtils.isEmpty(myProfileResponse.data.state)&&!TextUtils.isEmpty(myProfileResponse.data.city)){
                        etMyCity.text =StringBuilder().append(myProfileResponse.data.city).append("(").append(myProfileResponse.data.state).append(")")
                    cityId=myProfileResponse.data.city_id
                    stateId=myProfileResponse.data.state_id
                    }
                }
            }

            override fun onFailure(msg: String?) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pbProfileProgress.visibility = View.GONE
                Utils.showSimpleMessage(this@EditProfileActivity, msg!!).show()
            }
        })
    }

    private fun setData() {
        etEmail.setText(Utils.getPreferencesString(this,AppConstants.USER_EMAIL_ID))
        ivRight.visibility=View.VISIBLE
        ivLeftLocation.setOnClickListener { onBackPressed() }
        ivRight.setOnClickListener {
            if (Utils.isOnline(this)) {
                KeyboardUtil.hideKeyboard(this)
                updateProfileData()
            } else {
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            }
        }
        tvHeaderLoc.text = getString(R.string.my_profile)
        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Utils.hideKeyboardOnOutSideTouch(spinner1, this@EditProfileActivity)
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Utils.hideKeyboardOnOutSideTouch(spinner1, this@EditProfileActivity)
                gender = when (position) {
                    1 -> "Female"
                    2 -> "Male"
                    else -> ""
                }
            }
        }
        tvDate.setOnClickListener {
            setDateDialog()
        }
        ivProfileImage.setOnClickListener {
            addProfileImage()
        }
        llCity.setOnClickListener {
            val intent=Intent(this,RegionActivity::class.java)
            intent.putExtra(AppConstants.CITY_ID,cityId)
            intent.putExtra(AppConstants.STATE_ID,stateId)
            startActivityForResult(intent,3)
        }
    }

    private fun updateProfileData() {
        val updateCategoryRequest=   ProfileUpdateRequest(
                Utils.getPreferencesString(this,AppConstants.USER_ID),
                if (etUserName.text?.toString() != null) etUserName.text.toString() else "",
                if (etFirstName.text?.toString() != null) etFirstName.text.toString() else "",
                if (etLastName.text?.toString() != null) etLastName.text.toString() else "",
                cityName,stateName,countryName,cityId.toString(),stateId.toString(),countryId.toString(),
                if (etWebsite.text?.toString() != null) etWebsite.text.toString() else "",
                if (etBio.text?.toString() != null) etBio.text.toString() else "",
                if (mCurrentPhotoPath != null) mCurrentPhotoPath!! else "" ,
                if (etMobileNo.text?.toString() != null) etMobileNo.text.toString() else "",
                gender  ,tvDate.text.toString())
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbProfileProgress.visibility = View.VISIBLE
        ServiceHelper().updateProfileService(updateCategoryRequest, object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pbProfileProgress.visibility = View.GONE
                val homeDataResponse = response.body() as ProfileUpdateResponse
                Utils.showToast(this@EditProfileActivity,getString(R.string.profile_updated))
                  onBackPressed()
            }

            override fun onFailure(msg: String?) {
               window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pbProfileProgress.visibility = View.GONE
                Utils.showSimpleMessage(this@EditProfileActivity, msg!!).show()
            }
        })

    }


    private fun addProfileImage() {
        if (checkPermission()) {
            openPopUp()
        }
    }

    private fun setDateDialog() {
        Utils.setDatePicker(this, object : Utils.CurrentDate {
            override fun getSelectedDate(date: String) {
                tvDate.text = date
            }
        })
    }

    private fun checkPermission(): Boolean {
        val permissionCheck = PermissionCheck(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionCheck.checkGalleryPermission())
                return true
        } else
            return true
        return false
    }

    private fun openPopUp() {
        val shareDialog = selectOptionDialog(this, object : SelectionOptionListener {
            override fun leaveClick(option: String) {
                if (option.equals(getString(R.string.camera), ignoreCase = true)) {
                    takePicture()
                } else if (option.equals(getString(R.string.gallery), ignoreCase = true)) {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    intent.type = "image/*"
                    startActivityForResult(Intent.createChooser(intent, "Pick From"), REQUEST_GET_PHOTO)
                }
            }
        })
        shareDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_TAKE_PHOTO -> {
                    val f = File(mCurrentPhotoPath!!)
                    uriTemp = FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", f)
                    CropImage.activity(uriTemp)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(this)
                }

                REQUEST_GET_PHOTO -> {
                    uriTemp = data?.data
                    CropImage.activity(uriTemp)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(this)
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    mCurrentPhotoPath = Utils.getRealPathFromURI(this@EditProfileActivity, result.uri)
                    val file = File(mCurrentPhotoPath!!)
                    Picasso.with(this).load(file)
                            .resize(720, 720)
                            .centerInside()
                            .error(R.mipmap.gallery_place)
                            .placeholder(R.mipmap.gallery_place)
                            .into(ivProfileImage)
                }
            }
            if (requestCode === 3) {
                if (resultCode === Activity.RESULT_OK) {
                     cityName = data!!.getStringExtra(AppConstants.CITY_NAME)
                     cityId = data.getIntExtra(AppConstants.CITY_ID,0)
                     stateName = data.getStringExtra(AppConstants.STATE_NAME)
                     stateId = data.getIntExtra(AppConstants.STATE_ID,0)
                     countryId = data.getIntExtra(AppConstants.COUNTRY_ID,0)
                     countryName = data.getStringExtra(AppConstants.COUNTRY_NAME)
                   etMyCity.text=StringBuilder().append(cityName).append("(").append(stateName).append(")")
                }
                if (resultCode === Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                }
            }
        }


    }

    companion object {
        internal const val REQUEST_TAKE_PHOTO = 1
        internal const val REQUEST_GET_PHOTO = 2
    }

    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        FileProvider.getUriForFile(this,
                                applicationContext.packageName + ".provider", photoFile))
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                "." +
                        "", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

}