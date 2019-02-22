package com.verkoop.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.ksmtrivia.common.BaseFragment
import com.verkoop.R
import com.verkoop.activity.FullCategoriesActivity
import com.verkoop.activity.GalleryActivity
import com.verkoop.activity.HomeActivity
import com.verkoop.adapter.CategoryListAdapter
import com.verkoop.adapter.ItemAdapter
import com.verkoop.models.*
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.add_details_activity.*
import kotlinx.android.synthetic.main.home_fragment.*
import retrofit2.Response


class HomeFragment : BaseFragment() {
    val TAG = HomeFragment::class.java.simpleName.toString()
    private val categoryList = ArrayList<CategoryModal>()
    private lateinit var homeActivity: HomeActivity
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
        if (Utils.isOnline(homeActivity)) {
            getHomeDataService()
        } else {
            Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
        }
        setData()
     //   setItemList()

    }

    private fun setItemList() {
        val linearLayoutManager = GridLayoutManager(context, 2)
        rvItemList.layoutManager = linearLayoutManager
        val itemAdapter = ItemAdapter(homeActivity, categoryList)
        rvItemList.isNestedScrollingEnabled = false
        rvItemList.adapter = itemAdapter
    }

    private fun setCategoryAdapter(categoriesList: ArrayList<Category>) {
        val displayMetrics = DisplayMetrics()
        homeActivity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        rvCategoryHome.layoutParams.height = width / 3
        val linearLayoutManager = LinearLayoutManager(homeActivity, LinearLayoutManager.HORIZONTAL, false)
        rvCategoryHome.layoutManager = linearLayoutManager
        val categoryAdapter = CategoryListAdapter(homeActivity, categoriesList,rvCategoryHome)
        rvCategoryHome.adapter = categoryAdapter
    }

    private fun setData() {
        tvViewAll.setOnClickListener { Utils.showToast(homeActivity, "Work in progress.") }
        tvCategoryHome.setOnClickListener {
            val intent = Intent(homeActivity, FullCategoriesActivity::class.java)
            startActivity(intent)
        }
        llSearchHome.setOnClickListener { Utils.showToast(homeActivity, "Work in progress.") }
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
        mDemoSlider.stopAutoCycle()
    }
    private fun getHomeDataService() {
        pbProgressHome.visibility=View.VISIBLE
        ServiceHelper().getHomeDataService(Utils.getPreferencesString(homeActivity, AppConstants.USER_ID),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        pbProgressHome.visibility=View.GONE
                        val homeDataResponse = response.body() as HomeDataResponse?
                        if(homeDataResponse!=null) {
                            setApiData(homeDataResponse.data)
                        }
                    }

                    override fun onFailure(msg: String?) {
                        pbProgressHome.visibility=View.GONE
                        Utils.showSimpleMessage(homeActivity, msg!!).show()
                    }
                })
    }

    private fun setApiData(data: DataHome) {
        custom_indicator.setDefaultIndicatorColor(ContextCompat.getColor(homeActivity, R.color.white), ContextCompat.getColor(homeActivity, R.color.light_gray))
        mDemoSlider.setCustomIndicator(custom_indicator)
        for (i in 0 until data.advertisments.size) {
            val textSliderView = DefaultSliderView(homeActivity)
            textSliderView.image(AppConstants.IMAGE_URL+data.advertisments[i].image)
                    .setOnSliderClickListener({ slider -> }).scaleType = BaseSliderView.ScaleType.Fit
            mDemoSlider.addSlider(textSliderView)
        }
        mDemoSlider.setDuration(3000)
        setCategoryAdapter(data.categories)
    }
}