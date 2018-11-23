package com.verkoop.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ksmtrivia.adapter.CategoryAdapter
import com.verkoop.R
import com.verkoop.activity.CategoriesActivity
import com.verkoop.models.CategoryModal
import kotlinx.android.synthetic.main.first_category.*

class FirstCategoryFragment : Fragment() {
    private lateinit var categoriesActivity: CategoriesActivity
    private val categoryList=ArrayList<CategoryModal>()


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        categoriesActivity = activity as CategoriesActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.first_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setData()
    }

    private fun setData() {

         val nameList = arrayOf("Women's", "men's","Footwear","Desktop's","Mobiles","Furniture","Pets","Car","Books")
         val imageList= arrayOf(R.mipmap.women_unselected,R.mipmap.men_unselected,R.mipmap.footwear_unselected,R.mipmap.desktop_unselected,R.mipmap.mobile_unselected,R.mipmap.furniture_unselected,R.mipmap.pet_unseleted,R.mipmap.car_unseleted,R.mipmap.books_unselected)
         val imageListSelected= arrayOf(R.mipmap.women_selected,R.mipmap.men_selected,R.mipmap.footwear_selected,R.mipmap.desktop_selected,R.mipmap.mobile_selected,R.mipmap.furniture_selected,R.mipmap.pet_selected,R.mipmap.car_selected,R.mipmap.books_selected)
        for (i in nameList.indices){
         val categoryModal=  CategoryModal(nameList[i],imageList[i],imageListSelected[i],false)
            categoryList.add(categoryModal)
        }
        setAdapter()
    }

    private fun setAdapter() {
        val linearLayoutManager = GridLayoutManager(context, 3)
        rvCategoryList.layoutManager = linearLayoutManager
        val categoriesAdapter= CategoryAdapter(categoriesActivity,categoryList)
        rvCategoryList.adapter=categoriesAdapter
    }

    companion object {
        fun newInstance(id: Int): FirstCategoryFragment {
            val args = Bundle()
            args.putInt("frag", id)
            val fragment = FirstCategoryFragment()
            fragment.arguments = args
            return fragment
        }
    }

}