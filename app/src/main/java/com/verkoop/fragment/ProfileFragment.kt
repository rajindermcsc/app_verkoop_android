package com.verkoop.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ksmtrivia.common.BaseFragment
import com.verkoop.R
import com.verkoop.activity.FullCategoriesActivity
import com.verkoop.activity.HomeActivity
import com.verkoop.adapter.ItemAdapter
import com.verkoop.models.CategoryModal
import kotlinx.android.synthetic.main.profile_fragment.*


class ProfileFragment : BaseFragment() {
    private val TAG = ProfileFragment::class.java.simpleName.toString()
    private lateinit var homeActivity: HomeActivity
    private val categoryList = ArrayList<CategoryModal>()

    override fun getTitle(): Int {
        return 0
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        homeActivity=activity as HomeActivity

    }
    override fun getFragmentTag(): String? {
        return TAG
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setData()
    }

    private fun setAdapter() {
        val linearLayoutManager = GridLayoutManager(context, 2)
        rvPostsList.layoutManager = linearLayoutManager
        val itemAdapter = ItemAdapter(homeActivity, categoryList)
        rvPostsList.isNestedScrollingEnabled = false
        rvPostsList.isFocusable=false
        rvPostsList.adapter = itemAdapter
    }

    private fun setData() {
        tvCategoryProfile.setOnClickListener {
            val intent = Intent(homeActivity, FullCategoriesActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        fun newInstance(): ProfileFragment {
            val arg = Bundle()
            val fragment = ProfileFragment()
            fragment.arguments = arg
            return fragment
        }

    }
}