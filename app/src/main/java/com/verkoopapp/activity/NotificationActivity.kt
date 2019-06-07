package com.verkoopapp.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.verkoopapp.R
import kotlinx.android.synthetic.main.notifizcatin_fragment.*
import kotlinx.android.synthetic.main.toolbar_location.*

class NotificationActivity:AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notifizcatin_fragment)
        setData()
    }

    private fun setData() {
        tvHeaderLoc.text = getString(R.string.notifications)
        ivLeftLocation.setOnClickListener {
            onBackPressed()
        }
        /*  if(AppPreferences.is_Push==1){
            switchNotification.setChecked(true,false )
        }else{
            switchNotification.setChecked(false,false )
        }
*/
        switchNotification.setOnStateChangeListener { process, state, jtb ->
            if (state == com.nightonke.jellytogglebutton.State.LEFT) {
                //allCountRequest(0)
            }
            if (state == com.nightonke.jellytogglebutton.State.RIGHT) {
                // allCountRequest(1)
            }
        }
    }

}