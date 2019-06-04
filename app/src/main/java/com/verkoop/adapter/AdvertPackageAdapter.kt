package com.verkoop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoop.R

import com.verkoop.models.DataAdvert
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.coin_row.*
import android.app.Activity
import android.content.Intent
import com.verkoop.activity.AdvertPackagesActivity
import com.verkoop.utils.AppConstants


class AdvertPackageAdapter(private val context: Context,private val rvCoinList: RecyclerView):RecyclerView.Adapter<AdvertPackageAdapter.ViewHolder>(){
    private  var mInflater: LayoutInflater= LayoutInflater.from(context)
    private var advertList= ArrayList<DataAdvert>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val view=mInflater.inflate(R.layout.coin_row,parent,false)
        val params = rvCoinList.layoutParams
        params.width = rvCoinList.width / 3
        params.height = params.width
        view.layoutParams = params
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return advertList.size
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val modal=advertList[position]
        holder.bind(modal)

    }

    inner class ViewHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView!!),LayoutContainer{
        fun bind(modal: DataAdvert) {
            tvCoins.text=modal.name
            tvPriceCoins.text= StringBuilder().append(modal.coin).append(" ").append(context.getString(R.string.coins))
            itemView.setOnClickListener {
                val returnIntent = Intent()
                returnIntent.putExtra(AppConstants.INTENT_RESULT, modal.id)
                (context as AdvertPackagesActivity).setResult(Activity.RESULT_OK, returnIntent)
                context .finish()
            }
        }
    }

    fun setData(data: ArrayList<DataAdvert>) {
        advertList=data
    }
}