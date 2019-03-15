package com.verkoop.adapter

import android.app.Activity
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.verkoop.R
import com.verkoop.activity.AddDetailsActivity
import com.verkoop.models.ImageModal
import com.verkoop.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.image_list_row.*

class SelectedImageAdapter(private val context: Activity, private var selectedImageList: ArrayList<ImageModal>, private var flList: FrameLayout,private var listSize: Int) : RecyclerView.Adapter<SelectedImageAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var  selectedImageCount:SelectedImageCount

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.image_list_row, parent, false)
        val params = view.layoutParams
        params.width = flList.width / 3
        params.height = params.width
        view.layoutParams = params
        selectedImageCount=context as AddDetailsActivity
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return selectedImageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modalList = selectedImageList[position]
        holder.bind(modalList)
    }

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(modalList: ImageModal) {
            if (modalList.type) {
                flGallery.visibility = View.VISIBLE
                flImage.visibility = View.GONE
                flClose.visibility = View.GONE
            } else {
                flGallery.visibility = View.GONE
                flImage.visibility = View.VISIBLE
                flClose.visibility = View.VISIBLE
            }
            if (!TextUtils.isEmpty(modalList.imageUrl)) {
                Glide.with(context).load(Uri.parse(modalList.imageUrl))
                        .centerCrop()
                        .placeholder(R.mipmap.gallery_place)
                        .error(R.mipmap.gallery_place)
                        .override(750, 750)
                        .into(ivImageDetail)

            }
            flGallery.setOnClickListener {
                Utils.showToast(context, "cameraClick")
            }
            flClose.setOnClickListener {
                listSize--
                setData(modalList.imagePosition)
                selectedImageList.remove(modalList)

            }
            flGallery.setOnClickListener {
               context.onBackPressed()
            }
        }

        private fun setData(selectPosition:Int) {
            val imageModal = ImageModal("", false, true, 0,0)
            if (!selectedImageList.contains(imageModal)) {
                selectedImageList.add(imageModal)
            }
            notifyDataSetChanged()
            selectedImageCount.selectDetailCount(listSize,selectPosition)
        }
    }
    interface SelectedImageCount{
        fun selectDetailCount(count:Int,position:Int)
    }
}