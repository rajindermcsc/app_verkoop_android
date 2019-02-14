package com.verkoop.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoop.R
import com.verkoop.activity.ProductDetailsActivity
import com.verkoop.models.Item
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_row.*

/**
 * Created by intel on 14-02-2019.
 */
class MyProfileItemAdapter(private val context: Context, private val myItemsList: ArrayList<Item>) : RecyclerView.Adapter<MyProfileItemAdapter.ViewHolder>() {
    private var mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.item_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return myItemsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = myItemsList[position]
        holder.bind(data,position)
    }

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(data: Item, position: Int) {
            if(position %2==0){
                llSideDivider.visibility= View.VISIBLE
            }else{
                llSideDivider.visibility= View.GONE
            }
            if(data.item_type==1){
                tvCondition.text="New"
            }else{
                tvCondition.text=context.getString(R.string.used)
            }
            tvUserName.text= Utils.getPreferencesString(context, AppConstants.USER_NAME)
            tvProductName.text=data.name
            tvItemPrice.text="$"+data.price
            itemView.setOnClickListener {
                val intent= Intent(context, ProductDetailsActivity::class.java)
                context.startActivity(intent)
            }
        }
    }
}