package com.verkoop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.verkoop.R
import com.verkoop.models.CarType
import com.verkoop.utils.AppConstants
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.details_sub_category.*


class CarBodyTypeAdapter(private val context: Context, private val recyclerView: RecyclerView,private val carTypeLIst: ArrayList<CarType>):RecyclerView.Adapter<CarBodyTypeAdapter.ViewHolder>(){
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val view=mLayoutInflater.inflate(R.layout.details_sub_category,parent,false)
        val params = view.layoutParams
        params.width = (recyclerView.width-60 )/ 3
        params.height = params.width
        view.layoutParams = params
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
      return carTypeLIst.size
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val modal=carTypeLIst[position]
        holder.bind(modal)
    }

    inner class ViewHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView),LayoutContainer {
        fun bind(data: CarType) {
            tvSubCategoryPost.text=data.name
            if (!TextUtils.isEmpty(data.image)) {
                Picasso.with(context).load(AppConstants.IMAGE_URL+data.image)
                        .resize(720, 720)
                        .centerInside()
                        .error(R.mipmap.setting)
                        .into(ivSubCategory)
            }
            itemView.setOnClickListener {
                /*  val intent = Intent(context, CategoryDetailsActivity::class.java)
                  intent.putExtra(AppConstants.CATEGORY_ID, data.id)
                  intent.putExtra(AppConstants.TYPE, 0)
                  intent.putExtra(AppConstants.SUB_CATEGORY, data.name)
                  context.startActivityForResult(intent, 2)*/
            }
        }

    }

}