package com.verkoop.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import com.verkoop.R
import com.verkoop.activity.CategoriesActivity
import com.verkoop.models.CategoryModal
import com.verkoop.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.category_row.*


class CategoryAdapter(private var context: Context, private val categoryList: ArrayList<CategoryModal>, private val PagerPosition: Int,private val llParentCate: LinearLayout): RecyclerView.Adapter<CategoryAdapter.ViewHolder>(){
    lateinit var selectedCategory: SelectedCategory
    private val mInflater: LayoutInflater = LayoutInflater.from(context)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       var view = mInflater.inflate(
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
        fun bind(  data: CategoryModal,position: Int) {
            tvLevel.text=data.itamName
            ivItems.setImageResource(data.unselectedImage)

            llParent.setOnClickListener {
                if(data.selectedPosition){
                    data.selectedPosition=false
                    tvLevel.setTextColor(ContextCompat.getColor(context,R.color.light_gray))
                    llSelection.background = ContextCompat.getDrawable(context, R.drawable.transparent_rectangular_shape)
                    ivItems.setImageResource(data.unselectedImage)
                    ivSelected.visibility=View.GONE
                    selectedCategory.selectedCount(checkSelectionCount())
                }else{
                    if(checkSelectionCount()<3) {
                        data.selectedPosition = true
                        tvLevel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        llSelection.background = ContextCompat.getDrawable(context, R.drawable.red_transparent_shape)
                        ivItems.setImageResource(data.selectedImage)
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
            if(categoryList[i].selectedPosition){
                selectionCount++
            }
        }
        return selectionCount
    }
     interface SelectedCategory{
         fun selectedCount(addItem:Int)
    }
}