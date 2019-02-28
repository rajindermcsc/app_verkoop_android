package com.verkoop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoop.R
import com.verkoop.activity.FullCategoriesActivity
import com.verkoop.models.SubCategory
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.sub_category.*


class SubCategoryAdapter(private  var context: Context,private val subCategoryList: ArrayList<SubCategory>):RecyclerView.Adapter<SubCategoryAdapter.ViewHolder>(){
     lateinit var selectedSubcategory: SelectedSubcategory
    private val mInflater:LayoutInflater= LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
       val view= mInflater.inflate(R.layout.sub_category,parent,false)
        selectedSubcategory=context as FullCategoriesActivity
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return subCategoryList.size
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {

        var list=subCategoryList[position]
        holder.bind(list)
    }
    inner class ViewHolder(override val containerView: View):RecyclerView.ViewHolder(containerView),LayoutContainer{
        fun bind(subCategory: SubCategory) {
            tvSubCategory.text=subCategory.name
            tvSubCategory.setOnClickListener {
                selectedSubcategory.subCategoryName(subCategory.id,subCategory.name)
            }
        }

    }
    interface SelectedSubcategory{
        fun subCategoryName(categoryId:Int,subCategoryName:String)
    }
}