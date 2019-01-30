package com.verkoop.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.verkoop.R

import kotlinx.android.synthetic.main.item_details_activity.*


class ProductDetailsActivity:AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_details_activity)
        setData()
    }
    private fun setData() {
        custom_indicator_detail.setDefaultIndicatorColor(ContextCompat.getColor(this, R.color.white), ContextCompat.getColor(this, R.color.light_gray))
        mDemoSliderDetails.setCustomIndicator(custom_indicator_detail)
        val imageList = ArrayList<Int>()
        imageList.add(R.mipmap.pic_1)
        imageList.add(R.mipmap.pic_1)
        imageList.add(R.mipmap.pic_1)
        imageList.add(R.mipmap.pic_1)

        for (i in 0 until imageList.size) {
            val textSliderView = DefaultSliderView(this)
            // initialize a SliderLayout
            textSliderView.image(imageList[i])
                    .setOnSliderClickListener({ slider -> }).scaleType = BaseSliderView.ScaleType.Fit
            mDemoSliderDetails.addSlider(textSliderView)
        }
      //  mDemoSliderDetails.setDuration(0)
        mDemoSliderDetails.stopAutoCycle()

    }

}