package com.verkoop.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoop.R
import com.verkoop.activity.ProductDetailsActivity
import com.verkoop.models.CategoryModal
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.category_row.*
import kotlinx.android.synthetic.main.item_row.*


class ItemAdapter(private val context: Context,private val categoryList: ArrayList<CategoryModal>) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    private var mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.item_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

     //   val data = categoryList[position]
       holder.bind(position)
    }

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(  position: Int) {
        if(position %2==0){
            llSideDivider.visibility=View.VISIBLE
        }else{
            llSideDivider.visibility=View.GONE
        }
            itemView.setOnClickListener {
                val intent= Intent(context,ProductDetailsActivity::class.java)
                context.startActivity(intent)
            }
        }
    }
}