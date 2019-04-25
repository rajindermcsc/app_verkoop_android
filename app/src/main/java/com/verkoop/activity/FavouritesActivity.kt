package com.verkoop.activity

import android.app.Activity
import android.content.Intent
import android.nfc.tech.MifareUltralight
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.verkoop.LikeDisLikeListener
import com.verkoop.R
import com.verkoop.adapter.FavouritesAdapter
import com.verkoop.models.*
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.KeyboardUtil
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.favourites_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import retrofit2.Response


class FavouritesActivity:AppCompatActivity(), LikeDisLikeListener {
    private var isClicked: Boolean = false
    private  lateinit var linearLayoutManager:GridLayoutManager
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

    override fun getItemDetailsClick(itemId: Int,adapterPosition:Int) {
        val intent = Intent(this, ProductDetailsActivity::class.java)
        intent.putExtra(AppConstants.ITEM_ID, itemId)
        intent.putExtra(AppConstants.ADAPTER_POSITION, adapterPosition)
        startActivityForResult(intent,3)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favourites_activity)
        setAdapter()
        if (Utils.isOnline(this)) {
            getFavouriteApi()
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }

    }


    private fun setAdapter() {
         linearLayoutManager = GridLayoutManager(this, 2)
        rvFavouriteList.layoutManager = linearLayoutManager
        favouritesAdapter = FavouritesAdapter(this, rvFavouriteList)
        rvFavouriteList.adapter = favouritesAdapter
      //  rvFavouriteList.addOnScrollListener(recyclerViewOnScrollListener)
        ivLeftLocation.setOnClickListener { onBackPressed() }
        tvHeaderLoc.text=getString(R.string.favourites)
    }



/*private val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val lastCompletelyVisibleItemPosition = (recyclerView!!.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        val visibleItemCount = linearLayoutManager.findLastCompletelyVisibleItemPosition();
        val totalItemCount = linearLayoutManager.itemCount
        val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
     //   Log.e("LastVisibleCount", visibleItemCount.toString())
      //  Log.e("LastVisibleCount2", lastCompletelyVisibleItemPosition.toString())


    }
}*/

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

                        }else{
                            Utils.showSimpleMessage(this@FavouritesActivity, "No data found.").show()
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
                        itemsList[position].is_like=!itemsList[position].is_like
                        itemsList[position].items_like_count= itemsList[position].items_like_count+1
                        itemsList[position].like_id= responseLike.like_id
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
                        itemsList[position].is_like=!itemsList[position].is_like
                        itemsList[position].items_like_count= itemsList[position].items_like_count-1
                        itemsList[position].like_id= 0
                        favouritesAdapter.notifyItemChanged(position)
                    }

                    override fun onFailure(msg: String?) {
                        isClicked = false
                        //     Utils.showSimpleMessage(homeActivity, msg!!).show()
                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                val adapterPosition = data!!.getIntExtra(AppConstants.ADAPTER_POSITION,0)
                if(data.getStringExtra(AppConstants.TYPE).equals("soldItem",ignoreCase = true)){
                    itemsList[adapterPosition].is_sold=1
                    favouritesAdapter.notifyDataSetChanged()
                }else if(data.getStringExtra(AppConstants.TYPE).equals("deleteItem",ignoreCase = true)){
                    itemsList.removeAt(adapterPosition)
                    favouritesAdapter.notifyDataSetChanged()
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult
}