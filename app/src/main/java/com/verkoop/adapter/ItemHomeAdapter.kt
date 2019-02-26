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
import com.verkoop.activity.ProductDetailsActivity
import com.verkoop.fragment.HomeFragment
import com.verkoop.models.ItemHome
import com.verkoop.utils.AppConstants
import com.verkoop.utils.NonscrollRecylerview
import com.verkoop.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_row.*
import kotlinx.android.synthetic.main.my_profile_row.*


class ItemHomeAdapter(private val context: Context, private  val rvItemList: NonscrollRecylerview,private val homeFragment: HomeFragment) : RecyclerView.Adapter<ItemHomeAdapter.ViewHolder>() {
    private lateinit var likeDisLikeListener: LikeDisLikeListener
    private var mInflater: LayoutInflater = LayoutInflater.from(context)
    private var itemsList=ArrayList<ItemHome>()
    private var width=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.item_row, parent, false)
        val params = view.layoutParams
        params.width = rvItemList.width / 2
        width= params.width
        likeDisLikeListener=homeFragment
        view.layoutParams = params
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = itemsList[position]
        holder.bind(data,position)
    }

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(data: ItemHome, position: Int) {
            ivProductImageHome.layoutParams.height =width-16
            tvNameHome.text=data.username
            if(position %2==0){
                llSideDividerHome.visibility= View.VISIBLE
            }else{
                llSideDividerHome.visibility= View.GONE
            }
            if(data.is_like){
                tvLikesHome.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.post_liked, 0, 0, 0)
            }else{
                tvLikesHome.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.post_like, 0, 0, 0)
            }
            tvLikesHome.text=data.likes_count.toString()
            if(data.item_type==1){
                tvConditionHome.text="New"
            }else{
                tvConditionHome.text=context.getString(R.string.used)
            }

            if(!TextUtils.isEmpty(data.image_url)) {
                    Picasso.with(context).load(AppConstants.IMAGE_URL + data.image_url)
                            .resize(720, 720)
                            .centerInside()
                            .error(R.mipmap.setting)
                            .into(ivProductImageHome)

            }else{
              //  Utils.showToast(context,"error in image")
            }

            tvProductHome.text=data.name
            tvItemPriceHome.text="$"+data.price
            itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(AppConstants.ITEM_ID, data.id)
                context.startActivity(intent)
            }
        }
    }

    fun setData(items: ArrayList<ItemHome>) {
        itemsList=items
    }


}