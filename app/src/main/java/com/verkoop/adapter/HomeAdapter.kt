package com.verkoop.adapter

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.squareup.picasso.Picasso
import com.verkoop.LikeDisLikeListener
import com.verkoop.R
import com.verkoop.activity.FullCategoriesActivity
import com.verkoop.activity.HomeActivity
import com.verkoop.activity.ProductDetailsActivity
import com.verkoop.activity.UserProfileActivity
import com.verkoop.fragment.HomeFragment
import com.verkoop.models.Advertisment
import com.verkoop.models.Category
import com.verkoop.models.ItemHome
import com.verkoop.utils.AppConstants
import com.verkoop.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.adds_category_row.*
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.item_row.*


class HomeAdapter(private val context: Context, private val rvItemList: RecyclerView, private val homeFragment: HomeFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var likeDisLikeListener: LikeDisLikeListener
    val CATEGORY_LIST_ROW = 0
    val PROPERTIES_ROW = 1
    val ITEMS_ROW = 2
    private var width = 0
    private var widthOrg = 0
    private var itemsList = ArrayList<ItemHome>()
    private var categoryList = ArrayList<Category>()
    private var advertismentsList = ArrayList<Advertisment>()

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> CATEGORY_LIST_ROW
            1 -> PROPERTIES_ROW
            else -> ITEMS_ROW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return when (viewType) {
            CATEGORY_LIST_ROW -> {
                view = mLayoutInflater.inflate(R.layout.adds_category_row, parent, false)
                val params = view.layoutParams
                params.width = rvItemList.width
                widthOrg = params.width
                AddsAndItemsHolder(view)
            }
            PROPERTIES_ROW -> {
                view = mLayoutInflater.inflate(R.layout.cars_properties_row, parent, false)
                CarAndPropertiesHolder(view)
            }
            else -> {
                view = mLayoutInflater.inflate(R.layout.item_row, parent, false)
                val params = view.layoutParams
                params.width = rvItemList.width / 2
                width = params.width
                likeDisLikeListener = homeFragment
                //view.layoutParams = params
                ItemsHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size + 2
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == CATEGORY_LIST_ROW) {
            (holder as AddsAndItemsHolder).bind(categoryList, advertismentsList)
        } else if (position == PROPERTIES_ROW) {

        } else {
            val modal = itemsList[position - 2]
            (holder as ItemsHolder).bind(modal)
        }
    }

    inner class AddsAndItemsHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(categoryList: ArrayList<Category>, advertismentsList: ArrayList<Advertisment>) {
            mDemoSlider.removeAllSliders()
            custom_indicator.setDefaultIndicatorColor(ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.light_gray))
            mDemoSlider.setCustomIndicator(custom_indicator)
            for (i in 0 until advertismentsList.size) {
                val textSliderView = DefaultSliderView(context)
                textSliderView.image(AppConstants.IMAGE_URL + advertismentsList[i].image)
                        .setOnSliderClickListener({ slider -> }).scaleType = BaseSliderView.ScaleType.Fit
                if (mDemoSlider != null) {
                    mDemoSlider.addSlider(textSliderView)
                }
            }
            mDemoSlider.setDuration(3000)

            rvCategoryHome.layoutParams.height = widthOrg / 3
            val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvCategoryHome.layoutManager = linearLayoutManager
            val categoryAdapter = CategoryListAdapter(context as HomeActivity, categoryList, rvCategoryHome)
            rvCategoryHome.adapter = categoryAdapter
            rvCategoryHome.adapter.notifyDataSetChanged()
            tvViewAll.setOnClickListener {
                val intent = Intent(context, FullCategoriesActivity::class.java)
                context.startActivityForResult(intent, 2)
            }

        }

    }

    inner class CarAndPropertiesHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    }

    inner class ItemsHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(data: ItemHome) {
            ivProductImageHome.layoutParams.height = width - 16
            tvNameHome.text = data.username
            if (adapterPosition % 2 == 0) {
                llSideDividerHome.visibility = View.VISIBLE
            } else {
                llSideDividerHome.visibility = View.GONE
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
                likeDisLikeListener.getLikeDisLikeClick(data.is_like, adapterPosition - 2, data.like_id, data.id)
            }
            tvProductHome.text = data.name
            tvItemPriceHome.text = "$" + data.price
            itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(AppConstants.ITEM_ID, data.id)
                context.startActivity(intent)
            }
            tvPostOn.text = StringBuilder().append(Utils.getDateDifference(data.created_at.date)).append(" ").append("ago")

            llUserProfile.setOnClickListener {
                val reportIntent = Intent(context, UserProfileActivity::class.java)
                reportIntent.putExtra(AppConstants.USER_ID, data.user_id)
                reportIntent.putExtra(AppConstants.USER_NAME, data.username)
                context.startActivity(reportIntent)
            }
        }
    }

    fun setData(items: ArrayList<ItemHome>) {
        itemsList = items
    }

    fun setCategoryAndAddsData(advertisments: ArrayList<Advertisment>, categories: ArrayList<Category>) {
        categoryList = categories
        advertismentsList = advertisments
    }
}