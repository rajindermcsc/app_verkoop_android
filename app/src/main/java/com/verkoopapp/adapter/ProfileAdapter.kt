package com.verkoopapp.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.verkoopapp.R
import com.verkoopapp.activity.*
import com.verkoopapp.fragment.ProfileFragment
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_row.*
import kotlinx.android.synthetic.main.my_profile_details_row.*
import retrofit2.Response


class ProfileAdapter(private val context: Context, private val screenWidth: Int, private val profileFragment: ProfileFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mLayoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var itemsList = ArrayList<ItemHome>()
    val PROFILE_DETAILS = 0
    val ITEMS_ROW = 1
    val SHOW_LOADER = 2/*for future use*/
    private var width = 0
    private var dataProfile: DataProfile? = null


    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return return PROFILE_DETAILS
        } else {
            return ITEMS_ROW
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return when (viewType) {
            PROFILE_DETAILS -> {
                view = mLayoutInflater.inflate(R.layout.my_profile_details_row, parent, false)
                ProfileDetailsHolder(view)
            }
            SHOW_LOADER -> {
                view = mLayoutInflater.inflate(R.layout.show_loader_row, parent, false)
                ShowLoaderHolder(view)
            }
            else -> {
                view = mLayoutInflater.inflate(R.layout.item_row, parent, false)
                val params = view.layoutParams
                params.width = screenWidth / 2
                width = params.width
                ItemsHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == PROFILE_DETAILS) {
             (holder as ProfileDetailsHolder).bind(dataProfile)
        } else {
            val modal = itemsList[position - 1]
            (holder as ItemsHolder).bind(modal)
        }
    }


    inner class ShowLoaderHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind() {

        }

    }

    inner class ProfileDetailsHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(data: DataProfile?) {
            if(data!=null) {
                tvName.text = data.username
                tvFollowers.text = data.follower_count.toString()
                tvFollowing.text = data.follow_count.toString()
                tvGood.text=data.good.toString()
                tvAverage.text=data.avrage.toString()
                tvbad.text=data.sad.toString()
                tvJoiningDate.text = StringBuffer().append(": ").append(Utils.convertDate("yyyy-MM-dd hh:mm:ss", data.created_at, "dd MMMM yyyy"))
                if (!TextUtils.isEmpty(data.profile_pic)) {
                    Picasso.with(context).load(AppConstants.IMAGE_URL + data.profile_pic)
                            .resize(720, 720)
                            .centerInside()
                            .error(R.mipmap.pic_placeholder)
                            .placeholder(R.mipmap.pic_placeholder)
                            .into(ivProfilePic)
                }
                if (!TextUtils.isEmpty(data.city) && !TextUtils.isEmpty(data.state)) {
                    tvAddress.text = StringBuilder().append(data.state).append(", ").append(data.city)
                    tvAddress.visibility = View.VISIBLE
                } else {
                    tvAddress.visibility = View.GONE
                }
                tvCountry.text = data.country
            }
            llFollowers.setOnClickListener {
                val intent = Intent(context, FollowFollowingActivity::class.java)
                intent.putExtra(AppConstants.COMING_FROM, 0)
                intent.putExtra(AppConstants.USER_ID, Utils.getPreferencesString(context, AppConstants.USER_ID).toInt())
                context.startActivity(intent)
            }
            llFollowing.setOnClickListener {
                val intent = Intent(context, FollowFollowingActivity::class.java)
                intent.putExtra(AppConstants.COMING_FROM, 1)
                intent.putExtra(AppConstants.USER_ID, Utils.getPreferencesString(context, AppConstants.USER_ID).toInt())
                context.startActivity(intent)
            }
            llFavourite.setOnClickListener {
                val intent = Intent(context, FavouritesActivity::class.java)
                context.startActivity(intent)
            }
            ivSetting.setOnClickListener {
                val intent = Intent(context, SettingActivity::class.java)
                context.startActivity(intent)
            }
            llCoins.setOnClickListener {
                val intent = Intent(context, CoinsActivity::class.java)
                context.startActivity(intent)
            }
            ivScanner.setOnClickListener {
                val intent = Intent(context, QRScannerActivity::class.java)
                context.startActivity(intent)
            }
            llWallet.setOnClickListener {
                val intent = Intent(context, MyWalletActivity::class.java)
                context.startActivity(intent)
            }
        }

    }

    inner class ItemsHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(data: ItemHome) {
            llUserProfile.visibility = View.GONE
            ivProductImageHome.layoutParams.height = width - 16
            if (data.is_sold == 1) {
                tvSoldFav.visibility = View.VISIBLE
            } else {
                tvSoldFav.visibility = View.GONE
            }
            if (data.is_like) {
                tvLikesHome.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.post_liked, 0, 0, 0)
            } else {
                tvLikesHome.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.post_like, 0, 0, 0)
            }
            tvLikesHome.text = data.items_like_count.toString()
            if (data.item_type == 1) {
                tvConditionHome.text = "New"
            } else {
                tvConditionHome.text = context.getString(R.string.used)
            }

            if (!TextUtils.isEmpty(data.image_url)) {
                Picasso.with(context)
                        .load(AppConstants.IMAGE_URL + data.image_url)

                        .resize(720, 720)
                        .centerCrop()
                        .error(R.mipmap.post_placeholder)
                        .placeholder(R.mipmap.post_placeholder)
                        .into(ivProductImageHome)

            } else {
                ivProductImageHome.setImageResource(R.mipmap.post_placeholder)
            }
            tvLikesHome.setOnClickListener {

                if (!data.isClicked) {
                    if (data.like_id > 0) {
                        data.isClicked = !data.isClicked
                        deleteLikeApi(adapterPosition - 1, data.like_id)
                    } else {
                        data.isClicked = !data.isClicked
                        lickedApi(data.id, adapterPosition - 1)
                    }
                }
            }
            tvProductHome.text = data.name
            tvItemPriceHome.text = "R" + data.price
            itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(AppConstants.ITEM_ID, data.id)
                intent.putExtra(AppConstants.COMING_FROM, 1)
                intent.putExtra(AppConstants.COMING_TYPE, 1)
                intent.putExtra(AppConstants.ADAPTER_POSITION, adapterPosition)
                profileFragment.startActivityForResult(intent, 3)
            }

        }
    }


    private fun lickedApi(itemId: Int, position: Int) {
        val lickedRequest = LickedRequest(Utils.getPreferencesString(context, AppConstants.USER_ID), itemId)
        ServiceHelper().likeService(lickedRequest,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        itemsList[position].isClicked = !itemsList[position].isClicked
                        val responseLike = response.body() as LikedResponse
                        itemsList[position].is_like = !itemsList[position].is_like
                        itemsList[position].items_like_count = itemsList[position].items_like_count + 1
                        itemsList[position].like_id = responseLike.like_id
                        notifyItemChanged(position + 1)
                    }

                    override fun onFailure(msg: String?) {
                        itemsList[position].isClicked = !itemsList[position].isClicked
                        notifyItemChanged(position + 1)
                        //      Utils.showSimpleMessage(homeActivity, msg!!).show()
                    }
                })
    }

    private fun deleteLikeApi(position: Int, lickedId: Int) {
        ServiceHelper().disLikeService(lickedId,
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        itemsList[position].isClicked = !itemsList[position].isClicked
                        val likeResponse = response.body() as DisLikeResponse
                        itemsList[position].is_like = !itemsList[position].is_like
                        itemsList[position].items_like_count = itemsList[position].items_like_count - 1
                        itemsList[position].like_id = 0
                        notifyItemChanged(position + 1)
                    }

                    override fun onFailure(msg: String?) {
                        itemsList[position].isClicked = !itemsList[position].isClicked
                        notifyItemChanged(position + 1)
                    }
                })
    }

    fun setData(dataList: ArrayList<ItemHome>) {
        itemsList = dataList
    }

    fun profileDetail(data: DataProfile) {
        dataProfile = data
    }

}