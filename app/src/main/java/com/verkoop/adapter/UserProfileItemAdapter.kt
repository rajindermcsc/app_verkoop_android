package com.verkoop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.squareup.picasso.Picasso
import com.verkoop.LikeDisLikeListener
import com.verkoop.R
import com.verkoop.activity.UserProfileActivity
import com.verkoop.models.ItemUserProfile
import com.verkoop.utils.AppConstants
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.my_profile_row.*


class UserProfileItemAdapter(private val context:Context,private val llProfileParent: LinearLayout):RecyclerView.Adapter<UserProfileItemAdapter.ViewHolder>(){
    private var myItemsList= ArrayList<ItemUserProfile>()
    private var mInflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var likeDisLikeListener: LikeDisLikeListener
    private var width=0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val view = mInflater.inflate(R.layout.my_profile_row, parent, false)
        val params = view.layoutParams
        params.width = llProfileParent.width / 2
        width= params.width
        view.layoutParams = params
        likeDisLikeListener=context as UserProfileActivity
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return myItemsList.size
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val data = myItemsList[position]
        holder.bind(data,position)
    }
inner class ViewHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView!!),LayoutContainer{

     fun bind(data: ItemUserProfile, position: Int) {
         ivProductImage.layoutParams.height =width-16
         if(position %2==0){
             llSideDividerProfile.visibility= View.VISIBLE
         }else{
             llSideDividerProfile.visibility= View.GONE
         }
         if(data.is_like){
             tvLikesProfile.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.post_liked, 0, 0, 0)
         }else{
             tvLikesProfile.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.post_like, 0, 0, 0)
         }
         tvLikesProfile.text=data.items_like_count.toString()
         if(data.item_type==1){
             tvConditionProfile.text="New"
         }else{
             tvConditionProfile.text=context.getString(R.string.used)
         }
         if(data.is_sold==1){
             tvSold.visibility=View.VISIBLE
         }else{
             tvSold.visibility=View.GONE
         }
         if(!TextUtils.isEmpty(data.image_url)) {
             Picasso.with(context).load(AppConstants.IMAGE_URL + data.image_url)
                     .resize(720, 720)
                     .centerCrop()
                     .error(R.mipmap.post_placeholder)
                     .placeholder(R.mipmap.post_placeholder)
                     .into(ivProductImage)

         }else{
             ivProductImage.setImageResource(R.mipmap.post_placeholder)
         }

         tvNameProfile.text=data.name
         tvItemPriceProfile.text="$"+data.price
         itemView.setOnClickListener {
             likeDisLikeListener.getItemDetailsClick(data.id,data.user_id)

         }
         tvLikesProfile.setOnClickListener {
             likeDisLikeListener.getLikeDisLikeClick(data.is_like,adapterPosition,data.like_id,data.id)
         }
     }
}

    fun setData(data: ArrayList<ItemUserProfile>) {
        myItemsList=data
    }
}