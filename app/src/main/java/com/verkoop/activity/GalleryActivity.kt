package com.verkoop.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.View
import com.verkoop.R
import com.verkoop.adapter.GalleryAdapter
import com.verkoop.customgallery.Define
import com.verkoop.utils.PermissionCheck
import com.verkoop.customgallery.PickerController
import com.verkoop.customgallery.SingleMediaScanner
import com.verkoop.models.ImageModal
import com.verkoop.utils.AppConstants
import com.verkoop.utils.CommonUtils
import kotlinx.android.synthetic.main.gallery_activity.*
import kotlinx.android.synthetic.main.toolbar_filter.*
import java.io.File


class GalleryActivity : AppCompatActivity(), GalleryAdapter.ImageCountCallBack {
    private var define = Define()
    private var selectcount: Int = 0
    private lateinit var itemAdapter: GalleryAdapter
    private lateinit var pickerController: PickerController
    private val imageUris = ArrayList<ImageModal>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_activity)
        pickerController = PickerController(this)
        setData()
        setAdapter()
    }

    private fun setAdapter() {
        val linearLayoutManager = GridLayoutManager(this, 3)
        rvGallery.layoutManager = linearLayoutManager
        itemAdapter = GalleryAdapter(this, llParentGallery, pickerController, pickerController.getPathDir(0))
        rvGallery.adapter = itemAdapter
        if (checkPermission()) {
            pickerController.displayImage(0, true)
        }
    }

    fun setAdapterData(result: ArrayList<ImageModal>) {
        imageUris.addAll(result)
        itemAdapter.setData(imageUris)
        itemAdapter.notifyDataSetChanged()
    }

    private fun setData() {
        iv_leftGallery.setOnClickListener {
            val returnIntent = Intent()
            setResult(Activity.RESULT_CANCELED, returnIntent)
            finish()
        }
        tvChatGallery.setOnClickListener {
            setIntentData()
        }
        tvHeaderGallery.text = getString(R.string.photos)
        tvChatGallery.text = getString(R.string.next_s)
        tvChatGallery.visibility = View.INVISIBLE

    }

    private fun setIntentData() {
        val selectedList = ArrayList<String>()
        for (i in imageUris.indices) {
            if (selectedList.size < selectcount) {
                if (imageUris[i].isSelected) {
                    selectedList.add(imageUris[i].imageUrl)
                }
            } else {
                val intent = Intent(this, AddDetailsActivity::class.java)
                intent.putStringArrayListExtra(AppConstants.SELECTED_LIST, selectedList)
                startActivityForResult(intent, 1)
                break
            }
        }

    }

    private fun checkPermission(): Boolean {
        val permissionCheck = PermissionCheck(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionCheck.checkStoragePermission())
                return true
        } else
            return true
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == define.TAKE_A_PICK_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val savedFile = File(pickerController.getSavePath())
                SingleMediaScanner(this, savedFile)
                addImage(savedFile)
                val contentURI = CommonUtils.getImageContentUri(this@GalleryActivity, savedFile)
                Log.e("ImageContentURi", contentURI.toString())
            } else {
                File(pickerController.getSavePath()).delete()
            }
        }
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getIntExtra(AppConstants.TRANSACTION,0)
                if(result==1){
                    val returnIntent = Intent()
                    returnIntent.putExtra(AppConstants.TRANSACTION, result)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                    overridePendingTransition(0,0)
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

    }

    private fun addImage(path: File) {
        val imagemodal = ImageModal(CommonUtils.getImageContentUri(this@GalleryActivity, path).toString(), false, false, 0)
        imageUris.add(1, imagemodal)
        itemAdapter.notifyDataSetChanged()
        pickerController.setAddImagePath(Uri.fromFile(path))
    }

    override
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            28 -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        val pickerController = PickerController(this)
                        pickerController.displayImage(0, true)
                    } else {
                        checkPermission()
                        PermissionCheck(this).showPermissionDialog()
                        //  finish()
                    }
                }
            }
        }
    }

    override fun imageCount(count: Int, position: Int) {
        selectcount = count
        if (count > 0) {
            tvSelectedCount.text = StringBuilder().append(" ").append(count.toString()).append(" ").append("/ 10")
            tvChatGallery.visibility = View.VISIBLE
        } else {
            tvSelectedCount.text = StringBuilder().append(" ").append(getString(R.string.multiple))
            tvChatGallery.visibility = View.INVISIBLE
        }
    }

    override fun onBackPressed() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }
}