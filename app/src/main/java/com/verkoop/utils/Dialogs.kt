package com.verkoop.utils

import android.content.Context

import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*

import com.verkoop.R
import kotlinx.android.synthetic.main.add_details_activity.*
import kotlinx.android.synthetic.main.chat_activity.*
import kotlinx.android.synthetic.main.delete_comment_dialog.*
import kotlinx.android.synthetic.main.dialog_answer.*
import kotlinx.android.synthetic.main.dialog_create_offer.*
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
interface MakeOfferListener{
    fun makeOfferClick(offerPrice:Double)
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

class ResumeLocationDialog(context: Context, private val listener:SelectionListener)
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

class SelectOptionDialog(context: Context, private val listener:SelectionOptionListener)
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
        if(header.equals(context.getString(R.string.confirm_accpt_offer),ignoreCase = true)||header.equals(context.getString(R.string.cancel_offer),ignoreCase = true)){
            flParentDel.background=ContextCompat.getDrawable(context,R.drawable.white_rectangular_shape)
        }
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

class CreatOfferDialog(private val realPrice:Double,context: Context, private val listener:MakeOfferListener)
    :android.app.Dialog(context){
    var isFocus:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_create_offer)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.BOTTOM)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        etTotalPrice.setText(realPrice.toString())
        etTotalPrice.setSelection(etTotalPrice.text.length)
        llMakeOffer.setOnClickListener {
            listener.makeOfferClick((etTotalPrice.text.toString()).toDouble())
            dismiss()
        }

    }

    fun showDialog(type: Int) {
        if(type==1){
            llMakeOffer.visibility=View.GONE
        }else{
            llMakeOffer.visibility=View.VISIBLE
            makeCalculation()
        }
    }

    private fun makeCalculation() {
        val enteredPrice=etTotalPrice.text.toString()
        val sixtyPercent=realPrice*.6
        if(!TextUtils.isEmpty(enteredPrice)&&enteredPrice.toDouble()>=sixtyPercent){
            llMakeOffer.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
            llMakeOffer.isEnabled=true
        }else{
            llMakeOffer.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray))
            llMakeOffer.isEnabled=false
        }
    }

}

class WarningDialog(context: Context, private val header:String, private val categoryType: String , private val listener:SharePostListener)
    :android.app.Dialog(context){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.warning_dialog)
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