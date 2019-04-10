package com.verkoop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoop.R
import com.verkoop.models.DataSearch
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.row_search_item.*


class SearchListAdapter(context:Context):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        private val mLayoutInflater:LayoutInflater= LayoutInflater.from(context)
    private var searchItemList= ArrayList<DataSearch>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view=mLayoutInflater.inflate(R.layout.row_search_item,parent,false)
        return SearchKeyWordHolder(view)
    }

    override fun getItemCount(): Int {
        return searchItemList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val modal=searchItemList[position]
        (holder as SearchKeyWordHolder).bind(modal)
    }
    inner class SearchKeyWordHolder(override val containerView: View?):RecyclerView.ViewHolder(containerView),LayoutContainer{
        fun bind(modal: DataSearch) {
            tvHeading.text=modal.name
            tvCategorySearch.text=modal.category_name
        }
    }

    fun setData(searchItem: ArrayList<DataSearch>) {
        searchItemList=searchItem
    }
}