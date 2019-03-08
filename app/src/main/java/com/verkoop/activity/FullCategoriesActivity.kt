package com.verkoop.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import com.verkoop.R
import com.verkoop.VerkoopApplication
import com.verkoop.adapter.FullCategoryAdapter
import com.verkoop.adapter.SubCategoryAdapter
import com.verkoop.models.CategoriesResponse
import com.verkoop.models.DataCategory
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.full_categories_activity.*
import kotlinx.android.synthetic.main.toolbar_category.*
import retrofit2.Response
import java.util.*


class FullCategoriesActivity : AppCompatActivity(), FullCategoryAdapter.SelectedCategory, SubCategoryAdapter.SelectedSubcategory {
    override fun subCategoryName(categoryId: Int,subCategoryName:String) {
        val intent = Intent(this, CategoryDetailsActivity::class.java)
        intent.putExtra(AppConstants.CATEGORY_ID, categoryId)
        intent.putExtra(AppConstants.SUB_CATEGORY, subCategoryName)
        intent.putExtra(AppConstants.TYPE, 1)
        startActivityForResult(intent,2)
    }

    override fun categoryName(categoryId: Int,categoryName:String) {
        val intent = Intent(this, CategoryDetailsActivity::class.java)
        intent.putExtra(AppConstants.CATEGORY_ID, categoryId)
        intent.putExtra(AppConstants.SUB_CATEGORY, categoryName)
        intent.putExtra(AppConstants.TYPE, 0)
        startActivityForResult(intent,2)
    }

    private val categoryList = ArrayList<DataCategory>()

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
        rvCategoryList.layoutManager = mManager
        val fullCategoryAdapter = FullCategoryAdapter(this, categoryList)
        rvCategoryList.adapter = fullCategoryAdapter
        iv_left.setOnClickListener { onBackPressed() }
        ivFavourite.setOnClickListener {
            val intent=Intent(this,FavouritesActivity::class.java)
            startActivity(intent)
        }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getIntExtra(AppConstants.TRANSACTION,0)
                if(result==1){
                    val returnIntent = Intent()
                    returnIntent.putExtra(AppConstants.TRANSACTION, result)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                    overridePendingTransition(0, 0)
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    override fun onBackPressed() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }
}