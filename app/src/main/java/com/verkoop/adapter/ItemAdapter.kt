package com.verkoop.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.verkoop.R
import com.verkoop.activity.ProductDetailsActivity
import com.verkoop.models.CategoryModal
import com.verkoop.models.ItemHome
import com.verkoop.utils.AppConstants
import com.verkoop.utils.NonscrollRecylerview
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_row.*
import kotlinx.android.synthetic.main.my_profile_row.*


class ItemAdapter(private val context: Context,private val CategoryModal:ArrayList<CategoryModal>) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    private var mInflater: LayoutInflater = LayoutInflater.from(context)
    private var itemsList=ArrayList<ItemHome>()
    private var width=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.item_row, parent, false)
        val params = view.layoutParams
        //   params.height = (params.width)+(params.width/4)
        view.layoutParams = params
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 9
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = itemsList[position]
       holder.bind(data,position)
    }

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind( data: ItemHome,position: Int) {
          /*  ivProductImageHome.layoutParams.height =width-16
            tvNameHome.text=data.user.username*/
            if(position %2==0){
                llSideDividerHome.visibility=View.VISIBLE
            }else{
                llSideDividerHome.visibility=View.GONE
            }
            if(data.item_type==1){
                tvConditionProfile.text="New"
            }else{
                tvConditionProfile.text=context.getString(R.string.used)
            }
           /* if (data.items_images.isNotEmpty()) {
                Picasso.with(context).load(AppConstants.IMAGE_URL+data.items_images[0].url)
                        .resize(720, 720)
                        .centerInside()
                        .error(R.mipmap.setting)
                        .into(ivProductImage)
            }*/

          /*  tvNameProfile.text=data.name
            tvItemPriceProfile.text="$"+data.price
                    itemView.setOnClickListener {
                val intent= Intent(context,ProductDetailsActivity::class.java)
                context.startActivity(intent)
            }*/
        }
    }

    fun setData(items: ArrayList<ItemHome>) {
        itemsList=items
    }
}