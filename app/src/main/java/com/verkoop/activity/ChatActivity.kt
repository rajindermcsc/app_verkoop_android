package com.verkoop.activity


import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.ksmtrivia.common.BaseActivity

import com.verkoop.R

import com.verkoop.utils.BaseActivityChat
import android.view.ViewTreeObserver




class ChatActivity: AppCompatActivity() {

   /* override fun onShowKeyboard(keyboardHeight: Int) {
      //  Log.e("KeyBoard Status",keyboardHeight.toString())

    }

    override fun onHideKeyboard() {
       // Log.e("KeyBoard Status","Hide")
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)
      //  attachKeyboardListeners()
        initKeyBoardListener()
    }


    private fun initKeyBoardListener() {
        // Минимальное значение клавиатуры.
        // Threshold for minimal keyboard height.
        val MIN_KEYBOARD_HEIGHT_PX = 150
        // Окно верхнего уровня view.
        // Top-level window decor view.
        val decorView = window.decorView
        // Регистрируем глобальный слушатель. Register global layout listener.
        decorView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            // Видимый прямоугольник внутри окна.
            // Retrieve visible rectangle inside window.
            private val windowVisibleDisplayFrame = Rect()
            private var lastVisibleDecorViewHeight: Int = 0

            override fun onGlobalLayout() {
                decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame)
                val visibleDecorViewHeight = windowVisibleDisplayFrame.height()

                if (lastVisibleDecorViewHeight != 0) {
                    if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {
                        Log.e("Pasha", "SHOW")

                    } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {
                        Log.e("Pasha", "HIDE")
                    }

                }
                // Сохраняем текущую высоту view до следующего вызова.
                // Save current decor view height for the next call.
                lastVisibleDecorViewHeight = visibleDecorViewHeight
            }
        })
    }

}