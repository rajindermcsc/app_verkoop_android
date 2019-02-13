package com.verkoop.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.WindowManager
import com.verkoop.BuildConfig
import com.verkoop.R
import com.verkoop.VerkoopApplication
import com.verkoop.adapter.SelectedImageAdapter
import com.verkoop.models.AddItemRequest
import com.verkoop.models.AddItemResponse
import com.verkoop.models.ImageModal
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.ShareDialog
import com.verkoop.utils.SharePostListener
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.add_details_activity.*
import kotlinx.android.synthetic.main.details_toolbar.*
import retrofit2.Response
import java.io.File


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class AddDetailsActivity : AppCompatActivity(), SelectedImageAdapter.SelectedImageCount {
    private var imageList = ArrayList<String>()
    private var selectedImageList = ArrayList<ImageModal>()
    private val realPath = java.util.ArrayList<String>()
    private var uri: Uri? = null
    private var imageCount = 0
    private var categoryId = 0
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
        etPrice.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
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

    private fun isValidate(): Boolean {
        return when {
            TextUtils.isEmpty(etNameDetail.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, "Please enter item name.").show()
                false
            }
            categoryId == 0 -> {
                Utils.showSimpleMessage(this, "Please select category.").show()
                false
            }
            TextUtils.isEmpty(etPrice.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, "Please enter price.").show()
                false
            }
            TextUtils.isEmpty(etDescriptionDetail.text.toString().trim()) -> {
                Utils.showSimpleMessage(this, "Please enter description.").show()
                false
            }
            selectedImageList.size < 2 -> {
                Utils.showSimpleMessage(this, "Please upload atleast one item image.").show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode === 1) {
            if (resultCode === Activity.RESULT_OK) {
                val categoryName = data.getStringExtra(AppConstants.CATEGORY_NAME)
                categoryId = data.getIntExtra(AppConstants.CATEGORY_ID, 0)
                tvCategory.text = categoryName
                tvCategory.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                vSelectCategory.setBackgroundColor(ContextCompat.getColor(this@AddDetailsActivity, R.color.colorPrimary))
            }
            if (resultCode === Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

    }

    fun grabImage() {
        class Converter : AsyncTask<Void, Void, Bitmap>() {

            override fun onPreExecute() {
                super.onPreExecute()
            }

            override fun doInBackground(vararg params: Void): Bitmap? {
                val uriTemp = FileProvider.getUriForFile(this@AddDetailsActivity, BuildConfig.APPLICATION_ID + ".provider", File(Utils.getRealPathFromUri(this@AddDetailsActivity, Uri.parse(selectedImageList[imageCount].imageUrl))))
                // val uriTemp = FileProvider.getUriForFile(this@AddDetailsActivity, BuildConfig.APPLICATION_ID + ".provider", File(CommonUtils.getImageContentUri(this@AddDetailsActivity,File(selectedImageList[imageCount].imageUrl)).toString()))
                contentResolver.notifyChange(uriTemp, null)
                val cr = contentResolver
                var bmp: Bitmap? = null
                try {
                    bmp = android.provider.MediaStore.Images.Media.getBitmap(cr, uriTemp)
                    val scaledBitmap = Utils.scaleDown(bmp, 1024f, true)
                    uri = Utils.getImageUri(this@AddDetailsActivity, scaledBitmap)
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
                        /* window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                 WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)*/
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
                val addItemRequest = AddItemRequest(realPath, categoryId.toString(), etNameDetail.text.toString(), etPrice.text.toString(), itemType.toString(), etDescriptionDetail.text.toString())

                VerkoopApplication.instance.loader.show(this@AddDetailsActivity)
                ServiceHelper().addItemsApi(addItemRequest,
                        object : ServiceHelper.OnResponse {
                            override fun onSuccess(response: Response<*>) {
                                VerkoopApplication.instance.loader.hide(this@AddDetailsActivity)
                                val categoriesResponse = response.body() as AddItemResponse
                                Utils.showToast(this@AddDetailsActivity, categoriesResponse.message)
                                shareDialog()
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
}