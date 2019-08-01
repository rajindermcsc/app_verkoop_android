package com.verkoopapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import com.verkoopapp.R
import com.verkoopapp.adapter.DetailSubCategory
import com.verkoopapp.adapter.FilterAdapter
import com.verkoopapp.adapter.ItemAdapter
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.GridSpacingItemDecoration
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.category_details_activity.*
import kotlinx.android.synthetic.main.toolbar_details_.*
import retrofit2.Response

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class CategoryDetailsActivity : AppCompatActivity(), FilterAdapter.SelectFilterCallBack {
    private var isClicked: Boolean = false
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var filterAdapter: FilterAdapter
    private var itemsList = ArrayList<ItemHome>()
    private var filterRequest: FilterRequest? = null
    private var filterList = ArrayList<FilterModal>()

    override fun onSelectingFilter() {
        val intent = Intent(this, FilterActivity::class.java)
        intent.putExtra(AppConstants.POST_DATA, filterRequest)
        startActivityForResult(intent, 1)
    }

    override fun removeFilter(remove: String, position: Int) {
        when {
            remove.equals("Condition :", ignoreCase = true) -> {
                filterRequest = FilterRequest(filterRequest!!.category_id, filterRequest!!.type, filterRequest!!.userId, filterRequest!!.sort_no, filterRequest!!.latitude, filterRequest!!.longitude, "", filterRequest!!.meet_up, filterRequest!!.min_price, filterRequest!!.max_price)
                getDetailsApi(filterRequest!!)
                filterList.removeAt(position)
                filterAdapter.notifyDataSetChanged()

            }
            remove.equals("Deal Option :", ignoreCase = true) -> {
                filterRequest = FilterRequest(filterRequest!!.category_id, filterRequest!!.type, filterRequest!!.userId, filterRequest!!.sort_no, filterRequest!!.latitude, filterRequest!!.longitude, filterRequest!!.item_type, "", filterRequest!!.min_price, filterRequest!!.max_price)
                getDetailsApi(filterRequest!!)
                filterList.removeAt(position)
                filterAdapter.notifyDataSetChanged()
            }
            remove.equals("Price :", ignoreCase = true) -> {
                filterRequest = FilterRequest(filterRequest!!.category_id, filterRequest!!.type, filterRequest!!.userId, filterRequest!!.sort_no, filterRequest!!.latitude, filterRequest!!.longitude, filterRequest!!.item_type, filterRequest!!.meet_up, "", "")
                getDetailsApi(filterRequest!!)
                filterList.removeAt(position)
                filterAdapter.notifyDataSetChanged()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.category_details_activity)
        setData()
        setItemList()
    }

    private fun setSubcategoryData(subCategoryList: ArrayList<SubCategoryPost>) {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        rvSubCategory.layoutParams.height = (width / 3)
        val mManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvSubCategory.layoutManager = mManager
        val detailSubCategoryAdapter = DetailSubCategory(this, rvSubCategory, subCategoryList)
        rvSubCategory.adapter = detailSubCategoryAdapter
    }

    private fun setData() {
        ivChatDetail.setOnClickListener {
            val intent = Intent(this, ChatInboxActivity::class.java)
            startActivity(intent)
        }
        tvSell.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)
            startActivityForResult(intent, 2)

        }
        ivFavouriteDetail.setOnClickListener {
            val intent = Intent(this, FavouritesActivity::class.java)
            startActivity(intent)
        }
        val type = intent.getIntExtra(AppConstants.TYPE, 0)
        toolbar_title_.text = intent.getStringExtra(AppConstants.SUB_CATEGORY)
        toolbar_title_.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra(AppConstants.CATEGORY_NAME, toolbar_title_.text.toString())
            startActivityForResult(intent, 2)
        }
        if (type == 1) {
            llParent.visibility = View.GONE
            filterRequest = FilterRequest(intent.getIntExtra(AppConstants.CATEGORY_ID, 0).toString(), type, Utils.getPreferencesString(this, AppConstants.USER_ID), "2", "", "", "", "", "", "",intent.getIntExtra(AppConstants.ITEM_ID,0))
            getDetailsApi(filterRequest!!)

        } else {
            tvCategorySelected.text = intent.getStringExtra(AppConstants.SUB_CATEGORY)
            llParent.visibility = View.VISIBLE
            filterRequest = FilterRequest(intent.getIntExtra(AppConstants.CATEGORY_ID, 0).toString(), type, Utils.getPreferencesString(this, AppConstants.USER_ID), "2", "", "", "", "", "", "")
            getDetailsApi(filterRequest!!)
        }
        iv_left_detail.setOnClickListener { onBackPressed() }
        ivFilter.setOnClickListener {
            val intent = Intent(this, FilterActivity::class.java)
            intent.putExtra(AppConstants.POST_DATA, filterRequest)
            startActivityForResult(intent, 1)
        }
        val mManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvFilterSelected.layoutManager = mManager
        filterAdapter = FilterAdapter(this)
        rvFilterSelected.adapter = filterAdapter
        val filterModal = FilterModal(getString(R.string.shopr), getString(R.string.popular), false, 1)
        filterList.add(filterModal)
        filterAdapter.showData(filterList)
        filterAdapter.notifyDataSetChanged()
    }

    private fun setItemList() {
        val linearLayoutManager = GridLayoutManager(this, 2)
        rvItemListDetails.layoutManager = linearLayoutManager
        rvItemListDetails.addItemDecoration(GridSpacingItemDecoration(2,Utils.dpToPx(this,2F).toInt(),false))
        itemAdapter = ItemAdapter(this, rvItemListDetails)
        rvItemListDetails.isNestedScrollingEnabled = false
        rvItemListDetails.adapter = itemAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode === 1) {
            if (resultCode === Activity.RESULT_OK) {
                filterRequest = data!!.getParcelableExtra(AppConstants.POST_DATA)
                setFilter(filterRequest)
                getDetailsApi(filterRequest!!)
            }
            if (resultCode === Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getIntExtra(AppConstants.TRANSACTION, 0)
                if (result == 1) {
                    val returnIntent = Intent()
                    returnIntent.putExtra(AppConstants.TRANSACTION, result)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                    overridePendingTransition(0, 0)
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

    }

    private fun setFilter(filterRequest: FilterRequest?) {
        if (filterRequest != null) {
            filterList.clear()
            if (!TextUtils.isEmpty(filterRequest.sort_no)) {
                when {
                    filterRequest.sort_no.toInt() == 1 -> {
                        val filterModal = FilterModal("Sort :", getString(R.string.nearby), false, 1)
                        filterList.add(filterModal)
                    }
                    filterRequest.sort_no.toInt() == 2 -> {
                        val filterModal = FilterModal("Sort :", getString(R.string.popular), false, 1)
                        filterList.add(filterModal)
                    }
                    filterRequest.sort_no.toInt() == 3 -> {
                        val filterModal = FilterModal("Sort :", getString(R.string.recently_added), false, 1)
                        filterList.add(filterModal)
                    }
                    filterRequest.sort_no.toInt() == 4 -> {
                        val filterModal = FilterModal("Sort :", getString(R.string.price_high_to_low), false, 1)
                        filterList.add(filterModal)
                    }
                    filterRequest.sort_no.toInt() == 5 -> {
                        val filterModal = FilterModal("Sort :", getString(R.string.price_low_to_high), false, 1)
                        filterList.add(filterModal)
                    }
                    else -> {
                        val filterModal = FilterModal("Sort :", getString(R.string.popular), false, 1)
                        filterList.add(filterModal)
                    }
                }

            }
            if (!TextUtils.isEmpty(filterRequest.item_type)) {
                if (filterRequest.item_type.equals("1", ignoreCase = true)) {
                    val filterModal = FilterModal("Condition :", "New", false, 2)
                    filterList.add(filterModal)
                } else {
                    val filterModal = FilterModal("Condition :", getString(R.string.used), false, 2)
                    filterList.add(filterModal)
                }
            }
            if (!TextUtils.isEmpty(filterRequest.meet_up)) {
                if (filterRequest.meet_up.equals("1", ignoreCase = true)) {
                    val filterModal = FilterModal("Deal Option :", "Meet-up", false, 3)
                    filterList.add(filterModal)
                }
            }
            if (!TextUtils.isEmpty(filterRequest.min_price) && !TextUtils.isEmpty(filterRequest.max_price)) {
                val filterModal = FilterModal(getString(R.string.pric), "R" + filterRequest.min_price + " - R" + filterRequest.max_price, false, 4)
                filterList.add(filterModal)
            } else if (!TextUtils.isEmpty(filterRequest.min_price) && TextUtils.isEmpty(filterRequest.max_price)) {
                val filterModal = FilterModal(getString(R.string.pric), "From R" + filterRequest.min_price, false, 4)
                filterList.add(filterModal)
            } else if (TextUtils.isEmpty(filterRequest.min_price) && !TextUtils.isEmpty(filterRequest.max_price)) {
                val filterModal = FilterModal(getString(R.string.pric), "Up to R" + filterRequest.max_price, false, 4)
                filterList.add(filterModal)
            }
        }
        filterAdapter.showData(filterList)
        filterAdapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }

    private fun getDetailsApi(request: FilterRequest) {
        pvProgressDetail.visibility = View.VISIBLE
        ServiceHelper().categoryPostService(request,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        itemsList.clear()
                        pvProgressDetail.visibility = View.GONE
                        val categoryPostResponse = response.body() as CategoryPostResponse
                        if (categoryPostResponse.data.subCategoryList.size > 0) {
                            setSubcategoryData(categoryPostResponse.data.subCategoryList)
                        }
                        if (categoryPostResponse.data.items.size > 0) {
                            itemsList = categoryPostResponse.data.items
                            itemAdapter.setData(itemsList)
                            itemAdapter.notifyDataSetChanged()
                        } else {
                            itemsList.clear()
                            itemAdapter.notifyDataSetChanged()
                        }

                    }

                    override fun onFailure(msg: String?) {
                        pvProgressDetail.visibility = View.GONE
                        Utils.showSimpleMessage(this@CategoryDetailsActivity, msg!!).show()
                    }
                })

    }
}