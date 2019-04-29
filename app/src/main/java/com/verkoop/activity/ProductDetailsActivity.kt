package com.verkoop.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.squareup.picasso.Picasso
import com.verkoop.R
import com.verkoop.adapter.CommentListAdapter
import com.verkoop.models.*
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.SelectionListener
import com.verkoop.utils.Utils
import com.verkoop.utils.selectOptionDialog
import kotlinx.android.synthetic.main.item_details_activity.*
import kotlinx.android.synthetic.main.toolbar_product_details.*
import retrofit2.Response

class ProductDetailsActivity : AppCompatActivity() {
    private var imageURLLIst = ArrayList<String>()
    private val commentsList = ArrayList<CommentModal>()
    private val reportList = ArrayList<ReportResponse>()
    private val menuList = ArrayList<PowerMenuItem>()
    private var dataComment: CommentModal? = null
    private var dataIntent: DataItems? = null
    private var powerMenu: PowerMenu? = null
    private var itemId: Int = 0
    private var userId: Int = 0
    private var isSoldItem: Int = 0
    private var adapterPosition: Int = 0
    private var comingType: Int = 0
    private var screenHeight: Int = 0
    private var userName: String = ""
    private lateinit var commentListAdapter: CommentListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_details_activity)
        tvSellItem.visibility = View.GONE
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenHeight = displayMetrics.heightPixels
        mDemoSliderDetails.minimumHeight = (screenHeight / 2) + 100
        comingType = intent.getIntExtra(AppConstants.COMING_TYPE, 0)
        setCommentAdapter()
        adapterPosition = intent.getIntExtra(AppConstants.ADAPTER_POSITION, 0)
        if (intent.getIntExtra(AppConstants.COMING_FROM, 0) == 1) {
            tvSellItem.visibility = View.GONE
            ivRightProduct.visibility = View.VISIBLE
        }
        if (Utils.isOnline(this)) {
            getItemDetailsService(intent.getIntExtra(AppConstants.ITEM_ID, 0))
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }

    private fun setCommentAdapter() {
        val mManager = LinearLayoutManager(this)
        rvPostCommentList.layoutManager = mManager
        commentListAdapter = CommentListAdapter(this, pbProgressProduct, comingType)
        rvPostCommentList.adapter = commentListAdapter
    }

    private fun setData(imageURLLIst: ArrayList<String>, data: DataItems) {
        itemId = data.id
        userId = data.user_id
        userName = data.username
        isSoldItem = data.is_sold
        if (data.meet_up == 1 && data.latitude != 0.0 && data.longitude != 0.0) {
            tvAddress.text = data.address
            llMeetUp.visibility = View.VISIBLE
            viewMeetUp.visibility = View.VISIBLE
        } else {
            llMeetUp.visibility = View.GONE
            viewMeetUp.visibility = View.GONE
        }
        if (data.is_sold == 1 || data.user_id == Utils.getPreferencesString(this, AppConstants.USER_ID).toInt()) {
            tvSellItem.visibility = View.GONE
        } else {
            tvSellItem.visibility = View.VISIBLE
        }

        tvAddress.setOnClickListener {
            if (data.latitude != 0.0 && data.longitude != 0.0) {
                val geoUri = "http://maps.google.com/maps?q=loc:" + data.latitude + "," + data.longitude + "(" + data.address + ")"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
                startActivity(intent)
            }
        }
        if (!TextUtils.isEmpty(data.profile_pic)) {
            Picasso.with(this@ProductDetailsActivity).load(AppConstants.IMAGE_URL + data.profile_pic)
                    .resize(720, 720)
                    .centerInside()
                    .error(R.mipmap.pic_placeholder)
                    .placeholder(R.mipmap.pic_placeholder)
                    .into(ivProfileTool)
        } else {
            Picasso.with(this@ProductDetailsActivity).load(R.mipmap.pic_placeholder)
                    .resize(720, 720)
                    .centerInside()
                    .error(R.mipmap.pic_placeholder)
                    .placeholder(R.mipmap.pic_placeholder)
                    .into(ivProfileTool)
        }
        ivRightProduct.setImageResource(R.drawable.menu_icone)

        ivRightProduct.setOnClickListener {
            if (userId != Utils.getPreferencesString(this, AppConstants.USER_ID).toInt()) {
                menuList.clear()
                val powerMenu1 = PowerMenuItem(getString(R.string.report_listing), false)
                val powerMenu2 = PowerMenuItem(getString(R.string.share), false)
                menuList.add(powerMenu1)
                menuList.add(powerMenu2)
                openPowerMenu(menuList)
            } else {
                menuList.clear()
                val powerMenu = PowerMenuItem(getString(R.string.share), false)
                val powerMenu1 = PowerMenuItem(getString(R.string.edit_item), false)
                val powerMenu2 = PowerMenuItem(getString(R.string.mark_sold), false)
                val powerMenu3 = PowerMenuItem(getString(R.string.delete_listing), false)
                if (isSoldItem != 1) {
                    menuList.add(powerMenu)
                    menuList.add(powerMenu1)
                    menuList.add(powerMenu2)
                    menuList.add(powerMenu3)
                    openPowerMenu(menuList)
                } else {
                    menuList.add(powerMenu)
                    menuList.add(powerMenu3)
                    openPowerMenu(menuList)
                }

            }

        }
        tvPostComment.setOnClickListener {
            if (data.id != 0) {
                val i = Intent(this, AddCommentActivity::class.java)
                i.putExtra(AppConstants.ITEM_ID, data.id)
                startActivityForResult(i, 1)
            }
        }
        ivLeft.setOnClickListener { onBackPressed() }
        custom_indicator_detail.setDefaultIndicatorColor(ContextCompat.getColor(this, R.color.white), ContextCompat.getColor(this, R.color.light_gray))
        mDemoSliderDetails.setCustomIndicator(custom_indicator_detail)
        for (i in imageURLLIst.indices) {
            val textSliderView = DefaultSliderView(this)
            // initialize a SliderLayout
            textSliderView.image(AppConstants.IMAGE_URL + imageURLLIst[i]).setOnSliderClickListener({ slider ->
            }).scaleType = BaseSliderView.ScaleType.CenterCrop
            mDemoSliderDetails.addSlider(textSliderView)
        }
        mDemoSliderDetails.stopAutoCycle()
        tvProductName.text = data.name
        tvLikes.text = data.items_like_count.toString()
        tvPrice.text = StringBuilder().append(": ").append(getString(R.string.dollar)).append(data.price)
        tvDescription.text = data.description
        tvUserName.text = data.username
        tvDateDetails.text = StringBuilder().append(Utils.getDateDifferenceDetails(data.created_at)).append(" ").append("ago")
        tvDateTool.text = StringBuilder().append(Utils.getDateDifferenceDetails(data.created_at)).append(" ").append("ago")
        tvCategoryDetail.text = StringBuilder().append(": ").append(data.category_name)
        if (data.item_type == 1) {
            tvType.text = "New"
        } else {
            tvType.text = getString(R.string.used)
        }
        if (data.type == 1) {
            llCarDetails.visibility = View.VISIBLE
            CommonView.visibility = View.VISIBLE
            llPropertyDetails.visibility = View.GONE
            if (data.additional_info != null) {
                tvRegistrationYear.text = StringBuilder().append(": ").append(data.additional_info!!.registration_year)
                tvCarBrand.text = StringBuilder().append(": ").append(data.additional_info!!.brand_name)
                tvCarType.text = StringBuilder().append(": ").append(data.additional_info!!.car_type)
                if (data.additional_info!!.direct_owner.equals("1", ignoreCase = true)) {
                    tvDirectOwner.text = StringBuilder().append(": ").append(getString(R.string.res))
                } else {
                    tvDirectOwner.text = StringBuilder().append(": ").append(getString(R.string.no))
                }
            }

        } else if (data.type == 2) {
            llPropertyDetails.visibility = View.VISIBLE
            CommonView.visibility = View.VISIBLE
            llCarDetails.visibility = View.GONE
            if (data.additional_info != null) {
                tvStreetName.text = StringBuilder().append(": ").append(data.additional_info!!.street_name)
                tvPostalCode.text = StringBuilder().append(": ").append(data.additional_info!!.postal_code)
                tvBedRoom.text = StringBuilder().append(": ").append(data.additional_info!!.bedroom)
                tvBathRoom.text = StringBuilder().append(": ").append(data.additional_info!!.bathroom)
                tvZone.text = StringBuilder().append(": ").append(data.additional_info!!.zone)
                tvArea.text = StringBuilder().append(": ").append(data.additional_info!!.area)
            }
        } else {
            llCarDetails.visibility = View.GONE
            llPropertyDetails.visibility = View.GONE
            CommonView.visibility = View.GONE

        }
    }

    private fun openPowerMenu(menuList: ArrayList<PowerMenuItem>) {
        powerMenu = PowerMenu.Builder(this)
                .addItemList(menuList)
                .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
                .setMenuRadius(1f)
                .setMenuShadow(10f)
                .setTextColor(ContextCompat.getColor(this, R.color.black_dark))
                .setMenuColor(Color.WHITE)
                .setDivider(ContextCompat.getDrawable(this, R.drawable.horizontal_line))
                .setDividerHeight(1)
                .setSelectedMenuColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setOnMenuItemClickListener(onMenuItemClickListener)
                .build()
        powerMenu!!.showAsAnchorRightTop(ivRightProduct)
    }

    private val onMenuItemClickListener = OnMenuItemClickListener<PowerMenuItem> { position, item ->
        powerMenu!!.selectedPosition = position
        powerMenu!!.dismiss()
        if (item.title.equals(getString(R.string.report_listing), ignoreCase = true)) {
            val reportIntent = Intent(this, ReportUserActivity::class.java)
            reportIntent.putParcelableArrayListExtra(AppConstants.REPORT_LIST, reportList)
            reportIntent.putExtra(AppConstants.ITEM_ID, itemId)
            startActivity(reportIntent)
        } else if (item.title.equals(getString(R.string.share), ignoreCase = true)) {
            sharePost()

        } else if (item.title.equals(getString(R.string.mark_sold), ignoreCase = true)) {
            marksSoldDialogBox()

        } else if (item.title.equals(getString(R.string.delete_listing), ignoreCase = true)) {
            deleteProductDialog()

        } else if (item.title.equals(getString(R.string.edit_item), ignoreCase = true)) {
            //   val commentsList= ArrayList<CommentModal>()
            //   val reportsList=ArrayList<ReportResponse>()
            //  dataIntent!!.comments=commentsList
            //  dataIntent!!.reports=reportsList
            val intent = Intent(this, AddDetailsActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM, 1)
            intent.putExtra(AppConstants.PRODUCT_DETAIL, dataIntent!!)
            startActivityForResult(intent, 2)
        }
    }

    private fun deleteProductDialog() {
        val shareDialog = selectOptionDialog.DeleteCommentDialog(this, getString(R.string.delete_heading), getString(R.string.delete_des), object : SelectionListener {
            override fun leaveClick() {
                if (Utils.isOnline(this@ProductDetailsActivity)) {
                    deleteItemApi()
                } else {
                    Utils.showSimpleMessage(this@ProductDetailsActivity, getString(R.string.check_internet)).show()
                }

            }
        })
        shareDialog.show()

    }

    private fun deleteItemApi() {
        pbProgressProduct.visibility = View.VISIBLE
        ServiceHelper().deleteListingService(itemId,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        pbProgressProduct.visibility = View.GONE
                        val likeResponse = response.body() as DisLikeResponse
                        Utils.showToast(this@ProductDetailsActivity, "Item deleted successfully.")
                        val returnIntent = Intent()
                        returnIntent.putExtra(AppConstants.ADAPTER_POSITION, adapterPosition)
                        returnIntent.putExtra(AppConstants.TYPE, "deleteItem")
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }

                    override fun onFailure(msg: String?) {
                        pbProgressProduct.visibility = View.GONE
                        Utils.showSimpleMessage(this@ProductDetailsActivity, msg!!).show()
                    }
                })
    }

    private fun sharePost() {
        val i = Intent(android.content.Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Verkoop App product!")
        i.putExtra(android.content.Intent.EXTRA_TEXT, "find text for ")
        startActivity(Intent.createChooser(i, "Share via"))
    }

    private fun marksSoldDialogBox() {
        val shareDialog = selectOptionDialog.DeleteCommentDialog(this, getString(R.string.confirm_sold), getString(R.string.sold_des), object : SelectionListener {
            override fun leaveClick() {
                if (Utils.isOnline(this@ProductDetailsActivity)) {
                    markAsSoldApi()
                } else {
                    Utils.showSimpleMessage(this@ProductDetailsActivity, getString(R.string.check_internet)).show()
                }

            }
        })
        shareDialog.show()
    }

    private fun markAsSoldApi() {
        ServiceHelper().markAsSoldService(itemId, MarkAsSoldRequest(Utils.getPreferencesString(this, AppConstants.USER_ID)),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        //   val blockResponse = response.body() as DisLikeResponse
                        Utils.showToast(this@ProductDetailsActivity, "Item marked as sold successfully.")
                        val returnIntent = Intent()
                        returnIntent.putExtra(AppConstants.ADAPTER_POSITION, adapterPosition)
                        returnIntent.putExtra(AppConstants.TYPE, "soldItem")
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }

                    override fun onFailure(msg: String?) {
                        // pbProgressReport.visibility=View.GONE
                        Utils.showSimpleMessage(this@ProductDetailsActivity, msg!!).show()
                    }
                })

    }

    private fun getItemDetailsService(itemId: Int) {
        pbProgressProduct.visibility = View.VISIBLE
        ServiceHelper().getItemDetailService(itemId,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        pbProgressProduct.visibility = View.GONE
                        val detailsResponse = response.body() as ItemDetailsResponse
                        if (detailsResponse.data != null) {
                            dataIntent = detailsResponse.data
                            commentsList.addAll(detailsResponse.data.comments!!)
                            commentListAdapter.setData(commentsList)
                            commentListAdapter.notifyDataSetChanged()
                            reportList.addAll(detailsResponse.data.reports!!)
                            for (i in detailsResponse.data.items_image.indices) {
                                imageURLLIst.add(detailsResponse.data.items_image[i].url)
                            }
                            setData(imageURLLIst, detailsResponse.data)
                        } else {
                            Utils.showSimpleMessage(this@ProductDetailsActivity, detailsResponse.message).show()
                        }
                    }

                    override fun onFailure(msg: String?) {
                        pbProgressProduct.visibility = View.GONE
                        Utils.showSimpleMessage(this@ProductDetailsActivity, msg!!).show()
                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                dataComment = data.getParcelableExtra(AppConstants.COMMENT_RESULR)
                if (dataComment != null) {
                    val dataComment = CommentModal(dataComment!!.username, dataComment!!.profile_pic, dataComment!!.id, Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), dataComment!!.comment, dataComment!!.created_at)
                    commentsList.add(dataComment)
                    commentListAdapter.setData(commentsList)
                    commentListAdapter.notifyDataSetChanged()
                    Handler().postDelayed({
                        scrollView.fullScroll(View.FOCUS_DOWN)
                    }, 200)
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val type = data.getStringExtra(AppConstants.TYPE)
                if (type.equals("UpdateItem", ignoreCase = true)) {
                    val returnIntent = Intent()
                    returnIntent.putExtra(AppConstants.TYPE, "UpdateItem")
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                    overridePendingTransition(0, 0)
                }
            }

        }
        if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
        }
    }


    override fun onBackPressed() {
        if (powerMenu != null && powerMenu!!.isShowing) {
            powerMenu!!.dismiss()
        } else {
            //  super.onBackPressed()
            val returnIntent = Intent()
            setResult(Activity.RESULT_CANCELED, returnIntent)
            finish()
        }
    }
}