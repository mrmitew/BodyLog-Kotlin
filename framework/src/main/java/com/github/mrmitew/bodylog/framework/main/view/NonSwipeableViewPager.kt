package com.github.mrmitew.bodylog.framework.main.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

class NonSwipeableViewPager : ViewPager {
    var swipeEnabled: Boolean

    constructor(context: Context) : this(context = context, swipeEnabled = false)

    constructor(context: Context, swipeEnabled: Boolean) : super(context) {
        this.swipeEnabled = swipeEnabled
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        swipeEnabled = false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (swipeEnabled) {
            return super.onTouchEvent(event)
        }
        return false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (swipeEnabled) {
            return super.onInterceptTouchEvent(event)
        }
        return false
    }
}