package com.verkoop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoop.R
import com.verkoop.models.DataWallet
import com.verkoop.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.row_payment_history.*

class PaymentHistoryAdapter(context: Context) : RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var dataList = ArrayList<DataWallet>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_payment_history, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modal = dataList[position]
        holder.build(modal)
    }

    inner class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun build(modal: DataWallet) {
            tvPriceWallet.text = StringBuilder().append("$").append(modal.amount)
            tvData.text = StringBuilder().append(Utils.getDateDifferenceDetails(modal.created_at)).append(" ").append("ago")
        }
    }

    fun setData(responseWalletList: ArrayList<DataWallet>) {
        dataList = responseWalletList
    }
}