package com.verkoopapp.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ksmtrivia.common.BaseFragment
import com.verkoopapp.R
import com.verkoopapp.activity.ChatInboxActivity
import com.verkoopapp.activity.FoodHomeActivity
import com.verkoopapp.activity.HomeActivity
import com.verkoopapp.activity.PackageActivity
import com.verkoopapp.adapter.ActivitiesAdapter
import kotlinx.android.synthetic.main.fragment_vervo.*
import kotlinx.android.synthetic.main.toolbar_home.*


class VervoFragment  : BaseFragment(){

    val TAG = VervoFragment::class.java.simpleName.toString()
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_vervo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
    }


    fun setUI(){
        tv_food.setOnClickListener {
            val intent = Intent(context, FoodHomeActivity::class.java)
            startActivity(intent)
        }
        tv_package.setOnClickListener {
            val intent = Intent(context, PackageActivity::class.java)
            startActivity(intent)
        }
    }


    companion object {
        fun newInstance(): VervoFragment {
            val args = Bundle()
            val fragment = VervoFragment()
            fragment.arguments = args
            return fragment
        }
    }

}