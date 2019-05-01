package com.verkoop.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.verkoop.R
import com.verkoop.adapter.FollowFollowingAdapter
import com.verkoop.models.HomeRequest
import com.verkoop.models.SearchByUserResponse
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.KeyboardUtil
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.search_activity.*
import kotlinx.android.synthetic.main.toolbar_search_user.*
import retrofit2.Response


class FollowFollowingActivity : AppCompatActivity() {
    private lateinit var searchByUserAdapter: FollowFollowingAdapter
    private var comingFrom = 0
    private var userId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_activity)
        ll_search.visibility = View.GONE
        comingFrom = intent.getIntExtra(AppConstants.COMING_FROM, 0)
        userId = intent.getIntExtra(AppConstants.USER_ID, 0)
        setData()
        setUserSearchAdapter()
        if (Utils.isOnline(this)) {
            if (comingFrom == 0) {
                etSearchHeader.hint = "Search Followers"
                searchByUserNameApi(0, userId)
            } else {
                etSearchHeader.hint = "Search Following User"
                searchByUserNameApi(1, userId)
            }
            KeyboardUtil.hideKeyboard(this)
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }

    private fun setUserSearchAdapter() {
        val mManager = LinearLayoutManager(this)
        rvSearchList.layoutManager = mManager
        searchByUserAdapter = FollowFollowingAdapter(this)
        rvSearchList.adapter = searchByUserAdapter
    }

    private fun setData() {
        iv_left.setOnClickListener { onBackPressed() }
        etSearchHeader.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                searchByUserAdapter.filter.filter(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }


    private fun searchByUserNameApi(type: Int, userId: Int) {
        pbProgressSearch.visibility = View.VISIBLE
        ServiceHelper().followFollowingService(userId, HomeRequest(type),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        pbProgressSearch.visibility = View.GONE
                        val searchItemResponse = response.body() as SearchByUserResponse?
                        if (searchItemResponse != null) {
                            searchByUserAdapter.setData(searchItemResponse.data)
                            searchByUserAdapter.notifyDataSetChanged()
                        }
                        if (searchItemResponse!!.data.size > 0) {
                            tvDemoText.visibility = View.GONE
                        } else {
                            tvDemoText.visibility = View.VISIBLE
                        }
                    }

                    override fun onFailure(msg: String?) {
                        Utils.showSimpleMessage(this@FollowFollowingActivity, msg!!).show()
                        pbProgressSearch.visibility = View.GONE
                    }
                })
    }
}