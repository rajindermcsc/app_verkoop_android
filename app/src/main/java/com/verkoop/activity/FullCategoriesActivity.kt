package com.verkoop.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.verkoop.R
import kotlinx.android.synthetic.main.full_categories_activity.*
import android.support.v7.widget.StaggeredGridLayoutManager
import com.verkoop.R.id.iv_left
import com.verkoop.R.id.rvCategoryList
import com.verkoop.VerkoopApplication
import com.verkoop.adapter.FullCategoryAdapter
import com.verkoop.adapter.SubCategoryAdapter
import com.verkoop.models.CategoriesResponse
import com.verkoop.models.DataCategory
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.toolbar_category.*
import retrofit2.Response
import java.util.ArrayList


class FullCategoriesActivity: AppCompatActivity(), FullCategoryAdapter.SelectedCategory, SubCategoryAdapter.SelectedSubcategory {
    override fun subCategoryName(name: String) {
        val intent=Intent(this,CategoryDetailsActivity::class.java)
        startActivity(intent)
       // Utils.showSimpleMessage(this,name).show()
    }

    override fun categoryName(name: String) {
        val intent=Intent(this,CategoryDetailsActivity::class.java)
        startActivity(intent)
    }

    private val categoryList=ArrayList<DataCategory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.full_categories_activity)
        if (Utils.isOnline(this)) {
            callCategoriesApi()
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }

    private fun setData() {
        val mManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        rvCategoryList.layoutManager=mManager
        val fullCategoryAdapter= FullCategoryAdapter(this,categoryList)
        rvCategoryList.adapter=fullCategoryAdapter
        iv_left.setOnClickListener { onBackPressed() }

    }
    private fun callCategoriesApi() {
        VerkoopApplication.instance.loader.show(this)
        ServiceHelper().categoriesService(
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        VerkoopApplication.instance.loader.hide(this@FullCategoriesActivity)
                        val categoriesResponse = response.body() as CategoriesResponse
                        categoryList.addAll(categoriesResponse.data)
                        setData()
                    }

                    override fun onFailure(msg: String?) {
                        VerkoopApplication.instance.loader.hide(this@FullCategoriesActivity)
                        Utils.showSimpleMessage(this@FullCategoriesActivity, msg!!).show()
                    }
                })
    }
}