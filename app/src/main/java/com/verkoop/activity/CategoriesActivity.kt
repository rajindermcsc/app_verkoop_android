package com.verkoop.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.verkoop.R
import com.verkoop.fragment.FirstCategoryFragment
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.categories_screen.*

class CategoriesActivity:AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.categories_screen)
        setAdapter()
    }

    private fun setAdapter() {
        val mAdapter = PicturePreViewAdapter(supportFragmentManager)
        vpCategories.adapter = mAdapter
        vpCategories.offscreenPageLimit=3
        vpCategories.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                highlightIndicator(position)
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        tvNext.setOnClickListener {
            Utils.savePreferencesString(this,AppConstants.COMING_FROM,"category_screen")
            val intent = Intent(this@CategoriesActivity, PickOptionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        tvSkip.setOnClickListener {
            Utils.savePreferencesString(this,AppConstants.COMING_FROM,"category_screen")
            val intent = Intent(this@CategoriesActivity, PickOptionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    internal inner class PicturePreViewAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> {
                    return FirstCategoryFragment.newInstance(position)
                }
                1 -> {
                    return FirstCategoryFragment.newInstance(position)
                }
                else -> {
                    return FirstCategoryFragment.newInstance(position)
                }
            }

        }

        override fun getCount(): Int {
            return 3
        }
    }

    private fun highlightIndicator(count: Int) {
        when (count) {
            0 -> {
                setGrayBackground()
                ivIndicatorFirst.setImageResource(R.mipmap.dot_1)
            }
            1 -> {
                setGrayBackground()
                ivIndicatorSecond.setImageResource(R.mipmap.dot_1)
            }
            2 -> {
                setGrayBackground()
                ivIndicatorThird.setImageResource(R.mipmap.dot_1)
            }
        }
    }

    private fun setGrayBackground() {
        ivIndicatorFirst.setImageResource(R.mipmap.dot_2)
        ivIndicatorSecond.setImageResource(R.mipmap.dot_2)
        ivIndicatorThird.setImageResource(R.mipmap.dot_2)
    }
}