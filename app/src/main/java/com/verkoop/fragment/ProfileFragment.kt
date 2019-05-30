package com.verkoop.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.ksmtrivia.common.BaseFragment
import com.squareup.picasso.Picasso
import com.verkoop.R
import com.verkoop.activity.*
import com.verkoop.adapter.MyProfileItemAdapter
import com.verkoop.models.*
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.profile_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Response


class ProfileFragment : BaseFragment(), MyProfileItemAdapter.LikeDisLikeListener {
    private lateinit var myProfileItemAdapter: MyProfileItemAdapter
    private var itemsList = ArrayList<Item>()
    private var isClicked: Boolean = false

    override fun getItemDetailsClick(itemId: Int, position: Int) {
        val intent = Intent(context, ProductDetailsActivity::class.java)
        intent.putExtra(AppConstants.ITEM_ID, itemId)
        intent.putExtra(AppConstants.COMING_FROM, 1)
        intent.putExtra(AppConstants.COMING_TYPE, 1)
        intent.putExtra(AppConstants.ADAPTER_POSITION, position)
        this.startActivityForResult(intent, 3)
    }

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


    private val TAG = ProfileFragment::class.java.simpleName.toString()
    private lateinit var homeActivity: HomeActivity

    override fun getTitle(): Int {
        return 0
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        homeActivity = context as HomeActivity

    }

    override fun getFragmentTag(): String? {
        return TAG
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        setAdapter()
        if (Utils.isOnline(homeActivity)) {
            myProfileInfoApi()
        } else {
            Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
        }
    }

    private fun setAdapter() {
        val linearLayoutManager = GridLayoutManager(context, 2)
        rvPostsList.layoutManager = linearLayoutManager
        myProfileItemAdapter = MyProfileItemAdapter(homeActivity, llProfileParent, this)
        rvPostsList.isNestedScrollingEnabled = false
        rvPostsList.isFocusable = false
        rvPostsList.adapter = myProfileItemAdapter
    }

    private fun setData() {
        llFollowers.setOnClickListener {
            val intent=Intent(homeActivity,FollowFollowingActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM,0)
            intent.putExtra(AppConstants.USER_ID,Utils.getPreferencesString(homeActivity,AppConstants.USER_ID).toInt())
            startActivity(intent)
        }
        llFollowing.setOnClickListener {
            val intent=Intent(homeActivity,FollowFollowingActivity::class.java)
            intent.putExtra(AppConstants.COMING_FROM,1)
            intent.putExtra(AppConstants.USER_ID,Utils.getPreferencesString(homeActivity,AppConstants.USER_ID).toInt())
            startActivity(intent)
        }
        llFavourite.setOnClickListener {
            val intent = Intent(homeActivity, FavouritesActivity::class.java)
            startActivity(intent)
        }
        tvCategoryProfile.setOnClickListener {
            val intent = Intent(homeActivity, FullCategoriesActivity::class.java)
            startActivity(intent)
        }
        tvSellProfile.setOnClickListener {
            val intent = Intent(homeActivity, GalleryActivity::class.java)
            homeActivity.startActivityForResult(intent, 2)
        }
        ivSetting.setOnClickListener {
            val intent = Intent(homeActivity, SettingActivity::class.java)
            homeActivity.startActivity(intent)
        }
        llCoins.setOnClickListener {
            val intent = Intent(homeActivity, CoinsActivity::class.java)
            homeActivity.startActivity(intent)
        }
        llSearchProfile.setOnClickListener {
            val intent = Intent(homeActivity, SearchActivity::class.java)
            homeActivity.startActivityForResult(intent, 2)
        }
        ivScanner.setOnClickListener {
            //  val intent = Intent(homeActivity, QRScannerActivity::class.java)
            //    homeActivity.startActivity(intent)
        }
        llWallet.setOnClickListener {
              val intent = Intent(homeActivity, MyWalletActivity::class.java)
               startActivity(intent)
        }
    }

