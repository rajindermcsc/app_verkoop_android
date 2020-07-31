package com.verkoopapp.adapter

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.verkoopapp.R
import com.verkoopapp.VerkoopApplication
import com.verkoopapp.activity.RegionActivity
import com.verkoopapp.models.City
import com.verkoopapp.models.CityDataValue
import com.verkoopapp.models.CityResponse
import com.verkoopapp.models.StateDataValue
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.region_row.*
import retrofit2.Response


class RegionAdapter(private val context: Context, private val coming: Int) : RecyclerView.Adapter<RegionAdapter.ViewHolder>(), Filterable {
    private var statesList = ArrayList<StateDataValue>()
    private var mFilteredList = ArrayList<StateDataValue>()
    private lateinit var clickEventCallBack: ClickEventCallBack
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var citylist = ArrayList<CityDataValue>()
    val font = Typeface.createFromAsset(context.assets, "fonts/gothic.ttf")

    override fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {

                val charString = charSequence.toString()

                if (charString.isEmpty()) {
                    mFilteredList = statesList
                } else {

                    val filteredList = ArrayList<StateDataValue>()

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
                mFilteredList = filterResults.values as ArrayList<StateDataValue>
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
        holder.bind(data,position)
    }

    inner class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        fun bind(data: StateDataValue, position: Int) {
            cbRegion.typeface = font
//            cbRegion.isChecked = data.isSelected
            cbRegion.text = data.name

//            for (i in statesList) {
//
//                if (!Utils.getPreferencesString(context, AppConstants.STATE_NAME).isEmpty()){
//                    if (Utils.getPreferencesString(context, AppConstants.STATE_NAME).equals(statesList[position].name)){
//                        cbRegion.isChecked=true
//                    }
//                }
//            }
            cbRegion.setOnClickListener {
                refreshList(adapterPosition)
                if (coming == 0) {
                    getMyCity(data.name,data.id,0,statesList[position].id)
                } else {
                   val cityList=ArrayList<CityDataValue>()
                    clickEventCallBack.onSelectRegion(data.name, data.id, 1,cityList)
                }
            }
        }

    }


    private fun getMyCity(code: String, id: Int, i1: Int, id1: Int) {
//        VerkoopApplication.instance.loader.show()
        ServiceHelper().myCityList(id1, object : ServiceHelper.OnResponse {
            override fun onSuccess(response: Response<*>) {
                val responseCity = response.body() as CityResponse
                if (responseCity.data != null) {
                    citylist = responseCity.data.city
                    clickEventCallBack.onSelectRegion(code, id, 0,citylist)


                } else {
                    Utils.showSimpleMessage(context, "No data found.").show()
                }

            }

            override fun onFailure(msg: String?) {
                Utils.showSimpleMessage(context, msg!!).show()
            }
        })

    }

    private fun refreshList(adapterPosition: Int) {

//        for (i in mFilteredList.indices) {
//            mFilteredList[i].isSelected = false
//        }
//        for (j in statesList.indices) {
//            statesList[j].isSelected = false
//        }
//        mFilteredList[adapterPosition].isSelected = true
        notifyDataSetChanged()
    }

    fun setData(data: ArrayList<StateDataValue>) {
        this.statesList = data
        this.mFilteredList = data
    }

    interface ClickEventCallBack {
        fun onSelectRegion(regionName: String, regionId: Int, coming: Int, cityList: ArrayList<CityDataValue>)
    }
}