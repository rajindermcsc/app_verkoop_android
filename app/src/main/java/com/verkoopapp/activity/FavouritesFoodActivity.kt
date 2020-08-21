package com.verkoopapp.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.verkoopapp.R
import com.verkoopapp.adapter.FavroutiesFoodAdapter
import com.verkoopapp.adapter.OngoingOrderAdapter
import kotlinx.android.synthetic.main.activity_favourites_food.*
import kotlinx.android.synthetic.main.fragment_ongoing_order.*
import kotlinx.android.synthetic.main.toolbar_location.*
import kotlinx.android.synthetic.main.toolbar_location.view.*

class FavouritesFoodActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites_food)
        tool_lyt.tvHeaderLoc.text = getString(R.string.favourites)
        tool_lyt.ivLeftLocation.setOnClickListener {
            finish()
        }

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvFavouriteFoodList.layoutManager = linearLayoutManager
        val favFoodAdapter = FavroutiesFoodAdapter(this)
        rvFavouriteFoodList.setHasFixedSize(true)
        rvFavouriteFoodList.adapter = favFoodAdapter
        rvFavouriteFoodList!!.adapter!!.notifyDataSetChanged()
    }

}