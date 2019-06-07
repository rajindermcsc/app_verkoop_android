package com.verkoopapp.utils

import android.content.Context
import android.support.v7.widget.GridLayoutManager


class CustomGridLayoutManager(context: Context) : GridLayoutManager(context,2) {
    private var isScrollEnabled = true

    fun setScrollEnabled(flag: Boolean) {
        this.isScrollEnabled = flag
    }

    override fun canScrollVertically(): Boolean {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically()
    }
}