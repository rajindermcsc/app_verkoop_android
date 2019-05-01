package com.verkoop.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.squareup.picasso.Picasso
import com.verkoop.LikeDisLikeListener
import com.verkoop.R
import com.verkoop.adapter.UserProfileItemAdapter
import com.verkoop.models.*
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.SelectionListener
import com.verkoop.utils.Utils
import com.verkoop.utils.selectOptionDialog
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.toolbar_location.*
import kotlinx.android.synthetic.main.user_profile_activity.*
import retrofit2.Response

class UserProfileActivity:AppCompatActivity(), LikeDisLikeListener {
    private var powerMenu: PowerMenu? = null
    private lateinit var myProfileItemAdapter: UserProfileItemAdapter
    private var itemsList = ArrayList<ItemUserProfile>()
    private var reportsList=ArrayList<ReportResponse>()
    private var isClicked: Boolean = false
    private var isFollowClick: Boolean = false
    private var isBlockClick: Boolean = false
    private var userId:Int=0
    private var followId:Int=0
    private var blockedId:Int=0
    private var typeMenu:Int=0
    private var followers:Int=0
    private var userName:String=""


    override fun getLikeDisLikeClick(type: Boolean, position: Int, lickedId: Int, itemId: Int) {
        if (type) {
            if (!isClicked) {
                isClicked = true
                deleteLikeApi(position, lickedId)
            }
        } else {
            if (!isClicked) {
                isClicked = true
                lickedApi(itemId, position)
            }
        }
    }

    override fun getItemDetailsClick(itemId: Int,userId:Int) {
        val intent = Intent(this, ProductDetailsActivity::class.java)
        intent.putExtra(AppConstants.ITEM_ID, itemId)
        intent.putExtra(AppConstants.USER_ID,userId)
        intent.putExtra(AppConstants.COMING_FROM, 1)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile_activity)
        userId=intent.getIntExtra(AppConstants.USER_ID,0)
        userName=intent.getStringExtra(AppConstants.USER_NAME)
        setData()
        setAdapter()
        if (Utils.isOnline(this)) {
            myProfileInfoApi()
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }
    private fun setAdapter() {
        val linearLayoutManager = GridLayoutManager(this, 2)
        rvUserPostsList.layoutManager = linearLayoutManager
        myProfileItemAdapter = UserProfileItemAdapter(this, llUserParent)
        rvUserPostsList.isNestedScrollingEnabled = false
        rvUserPostsList.isFocusable = false
        rvUserPostsList.adapter = myProfileItemAdapter
    }
    private fun setData() {
        llFollowersUser.setOnClickListener {
            val intent=Intent(this,FollowFollowingActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM,0)
            intent.putExtra(AppConstants.USER_ID,userId)
            startActivity(intent)
        }
        llFollowingUser.setOnClickListener {
            val intent=Intent(this,FollowFollowingActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM,1)
            intent.putExtra(AppConstants.USER_ID,userId)
            startActivity(intent)
        }
        ivRight.setImageResource(R.drawable.menu_icone)
        ivRight.visibility= View.VISIBLE
        tvHeaderLoc.text=userName
        ivLeftLocation.setOnClickListener { onBackPressed() }
        ivRight.setOnClickListener {
            if(blockedId>0){
                openPowerMenu(1)
            }else{
                openPowerMenu(0)
            }

        }
        tvUserFollow.setOnClickListener {
            if(!isFollowClick) {
                isFollowClick=true
                if (tvUserFollow.text.toString().equals("Follow", ignoreCase = true)) {
                    followService()
                } else {
                    unFollowService()
                }
            }

        }

    }



