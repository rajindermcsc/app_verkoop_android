package com.verkoop.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.verkoop.R
import com.verkoop.adapter.HomePagerAdapter
import kotlinx.android.synthetic.main.home_activity.*
import kotlinx.android.synthetic.main.toolbar_home.*
import android.app.Activity
import android.support.v4.app.Fragment
import com.verkoop.fragment.ActivitiesFragment
import com.verkoop.fragment.HomeFragment
import com.verkoop.fragment.ProfileFragment
import com.verkoop.utils.AppConstants


class HomeActivity:AppCompatActivity(){
     private var homeFragment:HomeFragment? = null
     private var profileFragment:ProfileFragment? = null
     private var activitiesFragment:ActivitiesFragment? = null
    private var fragmentList=ArrayList<Fragment>()
    private var doubleBackToExitPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        homeFragment=HomeFragment.newInstance()
        profileFragment= ProfileFragment.newInstance()
        activitiesFragment= ActivitiesFragment.newInstance()
        fragmentList.add(homeFragment!!)
        fragmentList.add(activitiesFragment!!)
        fragmentList.add(profileFragment!!)
        setData()
    }

    private fun setTabLayout() {
        bottomTabLayout.setButtonTextStyle(R.style.TextGray12)
        // set buttons from menu resource
        bottomTabLayout.setItems(R.menu.menu_bottom_layout)
        //set on selected tab listener.
        bottomTabLayout.setListener { id ->
            switchFragment(id)
        }
       /* bottomTabLayout.setListener { position ->
            viewPager.currentItem = position
        }*/
        bottomTabLayout.setSelectedTab(R.id.menu_button1)
        //enable indicator
        bottomTabLayout.setIndicatorVisible(true)
        //indicator height
        bottomTabLayout.setIndicatorHeight(resources.getDimension(R.dimen.dp_2))
        //indicator color
        bottomTabLayout.setIndicatorColor(R.color.white)
        //indicator line color
        bottomTabLayout.setIndicatorLineColor(R.color.colorPrimary)
        //bottomTabLayout.setSelectedTab(R.id.menu_button5)
    }

    private fun switchFragment(id: Int) {
        when (id) {
            R.id.menu_button1 -> viewPager.currentItem=0
            R.id.menu_button2 -> viewPager.currentItem=1
            R.id.menu_button3 ->  viewPager.currentItem=2
        }
    }

    private fun setData() {
        val adapter = HomePagerAdapter(supportFragmentManager, 3,fragmentList)
        viewPager.adapter = adapter
       // viewPager.offscreenPageLimit = 2
        setTabLayout()
        ivChat.setOnClickListener {//logout()
        }
        ivFavourite.setOnClickListener {
          val intent=Intent(this,FavouritesActivity::class.java)
            startActivity(intent)
        }

    }

    private fun logout() {
        com.verkoop.utils.Utils.clearPreferences(this@HomeActivity)
        val intent = Intent(this@HomeActivity,WalkThroughActivity::class.java)
        startActivity(intent)
        finishAffinity()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data.getIntExtra(AppConstants.TRANSACTION,0)
                if(result==1){
                    when {
                        viewPager.currentItem==0 -> {
                            bottomTabLayout.selectTab(R.id.menu_button3)
                            viewPager.currentItem=3
                            profileFragment!!.refreshUI(0)

                        }
                        viewPager.currentItem==1 -> {
                            bottomTabLayout.selectTab(R.id.menu_button3)
                            viewPager.currentItem=3
                        }
                        else -> {
                            profileFragment!!.refreshUI(1)
                        }
                    }

                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}