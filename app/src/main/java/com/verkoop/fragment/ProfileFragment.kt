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
import com.verkoop.VerkoopApplication
import com.verkoop.activity.FullCategoriesActivity
import com.verkoop.activity.HomeActivity
import com.verkoop.adapter.ItemAdapter
import com.verkoop.adapter.MyProfileItemAdapter
import com.verkoop.models.CategoryModal
import com.verkoop.models.Item
import com.verkoop.models.MyProfileResponse
import com.verkoop.models.Users
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.profile_fragment.*
import retrofit2.Response


class ProfileFragment : BaseFragment() {
    private val TAG = ProfileFragment::class.java.simpleName.toString()
    private lateinit var homeActivity: HomeActivity
    private val categoryList = ArrayList<CategoryModal>()
    private var userDetails: Users? = null

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
        myProfileInfoApi()
    }

    private fun setAdapter(items: ArrayList<Item>) {
        val linearLayoutManager = GridLayoutManager(context, 2)
        rvPostsList.layoutManager = linearLayoutManager
        val itemAdapter = MyProfileItemAdapter(homeActivity, items)
        rvPostsList.isNestedScrollingEnabled = false
        rvPostsList.isFocusable=false
        rvPostsList.adapter = itemAdapter
    }

    private fun setData(users: Users) {
        tvName.text=users.username
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

    private fun  myProfileInfoApi() {
        VerkoopApplication.instance.loader.show(homeActivity)
        ServiceHelper().myProfileService(Utils.getPreferencesString(homeActivity,AppConstants.USER_ID),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        VerkoopApplication.instance.loader.hide(homeActivity)
                        val myProfileResponse = response.body() as MyProfileResponse
                        setData(myProfileResponse.data.users)
                        setAdapter(myProfileResponse.data.items)
                    }

                    override fun onFailure(msg: String?) {
                        VerkoopApplication.instance.loader.hide(homeActivity)
                        Utils.showSimpleMessage(homeActivity, msg!!).show()
                    }
                })
    }
}