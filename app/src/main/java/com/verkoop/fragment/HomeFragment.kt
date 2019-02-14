package com.verkoop.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
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
import com.verkoop.models.CategoryModal
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.home_fragment.*


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
        setData()
        setListData()
        setItemList()
    }

    private fun setItemList() {
        val linearLayoutManager = GridLayoutManager(context, 2)
        rvItemList.layoutManager = linearLayoutManager
        val itemAdapter = ItemAdapter(homeActivity, categoryList)
        rvItemList.isNestedScrollingEnabled = false
        rvItemList.adapter = itemAdapter
    }

    private fun setListData() {
        val nameList = arrayOf("Women's", "men's", "Footwear", "Desktop's", "Mobiles", "Furniture", "Pets", "Car", "Books")
        val imageList = arrayOf(R.mipmap.women_unselected, R.mipmap.men_unselected, R.mipmap.footwear_unselected, R.mipmap.desktop_unselected, R.mipmap.mobile_unselected, R.mipmap.furniture_unselected, R.mipmap.pet_unseleted, R.mipmap.car_unseleted, R.mipmap.books_unselected)
        val imageListSelected = arrayOf(R.mipmap.women_selected, R.mipmap.men_selected, R.mipmap.footwear_selected, R.mipmap.desktop_selected, R.mipmap.mobile_selected, R.mipmap.furniture_selected, R.mipmap.pet_selected, R.mipmap.car_selected, R.mipmap.books_selected)
        for (i in nameList.indices) {
            val categoryModal = CategoryModal(nameList[i], imageList[i], imageListSelected[i], false)
            categoryList.add(categoryModal)
        }
        setCategoryAdapter()
    }

    private fun setCategoryAdapter() {
        val linearLayoutManager = LinearLayoutManager(homeActivity, LinearLayoutManager.HORIZONTAL, false)
        rvCategoryHome.layoutManager = linearLayoutManager
        val categoryAdapter = CategoryListAdapter(homeActivity, categoryList)
        rvCategoryHome.adapter = categoryAdapter
    }

    private fun setData() {
        custom_indicator.setDefaultIndicatorColor(ContextCompat.getColor(homeActivity, R.color.white), ContextCompat.getColor(homeActivity, R.color.light_gray))
        mDemoSlider.setCustomIndicator(custom_indicator)
        val imageList = ArrayList<Int>()
        imageList.add(R.mipmap.pic_1)
        imageList.add(R.mipmap.pic_1)
        imageList.add(R.mipmap.pic_1)
        imageList.add(R.mipmap.pic_1)

        for (i in 0 until imageList.size) {
            val textSliderView = DefaultSliderView(homeActivity)
            // initialize a SliderLayout
            textSliderView.image(imageList[i])
                    .setOnSliderClickListener({ slider -> }).scaleType = BaseSliderView.ScaleType.Fit
            mDemoSlider.addSlider(textSliderView)
        }
        mDemoSlider.setDuration(3000)
        tvSell.setOnClickListener {
            val intent = Intent(homeActivity, GalleryActivity::class.java)
            homeActivity.startActivityForResult(intent, 2)
        }
        tvViewAll.setOnClickListener { Utils.showToast(homeActivity, "Work in progress.") }
        tvCategory.setOnClickListener {
            val intent = Intent(homeActivity, FullCategoriesActivity::class.java)
            startActivity(intent)
        }
        llSearch.setOnClickListener { Utils.showToast(homeActivity, "Work in progress.") }
    }

    companion object {
        fun newInstance(): HomeFragment {
            val args = Bundle()
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onStop() {
        super.onStop()
        mDemoSlider.stopAutoCycle()
    }

}