    companion object {
        fun newInstance(): ProfileFragment {
            val arg = Bundle()
            val fragment = ProfileFragment()
            fragment.arguments = arg
            return fragment
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                val adapterPosition = data!!.getIntExtra(AppConstants.ADAPTER_POSITION, 0)
                if (data.getStringExtra(AppConstants.TYPE).equals("soldItem", ignoreCase = true)) {
                    itemsList[adapterPosition].is_sold = 1
                    myProfileItemAdapter.notifyDataSetChanged()
                } else if (data.getStringExtra(AppConstants.TYPE).equals("deleteItem", ignoreCase = true)) {
                    itemsList.removeAt(adapterPosition)
                    myProfileItemAdapter.notifyDataSetChanged()
                } else if (data.getStringExtra(AppConstants.TYPE).equals("UpdateItem", ignoreCase = true)) {
                    if (Utils.isOnline(homeActivity)) {
                        myProfileInfoApi()
                    } else {
                        Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
                    }
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    private fun myProfileInfoApi() {
        homeActivity.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if (pbProgressProfile != null) {
            pbProgressProfile.visibility = View.VISIBLE
        }
        ServiceHelper().myProfileService(Utils.getPreferencesString(homeActivity, AppConstants.USER_ID),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        pbProgressProfile.visibility = View.GONE
                        homeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        val myProfileResponse = response.body() as MyProfileResponse
                        if (myProfileResponse.data != null) {
                            setApiData(myProfileResponse.data)
                        } else {
//                            Utils.showSimpleMessage(homeActivity, myProfileResponse.message).show()
                        }
                    }

                    override fun onFailure(msg: String?) {
                        homeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        if (pbProgressProfile != null) {
                            pbProgressProfile.visibility = View.GONE
                        }
                        Utils.showSimpleMessage(homeActivity, msg!!).show()
                    }
                })
    }

    private fun setApiData(data: DataProfile) {
        itemsList.clear()
        itemsList = data.items
        myProfileItemAdapter.setData(itemsList)
        myProfileItemAdapter.notifyDataSetChanged()
        tvName.text = data.username
        tvFollowers.text = data.follower_count.toString()
        tvFollowing.text = data.follow_count.toString()
        tvJoiningDate.text = StringBuffer().append(": ").append(Utils.convertDate("yyyy-MM-dd hh:mm:ss", data.created_at, "dd MMMM yyyy"))
        if (!TextUtils.isEmpty(data.profile_pic)) {
            Picasso.with(homeActivity).load(AppConstants.IMAGE_URL + data.profile_pic)
                    .resize(720, 720)
                    .centerInside()
                    .error(R.mipmap.pic_placeholder)
                    .placeholder(R.mipmap.pic_placeholder)
                    .into(ivProfilePic)
        }
        if (!TextUtils.isEmpty(data.city) && !TextUtils.isEmpty(data.state)) {
            tvAddress.text = StringBuilder().append(data.state).append(", ").append(data.city)
            tvAddress.visibility = View.VISIBLE
        } else {
            tvAddress.visibility = View.GONE
        }
        tvCountry.text = data.country
    }


    private fun lickedApi(itemId: Int, position: Int) {
        val lickedRequest = LickedRequest(Utils.getPreferencesString(homeActivity, AppConstants.USER_ID), itemId)
        ServiceHelper().likeService(lickedRequest,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        isClicked = false
                        val responseLike = response.body() as LikedResponse
                        itemsList[position].is_like = !itemsList[position].is_like
                        itemsList[position].likes_count = itemsList[position].likes_count + 1
                        itemsList[position].like_id = responseLike.like_id
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
                        itemsList[position].is_like = !itemsList[position].is_like
                        itemsList[position].likes_count = itemsList[position].likes_count - 1
                        itemsList[position].like_id = 0
                        myProfileItemAdapter.notifyItemChanged(position)
                    }

                    override fun onFailure(msg: String?) {
                        isClicked = false
                        //     Utils.showSimpleMessage(homeActivity, msg!!).show()
                    }
                })
    }

    fun refreshUI(from: Int) {
        if (from == 0) {
            //   rvPostsList.isFocusable=true
            //   rvPostsList.scrollToPosition(0)
        } else if (from == 1) {
            if (Utils.isOnline(homeActivity)) {
                myProfileInfoApi()
            } else {
                Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        if (Utils.isOnline(homeActivity)) {
            myProfileInfoApi()
        } else {
            Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }
}