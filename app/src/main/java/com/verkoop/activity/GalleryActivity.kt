package com.verkoop.activity

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import com.verkoop.R
import com.verkoop.adapter.GalleryAdapter
import com.verkoop.models.ImageModal
import com.verkoop.utils.PermissionCheck
import com.verkoop.utils.PickerController
import kotlinx.android.synthetic.main.gallery_activity.*
import kotlinx.android.synthetic.main.toolbar_filter.*

 class GalleryActivity : AppCompatActivity() {
     lateinit var itemAdapter:GalleryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_activity)

        setData()
        setAdapter()

    }

    private fun setAdapter() {
        val linearLayoutManager = GridLayoutManager(this, 3)
        rvGallery.layoutManager = linearLayoutManager
         itemAdapter = GalleryAdapter(this,llParentGallery)
        rvGallery.adapter = itemAdapter
        if (checkPermission()) {
            val pickerController= PickerController(this)
            pickerController.displayImage(0,true)
        }
    }

     fun setAdapterData(result: ArrayList<ImageModal>){
         itemAdapter.setData(result)
         itemAdapter.notifyDataSetChanged()
     }

    private fun setData() {
        iv_leftGallery.setOnClickListener { onBackPressed() }
        tvChatGallery.setOnClickListener { }
        tvHeaderGallery.text = getString(R.string.photos)
        tvChatGallery.text = getString(R.string.next_s)

    }

    private fun checkPermission(): Boolean {
        val permissionCheck = PermissionCheck(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionCheck.CheckStoragePermission())
                return true
        } else
            return true
        return false
    }

    override
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            28 -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        val pickerController= PickerController(this)
                        pickerController.displayImage(0,true)
                    } else {
                        checkPermission()
                        PermissionCheck(this).showPermissionDialog()
                      //  finish()
                    }
                }
            }
        }
    }
}