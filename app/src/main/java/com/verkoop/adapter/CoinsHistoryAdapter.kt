package com.verkoop.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.verkoop.R
import com.verkoop.models.DataHistory
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.coin_history_row.*
import kotlinx.android.synthetic.main.transfer_coin_dialog_activity.*


class CoinsHistoryAdapter(private val context:Context):RecyclerView.Adapter<CoinsHistoryAdapter.ViewHolder>(){
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var dataList = ArrayList<DataHistory>()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int):ViewHolder {
        val view = mInflater.inflate(R.layout.coin_history_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val modal = dataList[position]
        holder.build(modal)
    }
inner class ViewHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView!!),LayoutContainer{
    fun build(modal: DataHistory) {
        if (modal.type == 0) {
            ivUserPicCoins.visibility=View.GONE
            ivWallet.visibility=View.VISIBLE
            tvCoinHeader.text = context.getString(R.string.added_to_verkoop)
            tvNoCoin.setTextColor(ContextCompat.getColor(context, R.color.accept_offer))
            tvNoCoin.text = StringBuilder().append("$").append(modal.coin).append(" ").append(context.getString(R.string.coin))
        } else if (modal.type == 1) {
            ivUserPicCoins.visibility=View.GONE
            ivWallet.visibility=View.VISIBLE
            tvCoinHeader.text = context.getString(R.string.purchased_banner)
            tvNoCoin.setTextColor(ContextCompat.getColor(context, R.color.black_))
            tvNoCoin.text = StringBuilder().append("- ").append("$").append(modal.coin).append(" ").append(context.getString(R.string.coin))
        } else if (modal.type == 2) {
            ivUserPicCoins.visibility=View.VISIBLE
            ivWallet.visibility=View.GONE
            if (!TextUtils.isEmpty(modal.profilePic)) {
                Picasso.with(context).load(AppConstants.IMAGE_URL + modal.profilePic)
                        .resize(720, 720)
                        .centerInside()
                        .error(R.mipmap.pic_placeholder)
                        .placeholder(R.mipmap.pic_placeholder)
                        .into(ivUserPicSend)
            }
            tvCoinHeader.text =StringBuffer().append(context.getString(R.string.send_to)).append(" ").append(modal.userName)
            tvNoCoin.setTextColor(ContextCompat.getColor(context, R.color.black_))
            tvNoCoin.text = StringBuilder().append("- ").append("$").append(modal.coin).append(" ").append(context.getString(R.string.coin))
        }else{
            ivUserPicCoins.visibility=View.VISIBLE
            ivWallet.visibility=View.GONE
            if (!TextUtils.isEmpty(modal.profilePic)) {
                Picasso.with(context).load(AppConstants.IMAGE_URL + modal.profilePic)
                        .resize(720, 720)
                        .centerInside()
                        .error(R.mipmap.pic_placeholder)
                        .placeholder(R.mipmap.pic_placeholder)
                        .into(ivUserPicSend)
            }
            tvCoinHeader.text =StringBuffer().append(context.getString(R.string.received_from)).append(" ").append(modal.userName)
            tvNoCoin.setTextColor(ContextCompat.getColor(context, R.color.black_))
            tvNoCoin.text = StringBuilder().append("- ").append(modal.coin).append(" ").append(context.getString(R.string.coin))
        }
        if (!TextUtils.isEmpty(modal.created_at)) {
            tvDataCoins.text = StringBuilder().append(Utils.getDateDifferenceDetails(modal.created_at)).append(" ").append("ago")
        }
    }


}
    fun setData(responseWalletList: ArrayList<DataHistory>) {
        dataList = responseWalletList
    }
}