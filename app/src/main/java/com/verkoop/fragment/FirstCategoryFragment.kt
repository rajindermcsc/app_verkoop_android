package com.verkoop.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoop.adapter.CategoryAdapter
import com.verkoop.R
import com.verkoop.activity.CategoriesActivity
import com.verkoop.models.CategoryModal
import com.verkoop.utils.AppConstants.CATEGORY_LIST
import com.verkoop.utils.AppConstants.PAGER_POSITION
import kotlinx.android.synthetic.main.first_category.*

class FirstCategoryFragment : Fragment() {
    private lateinit var categoriesActivity: CategoriesActivity
    private var categoryList=ArrayList<CategoryModal>()
    private var position=0


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        categoriesActivity = activity as CategoriesActivity
        categoryList= arguments!!.getParcelableArrayList(CATEGORY_LIST)
        position= arguments!!.getInt(PAGER_POSITION)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.first_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setData()
        setAdapter(categoryList)
    }


    private fun setAdapter(categoryList: ArrayList<CategoryModal>) {
        val linearLayoutManager = GridLayoutManager(context, 3)
        rvCategoryList.layoutManager = linearLayoutManager
        val categoriesAdapter= CategoryAdapter(categoriesActivity, categoryList, position,llParentCate)
        rvCategoryList.adapter=categoriesAdapter
    }

    companion object {
        fun newInstance(id: Int, categoryList: ArrayList<CategoryModal>): FirstCategoryFragment {
            val args = Bundle()
            args.putInt(PAGER_POSITION, id)
            args.putParcelableArrayList(CATEGORY_LIST, categoryList)
            val fragment = FirstCategoryFragment()
            fragment.arguments = args
            return fragment
        }
    }
}