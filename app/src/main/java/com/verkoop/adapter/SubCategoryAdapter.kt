package com.verkoop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoop.R
import com.verkoop.activity.FullCategoriesActivity
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.sub_category.*


class SubCategoryAdapter(private  var context: Context,private val answers: List<String>):RecyclerView.Adapter<SubCategoryAdapter.ViewHolder>(){
     lateinit var selectedSubcategory: SelectedSubcategory
    private val mInflater:LayoutInflater= LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
       val view= mInflater.inflate(R.layout.sub_category,parent,false)
        selectedSubcategory=context as FullCategoriesActivity
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return answers.size
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {

        var list=answers[position]
        holder.bind(list)
    }
    inner class ViewHolder(override val containerView: View):RecyclerView.ViewHolder(containerView),LayoutContainer{
        fun bind(list: String) {
            tvSubCategory.text=list
            tvSubCategory.setOnClickListener {
                selectedSubcategory.subCategoryName(list)
            }
        }

    }
    interface SelectedSubcategory{
        fun subCategoryName(name:String)
    }
}