package com.verkoop.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.squareup.picasso.Picasso
import com.verkoop.R
import com.verkoop.activity.ProductDetailsActivity
import com.verkoop.fragment.ProfileFragment
import com.verkoop.models.Item
import com.verkoop.utils.AppConstants
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.my_profile_row.*

class MyProfileItemAdapter(private val context: Context, private val myItemsList: ArrayList<Item>, private val llProfileParent: LinearLayout, private val profileFragment: ProfileFragment) : RecyclerView.Adapter<MyProfileItemAdapter.ViewHolder>() {
    private var mInflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var likeDisLikeListener: LikeDisLikeListener
    private var width=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.my_profile_row, parent, false)
        val params = view.layoutParams
        params.width = llProfileParent.width / 2
        width= params.width
        likeDisLikeListener=profileFragment
     //   params.height = (params.width)+(params.width/4)
        view.layoutParams = params
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
           ivProductImage.layoutParams.height =width-16
            if(position %2==0){
                llSideDividerProfile.visibility= View.VISIBLE
            }else{
                llSideDividerProfile.visibility= View.GONE
            }
            if(data.item_type==1){
                tvConditionProfile.text="New"
            }else{
                tvConditionProfile.text=context.getString(R.string.used)
            }
            if (data.items_images.isNotEmpty()) {
                Picasso.with(context).load(AppConstants.IMAGE_URL+data.items_images[0].url)
                        .resize(720, 720)
                        .centerInside()
                        .error(R.mipmap.setting)
                        .into(ivProductImage)
            }

            tvNameProfile.text=data.name
            tvItemPriceProfile.text="$"+data.price
            itemView.setOnClickListener {
                likeDisLikeListener.getItemDetailsClick(data.id)

            }
            tvLikes.setOnClickListener {
              /*  if(data.likes.isNotEmpty()){

                }else{

                }
                likeDisLikeListener.getLikeDisLikeClick(,1)*/
            }
        }
    }
    interface LikeDisLikeListener{
        fun getLikeDisLikeClick(type:Int,position:Int)
        fun getItemDetailsClick(itemId:Int)
    }
}