package com.verkoopapp.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.verkoopapp.R
import com.verkoopapp.adapter.FavouritesAdapter
import com.verkoopapp.models.CarsFilterRequest
import com.verkoopapp.models.FavouritesResponse
import com.verkoopapp.models.ItemHome
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.favourites_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import retrofit2.Response


class CarsFilterActivity:AppCompatActivity(){
    private  lateinit var linearLayoutManager: GridLayoutManager
    private lateinit var favouritesAdapter: FavouritesAdapter
    private var itemsList=ArrayList<ItemHome>()
    private var comingFrom=0
    private var filter_id=0
    private var filterType=""
    private var carFilterRequest: CarsFilterRequest?=null

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favourites_activity)
       comingFrom=intent.getIntExtra(AppConstants.TYPE,0)
       filter_id=intent.getIntExtra(AppConstants.FILTER_ID,0)
       filterType=intent.getStringExtra(AppConstants.FILTER_TYPE)
       setAdapter()
       if(filterType.equals(getString(R.string.brand),ignoreCase = true)){
           carFilterRequest= CarsFilterRequest(comingFrom,filter_id)

       } else if(filterType.equals(getString(R.string.car_type),ignoreCase = true)){
           carFilterRequest= CarsFilterRequest(comingFrom,0,filter_id)

       }else if(filterType.equals(getString(R.string.cost_filter),ignoreCase = true)){
           carFilterRequest= CarsFilterRequest(comingFrom,0,0,filter_id)

       }else if(filterType.equals(getString(R.string.zone),ignoreCase = true)){
           carFilterRequest= CarsFilterRequest(comingFrom,0,0,0,filter_id)

       }
        if (Utils.isOnline(this)) {
            getFavouriteApi(carFilterRequest)
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }
    private fun setAdapter() {
        linearLayoutManager = GridLayoutManager(this, 2)
        rvFavouriteList.layoutManager = linearLayoutManager
        favouritesAdapter = FavouritesAdapter(this, rvFavouriteList,1)
        rvFavouriteList.adapter = favouritesAdapter
        //  rvFavouriteList.addOnScrollListener(recyclerViewOnScrollListener)
        ivLeftLocation.setOnClickListener { onBackPressed() }
        if(comingFrom==1) {
            tvHeaderLoc.text = getString(R.string.cars)
        }else{
            tvHeaderLoc.text = getString(R.string.properties)
        }
    }
    private fun getFavouriteApi(carFilterRequest: CarsFilterRequest?) {
        pbProgressFav.visibility= View.VISIBLE
        ServiceHelper().getCarFilterService(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), carFilterRequest!!,object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                pbProgressFav.visibility= View.GONE
                val responseFav = response.body() as FavouritesResponse
                if(responseFav.data!=null){
                    if (responseFav.data.items.isNotEmpty()) {
                        itemsList = responseFav.data.items
                        favouritesAdapter.setData(itemsList)
                        favouritesAdapter.notifyDataSetChanged()

                    }else{
                        Utils.showSimpleMessage(this@CarsFilterActivity, "No data found.").show()
                    }
                }


            }

            override fun onFailure(msg: String?) {
                pbProgressFav.visibility= View.GONE
                Utils.showSimpleMessage(this@CarsFilterActivity, msg!!).show()
            }
        })

    }
}