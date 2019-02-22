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
import com.verkoop.activity.ProductDetailsActivity
import com.verkoop.adapter.MyProfileItemAdapter
import com.verkoop.models.Item
import com.verkoop.models.MyProfileResponse
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.profile_fragment.*
import retrofit2.Response

class ProfileFragment : BaseFragment(), MyProfileItemAdapter.LikeDisLikeListener {

    override fun getItemDetailsClick(itemId: Int) {
        val intent = Intent(context, ProductDetailsActivity::class.java)
        intent.putExtra(AppConstants.ITEM_ID, itemId)
        homeActivity.startActivity(intent)
    }

    override fun getLikeDisLikeClick(type: Int, position: Int) {
        //Utils.showToast(homeActivity,"work in progress")
    }

    private val TAG = ProfileFragment::class.java.simpleName.toString()
    private lateinit var homeActivity: HomeActivity

    override fun getTitle(): Int {
        return 0
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        homeActivity = activity as HomeActivity

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
        if (Utils.isOnline(homeActivity)) {
            myProfileInfoApi()
        } else {
            Utils.showSimpleMessage(homeActivity, getString(R.string.check_internet)).show()
        }
    }

    private fun setAdapter(items: ArrayList<Item>) {
        val linearLayoutManager = GridLayoutManager(context, 2)
        rvPostsList.layoutManager = linearLayoutManager
        val itemAdapter = MyProfileItemAdapter(homeActivity, items, llProfileParent, this)
        rvPostsList.isNestedScrollingEnabled = false
        rvPostsList.isFocusable = false
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

    private fun myProfileInfoApi() {
     //   VerkoopApplication.instance.loader.show(homeActivity)
        pbProgressProfile.visibility=View.VISIBLE
        ServiceHelper().myProfileService(Utils.getPreferencesString(homeActivity, AppConstants.USER_ID),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                    //    VerkoopApplication.instance.loader.hide(homeActivity)
                        pbProgressProfile.visibility=View.GONE
                        val myProfileResponse = response.body() as MyProfileResponse
                        setData()
                        setAdapter(myProfileResponse.data.items)
                        tvName.text = myProfileResponse.data.username
                    }

                    override fun onFailure(msg: String?) {
                       // VerkoopApplication.instance.loader.hide(homeActivity)
                        pbProgressProfile.visibility=View.GONE
                        Utils.showSimpleMessage(homeActivity, msg!!).show()
                    }
                })
    }
}