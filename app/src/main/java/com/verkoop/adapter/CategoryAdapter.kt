package com.verkoop.adapter

import android.content.Context
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.TextUtils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso

import com.verkoop.R
import com.verkoop.activity.CategoriesActivity
import com.verkoop.models.CategoryModal
import com.verkoop.models.DataCategory
import com.verkoop.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.category_row.*
import kotlinx.android.synthetic.main.gallery_item.*


class CategoryAdapter(private var context: Context, private val categoryList: ArrayList<DataCategory>, private val PagerPosition: Int, private val llParentCate: LinearLayout): RecyclerView.Adapter<CategoryAdapter.ViewHolder>(){
    lateinit var selectedCategory: SelectedCategory
    private val mInflater: LayoutInflater = LayoutInflater.from(context)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = mInflater.inflate(
                R.layout.category_row, parent, false)
        selectedCategory=context as CategoriesActivity
      /*val params = llParentCate.layoutParams
        params.width =llParentCate.width / 3
        params.height =llParentCate.height / 3
        view.layoutParams = params*/
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        when (PagerPosition) {
            0 -> {
                val data = categoryList[position]
                holder.bind(data,position)
            }
            1 -> {
                val data = categoryList[position+9]
                holder.bind(data,position)
            }
            else -> {
                val data = categoryList[position+18]
                holder.bind(data,position)
            }
        }
    }

    override fun getItemCount(): Int {
        return 9
    }


    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
            LayoutContainer {
        fun bind(  data: DataCategory,position: Int) {
            tvLevel.text=data.name
            if(!TextUtils.isEmpty(data.image)) {
             /*   Picasso.with(context).load(data.image)
                        .resize(50, 50).error(R.mipmap.setting).into(ivItemsCategory)*/
             /*   Glide.with(context).load(imageUrl)
                        .error(R.mipmap.gallery_place)

                        .into(ivItemsCategory)*/
              /*  Picasso.with(context).load(imageUrl)
                        .resize(50, 50).centerCrop().into(ivItemsCategory)*/
              /*  Glide.with(context).load(data.image)
                        .centerCrop()
                        .placeholder(R.mipmap.gallery_place)
                        .error(R.mipmap.gallery_place)

                        .into(ivItemsCategory)*/

            }
            llParent.setOnClickListener {
                if(data.isSelected){
                    data.isSelected=false
                    tvLevel.setTextColor(ContextCompat.getColor(context,R.color.light_gray))
                    llSelectionGallery.background = ContextCompat.getDrawable(context, R.drawable.transparent_rectangular_shape)
                   // ivItems.setImageResource(data.unselectedImage)
                    ivSelected.visibility=View.GONE
                    selectedCategory.selectedCount(checkSelectionCount())
                }else{
                    if(checkSelectionCount()<3) {
                        data.isSelected = true
                        tvLevel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        llSelectionGallery.background = ContextCompat.getDrawable(context, R.drawable.red_transparent_shape)
                     //   ivItems.setImageResource(data.selectedImage)
                        ivSelected.visibility = View.VISIBLE
                        selectedCategory.selectedCount(checkSelectionCount())
                    }else{
                        Utils.showSimpleMessage(context, "You can not select more then 3 option.").show()
                    }
                }
            }
        }
    }

    private fun checkSelectionCount(): Int {
        var selectionCount=0
        for (i in categoryList.indices){
            if(categoryList[i].isSelected){
                selectionCount++
            }
        }
        return selectionCount
    }
     interface SelectedCategory{
         fun selectedCount(addItem:Int)
    }
}