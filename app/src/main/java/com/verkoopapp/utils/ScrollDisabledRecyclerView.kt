package com.verkoopapp.utils

import android.content.Context
import android.support.annotation.Nullable
import android.view.MotionEvent
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet


/**
 * Created by intel on 18-03-2019.
 */
class ScrollDisabledRecyclerView : RecyclerView {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, @Nullable attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, @Nullable attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun onTouchEvent(e: MotionEvent): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        return false
    }
}