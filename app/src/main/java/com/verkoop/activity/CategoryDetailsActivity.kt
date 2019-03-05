package com.verkoop.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.view.View
import com.verkoop.LikeDisLikeListener
import com.verkoop.R
import com.verkoop.adapter.DetailSubCategory
import com.verkoop.adapter.FilterAdapter
import com.verkoop.adapter.ItemAdapter
import com.verkoop.models.*
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.category_details_activity.*
import kotlinx.android.synthetic.main.toolbar_details_.*
import retrofit2.Response
import android.app.Activity

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class CategoryDetailsActivity : AppCompatActivity(), LikeDisLikeListener {
    private var isClicked: Boolean = false
    private lateinit var itemAdapter: ItemAdapter
    private var itemsList = ArrayList<Item>()
    private var filterRequest: FilterRequest? =null
    override fun getLikeDisLikeClick(type: Boolean, position: Int, lickedId: Int, itemId: Int) {
        if (type) {
            if (!isClicked) {
                isClicked = true
                deleteLikeApi(position, lickedId)
            }
        } else {
            if (!isClicked) {
                isClicked = true
                lickedApi(itemId, position)
            }
        }
    }

    override fun getItemDetailsClick(itemId: Int) {
        val intent = Intent(this, ProductDetailsActivity::class.java)
        intent.putExtra(AppConstants.ITEM_ID, itemId)
        startActivity(intent)
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
        ivFavourite.setOnClickListener {
            val intent=Intent(this,FavouritesActivity::class.java)
            startActivity(intent)
        }
        val type = intent.getIntExtra(AppConstants.TYPE, 0)
        toolbar_title.text = intent.getStringExtra(AppConstants.SUB_CATEGORY)
        if (type == 1) {
            llParent.visibility = View.GONE
            val lickedRequest = FilterRequest(intent.getIntExtra(AppConstants.CATEGORY_ID,0).toString(), type, Utils.getPreferencesString(this, AppConstants.USER_ID),"","","","","","","")
            getDetailsApi(lickedRequest)

        } else {
            tvCategorySelected.text = intent.getStringExtra(AppConstants.SUB_CATEGORY)
            llParent.visibility = View.VISIBLE
            val lickedRequest = FilterRequest(intent.getIntExtra(AppConstants.CATEGORY_ID,0).toString(), type, Utils.getPreferencesString(this, AppConstants.USER_ID),"","","","","","","")
            getDetailsApi(lickedRequest)
        }
        iv_left.setOnClickListener { onBackPressed() }
        ivFilter.setOnClickListener {
            val intent = Intent(this, FilterActivity::class.java)
            startActivityForResult(intent, 1)
        }
        val mManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvFilterSelected.layoutManager = mManager
        val filterAdapter = FilterAdapter(this)
        rvFilterSelected.adapter = filterAdapter
    }

    private fun setItemList() {
        val linearLayoutManager = GridLayoutManager(this, 2)
        rvItemListDetails.layoutManager = linearLayoutManager
        itemAdapter = ItemAdapter(this, rvItemListDetails)
        rvItemListDetails.isNestedScrollingEnabled = false
        rvItemListDetails.adapter = itemAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode === 1) {
            if (resultCode === Activity.RESULT_OK) {
                filterRequest= data!!.getParcelableExtra(AppConstants.POST_DATA)
              //  getDetailsApi(intent.getIntExtra(AppConstants.CATEGORY_ID, 0), type)

            }
            if (resultCode === Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

    }

    override fun onBackPressed() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()

    }
    private fun getDetailsApi(request: FilterRequest) {
        pvProgressDetail.visibility=View.VISIBLE
        ServiceHelper().categoryPostService(request,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        pvProgressDetail.visibility=View.GONE
                        val categoryPostResponse = response.body() as CategoryPostResponse
                        if (categoryPostResponse.data.subCategoryList.size > 0) {
                            setSubcategoryData(categoryPostResponse.data.subCategoryList)
                        }
                        if (categoryPostResponse.data.items.size > 0) {
                            itemsList = categoryPostResponse.data.items
                            itemAdapter.setData(itemsList)
                            itemAdapter.notifyDataSetChanged()
                        }

                    }

                    override fun onFailure(msg: String?) {
                        pvProgressDetail.visibility=View.GONE
                        Utils.showSimpleMessage(this@CategoryDetailsActivity, msg!!).show()
                    }
                })

    }

    private fun lickedApi(itemId: Int, position: Int) {
        val lickedRequest = LickedRequest(Utils.getPreferencesString(this, AppConstants.USER_ID), itemId)
        ServiceHelper().likeService(lickedRequest,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        isClicked = false
                        val responseLike = response.body() as LikedResponse
                        val items = Item(itemsList[position].id,
                                itemsList[position].user_id,
                                itemsList[position].category_id,
                                itemsList[position].name,
                                itemsList[position].price,
                                itemsList[position].item_type,
                                itemsList[position].created_at,
                                itemsList[position].likes_count + 1,
                                responseLike.like_id,
                                !itemsList[position].is_like,
                                itemsList[position].image_url)
                        itemsList[position] = items
                        itemAdapter.notifyItemChanged(position)
                    }

                    override fun onFailure(msg: String?) {
                        isClicked = false
                        //Utils.showSimpleMessage(homeActivity, msg!!).show()
                    }
                })
    }

    private fun deleteLikeApi(position: Int, lickedId: Int) {
        ServiceHelper().disLikeService(lickedId,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        isClicked = false
                        val likeResponse = response.body() as DisLikeResponse
                        val items = Item(itemsList[position].id,
                                itemsList[position].user_id,
                                itemsList[position].category_id,
                                itemsList[position].name,
                                itemsList[position].price,
                                itemsList[position].item_type,
                                itemsList[position].created_at,
                                itemsList[position].likes_count - 1,
                                0,
                                !itemsList[position].is_like,
                                itemsList[position].image_url)
                        itemsList[position] = items
                        itemAdapter.notifyItemChanged(position)
                    }

                    override fun onFailure(msg: String?) {
                        isClicked = false
                        //     Utils.showSimpleMessage(homeActivity, msg!!).show()
                    }
                })
    }
}