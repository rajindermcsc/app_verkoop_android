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
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.verkoop.R
import com.verkoop.utils.PermissionCheck
import com.verkoop.utils.SelectionOptionListener
import com.verkoop.utils.Utils
import com.verkoop.utils.selectOptionDialog
import kotlinx.android.synthetic.main.edit_profile_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    private var uriTemp: Uri? = null
    private var mCurrentPhotoPath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile_activity)
        setData()
    }

    private fun setData() {
        ivRight.visibility=View.VISIBLE
        ivLeftLocation.setOnClickListener { onBackPressed() }
        ivRight.setOnClickListener { }
        tvHeaderLoc.text = getString(R.string.my_profile)
        spinner1.setSelection(1)
        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Utils.hideKeyboardOnOutSideTouch(spinner1, this@EditProfileActivity)
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.e("selectedPosition", position.toString())
                Log.e("selectedString", parent!!.getItemAtPosition(position).toString())
                Utils.hideKeyboardOnOutSideTouch(spinner1, this@EditProfileActivity)
            }
        }
        tvDate.setOnClickListener {
            setDateDialog()
        }
        ivProfileImage.setOnClickListener {
            addProfileImage()
        }
        etMyCity.setOnClickListener {
            val intent=Intent(this,RegionActivity::class.java)
            startActivity(intent)
        }
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
                            .error(R.mipmap.setting)
                            .placeholder(R.mipmap.gallery_place)
                            .into(ivProfileImage)
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