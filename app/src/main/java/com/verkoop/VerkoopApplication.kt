package com.verkoop

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.verkoop.utils.Loading
import org.acra.ACRA
import org.acra.ReportingInteractionMode
import org.acra.annotation.ReportsCrashes

@ReportsCrashes(mailTo = "anmol@mobilecoderz.com", mode = ReportingInteractionMode.TOAST, resToastText = R.string.application_crash)// my email here

class VerkoopApplication : Application() {
     private var loadDialog: Loading? = null
    val loader: Loading
        get() {
            if (loadDialog == null)
                loadDialog = Loading()
            return loadDialog!!
        }

     override fun onCreate() {
         super.onCreate()
         instance = this
     }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
        ACRA.init(this)

    }
    companion object {
        lateinit var instance: VerkoopApplication
        operator fun get(context: Context): VerkoopApplication {
            return context.applicationContext as VerkoopApplication
        }
    }
}