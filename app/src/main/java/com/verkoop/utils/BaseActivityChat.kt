package com.verkoop.utils

import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.content.Intent
import com.ksmtrivia.common.BaseActivity
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.view.Window.ID_ANDROID_CONTENT
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import com.verkoop.R


/**
 * Created by intel on 06-05-2019.
 */
abstract class BaseActivityChat:AppCompatActivity(){

    private val keyboardLayoutListener = OnGlobalLayoutListener {
        val heightDiff = rootLayout!!.rootView.height - rootLayout!!.height
        val contentViewTop = window.findViewById<View>(Window.ID_ANDROID_CONTENT).getTop()

        val broadcastManager = LocalBroadcastManager.getInstance(this@BaseActivityChat)

        if (heightDiff <= contentViewTop) {
            onHideKeyboard()

            val intent = Intent("KeyboardWillHide")
            broadcastManager.sendBroadcast(intent)
        } else {
            val keyboardHeight = heightDiff - contentViewTop
            onShowKeyboard(keyboardHeight)

            val intent = Intent("KeyboardWillShow")
            intent.putExtra("KeyboardHeight", keyboardHeight)
            broadcastManager.sendBroadcast(intent)
        }
    }

    private var keyboardListenersAttached = false
    private var rootLayout: ViewGroup? = null

    abstract fun onShowKeyboard(keyboardHeight: Int)
    abstract fun onHideKeyboard()

    protected fun attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return
        }

        rootLayout = findViewById(R.id.rootLayout) as ViewGroup
        rootLayout!!.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)

        keyboardListenersAttached = true
    }

    override fun onDestroy() {
        super.onDestroy()

        if (keyboardListenersAttached) {
            rootLayout!!.viewTreeObserver.removeGlobalOnLayoutListener(keyboardLayoutListener)
        }
    }

}