package com.verkoop.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.verkoop.R
import com.verkoop.VerkoopApplication
import com.verkoop.adapter.CommentListAdapter
import com.verkoop.models.DataItems
import com.verkoop.models.ItemDetailsResponse
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils

import kotlinx.android.synthetic.main.item_details_activity.*
import kotlinx.android.synthetic.main.toolbar_product_details.*
import retrofit2.Response
import android.content.Intent
import android.app.Activity




class ProductDetailsActivity:AppCompatActivity(){
   private var imageURLLIst=ArrayList<String>()
    private lateinit var commentListAdapter: CommentListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_details_activity)
        setCommentAdapter()
        if(intent.getIntExtra(AppConstants.COMING_FROM,0)==1){
            tvSell.visibility= View.GONE
            ivRight.visibility= View.INVISIBLE
        }
        if (Utils.isOnline(this)) {
            getItemDetailsService(intent.getIntExtra(AppConstants.ITEM_ID,0))
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }

    private fun setCommentAdapter() {
        val mManager=LinearLayoutManager(this)
        rvPostCommentList.layoutManager=mManager
        commentListAdapter= CommentListAdapter(this)
        rvPostCommentList.adapter=commentListAdapter
    }

    private fun setData(imageURLLIst: ArrayList<String>, data: DataItems) {
        tvPostComment.setOnClickListener {
            if(data.id!=0) {
                val i = Intent(this, AddCommentActivity::class.java)
                i.putExtra(AppConstants.ITEM_ID, data.id)
                startActivityForResult(i, 1)
            }
        }
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
        tvDateDetails.text=StringBuilder().append(Utils.getDateDifferenceDetails(data.created_at)).append(" ").append("ago")
        tvDate.text=StringBuilder().append(Utils.getDateDifferenceDetails(data.created_at)).append(" ").append("ago")
        tvCategoryDetail.text=StringBuilder().append(": ").append(data.category_name)
        if(data.item_type==1){
            tvType.text="New"
        }else{
            tvType.text=getString(R.string.used)
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data.getStringExtra("result")
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}