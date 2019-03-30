package com.verkoop.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.ksmtrivia.common.BaseFragment
import com.verkoop.R
import com.verkoop.activity.*
import com.verkoop.adapter.MyProfileItemAdapter
import com.verkoop.models.*
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.profile_fragment.*
import retrofit2.Response

class ProfileFragment : BaseFragment(), MyProfileItemAdapter.LikeDisLikeListener {
    private lateinit var myProfileItemAdapter: MyProfileItemAdapter
    private var  itemsList=ArrayList<Item>()
    private var isClicked:Boolean = false

    override fun getItemDetailsClick(itemId: Int) {
        val intent = Intent(context, ProductDetailsActivity::class.java)
        intent.putExtra(AppConstants.ITEM_ID, itemId)
        intent.putExtra(AppConstants.COMING_FROM, 1)
        homeActivity.startActivity(intent)
    }

    override fun getLikeDisLikeClick(type: Boolean, position: Int,lickedId:Int,itemId:Int) {
        if(type){
            if(!isClicked) {
                isClicked=true
                deleteLikeApi(position, lickedId)
            }
        }else{
            if(!isClicked) {
                isClicked=true
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
        homeActivity = activity as HomeActivity

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
        llFavourite.setOnClickListener {
            val intent=Intent(homeActivity, FavouritesActivity::class.java)
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
        ivScanner.setOnClickListener {
          //  val intent = Intent(homeActivity, QRScannerActivity::class.java)
        //    homeActivity.startActivity(intent)
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

    private fun myProfileInfoApi() {
        homeActivity.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if(pbProgressProfile!=null) {
            pbProgressProfile.visibility = View.VISIBLE
        }
        ServiceHelper().myProfileService(Utils.getPreferencesString(homeActivity, AppConstants.USER_ID),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                            pbProgressProfile.visibility = View.GONE
                        homeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        val myProfileResponse = response.body() as MyProfileResponse
                        itemsList.clear()
                        itemsList=myProfileResponse.data.items
                        myProfileItemAdapter.setData(itemsList)
                        myProfileItemAdapter.notifyDataSetChanged()

                        tvName.text = myProfileResponse.data.username
                        tvJoiningDate.text= StringBuffer().append(": ").append(Utils.convertDate("yyyy-MM-dd hh:mm:ss",myProfileResponse.data.created_at,"dd MMMM yyyy"))
                    }

                    override fun onFailure(msg: String?) {
                        homeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        if(pbProgressProfile!=null) {
                            pbProgressProfile.visibility = View.GONE
                        }
                        Utils.showSimpleMessage(homeActivity, msg!!).show()
                    }
                })
    }


    private fun lickedApi(itemId: Int, position: Int) {
        val lickedRequest=LickedRequest(Utils.getPreferencesString(homeActivity,AppConstants.USER_ID),itemId)
        ServiceHelper().likeService(lickedRequest,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        isClicked=false
                        val responseLike = response.body() as LikedResponse
                        val items= Item(itemsList[position].id,
                                itemsList[position].user_id,
                                itemsList[position].category_id,
                                itemsList[position].name,
                                itemsList[position].price,
                                itemsList[position].item_type,
                                itemsList[position].created_at,
                                itemsList[position].likes_count+1,
                                responseLike.like_id,
                                !itemsList[position].is_like,
                                itemsList[position].image_url)
                        itemsList[position] = items
                        myProfileItemAdapter.notifyItemChanged(position)
                    }

                    override fun onFailure(msg: String?) {
                        isClicked=false
                     //Utils.showSimpleMessage(homeActivity, msg!!).show()
                    }
                })
    }

    private fun deleteLikeApi(position: Int, lickedId: Int) {
        ServiceHelper().disLikeService(lickedId,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        isClicked=false
                        val likeResponse = response.body() as DisLikeResponse
                        val items= Item(itemsList[position].id,
                                itemsList[position].user_id,
                                itemsList[position].category_id,
                                itemsList[position].name,
                                itemsList[position].price,
                                itemsList[position].item_type,
                                itemsList[position].created_at,
                                itemsList[position].likes_count-1,
                               0,
                                !itemsList[position].is_like,
                                itemsList[position].image_url)
                        itemsList[position] = items
                        myProfileItemAdapter.notifyItemChanged(position)
                    }

                    override fun onFailure(msg: String?) {
                        isClicked=false
                   //     Utils.showSimpleMessage(homeActivity, msg!!).show()
                    }
                })
    }

    fun refreshUI(from: Int) {
        if(from==0){
         //   rvPostsList.isFocusable=true
         //   rvPostsList.scrollToPosition(0)
        }else if(from==1){
            if (Utils.isOnline(homeActivity)) {
                myProfileInfoApi()
            } else {
                Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
            }
        }

    }
}