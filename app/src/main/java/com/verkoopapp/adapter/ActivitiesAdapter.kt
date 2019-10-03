package com.verkoopapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.verkoopapp.R
import com.verkoopapp.activity.HomeActivity
import com.verkoopapp.fragment.ActivitiesFragment
import kotlinx.android.extensions.LayoutContainer

class ActivitiesAdapter(context: Context, activitiesFragment: ActivitiesFragment) : RecyclerView.Adapter<ActivitiesAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.notification_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
    }

//    StringBuilder().append(Utils.getDateDifference(data.created_at!!.date)).append(" ").append("ago")
}
