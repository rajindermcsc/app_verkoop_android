package com.verkoop.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.verkoop.adapter.CategoryAdapter
import com.verkoop.R
import com.verkoop.VerkoopApplication
import com.verkoop.fragment.FirstCategoryFragment
import com.verkoop.models.CategoriesResponse
import com.verkoop.models.DataCategory
import com.verkoop.network.ServiceHelper
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.synthetic.main.categories_screen.*
import retrofit2.Response

class CategoriesActivity:AppCompatActivity(), CategoryAdapter.SelectedCategory {
    var selectionCount:Int=0
    var doubleBackToExitPressedOnce = false
    private val categoryList=ArrayList<DataCategory>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.categories_screen)
        callCategoriesApi()

    }

    override fun selectedCount(addItem: Int) {
        tvSelectionCount.text=" "+addItem.toString()+" / 3"
        selectionCount=addItem

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
           if(selectionCount==3){
               Utils.savePreferencesString(this,AppConstants.COMING_FROM,"category_screen")
               val intent = Intent(this@CategoriesActivity, PickOptionActivity::class.java)
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
               startActivity(intent)
           }else{
               Utils.showSimpleMessage(this, getString(R.string.select_three)).show()
           }

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
            return when (position) {
                0 -> {
                    FirstCategoryFragment.newInstance(position,categoryList)
                }
                1 -> {
                    FirstCategoryFragment.newInstance(position,categoryList)
                }
                else -> {
                    FirstCategoryFragment.newInstance(position,categoryList)
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
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finishAffinity()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
    /*private fun setData() {
        val nameList = arrayOf("Women's0", "men's","Footwear","Desktop's","Mobiles","Furniture","Pets","Car","Books","Women's1", "men's","Footwear","Desktop's","Mobiles","Furniture","Pets","Car","Books","Women's2", "men's","Footwear","Desktop's","Mobiles","Furniture","Pets","Car","Books")
        val imageList= arrayOf(R.mipmap.women_unselected,R.mipmap.men_unselected,R.mipmap.footwear_unselected,R.mipmap.desktop_unselected,R.mipmap.mobile_unselected,R.mipmap.furniture_unselected,R.mipmap.pet_unseleted,R.mipmap.car_unseleted,R.mipmap.books_unselected,R.mipmap.women_unselected,R.mipmap.men_unselected,R.mipmap.footwear_unselected,R.mipmap.desktop_unselected,R.mipmap.mobile_unselected,R.mipmap.furniture_unselected,R.mipmap.pet_unseleted,R.mipmap.car_unseleted,R.mipmap.books_unselected,R.mipmap.women_unselected,R.mipmap.men_unselected,R.mipmap.footwear_unselected,R.mipmap.desktop_unselected,R.mipmap.mobile_unselected,R.mipmap.furniture_unselected,R.mipmap.pet_unseleted,R.mipmap.car_unseleted,R.mipmap.books_unselected)
        val imageListSelected= arrayOf(R.mipmap.women_selected,R.mipmap.men_selected,R.mipmap.footwear_selected,R.mipmap.desktop_selected,R.mipmap.mobile_selected,R.mipmap.furniture_selected,R.mipmap.pet_selected,R.mipmap.car_selected,R.mipmap.books_selected,R.mipmap.women_selected,R.mipmap.men_selected,R.mipmap.footwear_selected,R.mipmap.desktop_selected,R.mipmap.mobile_selected,R.mipmap.furniture_selected,R.mipmap.pet_selected,R.mipmap.car_selected,R.mipmap.books_selected,R.mipmap.women_selected,R.mipmap.men_selected,R.mipmap.footwear_selected,R.mipmap.desktop_selected,R.mipmap.mobile_selected,R.mipmap.furniture_selected,R.mipmap.pet_selected,R.mipmap.car_selected,R.mipmap.books_selected)
        for (i in nameList.indices){
            val categoryModal=  CategoryModal(nameList[i],imageList[i],imageListSelected[i],false)
            categoryList.add(categoryModal)
        }
        setAdapter()
    }*/
    private fun callCategoriesApi() {
        VerkoopApplication.instance.loader.show(this)
        ServiceHelper().categoriesService(
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        VerkoopApplication.instance.loader.hide(this@CategoriesActivity)
                        val categoriesResponse = response.body() as CategoriesResponse
                        categoryList.addAll(categoriesResponse.data)
                        setAdapter()
                    }

                    override fun onFailure(msg: String?) {
                        VerkoopApplication.instance.loader.hide(this@CategoriesActivity)
                        Utils.showSimpleMessage(this@CategoriesActivity, msg!!).show()
                    }
                })
    }
}