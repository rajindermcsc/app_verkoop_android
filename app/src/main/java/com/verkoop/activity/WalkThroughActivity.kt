package com.verkoop.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.verkoop.R

import kotlinx.android.synthetic.main.walk_through_activity.*

/**
 * Created by intel on 22-11-2018.
 */
class WalkThroughActivity:AppCompatActivity(){
    private val mImageResources = intArrayOf(R.mipmap.walkthrough, R.mipmap.walkthrough_2, R.mipmap.walkthrough_3)
    private var vpPosition:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.walk_through_activity)
        setAdapter()
    }

    private fun setAdapter() {
       val mAdapter = PicturePreViewAdapter(this, mImageResources)
        vpWalkThrough.adapter = mAdapter
        vpWalkThrough.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                highlightIndicator(position)
                vpPosition=position
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        tvNextW.setOnClickListener {
            if(tvNextW.text.toString().equals("NEXT",ignoreCase = true)){
                vpWalkThrough.currentItem = vpPosition+1
            //    vpWalkThrough.currentItem = vpPosition
            }else{
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        tvSkipW.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun highlightIndicator(count: Int) {
        when (count) {
            0 -> {
                setGrayBackground()
                ivIndicatorFirstW.setImageResource(R.mipmap.dot_1)
                tvNextW.text=getString(R.string.next)
                tvSkipW.visibility=View.VISIBLE
            }
            1 -> {
                setGrayBackground()
                ivIndicatorSecondW.setImageResource(R.mipmap.dot_1)
                tvNextW.text=getString(R.string.next)
                tvSkipW.visibility=View.VISIBLE
            }
            2 -> {
                setGrayBackground()
                ivIndicatorThirdW.setImageResource(R.mipmap.dot_1)
                tvNextW.text=getString(R.string.done)
                tvSkipW.visibility=View.INVISIBLE
            }
        }
    }

    private fun setGrayBackground() {
        ivIndicatorFirstW.setImageResource(R.mipmap.dot_2)
        ivIndicatorSecondW.setImageResource(R.mipmap.dot_2)
        ivIndicatorThirdW.setImageResource(R.mipmap.dot_2)
    }
    class PicturePreViewAdapter(private val mContext: Context, private var mImageResources: IntArray) : PagerAdapter() {
       // private val mImageResources = intArrayOf(R.mipmap.walkthrough, R.mipmap.walkthrough_2, R.mipmap.walkthrough_3)
        var mLayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false)
            val imageView = itemView.findViewById(R.id.ivPagerItem) as ImageView
            imageView.setImageResource(mImageResources[position])
            container.addView(itemView)
            return itemView
        }

        override fun getCount(): Int {
            return 3
        }


        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as FrameLayout)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}