package com.verkoopapp.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.ksmtrivia.common.BaseFragment
import com.verkoopapp.R
import com.verkoopapp.activity.FullCategoriesActivity
import com.verkoopapp.activity.GalleryActivity
import com.verkoopapp.activity.HomeActivity
import com.verkoopapp.activity.SearchActivity
import com.verkoopapp.adapter.HomeAdapter
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.home_fragment.*
import retrofit2.Response
import android.util.Log


class HomeFragment : BaseFragment() {
    val TAG = HomeFragment::class.java.simpleName.toString()
    private lateinit var homeActivity: HomeActivity
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var linearLayoutManager: GridLayoutManager
    private var itemsList = ArrayList<ItemHome>()
    private var isLoading = false
    private var totalPageCount: Int? = null
    private var currentPage = 0


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
        val display = homeActivity.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        setItemList(width)
        if (Utils.isOnline(homeActivity)) {
            itemsList.clear()
            currentPage = 1
            homeActivity.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            if (pbProgressHome != null) {
                pbProgressHome.visibility = View.VISIBLE
            }
            getItemService(0)
        } else {
            Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
        }
        setData()
    }

    private fun setItemList(width: Int) {
        linearLayoutManager = GridLayoutManager(homeActivity, 2)
        linearLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (homeAdapter.getItemViewType(position)) {
                    homeAdapter.CATEGORY_LIST_ROW -> 2
                    homeAdapter.YOUR_DAILY_PICKS -> 2
                    homeAdapter.PROPERTIES_ROW -> 2
                    homeAdapter.RECOMMENDED_YOU -> 2
                    homeAdapter.SHOW_LOADER -> 2
                    else ->
                        1
                }
            }
        }
        rvHomeList.layoutManager = linearLayoutManager
        rvHomeList.setHasFixedSize(false)
        homeAdapter = HomeAdapter(homeActivity, width, this)
        rvHomeList.adapter = homeAdapter
        //  rvHomeList.setItemViewCacheSize(5);
        rvHomeList.addOnScrollListener(recyclerViewOnScrollListener)
    }

    private val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount = linearLayoutManager.findLastCompletelyVisibleItemPosition()
            val totalItemCount = linearLayoutManager.itemCount
            val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()

            if (!isLoading && currentPage != totalPageCount) {
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE) {
                    if (Utils.isOnline(homeActivity)) {
                        itemsList.add(ItemHome(isLoading=true))
                        Log.e("AddItemPosition",(itemsList.size-1).toString())
                        homeAdapter.notifyItemInserted((itemsList.size-1)+4)
                        currentPage += 1
                        getItemService(0)
                    } else {
                     //   Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
                    }
                }
            }
        }
    }

    private fun setData() {
        swipeContainer.setOnRefreshListener {
            if (Utils.isOnline(homeActivity)) {
                currentPage = 1
                getItemService(1)
            } else {
                swipeContainer.isRefreshing = false
                Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
            }
        }
        swipeContainer.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary)

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


    private fun setApiData(data: DataHome?,loadMore:Int) {
        if (data!!.categories.size > 0 && data.advertisments!!.size > 0) {
            homeAdapter.setCategoryAndAddsData(data.advertisments, data.categories)
        }
        if (data.daily_pic.size > 0) {
            homeAdapter.updateDailyPicksData(data.daily_pic)
        }

        totalPageCount = data.totalPage
        if(loadMore==1) {
            itemsList.clear()
            itemsList.addAll(data.items)
        }else{
            itemsList.addAll(data.items)
        }
        homeAdapter.setData(itemsList)
        homeAdapter.notifyDataSetChanged()
    }


    private fun getItemService(loadMore:Int) {
        isLoading = true
        ServiceHelper().getItemsService(HomeRequest(0), currentPage, Utils.getPreferencesString(homeActivity, AppConstants.USER_ID), object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                homeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if(swipeContainer!=null) {
                    swipeContainer.isRefreshing = false
                }
                isLoading = false
                if (pbProgressHome != null) {
                    pbProgressHome.visibility = View.GONE
                    rvHomeList.visibility = View.VISIBLE
                }
                if(currentPage>1&&itemsList.size>0) {
                    itemsList.removeAt(itemsList.size - 1)
                    val scrollPosition = itemsList.size
                    homeAdapter.notifyItemRemoved(scrollPosition+4)
                }
                val homeDataResponse = response.body() as HomeDataResponse?
                if (homeDataResponse!!.data != null) {
                    setApiData(homeDataResponse.data,loadMore)
                }
            }

            override fun onFailure(msg: String?) {
                homeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if(swipeContainer!=null) {
                    swipeContainer.isRefreshing = false
                }
                isLoading = false
                if (pbProgressHome != null) {
                    pbProgressHome.visibility = View.GONE
                }
                Utils.showSimpleMessage(homeActivity, msg!!).show()
                if (currentPage >= 2) {
                    currentPage -= 1
                }

                if(currentPage>1&&itemsList.size>0) {
                    itemsList.removeAt(itemsList.size - 1)
                    val scrollPosition = itemsList.size
                    homeAdapter.notifyItemRemoved(scrollPosition)
                }
            }
        })
    }

}