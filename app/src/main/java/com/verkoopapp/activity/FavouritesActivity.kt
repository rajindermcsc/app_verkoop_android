package com.verkoopapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.verkoopapp.R
import com.verkoopapp.adapter.FavouritesAdapter
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.favourites_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import retrofit2.Response


class FavouritesActivity:AppCompatActivity() {
    private  lateinit var linearLayoutManager:GridLayoutManager
    private lateinit var favouritesAdapter: FavouritesAdapter
    private var itemsList=ArrayList<ItemHome>()
    private var comingFrom=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favourites_activity)
        comingFrom=intent.getIntExtra(AppConstants.COMING_FROM,0)
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
        favouritesAdapter = FavouritesAdapter(this, rvFavouriteList,0)
        rvFavouriteList.adapter = favouritesAdapter
      //  rvFavouriteList.addOnScrollListener(recyclerViewOnScrollListener)
        ivLeftLocation.setOnClickListener { onBackPressed() }
        if(comingFrom!=1) {
            tvHeaderLoc.text = getString(R.string.favourites)
        }else{
            tvHeaderLoc.text = getString(R.string.your_daily_picks)
        }
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