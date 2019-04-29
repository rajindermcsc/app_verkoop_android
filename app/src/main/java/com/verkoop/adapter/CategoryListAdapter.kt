package com.verkoop.adapter

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.verkoop.R
import com.verkoop.activity.CategoryDetailsActivity
import com.verkoop.activity.HomeActivity
import com.verkoop.models.Category
import com.verkoop.utils.AppConstants
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.category_home_row.*


class CategoryListAdapter(private val context: HomeActivity, private var categoryList: ArrayList<Category>, private val rvCategoryHome: RecyclerView): RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.category_home_row, parent, false)
        val params = view.layoutParams
        params.width = rvCategoryHome.width / 3
        params.height = params.width
        view.layoutParams = params
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = categoryList[position]
        holder.bind(data,position)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }


    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
            LayoutContainer {
        fun bind(data: Category, position: Int) {
            tvLevelHome.text=data.name
            if (!TextUtils.isEmpty(data.image)) {
                Picasso.with(context).load(AppConstants.IMAGE_URL+data.image)
                        .resize(720, 720)
                        .centerInside()
                        .error(R.mipmap.setting)
                        .into(ivItemsHome)
            }
            itemView.setOnClickListener {
                val intent = Intent(context, CategoryDetailsActivity::class.java)
                intent.putExtra(AppConstants.CATEGORY_ID, data.id)
                intent.putExtra(AppConstants.TYPE, 0)
                intent.putExtra(AppConstants.SUB_CATEGORY, data.name)
                context.startActivityForResult(intent, 2)
            }
        }
    }

}