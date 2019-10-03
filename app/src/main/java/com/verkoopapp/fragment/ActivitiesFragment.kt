package com.verkoopapp.fragment

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ksmtrivia.common.BaseFragment
import com.verkoopapp.R
import com.verkoopapp.activity.HomeActivity
import com.verkoopapp.adapter.ActivitiesAdapter
import com.verkoopapp.adapter.ProfileAdapter
import kotlinx.android.synthetic.main.activities_fragment.*

class ActivitiesFragment : BaseFragment() {
    val TAG = ActivitiesFragment::class.java.simpleName.toString()
    private lateinit var activitesAdapter: ActivitiesAdapter
    private lateinit var homeActivity: HomeActivity
    override fun getTitle(): Int {
        return 0
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        homeActivity = context as HomeActivity
    }

    override fun getFragmentTag(): String? {
        return TAG
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activities_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()

        slActivities.setOnRefreshListener {
            slActivities.isRefreshing = false
        }
    }

    private fun setAdapter() {
        val linearLayoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        rvNotificationList.layoutManager=linearLayoutManager
        activitesAdapter = ActivitiesAdapter(homeActivity,this)
        rvNotificationList.adapter=activitesAdapter
    }

    companion object {
        fun newInstance(): ActivitiesFragment {
            val args = Bundle()
            val fragment = ActivitiesFragment()
            fragment.arguments = args
            return fragment
        }
    }

}