package com.verkoop.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoop.R
import com.verkoop.models.CategoryModal
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.category_home_row.*
import kotlinx.android.synthetic.main.category_row.*


class CategoryListAdapter(context: Context, private var categoryList: ArrayList<CategoryModal>): RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(
                R.layout.category_home_row, parent, false)
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
        fun bind(  data: CategoryModal,position: Int) {
            tvLevelHome.text=data.itamName
            ivItems.setImageResource(data.unselectedImage)
        }
    }

}