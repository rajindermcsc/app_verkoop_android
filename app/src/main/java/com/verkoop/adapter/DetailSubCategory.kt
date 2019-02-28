package com.verkoop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoop.R
import com.verkoop.models.SubCategoryPost
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.details_sub_category.*


class DetailSubCategory(private val context:Context,private val recyclerView: RecyclerView,private val subCategoryList: ArrayList<SubCategoryPost>):RecyclerView.Adapter<DetailSubCategory.ViewHolder>(){
    private var mInflater:LayoutInflater= LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
       val view=mInflater.inflate(R.layout.details_sub_category,parent,false)
        val params = view.layoutParams
        params.width = recyclerView.width / 3
        params.height = params.width
        view.layoutParams = params
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return subCategoryList.size
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val data=subCategoryList[position]
       holder.bind(data)
    }
    inner class ViewHolder(override val containerView: View):RecyclerView.ViewHolder(containerView),LayoutContainer{
        fun bind(data: SubCategoryPost) {
            tvSubCategoryPost.text=data.name
        }
    }
}