package com.verkoop.adapter

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoop.R
import com.verkoop.models.DataCategory
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.category_dialog.*
import java.util.*

class CategoryDialogAdapter(private val context: Context, private val categoryList: ArrayList<DataCategory>) : RecyclerView.Adapter<CategoryDialogAdapter.ViewHolder>() {
    private var mInflater: LayoutInflater = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.category_dialog, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modal = categoryList[position]
        holder.bind(modal)
    }

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(modal: DataCategory) {
            if(modal.isSelected){
                expansionLayout.expand(true)
                itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.gray_semi))
            }else{
                expansionLayout.collapse(true)
                itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.semi_white))
            }
            tvCategoryNameDialog.text = modal.name
            val mManager = LinearLayoutManager(context)
            rvSubCategory.layoutManager = mManager
            val subCategoryAdapter = SubCategoryDialogAdapter(context, modal.sub_category,modal.id)
            rvSubCategory.isFocusable = false
            rvSubCategory.adapter = subCategoryAdapter

            tvCategoryNameDialog.setOnClickListener {
                updateList(adapterPosition)

            }
        }
    }

    private fun updateList(position: Int) {
        for (i in categoryList.indices) {
            if(i==position){
                categoryList[i].isSelected=! categoryList[i].isSelected
            }else{
                categoryList[i].isSelected=false
            }
        }
        notifyDataSetChanged()
    }

}