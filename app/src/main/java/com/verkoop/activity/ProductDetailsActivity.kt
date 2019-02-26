package com.verkoop.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.verkoop.R
import com.verkoop.VerkoopApplication
import com.verkoop.models.DataItems
import com.verkoop.models.ItemDetailsResponse
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils

import kotlinx.android.synthetic.main.item_details_activity.*
import kotlinx.android.synthetic.main.toolbar_product_details.*
import retrofit2.Response


class ProductDetailsActivity:AppCompatActivity(){
   private var imageURLLIst=ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_details_activity)
        if (Utils.isOnline(this)) {
            getItemDetailsService(intent.getIntExtra(AppConstants.ITEM_ID,0))
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }

    }

    private fun setData(imageURLLIst: ArrayList<String>, data: DataItems) {
        ivLeft.setOnClickListener { onBackPressed() }
        custom_indicator_detail.setDefaultIndicatorColor(ContextCompat.getColor(this, R.color.white), ContextCompat.getColor(this, R.color.light_gray))
        mDemoSliderDetails.setCustomIndicator(custom_indicator_detail)
        for (i in imageURLLIst.indices) {
            val textSliderView = DefaultSliderView(this)
            // initialize a SliderLayout
            textSliderView.image(AppConstants.IMAGE_URL+imageURLLIst[i]).setOnSliderClickListener({ slider ->
                    }).scaleType = BaseSliderView.ScaleType.Fit
            mDemoSliderDetails.addSlider(textSliderView)
        }
        mDemoSliderDetails.stopAutoCycle()
        tvProductName.text=data.name
        tvLikes.text=data.items_like_count.toString()
        tvPrice.text=StringBuilder().append(": ").append(getString(R.string.dollar)).append(data.price)
        tvDescription.text=data.description
        tvUserName.text=data.username
    }

    private fun getItemDetailsService(itemId: Int) {
        VerkoopApplication.instance.loader.show(this)
        ServiceHelper().getItemDetailService(itemId,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        VerkoopApplication.instance.loader.hide(this@ProductDetailsActivity)
                        val detailsResponse = response.body() as ItemDetailsResponse
                         for (i in detailsResponse.data.items_image.indices){
                             imageURLLIst.add(detailsResponse.data.items_image[i].url)
                         }
                        setData(imageURLLIst,detailsResponse.data)
                    }

                    override fun onFailure(msg: String?) {
                        VerkoopApplication.instance.loader.hide(this@ProductDetailsActivity)
                        Utils.showSimpleMessage(this@ProductDetailsActivity, msg!!).show()
                    }
                })
    }

}