package com.verkoopapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.github.nkzawa.socketio.client.Ack
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.verkoopapp.R
import com.verkoopapp.VerkoopApplication
import com.verkoopapp.adapter.HomePagerAdapter
import com.verkoopapp.fragment.ActivitiesFragment
import com.verkoopapp.fragment.HomeFragment
import com.verkoopapp.fragment.ProfileFragment
import com.verkoopapp.models.SocketCheckConnectionEvent
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.home_activity.*
import kotlinx.android.synthetic.main.toolbar_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject


class HomeActivity : AppCompatActivity() {
    private var homeFragment: HomeFragment? = null
    private var profileFragment: ProfileFragment? = null
    private var activitiesFragment: ActivitiesFragment? = null
    private var fragmentList = ArrayList<Fragment>()
    private var doubleBackToExitPressedOnce = false
    private var comingFrom: Int = 0
    private var socket: Socket? = VerkoopApplication.getAppSocket()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        comingFrom = intent.getIntExtra(AppConstants.COMING_FROM, 0)
        homeFragment = HomeFragment.newInstance()
        profileFragment = ProfileFragment.newInstance()
        activitiesFragment = ActivitiesFragment.newInstance()
        fragmentList.add(homeFragment!!)
        fragmentList.add(activitiesFragment!!)
        fragmentList.add(profileFragment!!)
        setData()
        callInit()
        val picOption=intent.getIntExtra(AppConstants.PICK_OPTION,0)
        if(picOption==1){
            val intent = Intent(this, GalleryActivity::class.java)
            startActivityForResult(intent, 2)
        }
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
            R.id.menu_button1 -> viewPager.currentItem = 0
            R.id.menu_button2 -> viewPager.currentItem = 1
            R.id.menu_button3 -> viewPager.currentItem = 2
        }
    }

    private fun setData() {
        val adapter = HomePagerAdapter(supportFragmentManager, 3, fragmentList)
        viewPager.adapter = adapter
        // viewPager.offscreenPageLimit = 2
        setTabLayout()
        ivChat.setOnClickListener {
            val intent = Intent(this, ChatInboxActivity::class.java)
            startActivity(intent)
        }
        ivFavourite.setOnClickListener {
            val intent = Intent(this, FavouritesActivity::class.java)
            startActivity(intent)
        }
        if (comingFrom == 1) {
            when {
                viewPager.currentItem == 0 -> {
                    bottomTabLayout.selectTab(R.id.menu_button3)
                    viewPager.currentItem = 3
                    profileFragment!!.refreshUI(0)
                }
                viewPager.currentItem == 1 -> {
                    bottomTabLayout.selectTab(R.id.menu_button3)
                    viewPager.currentItem = 3
                }
                else -> {
                    profileFragment!!.refreshUI(1)
                }
            }
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getIntExtra(AppConstants.TRANSACTION, 0)
                if (result == 1) {
                    when {
                        viewPager.currentItem == 0 -> {
                            bottomTabLayout.selectTab(R.id.menu_button3)
                            viewPager.currentItem = 3
                            profileFragment!!.refreshUI(0)

                        }
                        viewPager.currentItem == 1 -> {
                            bottomTabLayout.selectTab(R.id.menu_button3)
                            viewPager.currentItem = 3
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: SocketCheckConnectionEvent) {
        /* Do something */
        callInit()

    }

    private fun callInit() {
        socket?.emit(AppConstants.INIT_USER_ID, getObj(), Ack {
            Log.e("<<<ACKRESPONSE--5>>>", Gson().toJson(it[0]))
        })

    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
    private fun getObj(): Any {
        val jsonObject: JSONObject?
        jsonObject = JSONObject()
        try {
            jsonObject.put("user_id", Utils.getPreferencesString(applicationContext,AppConstants.USER_ID).toInt())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }
}