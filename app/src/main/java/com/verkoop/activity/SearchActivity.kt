package com.verkoop.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.verkoop.R
import com.verkoop.adapter.SearchListAdapter
import com.verkoop.models.DataSearch
import com.verkoop.models.SearchItemRequest
import com.verkoop.models.SearchItemResponse
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.search_activity.*
import kotlinx.android.synthetic.main.toolbar_search_user.*
import retrofit2.Response


class SearchActivity : AppCompatActivity() {
    private lateinit var searchListAdapter: SearchListAdapter
    private val searchItemList = ArrayList<DataSearch>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_activity)
        setData()
        setAdapter()

    }

    private fun setAdapter() {
        val mManager = LinearLayoutManager(this)
        rvSearchList.layoutManager = mManager
        searchListAdapter = SearchListAdapter(this)
        rvSearchList.adapter = searchListAdapter

    }

    private fun setData() {
        iv_left.setOnClickListener { onBackPressed() }
        etSearchHeader.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (Utils.isOnline(this@SearchActivity)) {
                    if (!TextUtils.isEmpty(charSequence.toString().trim())) {
                        callSearchApi(charSequence.toString())
                    }else{
                        searchItemList.clear()
                        searchListAdapter.setData(searchItemList)
                        searchListAdapter.notifyDataSetChanged()
                    }
                } else {
                    Utils.showSimpleMessage(this@SearchActivity, getString(R.string.check_internet)).show()
                }

            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun callSearchApi(searchItem: String) {
        ServiceHelper().searchItemService(SearchItemRequest(searchItem),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        val searchItemResponse = response.body() as SearchItemResponse?
                        if (searchItemResponse != null) {
                            searchItemList.clear()
                            searchItemList.addAll(searchItemResponse.data)
                            searchListAdapter.setData(searchItemList)
                            searchListAdapter.notifyDataSetChanged()
                        }

                    }

                    override fun onFailure(msg: String?) {
                        Utils.showSimpleMessage(this@SearchActivity, msg!!).show()
                    }
                })
    }

    override fun onBackPressed() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }
}