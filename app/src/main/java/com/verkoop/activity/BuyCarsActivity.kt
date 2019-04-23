package com.verkoop.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.verkoop.LikeDisLikeListener
import com.verkoop.R
import com.verkoop.adapter.BuyCarsAdapter
import kotlinx.android.synthetic.main.buy_cars_activity.*


class BuyCarsActivity:AppCompatActivity(), LikeDisLikeListener {

    override fun getLikeDisLikeClick(type: Boolean, position: Int, lickedId: Int, itemId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemDetailsClick(itemId: Int, userId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var linearLayoutManager: GridLayoutManager
    private lateinit var buyCarsAdapter:BuyCarsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.buy_cars_activity)
        setData()
        setBuyCarAdapter()
    }

    private fun setData() {
        tvSellCar.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)
             startActivityForResult(intent, 2)
        }
    }

    private fun setBuyCarAdapter() {
            linearLayoutManager =  GridLayoutManager(this,2 )
            linearLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (buyCarsAdapter.getItemViewType(position)) {
                        buyCarsAdapter.CATEGORY_LIST_ROW -> 2
                        else -> 1
                    }
                }
            }
        rvBuyCarList.layoutManager = linearLayoutManager
        rvBuyCarList.setHasFixedSize(false)
        buyCarsAdapter = BuyCarsAdapter(this, rvBuyCarList)
        rvBuyCarList.adapter = buyCarsAdapter
        rvBuyCarList.addOnScrollListener(recyclerViewOnScrollListener)

    }
    private val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount = linearLayoutManager.findLastCompletelyVisibleItemPosition()
            val totalItemCount = linearLayoutManager.itemCount
            val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()

            /*if (!isLoading && currentPage != totalPageCount) {
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= MifareUltralight.PAGE_SIZE) {
                    currentPage += 1
                    getItemService()
                }
            }*/
        }
    }

}