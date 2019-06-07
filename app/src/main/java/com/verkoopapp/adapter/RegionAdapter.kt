package com.verkoopapp.adapter

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.verkoopapp.R
import com.verkoopapp.activity.RegionActivity
import com.verkoopapp.models.City
import com.verkoopapp.models.State
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.region_row.*


class RegionAdapter(private val context: Context, private val coming: Int) : RecyclerView.Adapter<RegionAdapter.ViewHolder>(), Filterable {
    private var statesList = ArrayList<State>()
    private var mFilteredList = ArrayList<State>()
    private lateinit var clickEventCallBack: ClickEventCallBack
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    val font = Typeface.createFromAsset(context.assets, "fonts/gothic.ttf")

    override fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {

                val charString = charSequence.toString()

                if (charString.isEmpty()) {
                    mFilteredList = statesList
                } else {

                    val filteredList = ArrayList<State>()

                    for (androidVersion in statesList) {

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
                mFilteredList = filterResults.values as ArrayList<State>
                notifyDataSetChanged()
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.region_row, parent, false)
        clickEventCallBack = context as RegionActivity
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mFilteredList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mFilteredList[position]
        holder.bind(data)
    }

    inner class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(data: State) {
            cbRegion.typeface = font
            cbRegion.isChecked = data.isSelected
            cbRegion.text = data.name
            cbRegion.setOnClickListener {
                refreshList(adapterPosition)
                if (coming == 0) {
                    clickEventCallBack.onSelectRegion(data.name, data.id, 0,data.cities)
                } else {
                   val cityList=ArrayList<City>()
                    clickEventCallBack.onSelectRegion(data.name, data.id, 1,cityList)
                }
            }
        }

    }

    private fun refreshList(adapterPosition: Int) {
        for (i in mFilteredList.indices) {
            mFilteredList[i].isSelected = false
        }
        for (j in statesList.indices) {
            statesList[j].isSelected = false
        }
        mFilteredList[adapterPosition].isSelected = true
        notifyDataSetChanged()
    }

    fun setData(data: ArrayList<State>) {
        this.statesList = data
        this.mFilteredList = data
    }

    interface ClickEventCallBack {
        fun onSelectRegion(regionName: String, regionId: Int, coming: Int, cityList: ArrayList<City>)
    }
}