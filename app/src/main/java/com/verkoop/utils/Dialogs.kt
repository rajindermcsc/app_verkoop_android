package com.verkoop.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.verkoop.R
import kotlinx.android.synthetic.main.delete_comment_dialog.*
import kotlinx.android.synthetic.main.delete_comment_dialog.view.*
import kotlinx.android.synthetic.main.dialog_answer.*
import kotlinx.android.synthetic.main.dialog_select_met_up.*
import kotlinx.android.synthetic.main.select_option_dialoog.*


interface SharePostListener{
    fun onWhatAppClick()
    fun onFacebookClick()
    fun onShareClick()
}
interface SelectionListener{
    fun leaveClick()
}
interface SelectionOptionListener{
    fun leaveClick(option:String)
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

class resumeLocationDialog(context: Context, private val listener:SelectionListener)
    :android.app.Dialog(context){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_select_met_up)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        setCancelable(false)

        tvResume.setOnClickListener {
            dismiss()
        }
        tvLeave.setOnClickListener {
            listener.leaveClick()
            dismiss()
        }
    }
}

class selectOptionDialog(context: Context, private val listener:SelectionOptionListener)
    :android.app.Dialog(context){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.select_option_dialoog)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        setCancelable(false)

        llCamera.setOnClickListener {
            listener.leaveClick(context.getString(R.string.camera))
            dismiss()
        }
        tvGallery.setOnClickListener {
            listener.leaveClick(context.getString(R.string.gallery))
            dismiss()
        }
        llCancel.setOnClickListener {
            dismiss()
        }
    }

    class DeleteCommentDialog(context: Context,private val header:String,private val description:String, private val listener:SelectionListener)
        :android.app.Dialog(context){
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.delete_comment_dialog)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            setCanceledOnTouchOutside(true)
            setCancelable(true)
            tvHeaderDel.text=header
            tvDescriptionDel.text=description
            tvLeaveDelete.setOnClickListener {
                listener.leaveClick()
                dismiss()
            }
            tvNo.setOnClickListener {
                dismiss()
            }
        }
    }
}