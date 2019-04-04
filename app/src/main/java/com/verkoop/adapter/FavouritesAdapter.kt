package com.verkoop.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.verkoop.LikeDisLikeListener
import com.verkoop.R
import com.verkoop.activity.FavouritesActivity
import com.verkoop.activity.UserProfileActivity
import com.verkoop.models.ItemHome
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_row.*


class FavouritesAdapter(private val context: Context,private val rvFavourites: RecyclerView) : RecyclerView.Adapter<FavouritesAdapter.ViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var likeDisLikeListener:LikeDisLikeListener
    private var listFavourites = ArrayList<ItemHome>()
    private var width=0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_row, parent, false)
        val params = view.layoutParams
        params.width = rvFavourites.width / 2
        width= params.width
        likeDisLikeListener=context as FavouritesActivity
        view.layoutParams = params
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listFavourites.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listFavourites[position]
        holder.bind(data)
    }

    inner class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(data: ItemHome) {
            ivProductImageHome.layoutParams.height =width-16
            tvNameHome.text=data.username
            tvProductHome.text=data.name
            if(adapterPosition %2==0){
                llSideDividerHome.visibility=View.VISIBLE
            }else{
                llSideDividerHome.visibility=View.GONE
            }
            if(data.item_type==1){
                tvConditionHome.text="New"
            }else{
                tvConditionHome.text=context.getString(R.string.used)
            }

            if(data.is_like){
                tvLikesHome.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.post_liked, 0, 0, 0)
            }else{
                tvLikesHome.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.post_like, 0, 0, 0)
            }
            tvLikesHome.text=data.items_like_count.toString()
            if(data.item_type==1){
                tvConditionHome.text="New"
            }else{
                tvConditionHome.text=context.getString(R.string.used)
            }
            if (!TextUtils.isEmpty(data.profile_pic)) {
                Picasso.with(context)
                        .load(AppConstants.IMAGE_URL + data.profile_pic)
                        .resize(720, 720)
                        .centerCrop()
                        .error(R.mipmap.pic_placeholder)
                        .placeholder(R.mipmap.pic_placeholder)
                        .into(ivPicProfile)

            } else {
                ivPicProfile.setImageResource(R.mipmap.pic_placeholder)
            }
            if(!TextUtils.isEmpty(data.image_url)) {
                Picasso.with(context).load(AppConstants.IMAGE_URL + data.image_url)
                        .resize(720, 720)
                        .centerCrop()
                        .error(R.mipmap.post_placeholder)
                        .placeholder(R.mipmap.post_placeholder)
                        .into(ivProductImageHome)

            }else{
                ivProductImageHome.setImageResource(R.mipmap.post_placeholder)
            }

            tvItemPriceHome.text="$"+data.price
            itemView.setOnClickListener {
                likeDisLikeListener.getItemDetailsClick(data.id)

            }
            tvLikesHome.setOnClickListener {
               likeDisLikeListener.getLikeDisLikeClick(data.is_like,adapterPosition,data.like_id,data.id)
            }
            tvPostOn.text = StringBuilder().append(Utils.getDateDifference(data.created_at.date)).append(" ").append("ago")
            llUserProfile.setOnClickListener {
                val reportIntent = Intent(context, UserProfileActivity::class.java)
                reportIntent.putExtra(AppConstants.USER_ID,data.user_id)
                reportIntent.putExtra(AppConstants.USER_NAME,data.username)
                context.startActivity(reportIntent)
            }
        }

    }

    fun setData(itemsList: ArrayList<ItemHome>) {
        listFavourites = itemsList
    }
}