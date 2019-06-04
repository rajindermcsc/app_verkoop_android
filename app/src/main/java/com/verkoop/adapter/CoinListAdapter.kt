package com.verkoop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoop.R
import com.verkoop.activity.CoinsActivity
import com.verkoop.fragment.GetCoinsFragment
import com.verkoop.models.CoinPlan
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.coin_row.*


class CoinListAdapter(private val context: Context, private val rvCoinList: RecyclerView,private val  getCoinsFragment: GetCoinsFragment):RecyclerView.Adapter<CoinListAdapter.ViewHolder>(){
    private val minflater:LayoutInflater=LayoutInflater.from(context)
    private var coinPlanList=ArrayList<CoinPlan>()
    private lateinit var purchaseCoinCallBack:PurchaseCoinCallBack

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
       val view=minflater.inflate(R.layout.coin_row,parent,false)
        purchaseCoinCallBack=getCoinsFragment
        /*val params = rvCoinList.layoutParams
        params.width = rvCoinList.width / 3
        params.height = params.width
        view.layoutParams = params*/
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return coinPlanList.size
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val modal=coinPlanList[position]
        holder.bind(modal)
    }

    inner class ViewHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView!!),LayoutContainer{
        fun bind(modal: CoinPlan) {
            tvCoins.text=modal.coin.toString()
            tvPriceCoins.text= StringBuilder().append(context.getString(R.string.dollar)).append(modal.amount)
            itemView.setOnClickListener {
            purchaseCoinCallBack.purchaseCoin(modal.id,adapterPosition,modal.amount,modal.coin)
            }
        }

    }

    fun setData(data: ArrayList<CoinPlan>) {
        coinPlanList=data
    }

    interface PurchaseCoinCallBack{
        fun purchaseCoin(coinPlanId:Int,position: Int,price:Int,totalCoin:Int)
    }

}