    private fun openPowerMenu(type:Int) {
        var option="Block user"
        if(type==1){
            option="Unblock user"
        }
        powerMenu = PowerMenu.Builder(this)

                .addItem(PowerMenuItem(option, false)) // add an item.
                .addItem(PowerMenuItem("Report user", false)) // aad an item list.
                .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT) // Animation start point (TOP | LEFT).
                .setMenuRadius(1f) // sets the corner radius.
                .setMenuShadow(10f) // sets the shadow.
                .setTextColor(ContextCompat.getColor(this, R.color.black_dark))
                .setMenuColor(Color.WHITE)
                .setDivider(ContextCompat.getDrawable(this, R.drawable.horizontal_line))
                .setDividerHeight(1)
                .setSelectedMenuColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setOnMenuItemClickListener(onMenuItemClickListener)
                .build()
        powerMenu!!.showAsAnchorRightTop(ivRight)
    }
    private val onMenuItemClickListener = OnMenuItemClickListener<PowerMenuItem> { position, item ->
        powerMenu!!.selectedPosition = position
        powerMenu!!.dismiss()

        if (position == 1) {
            val reportIntent = Intent(this, ReportUserActivity::class.java)
            reportIntent.putParcelableArrayListExtra(AppConstants.REPORT_LIST, reportsList)
            reportIntent.putExtra(AppConstants.COMING_FROM,1)
            reportIntent.putExtra(AppConstants.ITEM_ID,userId)
            startActivity(reportIntent)
        } else {
            if(!isBlockClick) {
                if (item.getTitle().equals("Block user", ignoreCase = true)) {
                    blockUserDialog()
                } else if (item.getTitle().equals("Unblock user", ignoreCase = true)) {
                    unBlockUserDialog()
                }
            }
        }
    }

    private fun unBlockUserDialog() {
        val shareDialog = selectOptionDialog.DeleteCommentDialog(this,getString(R.string.unblock_user),getString(R.string.unblock_sure),object : SelectionListener {
            override fun leaveClick() {
                isBlockClick=true
                unBockUserApi()
            }
        })
        shareDialog.show()
    }



    private fun blockUserDialog() {
        val shareDialog = selectOptionDialog.DeleteCommentDialog(this,getString(R.string.block_user),getString(R.string.block_sure),object : SelectionListener {
            override fun leaveClick() {
                isBlockClick=true
               bockUserApi()
            }
        })
        shareDialog.show()
    }



    private fun myProfileInfoApi() {
       window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbProgressUser.visibility = View.VISIBLE
        val userRequest=FollowRequest(Utils.getPreferencesString(this,AppConstants.USER_ID),userId)
        ServiceHelper().userProfileService(userRequest,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        pbProgressUser.visibility = View.GONE
                     window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        val myProfileResponse = response.body() as UserProfileResponse
                        if (myProfileResponse.data != null) {
                            setApiData(myProfileResponse.data)
                        } else {
//                            Utils.showSimpleMessage(homeActivity, myProfileResponse.message).show()
                        }
                    }

                    override fun onFailure(msg: String?) {
                       window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgressUser.visibility = View.GONE

                        Utils.showSimpleMessage(this@UserProfileActivity, msg!!).show()
                    }
                })
    }

    private fun setApiData(data: DataUserProfile) {
        itemsList.clear()
        itemsList = data.items
        followId = data.follower_id
        followers=data.follower_count
        tvUserFollowers.text=followers.toString()
        tvUserFollowing.text=data.follow_count.toString()
        myProfileItemAdapter.setData(itemsList)
        myProfileItemAdapter.notifyDataSetChanged()
        tvNameUser.text = data.username
        blockedId=data.block_id
        reportsList=data.reports
        if(data.follower_id>0){
            tvUserFollow.background=ContextCompat.getDrawable(this@UserProfileActivity,R.drawable.brown_rectangular_shape)
            tvUserFollow.text = getString(R.string.following)
            tvUserFollow.setPaddingRelative(Utils.dpToPx(this@UserProfileActivity,48.0f).toInt(),Utils.dpToPx(this@UserProfileActivity,10.0f).toInt(),Utils.dpToPx(this@UserProfileActivity,48.0f).toInt(),Utils.dpToPx(this@UserProfileActivity,10.0f).toInt())
        }else{
            tvUserFollow.background=ContextCompat.getDrawable(this@UserProfileActivity,R.drawable.red_rectangular_shape)
            tvUserFollow.text = getString(R.string.follow)
            tvUserFollow.setPaddingRelative(Utils.dpToPx(this@UserProfileActivity,60.0f).toInt(),Utils.dpToPx(this@UserProfileActivity,10.0f).toInt(),Utils.dpToPx(this@UserProfileActivity,60.0f).toInt(),Utils.dpToPx(this@UserProfileActivity,10.0f).toInt())
        }
        tvUserJoiningDate.text = StringBuffer().append(": ").append(Utils.convertDate("yyyy-MM-dd hh:mm:ss", data.created_at, "dd MMMM yyyy"))
        if (!TextUtils.isEmpty(data.profile_pic)) {
            Picasso.with(this).load(AppConstants.IMAGE_URL + data.profile_pic)
                    .resize(720, 720)
                    .centerInside()
                    .error(R.mipmap.pic_placeholder)
                    .placeholder(R.mipmap.pic_placeholder)
                    .into(ivUserPic)
        }
        if (!TextUtils.isEmpty(data.city) && !TextUtils.isEmpty(data.state)) {
            tvAddressUser.text = StringBuilder().append(data.state).append(", ").append(data.city)

            tvAddressUser.visibility = View.VISIBLE
        } else {
            tvAddressUser.visibility = View.GONE
        }
        tvCountryUser.text = data.country
    }

    override fun onBackPressed() {
        if (powerMenu!=null&&powerMenu!!.isShowing) {
            powerMenu!!.dismiss()
        } else {
            super.onBackPressed()
        }
    }

    private fun unBockUserApi() {
        ServiceHelper().unBlockUserService(blockedId,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        isBlockClick=false
                        val likeResponse = response.body() as DisLikeResponse
                        Utils.showSimpleMessage(this@UserProfileActivity, "This user has been unblocked.").show()
                        blockedId=0
                    }
                    override fun onFailure(msg: String?) {
                        isBlockClick=false
                        Utils.showSimpleMessage(this@UserProfileActivity, msg!!).show()
                    }
                })
    }

    private fun bockUserApi() {
        val blockUserRequest= BlockUserRequest(Utils.getPreferencesString(this,AppConstants.USER_ID), userId)
        ServiceHelper().blockUserService(blockUserRequest,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        isBlockClick=false
                        val blockResponse = response.body() as BlockUserResponse
                        if(blockResponse.data!=null) {
                            Utils.showSimpleMessage(this@UserProfileActivity, "This user has been blocked.").show()
                            blockedId = blockResponse.data.id
                            typeMenu = 1
                        }
                    }

                    override fun onFailure(msg: String?) {
                        isBlockClick=false
                        //    pbProgressReport.visibility=View.GONE
                        //    Utils.showSimpleMessage(this@UserProfileActivity, msg!!).show()
                    }
                })

    }

    private fun unFollowService() {
        ServiceHelper().unFollowService(followId,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        isFollowClick=false
                        val likeResponse = response.body() as DisLikeResponse
                        tvUserFollow.background=ContextCompat.getDrawable(this@UserProfileActivity,R.drawable.red_rectangular_shape)
                        tvUserFollow.text = getString(R.string.follow)
                        tvUserFollow.setPaddingRelative(Utils.dpToPx(this@UserProfileActivity,60.0f).toInt(),Utils.dpToPx(this@UserProfileActivity,10.0f).toInt(),Utils.dpToPx(this@UserProfileActivity,60.0f).toInt(),Utils.dpToPx(this@UserProfileActivity,10.0f).toInt())
                        followers -= 1
                        tvUserFollowers.text=followers.toString()
                        followId=0
                    }
                    override fun onFailure(msg: String?) {
                        isFollowClick=false
                        Utils.showSimpleMessage(this@UserProfileActivity, msg!!).show()
                    }
                })
    }

    private fun followService() {
        //  pbProgressUser.visibility=View.VISIBLE
        val reportUserResponse= FollowRequest(Utils.getPreferencesString(this,AppConstants.USER_ID),userId)
        ServiceHelper().followService(reportUserResponse,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        isFollowClick=false
                        //     pbProgressReport.visibility=View.GONE
                        val followResponse = response.body() as FollowResponse
                        tvUserFollow.background=ContextCompat.getDrawable(this@UserProfileActivity,R.drawable.brown_rectangular_shape)
                        tvUserFollow.text = getString(R.string.following)
                        followId=followResponse.data.id
                        followers += 1
                        tvUserFollowers.text=followers.toString()
                        tvUserFollow.setPaddingRelative(Utils.dpToPx(this@UserProfileActivity,48.0f).toInt(),Utils.dpToPx(this@UserProfileActivity,10.0f).toInt(),Utils.dpToPx(this@UserProfileActivity,48.0f).toInt(),Utils.dpToPx(this@UserProfileActivity,10.0f).toInt())
                    }

                    override fun onFailure(msg: String?) {
                        isFollowClick=false
                        //    pbProgressReport.visibility=View.GONE
                        //    Utils.showSimpleMessage(this@UserProfileActivity, msg!!).show()
                    }
                })

    }

    private fun lickedApi(itemId: Int, position: Int) {
        val lickedRequest = LickedRequest(Utils.getPreferencesString(this, AppConstants.USER_ID), itemId)
        ServiceHelper().likeService(lickedRequest,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        isClicked = false
                        val responseLike = response.body() as LikedResponse
                        itemsList[position].is_like=!itemsList[position].is_like
                        itemsList[position].items_like_count= itemsList[position].items_like_count+1
                        itemsList[position].like_id= responseLike.like_id
                        myProfileItemAdapter.notifyItemChanged(position)
                    }

                    override fun onFailure(msg: String?) {
                        isClicked = false
                        //Utils.showSimpleMessage(homeActivity, msg!!).show()
                    }
                })
    }

    private fun deleteLikeApi(position: Int, lickedId: Int) {
        ServiceHelper().disLikeService(lickedId,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        isClicked = false
                        val likeResponse = response.body() as DisLikeResponse
                        itemsList[position].is_like=!itemsList[position].is_like
                        itemsList[position].items_like_count= itemsList[position].items_like_count-1
                        itemsList[position].like_id= 0
                        myProfileItemAdapter.notifyItemChanged(position)
                    }

                    override fun onFailure(msg: String?) {
                        isClicked = false
                        //     Utils.showSimpleMessage(homeActivity, msg!!).show()
                    }
                })
    }
}