package com.verkoop

import android.app.Application
import android.content.Context
import com.verkoop.utils.Loading
import com.verkoop.utils.Utils
import okhttp3.internal.Internal.instance

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

    companion object {
        lateinit var instance: VerkoopApplication
        operator fun get(context: Context): VerkoopApplication {
            return context.applicationContext as VerkoopApplication
        }
    }
}