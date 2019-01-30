package com.verkoop.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import com.verkoop.R
import com.verkoop.adapter.GalleryAdapter
import kotlinx.android.synthetic.main.gallery_activity.*
import kotlinx.android.synthetic.main.toolbar_filter.*

class GalleryActivity :AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_activity)
        setData()
        setAdapter()

    }

    private fun setAdapter() {
        val linearLayoutManager = GridLayoutManager(this, 3)
        rvGallery.layoutManager = linearLayoutManager
        val itemAdapter = GalleryAdapter(this)
        rvGallery.adapter = itemAdapter
    }

    private fun setData() {
        iv_leftGallery.setOnClickListener {onBackPressed()  }
        tvChatGallery.setOnClickListener {  }
        tvHeaderGallery.text=getString(R.string.photos)
        tvChatGallery.text=getString(R.string.next_s)

    }
}