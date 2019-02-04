package com.verkoop.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.verkoop.R
import kotlinx.android.synthetic.main.full_categories_activity.*
import android.support.v7.widget.StaggeredGridLayoutManager
import com.verkoop.adapter.FullCategoryAdapter
import com.verkoop.adapter.SubCategoryAdapter
import com.verkoop.models.QuestionsDataModel
import kotlinx.android.synthetic.main.toolbar_category.*
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



    private val newList = ArrayList<String>()
    private val newList1 = ArrayList<String>()
    private val categoryList=ArrayList<QuestionsDataModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.full_categories_activity)
        setListData()
    }

    private fun setData() {
        val mManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        rvCategoryList.layoutManager=mManager
        val fullCategoryAdapter= FullCategoryAdapter(this,categoryList)
        rvCategoryList.adapter=fullCategoryAdapter
        iv_left.setOnClickListener { onBackPressed() }

    }
    private fun setListData() {
        val nameList = arrayOf("Women's", "men's", "Footwear", "Desktop's", "Mobiles", "Furniture", "Pets", "Car", "Books")
        val subList = arrayOf("Women's", "men's", "Footwear", "Desktop's", "Mobiles")
        val subList2 = arrayOf("Women's", "men's", "Footwear", "Desktop's", "Mobiles", "Desktop's", "Mobiles", "Furniture", "Pets", "Car", "Books")
        for(j in subList.indices){
            newList.add(subList[j])
        }
        for(j in subList2.indices){
            newList1.add(subList2[j])
        }
        for(j in subList2.indices){
            newList1.add(subList2[j])
        }
        for (i in nameList.indices) {
            if(i%2==0) {
                val categoryModal = QuestionsDataModel(newList, nameList[i])
                categoryList.add(categoryModal)
            }else{
                val categoryModal = QuestionsDataModel(newList1, nameList[i])
                categoryList.add(categoryModal)
            }

        }
        setData()
    }
}