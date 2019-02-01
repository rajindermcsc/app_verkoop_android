package com.verkoop.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.futuremind.recyclerviewfastscroll.Utils
import com.verkoop.R
import com.verkoop.adapter.GalleryAdapter
import com.verkoop.customgallery.Define
import com.verkoop.customgallery.PermissionCheck
import com.verkoop.customgallery.PickerController
import com.verkoop.customgallery.SingleMediaScanner
import com.verkoop.models.ImageModal
import com.verkoop.utils.AppConstants
import kotlinx.android.synthetic.main.gallery_activity.*
import kotlinx.android.synthetic.main.toolbar_filter.*
import java.io.File

class GalleryActivity : AppCompatActivity(), GalleryAdapter.ImageCountCallBack {
    private var define = Define()
    private var selectcount:Int=0
    private lateinit var itemAdapter: GalleryAdapter
    private lateinit var pickerController: PickerController
    private val imageUris =ArrayList<ImageModal>()
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
        iv_leftGallery.setOnClickListener { onBackPressed() }
        tvChatGallery.setOnClickListener {
            setIntentData()
        }
        tvHeaderGallery.text = getString(R.string.photos)
        tvChatGallery.text = getString(R.string.next_s)
        tvChatGallery.visibility=View.INVISIBLE

    }

    private fun setIntentData() {
        val selectedList=ArrayList<String>()
       for (i in imageUris.indices){
           if(selectedList.size<10) {
               if (imageUris[i].isSelected) {
                   selectedList.add(imageUris[i].imageUrl)
               }
           }else{
               val intent=Intent(this,AddDetailsActivity::class.java)
               intent.putStringArrayListExtra(AppConstants.SELECTED_LIST,selectedList)
               startActivity(intent)
               break
           }
       }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == define.TAKE_A_PICK_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val savedFile = File(pickerController.getSavePath())
                SingleMediaScanner(this, savedFile)
                addImage(Uri.fromFile(savedFile))
            } else {
                File(pickerController.getSavePath()).delete()
            }
        }
    }

    private fun addImage(path: Uri) {
        val imagemodal = ImageModal(path.toString(), false, false, 0)
        imageUris.add(1, imagemodal)
        itemAdapter.notifyDataSetChanged()
        pickerController.setAddImagePath(path)
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
        selectcount=count
        if(count>0){
            tvSelectedCount.text=StringBuilder().append(" ").append(count.toString()).append(" ").append("/ 10")
            tvChatGallery.visibility=View.VISIBLE
        }else{
            tvSelectedCount.text= StringBuilder().append(" ").append(getString(R.string.multiple))
            tvChatGallery.visibility=View.INVISIBLE
        }
    }
}