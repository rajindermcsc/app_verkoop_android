package com.verkoop.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.verkoop.R
import kotlinx.android.synthetic.main.dialog_answer.*


interface SharePostListener{
    fun onWhatAppClick()
    fun onFacebookClick()
    fun onShareClick()
}


class ShareDialog(context: Context, private val header:String, private val categoryType: String , private val listener:SharePostListener)
    :android.app.Dialog(context){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_answer)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        setCancelable(false)

        ivFinishDialog.setOnClickListener {
            dismiss()
        }
        ivWhatAppShareDialog.setOnClickListener {
            listener.onWhatAppClick()
            dismiss()
        }
        tvFacebookShareDialog.setOnClickListener {
            listener.onFacebookClick()
            dismiss()
        }
        tvShareDialog.setOnClickListener {
            listener.onShareClick()
            dismiss()
        }
    }
}