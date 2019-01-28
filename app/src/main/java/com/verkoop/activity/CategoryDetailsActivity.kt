package com.verkoop.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import com.verkoop.R
import com.verkoop.adapter.DetailSubCategory
import com.verkoop.adapter.FilterAdapter
import com.verkoop.adapter.ItemAdapter
import com.verkoop.models.CategoryModal
import kotlinx.android.synthetic.main.category_details_activity.*
import kotlinx.android.synthetic.main.toolbar_details_.*

class CategoryDetailsActivity : AppCompatActivity() {
    private val categoryList = ArrayList<CategoryModal>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.category_details_activity)
        setData()
        setSubcategoryData()
        setListData()
    }

    private fun setSubcategoryData() {
        val mManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvSubCategory.layoutManager = mManager
        val detailSubCategoryAdapter = DetailSubCategory(this)
        rvSubCategory.adapter = detailSubCategoryAdapter
    }

    private fun setData() {
        iv_left.setOnClickListener { onBackPressed() }
        ivFilter.setOnClickListener {
            val intent = Intent(this, FilterActivity::class.java)
            startActivityForResult(intent, 1)
        }
        val mManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvFilterSelected.layoutManager = mManager
        val filterAdapter = FilterAdapter(this)
        rvFilterSelected.adapter = filterAdapter
    }

    private fun setItemList() {
        val linearLayoutManager = GridLayoutManager(this, 2)
        rvItemListDetails.layoutManager = linearLayoutManager
        val itemAdapter = ItemAdapter(this, categoryList)
        rvItemListDetails.isNestedScrollingEnabled = false
        rvItemListDetails.adapter = itemAdapter
    }

    private fun setListData() {
        val nameList = arrayOf("Women's", "men's", "Footwear", "Desktop's", "Mobiles", "Furniture", "Pets", "Car", "Books")
        val imageList = arrayOf(R.mipmap.women_unselected, R.mipmap.men_unselected, R.mipmap.footwear_unselected, R.mipmap.desktop_unselected, R.mipmap.mobile_unselected, R.mipmap.furniture_unselected, R.mipmap.pet_unseleted, R.mipmap.car_unseleted, R.mipmap.books_unselected)
        val imageListSelected = arrayOf(R.mipmap.women_selected, R.mipmap.men_selected, R.mipmap.footwear_selected, R.mipmap.desktop_selected, R.mipmap.mobile_selected, R.mipmap.furniture_selected, R.mipmap.pet_selected, R.mipmap.car_selected, R.mipmap.books_selected)
        for (i in nameList.indices) {
            val categoryModal = CategoryModal(nameList[i], imageList[i], imageListSelected[i], false)
            categoryList.add(categoryModal)
        }
        setItemList()
    }
}