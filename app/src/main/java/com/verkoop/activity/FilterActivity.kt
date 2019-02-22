package com.verkoop.activity

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.verkoop.R
import com.verkoop.adapter.FilterAdapter
import kotlinx.android.synthetic.main.filter_activity.*
import kotlinx.android.synthetic.main.toolbar_filter.*
import com.verkoop.utils.Utils


class FilterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filter_activity)
        setSelect()
        setData()
        setItemList()

    }

    private fun setSelect() {
        ivNew.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.new_active))
        tvNew.setTextColor(ContextCompat.getColor(this, R.color.white))
        llNew.background = ContextCompat.getDrawable(this, R.drawable.red_rectangle_shape)
    }

    private fun setData() {
        val font = Typeface.createFromAsset(assets, "fonts/gothic.ttf")
        rbNearBy.typeface = font
        rbPopular.typeface = font
        rbRecentlyAdded.typeface = font
        rbPriceHigh.typeface = font
        rbPriceLow.typeface = font
        iv_leftGallery.setOnClickListener { onBackPressed() }
        llNew.setOnClickListener {
            setSelection()
            setSelect()
        }
        llUsed.setOnClickListener {
            setSelection()
            ivUsed.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.used_active))
            tvUsed.setTextColor(ContextCompat.getColor(this, R.color.white))
            llUsed.background = ContextCompat.getDrawable(this, R.drawable.red_rectangle_shape)
        }
        rbGroup.setOnCheckedChangeListener({ group, checkedId ->
            if(checkedId==R.id.rbNearBy){
                Utils.showToast(this,"rbNearBy")
            }else if(checkedId==R.id.rbPopular){
                Utils.showToast(this,"rbPopular")
            }
        })
    }

    private fun setSelection() {
        ivNew.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.new_inactive))
        tvNew.setTextColor(ContextCompat.getColor(this, R.color.gray_light))
        llNew.background = ContextCompat.getDrawable(this, R.drawable.item_type)
        ivUsed.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.used_inactive))
        tvUsed.setTextColor(ContextCompat.getColor(this, R.color.gray_light))
        llUsed.background = ContextCompat.getDrawable(this, R.drawable.item_type)
    }

    private fun setItemList() {
        val mManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvFilter.layoutManager = mManager
        val filterAdapter = FilterAdapter(this)
        rvFilter.adapter = filterAdapter
    }
}