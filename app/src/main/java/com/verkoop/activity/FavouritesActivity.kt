package com.verkoop.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.verkoop.LikeDisLikeListener
import com.verkoop.R
import com.verkoop.adapter.FavouritesAdapter
import com.verkoop.models.*
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.favourites_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import retrofit2.Response


class FavouritesActivity:AppCompatActivity(), LikeDisLikeListener {
    private var isClicked: Boolean = false
    private lateinit var favouritesAdapter: FavouritesAdapter
    private var itemsList=ArrayList<ItemHome>()

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
        setContentView(R.layout.favourites_activity)
        setAdapter()
        getFavouriteApi()
    }


    private fun setAdapter() {
        val linearLayoutManager = GridLayoutManager(this, 2)
        rvFavouriteList.layoutManager = linearLayoutManager
        favouritesAdapter = FavouritesAdapter(this, rvFavouriteList)
        rvFavouriteList.adapter = favouritesAdapter
        ivLeftLocation.setOnClickListener { onBackPressed() }
        tvHeaderLoc.text=getString(R.string.favourites)
    }

    private fun getFavouriteApi() {
        pbProgressFav.visibility= View.VISIBLE
        ServiceHelper().getFavouritesService(Utils.getPreferencesString(this,AppConstants.USER_ID),object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        pbProgressFav.visibility= View.GONE
                        val responseFav = response.body() as FavouritesResponse
                        if (responseFav.data.isNotEmpty()) {
                            itemsList = responseFav.data
                            favouritesAdapter.setData(itemsList)
                            favouritesAdapter.notifyDataSetChanged()
                        }

                    }

                    override fun onFailure(msg: String?) {
                        pbProgressFav.visibility= View.GONE
                        Utils.showSimpleMessage(this@FavouritesActivity, msg!!).show()
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
                        val items = ItemHome(itemsList[position].id,
                                itemsList[position].user_id,
                                itemsList[position].category_id,
                                itemsList[position].name,
                                itemsList[position].price,
                                itemsList[position].item_type,
                                itemsList[position].created_at,
                                itemsList[position].items_like_count + 1,
                                responseLike.like_id,
                                !itemsList[position].is_like,
                                itemsList[position].image_url,
                                itemsList[position].username,
                                itemsList[position].profile_pic)
                        itemsList[position] = items
                        favouritesAdapter.notifyItemChanged(position)
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
                        val items = ItemHome(itemsList[position].id,
                                itemsList[position].user_id,
                                itemsList[position].category_id,
                                itemsList[position].name,
                                itemsList[position].price,
                                itemsList[position].item_type,
                                itemsList[position].created_at,
                                itemsList[position].items_like_count - 1,
                                0,
                                !itemsList[position].is_like,
                                itemsList[position].image_url,
                                itemsList[position].username,
                                itemsList[position].profile_pic)
                        itemsList[position] = items
                        favouritesAdapter.notifyItemChanged(position)
                    }

                    override fun onFailure(msg: String?) {
                        isClicked = false
                        //     Utils.showSimpleMessage(homeActivity, msg!!).show()
                    }
                })
    }
}