package com.verkoop.adapter

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.verkoop.R
import com.verkoop.activity.StateActivity
import com.verkoop.models.City
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.region_row.*


class CityListAdapter(private val context: Context) : RecyclerView.Adapter<CityListAdapter.ViewHolder>(), Filterable {

    private var cityList = ArrayList<City>()
    private var mFilteredList = ArrayList<City>()
    private lateinit var clickEventCallBack: ClickEventCallBack
    val font = Typeface.createFromAsset(context.assets, "fonts/gothic.ttf")
    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {

                val charString = charSequence.toString()

                if (charString.isEmpty()) {
                    mFilteredList = cityList
                } else {

                    val filteredList = ArrayList<City>()

                    for (androidVersion in cityList) {

                        if (androidVersion.name.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(androidVersion)
                        }
                    }

                    mFilteredList = filteredList
                }

                val filterResults = Filter.FilterResults()
                filterResults.values = mFilteredList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                mFilteredList = filterResults.values as ArrayList<City>
                notifyDataSetChanged()
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.region_row, parent, false)
        clickEventCallBack = context as StateActivity
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mFilteredList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mFilteredList[position]
        holder.bind(data)
    }

    inner class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(data: City) {
            cbRegion.typeface = font
            cbRegion.isChecked = data.isSelected
            cbRegion.text = data.name
            cbRegion.setOnClickListener {
                refreshList(adapterPosition)
                clickEventCallBack.onSelectRegion(data.name, data.id)
            }
        }

    }

    private fun refreshList(adapterPosition: Int) {
        for (i in mFilteredList.indices) {
            mFilteredList[i].isSelected = false
        }
        for (j in cityList.indices) {
            cityList[j].isSelected = false
        }
        mFilteredList[adapterPosition].isSelected = true
        notifyDataSetChanged()
    }

    fun setData(data: ArrayList<City>) {
       cityList = data
       mFilteredList = data
    }

    interface ClickEventCallBack {
        fun onSelectRegion(regionName: String, regionId: Int)
    }
}