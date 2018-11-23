package com.ksmtrivia.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.verkoop.R
import com.verkoop.models.CategoryModal
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.category_row.*


class CategoryAdapter(private var context: Context,private val categoryList:ArrayList<CategoryModal>): RecyclerView.Adapter<CategoryAdapter.ViewHolder>(){
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private val nameList = arrayOf("Women's", "men's","Footwear","Desktop's","Mobiles","Furniture","Pets","Car","Books")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(
                R.layout.category_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = categoryList[position]
        holder.bind(data,position)
        categoryList[position].selectedPosition=false
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }


    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
            LayoutContainer {
        fun bind(  data: CategoryModal,position: Int) {
            tvLevel.text=data.itamName
            ivItems.setImageResource(data.unselectedImage)
            llParent.setOnClickListener {
              //  refreshData(position)
                data.selectedPosition=true
                tvLevel.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))
                llSelection.background = ContextCompat.getDrawable(context, R.drawable.red_transparent_shape)
                ivItems.setImageResource(data.selectedImage)
                ivSelected.visibility=View.VISIBLE
            }
        }
    }

    private fun refreshData(position: Int) {
        for(i in categoryList.indices){
            categoryList[i].selectedPosition=false
        }
    }
}