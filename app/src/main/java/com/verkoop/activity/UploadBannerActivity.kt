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
import android.view.View
import android.view.WindowManager
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.verkoop.R
import com.verkoop.models.ProfileUpdateResponse
import com.verkoop.models.UploadBannerRequest
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.*
import kotlinx.android.synthetic.main.toolbar_location.*
import kotlinx.android.synthetic.main.upload_banner_activity.*
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.text.TextUtils

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class UploadBannerActivity:AppCompatActivity() {
    private var planId:Int=0
    private var uriTemp: Uri? = null
    private var mCurrentPhotoPath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.upload_banner_activity)
        setData()
    }

    private fun setData() {
        ivRight.setImageResource(R.mipmap.get_coins)
        ivRight.visibility= View.VISIBLE
        tvHeaderLoc.text = getString(R.string.featured_product)
        ivLeftLocation.setOnClickListener { onBackPressed() }
        tvUploadImage.setOnClickListener {
            addProfileImage()
        }
        tvSaveBanner.setOnClickListener {
            if(!TextUtils.isEmpty(mCurrentPhotoPath)) {
                updateProfileData()
            }else{
                Utils.showSimpleMessage(this@UploadBannerActivity,getString(R.string.upload_banne)).show()
            }
           /* if(tvSaveBanner.text.toString().equals("NEXT",ignoreCase = true)) {
                val intent = Intent(this, AdvertPackagesActivity::class.java)
                startActivityForResult(intent,4)
            }else{
                if(!TextUtils.isEmpty(mCurrentPhotoPath)) {
                    updateProfileData()
                }else{
                    Utils.showSimpleMessage(this@UploadBannerActivity,getString(R.string.upload_banne)).show()
                }
            }*/
        }
        ivRight.setOnClickListener {
            val intent=Intent(this,AdvertPackagesActivity::class.java)
           startActivity(intent)
        }
    }

    private fun addProfileImage() {
        if (checkPermission()) {
            openPopUp()
        }
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
        val shareDialog = SelectOptionDialog(this, object : SelectionOptionListener {
            override fun leaveClick(option: String) {
                if (option.equals(getString(R.string.camera), ignoreCase = true)) {
                    takePicture()
                } else if (option.equals(getString(R.string.gallery), ignoreCase = true)) {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    intent.type = "image/*"
                    startActivityForResult(Intent.createChooser(intent, "Pick From"), EditProfileActivity.REQUEST_GET_PHOTO)
                }
            }
        })
        shareDialog.show()
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
                startActivityForResult(takePictureIntent, EditProfileActivity.REQUEST_TAKE_PHOTO)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                EditProfileActivity.REQUEST_TAKE_PHOTO -> {

                    val f = File(mCurrentPhotoPath!!)
                    uriTemp = FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", f)
                    CropImage.activity(uriTemp)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(this)
                }

                EditProfileActivity.REQUEST_GET_PHOTO -> {
                    uriTemp = data?.data
                    CropImage.activity(uriTemp)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(this)
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    tvUploadImage.text="Change Banner"
                    val result = CropImage.getActivityResult(data)
                    mCurrentPhotoPath = Utils.getRealPathFromURI(this@UploadBannerActivity, result.uri)
                    val file = File(mCurrentPhotoPath!!)
                    Picasso.with(this).load(file)
                            .resize(1024, 1024)
                            .centerCrop()
                            .error(R.mipmap.gallery_place)
                            .placeholder(R.mipmap.gallery_place)
                            .into(ivBanner)
                }

            }
            /*if (requestCode === 4) {
                if (resultCode === Activity.RESULT_OK) {
                    planId = data!!.getIntExtra(AppConstants.INTENT_RESULT,0)
                    if(planId>0){
                        tvSaveBanner.text="SAVE"
                    }else{
                        tvSaveBanner.text="NEXT"
                    }
                }
                if (resultCode === Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                }
            }*/
        }
    }

    private fun updateProfileData() {
        val uploadBannerRequest = UploadBannerRequest(Utils.getPreferencesString(this,AppConstants.USER_ID).toInt(),planId,mCurrentPhotoPath!!)
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbUpload.visibility = View.VISIBLE
        ServiceHelper().updateBannerService(uploadBannerRequest, object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pbUpload.visibility = View.GONE
                val homeDataResponse = response.body() as ProfileUpdateResponse
                Utils.showToast(this@UploadBannerActivity,homeDataResponse.message)
                setDialogBox()

            }

            override fun onFailure(msg: String?) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pbUpload.visibility = View.GONE
                Utils.showSimpleMessage(this@UploadBannerActivity, msg!!).show()
            }
        })

    }

    private fun setDialogBox() {
            val shareDialog = WarningDialog(this,  object : SelectionListener {
                override fun leaveClick() {
                    onBackPressed()
                }
            })
            shareDialog.show()

    }
}