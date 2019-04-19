package com.verkoop.fragment

import android.content.Context
import android.content.Intent
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.ksmtrivia.common.BaseFragment
import com.verkoop.LikeDisLikeListener
import com.verkoop.R
import com.verkoop.activity.FullCategoriesActivity
import com.verkoop.activity.GalleryActivity
import com.verkoop.activity.HomeActivity
import com.verkoop.activity.SearchActivity
import com.verkoop.adapter.HomeAdapter
import com.verkoop.models.*
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.home_fragment.*
import retrofit2.Response

class HomeFragment : BaseFragment(), LikeDisLikeListener {
    val TAG = HomeFragment::class.java.simpleName.toString()
    private lateinit var homeActivity: HomeActivity
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var linearLayoutManager: GridLayoutManager
    private var itemsList = ArrayList<ItemHome>()
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
        linearLayoutManager =  GridLayoutManager(homeActivity,2 )
        linearLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (homeAdapter.getItemViewType(position)) {
                    homeAdapter.CATEGORY_LIST_ROW -> 2
                    homeAdapter.PROPERTIES_ROW -> 2
                    else -> 1
                }
            }
        }
        rvHomeList.layoutManager = linearLayoutManager
        rvHomeList.setHasFixedSize(false)
        homeAdapter = HomeAdapter(homeActivity, rvHomeList,this)
        rvHomeList.adapter = homeAdapter
        rvHomeList.addOnScrollListener(recyclerViewOnScrollListener)
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

    private fun setData() {

        tvCategoryHome.setOnClickListener {
            val intent = Intent(homeActivity, FullCategoriesActivity::class.java)
            homeActivity.startActivityForResult(intent, 2)
        }
        llSearchHome.setOnClickListener {
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




    private fun setApiData(data: DataHome?) {
        if(data!!.categories.size>0&&data.advertisments.size>0) {
            homeAdapter.setCategoryAndAddsData(data.advertisments, data.categories)
        }
        totalPageCount = data.totalPage
        itemsList.addAll(data.items)
        homeAdapter.setData(itemsList)
        homeAdapter.notifyDataSetChanged()
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
                        homeAdapter.notifyItemChanged(position+2)
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
                        homeAdapter.notifyItemChanged(position+2)
                }

                    override fun onFailure(msg: String?) {
                        isClicked = false
                        //  Utils.showSimpleMessage(homeActivity, msg!!).show()
                    }
                })
    }
}