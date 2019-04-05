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
import android.view.View
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.squareup.picasso.Picasso
import com.verkoop.R
import com.verkoop.VerkoopApplication
import com.verkoop.adapter.CommentListAdapter
import com.verkoop.models.*
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.SelectionListener
import com.verkoop.utils.Utils
import com.verkoop.utils.selectOptionDialog
import kotlinx.android.synthetic.main.item_details_activity.*
import kotlinx.android.synthetic.main.toolbar_product_details.*
import okhttp3.internal.Util
import retrofit2.Response
import kotlin.collections.ArrayList

 class ProductDetailsActivity : AppCompatActivity() {
    private var imageURLLIst = ArrayList<String>()
    private val commentsList = ArrayList<CommentModal>()
    private val reportList = ArrayList<ReportResponse>()
    private val  menuList=ArrayList<PowerMenuItem>()
    private var dataComment: CommentModal? = null
    private var powerMenu: PowerMenu? = null
    private var itemId: Int = 0
    private var userId: Int = 0
    private var userName: String = ""
    private lateinit var commentListAdapter: CommentListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_details_activity)
        setCommentAdapter()
        if (intent.getIntExtra(AppConstants.COMING_FROM, 0) == 1) {
            tvSell.visibility = View.GONE
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
        commentListAdapter = CommentListAdapter(this, pbProgress)
        rvPostCommentList.adapter = commentListAdapter
    }

    private fun setData(imageURLLIst: ArrayList<String>, data: DataItems) {
        itemId=data.id
        userId=data.user_id
        userName=data.username
        if(data.meet_up==1){
            tvAddress.text=data.address
        }else{

        }
        tvAddress.setOnClickListener {
            if(!TextUtils.isEmpty(data.latitude)&&!TextUtils.isEmpty(data.longitude)){
                //val geoUri = "http://maps.google.com/maps?q=loc:$data.latitude,$data.longitude"
                val geoUri = "http://maps.google.com/maps?q=loc:" + data.latitude + "," + data.longitude /*+ "(" + classes.getName() + ")"*/
               // val uri = String.format(Locale.ENGLISH, "geo:%f,%f", data.latitude, data.longitude)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
               startActivity(intent)
            }
        }
        if(!TextUtils.isEmpty(data.profile_pic)){
            Picasso.with(this@ProductDetailsActivity).load(AppConstants.IMAGE_URL+data.profile_pic)
                    .resize(720, 720)
                    .centerInside()
                    .error(R.mipmap.pic_placeholder)
                    .placeholder(R.mipmap.pic_placeholder)
                    .into(ivProfileTool)
        }else{
            Picasso.with(this@ProductDetailsActivity).load(R.mipmap.pic_placeholder)
                    .resize(720, 720)
                    .centerInside()
                    .error(R.mipmap.pic_placeholder)
                    .placeholder(R.mipmap.pic_placeholder)
                    .into(ivProfileTool)
        }
        ivRightProduct.setImageResource(R.drawable.menu_icone)

        ivRightProduct.setOnClickListener {
            if(userId!=Utils.getPreferencesString(this,AppConstants.USER_ID).toInt()){
                menuList.clear()
              val powerMenu1=  PowerMenuItem(getString(R.string.report_listing), false)
              val powerMenu2=  PowerMenuItem(getString(R.string.share), false)
                menuList.add(powerMenu1)
                menuList.add(powerMenu2)
                openPowerMenu(menuList)
            }else{
                menuList.clear()
                val powerMenu=  PowerMenuItem(getString(R.string.share), false)
                val powerMenu1=  PowerMenuItem(getString(R.string.edit_item), false)
                val powerMenu2=  PowerMenuItem(getString(R.string.mark_sold), false)
                val powerMenu3=  PowerMenuItem(getString(R.string.delete_listing), false)
                menuList.add(powerMenu)
                menuList.add(powerMenu1)
                menuList.add(powerMenu2)
                menuList.add(powerMenu3)
                openPowerMenu(menuList)
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
            }).scaleType = BaseSliderView.ScaleType.Fit
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
        if (item.title.equals(getString(R.string.report_listing),ignoreCase = true)) {
            val reportIntent = Intent(this, ReportUserActivity::class.java)
            reportIntent.putParcelableArrayListExtra(AppConstants.REPORT_LIST, reportList)
            reportIntent.putExtra(AppConstants.ITEM_ID,itemId)
            startActivity(reportIntent)
        } else if(item.title.equals(getString(R.string.share),ignoreCase = true)) {
            Utils.showToast(this, "share")

        }else if(item.title.equals(getString(R.string.mark_sold),ignoreCase = true)) {
            marksSoldDialogBox()

        }else if(item.title.equals(getString(R.string.delete_listing),ignoreCase = true)) {
            Utils.showToast(this, "delete listing")

        }else if(item.title.equals(getString(R.string.edit_item),ignoreCase = true)) {
            Utils.showToast(this, "Edit listing")
        }
            /*val reportIntent = Intent(this, UserProfileActivity::class.java)
            reportIntent.putExtra(AppConstants.USER_ID,userId)
            reportIntent.putExtra(AppConstants.USER_NAME,userName)
            startActivity(reportIntent)*/

    }

     private fun marksSoldDialogBox() {
         val shareDialog = selectOptionDialog.DeleteCommentDialog(this,getString(R.string.confirm_sold),getString(R.string.sold_des),object : SelectionListener {
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
         ServiceHelper().markAsSoldService(itemId,MarkAsSoldRequest(Utils.getPreferencesString(this,AppConstants.USER_ID)),
                 object : ServiceHelper.OnResponse {
                     override fun onSuccess(response: Response<*>) {
                      //   val blockResponse = response.body() as DisLikeResponse
                         Utils.showSimpleMessage(this@ProductDetailsActivity, "SoldSuccessfully.").show()


                     }

                     override fun onFailure(msg: String?) {
                        // pbProgressReport.visibility=View.GONE
                            Utils.showSimpleMessage(this@ProductDetailsActivity, msg!!).show()
                     }
                 })

     }
     private fun getItemDetailsService(itemId: Int) {
        VerkoopApplication.instance.loader.show(this)
        ServiceHelper().getItemDetailService(itemId,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        VerkoopApplication.instance.loader.hide(this@ProductDetailsActivity)
                        val detailsResponse = response.body() as ItemDetailsResponse
                        if (detailsResponse.data != null) {
                            commentsList.addAll(detailsResponse.data.comments)
                            commentListAdapter.setData(commentsList)
                            commentListAdapter.notifyDataSetChanged()
                            reportList.addAll(detailsResponse.data.reports)
                            for (i in detailsResponse.data.items_image.indices) {
                                imageURLLIst.add(detailsResponse.data.items_image[i].url)
                            }
                            setData(imageURLLIst, detailsResponse.data)
                        } else {
                            Utils.showSimpleMessage(this@ProductDetailsActivity, detailsResponse.message).show()
                        }
                    }

                    override fun onFailure(msg: String?) {
                        VerkoopApplication.instance.loader.hide(this@ProductDetailsActivity)
                        Utils.showSimpleMessage(this@ProductDetailsActivity, msg!!).show()
                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                dataComment = data.getParcelableExtra(AppConstants.COMMENT_RESULR)
                if (dataComment != null) {
                    val dataComment = CommentModal(dataComment!!.username, dataComment!!.profile_pic, dataComment!!.id, dataComment!!.comment, dataComment!!.created_at)
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
    }

    override fun onBackPressed() {
        if (powerMenu!=null&&powerMenu!!.isShowing) {
            powerMenu!!.dismiss()
        } else {
            super.onBackPressed()
        }
    }
}