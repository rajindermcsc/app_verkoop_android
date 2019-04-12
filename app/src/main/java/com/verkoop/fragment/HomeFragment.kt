package com.verkoop.fragment

import android.content.Context
import android.content.Intent
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.ksmtrivia.common.BaseFragment
import com.verkoop.LikeDisLikeListener
import com.verkoop.R
import com.verkoop.activity.FullCategoriesActivity
import com.verkoop.activity.GalleryActivity
import com.verkoop.activity.HomeActivity
import com.verkoop.activity.SearchActivity
import com.verkoop.adapter.CategoryListAdapter
import com.verkoop.adapter.ItemHomeAdapter
import com.verkoop.models.*
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.home_fragment.*
import retrofit2.Response

class HomeFragment : BaseFragment(), LikeDisLikeListener {
    val TAG = HomeFragment::class.java.simpleName.toString()
    private lateinit var homeActivity: HomeActivity
    private lateinit var itemAdapter: ItemHomeAdapter
    private lateinit var linearLayoutManager: GridLayoutManager
    private var itemsList = ArrayList<ItemHome>()
    private var categoriesList= ArrayList<Category>()
    private var isClicked: Boolean = false
    private var isLoading = false
    private var totalPageCount: Int? = null
    private var currentPage = 0


    override fun getLikeDisLikeClick(type: Boolean, position: Int, lickedId: Int, itemId: Int) {
        if (Utils.isOnline(homeActivity)) {
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
        } else {
            Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
        }
    }

    override fun getItemDetailsClick(itemId: Int,userId:Int) {
    }


    override fun getTitle(): Int {
        return 0
    }

    override fun getFragmentTag(): String? {
        return TAG
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        homeActivity = activity as HomeActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setItemList()
        if (Utils.isOnline(homeActivity)) {
            itemsList.clear()
            categoriesList.clear()
            currentPage=1
            homeActivity.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            getItemService()
        } else {
            Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
        }
        setData()
    }

    private fun setItemList() {
        linearLayoutManager =  GridLayoutManager(homeActivity,2)
        rvItemList.layoutManager = linearLayoutManager
        rvItemList.setHasFixedSize(false)
        itemAdapter = ItemHomeAdapter(homeActivity, rvItemList, this)
        rvItemList.adapter = itemAdapter
         rvItemList.addOnScrollListener(recyclerViewOnScrollListener)
    }

    private val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount = linearLayoutManager.findLastCompletelyVisibleItemPosition()
            val totalItemCount = linearLayoutManager.itemCount
            val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()

            if (!isLoading && currentPage != totalPageCount) {
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {
                    currentPage += 1
                    getItemService()
                }
            }
        }
    }

    private fun setCategoryAdapter(categoriesList: ArrayList<Category>) {
        val displayMetrics = DisplayMetrics()
        homeActivity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        rvCategoryHome.layoutParams.height = width / 3
        val linearLayoutManager = LinearLayoutManager(homeActivity, LinearLayoutManager.HORIZONTAL, false)
        rvCategoryHome.layoutManager = linearLayoutManager
        val categoryAdapter = CategoryListAdapter(homeActivity, categoriesList, rvCategoryHome)
        rvCategoryHome.adapter = categoryAdapter
    }

    private fun setData() {
        tvViewAll.setOnClickListener {
            val intent = Intent(homeActivity, FullCategoriesActivity::class.java)
            homeActivity.startActivityForResult(intent, 2)
        }
        tvCategoryHome.setOnClickListener {
            val intent = Intent(homeActivity, FullCategoriesActivity::class.java)
            homeActivity.startActivityForResult(intent, 2)
        }
        llSearchHome.setOnClickListener {
            //Utils.showToast(homeActivity, "Work in progress.")
            val intent = Intent(homeActivity, SearchActivity::class.java)
            homeActivity.startActivityForResult(intent, 2)
        }
        tvSell.setOnClickListener {
            val intent = Intent(homeActivity, GalleryActivity::class.java)
            homeActivity.startActivityForResult(intent, 2)
        }
    }

    companion object {
        fun newInstance(): HomeFragment {
            val args = Bundle()
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mDemoSlider != null) {
            mDemoSlider.stopAutoCycle()
        }
    }


    private fun setApiData(data: DataHome?) {
        if (custom_indicator != null) {
            custom_indicator.setDefaultIndicatorColor(ContextCompat.getColor(homeActivity, R.color.white), ContextCompat.getColor(homeActivity, R.color.light_gray))
            mDemoSlider.setCustomIndicator(custom_indicator)
        }
        for (i in 0 until data!!.advertisments.size) {
            val textSliderView = DefaultSliderView(homeActivity)
            textSliderView.image(AppConstants.IMAGE_URL + data.advertisments[i].image)
                    .setOnSliderClickListener({ slider -> }).scaleType = BaseSliderView.ScaleType.Fit
            if (mDemoSlider != null) {
                mDemoSlider.addSlider(textSliderView)
            }
        }
        if (mDemoSlider != null) {
            mDemoSlider.setDuration(3000)
        }
        categoriesList.addAll(data.categories)
        setCategoryAdapter(categoriesList)
        totalPageCount = data.totalPage
        itemsList.addAll(data.items)
        itemAdapter.setData(itemsList)
        itemAdapter.notifyDataSetChanged()
    }


    private fun getItemService() {
        if (pbProgressHome != null) {
            pbProgressHome.visibility = View.VISIBLE
        }
        isLoading = true
        ServiceHelper().getItemsService(currentPage, Utils.getPreferencesString(homeActivity, AppConstants.USER_ID), object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                isLoading = false
                homeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (pbProgressHome != null) {
                    pbProgressHome.visibility = View.GONE
                }
                val homeDataResponse = response.body() as HomeDataResponse?
                if (homeDataResponse!!.data != null) {
                    setApiData(homeDataResponse.data)
                }
            }

            override fun onFailure(msg: String?) {
                if(currentPage>=2){
                    currentPage -= 1
                }
                isLoading = false
                homeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (pbProgressHome != null) {
                    pbProgressHome.visibility = View.GONE
                }
                Utils.showSimpleMessage(homeActivity, msg!!).show()
            }
        })
    }

    private fun lickedApi(itemId: Int, position: Int) {
        val lickedRequest = LickedRequest(Utils.getPreferencesString(homeActivity, AppConstants.USER_ID), itemId)
        ServiceHelper().likeService(lickedRequest,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        isClicked = false
                        val responseLike = response.body() as LikedResponse
                        itemsList[position].is_like=!itemsList[position].is_like
                        itemsList[position].items_like_count= itemsList[position].items_like_count+1
                        itemsList[position].like_id= responseLike.like_id
                        itemAdapter.notifyItemChanged(position)
                    }

                    override fun onFailure(msg: String?) {
                        isClicked = false
                        //      Utils.showSimpleMessage(homeActivity, msg!!).show()
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
                        itemAdapter.notifyItemChanged(position)
                }

                    override fun onFailure(msg: String?) {
                        isClicked = false
                        //  Utils.showSimpleMessage(homeActivity, msg!!).show()
                    }
                })
    }
}