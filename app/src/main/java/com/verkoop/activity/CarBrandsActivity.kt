package com.verkoop.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import com.verkoop.R
import com.verkoop.adapter.CarBrandsAdapter
import com.verkoop.adapter.FavouritesAdapter
import com.verkoop.models.Brand
import com.verkoop.models.ItemHome
import com.verkoop.utils.AppConstants
import kotlinx.android.synthetic.main.favourites_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*


class CarBrandsActivity:AppCompatActivity(){
    private  lateinit var linearLayoutManager:GridLayoutManager
    private lateinit var carBrandsAdapter: CarBrandsAdapter
    private var brandList= ArrayList<Brand>()
    private var comingFrom=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favourites_activity)
        brandList=intent.getParcelableArrayListExtra(AppConstants.CAR_BRAND_LIST)
        setData()
        setAdapter()
    }

    private fun setData() {
        ivLeftLocation.setOnClickListener { onBackPressed() }
        tvHeaderLoc.text=getString(R.string.car_brands)
    }

    private fun setAdapter() {
        linearLayoutManager = GridLayoutManager(this, 3)
        rvFavouriteList.layoutManager = linearLayoutManager
        carBrandsAdapter = CarBrandsAdapter(this, rvFavouriteList,brandList)
        rvFavouriteList.adapter = carBrandsAdapter
    }
}