package com.verkoop.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.squareup.picasso.Picasso
import com.verkoop.R
import com.verkoop.models.ImageModal
import com.verkoop.utils.Utils.getRealPathFromURI
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.gallery_item.*
import java.io.File
import android.R.attr.path



class GalleryAdapter(private var context: Context, private var llParent: LinearLayout) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    private var mInflater: LayoutInflater = LayoutInflater.from(context)
    private var imageList = ArrayList<ImageModal>()
    private val TYPE_HEADER = Integer.MIN_VALUE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val  view = mInflater.inflate(R.layout.gallery_item, parent, false)
        val params = view.layoutParams
        params.height = llParent.height / 5
        params.width = llParent.width / 3
        view.layoutParams = params
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modal = imageList[position]
        holder.bind(modal)
    }

    inner class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(modal: ImageModal) {
            if(modal.type){
                flCamera.visibility=View.VISIBLE
                flItem.visibility=View.GONE
            }else{
                flCamera.visibility=View.GONE
                flItem.visibility=View.VISIBLE
            }
           if(!TextUtils.isEmpty(modal.imageUrl)) {
               Glide.with(context).load(Uri.parse(modal.imageUrl))
                       .centerCrop()
                       .placeholder(R.mipmap.gallery_place)
                       .error(R.mipmap.gallery_place)
                       .override(750,750)
                     //  .diskCacheStrategy(DiskCacheStrategy.ALL)
                       .into(ivImage)
              /* Picasso.with(context).load(File(getRealPathFromURI(context, Uri.parse(modal.imageUrl))) )
                       .placeholder(R.mipmap.gallery_place)
                       .resize(750, 750)
                       .centerCrop()
                       .error(R.mipmap.gallery_place)
                       .into(ivImage)*/
           }

        }
    }


    fun setData(result: ArrayList<ImageModal>) {
        imageList = result
    }
}